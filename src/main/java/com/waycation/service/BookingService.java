package com.waycation.service;

import com.waycation.model.Booking;
import com.waycation.model.BookingStatus;
import com.waycation.model.Hotel;
import com.waycation.model.HotelStatus;
import com.waycation.model.RoomInventory;
import com.waycation.model.RoomType;
import com.waycation.repository.BookingRepository;
import com.waycation.repository.HotelRepository;
import com.waycation.repository.RoomInventoryRepository;
import com.waycation.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service handling tourist room reservations, atomic daily inventory allocation,
 * cancellations, rescheduling, and stay completion.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final HotelRepository hotelRepository;
    private final CouponService couponService;

    public BookingService(
            BookingRepository bookingRepository,
            RoomTypeRepository roomTypeRepository,
            RoomInventoryRepository roomInventoryRepository,
            HotelRepository hotelRepository,
            CouponService couponService) {
        this.bookingRepository = bookingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomInventoryRepository = roomInventoryRepository;
        this.hotelRepository = hotelRepository;
        this.couponService = couponService;
    }

    /**
     * Calculates the total base room price for a reservation.
     */
    public BigDecimal calculateRoomPrice(BigDecimal basePrice, long nights, int roomCount) {
        if (nights <= 0) {
            throw new IllegalArgumentException("INVALID_DATES: Stay duration must be at least 1 night");
        }
        if (roomCount <= 0) {
            throw new IllegalArgumentException("INVALID_ROOM_COUNT: Room count must be at least 1");
        }
        double raw = (basePrice != null ? basePrice.doubleValue() : 0.0) * nights * roomCount;
        return BigDecimal.valueOf(Math.round(raw));
    }

    /**
     * Atomically creates a confirmed hotel booking, applies promotional coupons, and reserves room inventory.
     */
    @Transactional
    public Booking createBooking(
            String touristUid,
            String roomTypeId,
            LocalDate checkIn,
            LocalDate checkOut,
            int guestCount,
            int roomCount,
            String couponCode) {

        if (touristUid == null || touristUid.isBlank()) {
            throw new IllegalArgumentException("UNAUTHORIZED: Tourist UID is required to create a booking");
        }
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("INVALID_DATES: Check-out date must be after check-in date");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < 1 || nights > 30) {
            throw new IllegalArgumentException("INVALID_DATES: Stay duration must be between 1 and 30 nights");
        }
        if (roomCount <= 0) {
            throw new IllegalArgumentException("INVALID_ROOM_COUNT: Room count must be at least 1");
        }
        if (guestCount <= 0) {
            throw new IllegalArgumentException("INVALID_GUEST_COUNT: Guest count must be at least 1");
        }

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: Room type not found: " + roomTypeId));

        Hotel hotel = roomType.getHotel();
        if (hotel == null || hotel.getStatus() != HotelStatus.ACTIVE) {
            throw new IllegalStateException("HOTEL_UNAVAILABLE: Hotel is not currently accepting bookings");
        }

        int maxGuests = roomType.getCapacity() * roomCount;
        if (guestCount > maxGuests) {
            throw new IllegalArgumentException("CAPACITY_EXCEEDED: Selected room count only accommodates up to " + maxGuests + " guests");
        }

        allocateInventory(roomType, checkIn, checkOut, roomCount);

        BigDecimal basePriceTotal = calculateRoomPrice(roomType.getBasePrice(), nights, roomCount);
        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                discount = couponService.calculateCouponDiscountPreview(couponCode.trim(), touristUid, basePriceTotal);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        BigDecimal totalPrice = basePriceTotal.subtract(discount).max(BigDecimal.ZERO);

        Booking booking = new Booking();
        booking.setTouristUid(touristUid);
        booking.setHotelId(hotel.getId());
        booking.setRoomTypeId(roomType.getId());
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuestCount(guestCount);
        booking.setRoomCount(roomCount);
        booking.setPriceTotal(totalPrice);
        booking.setCurrency(roomType.getCurrency() != null ? roomType.getCurrency() : "LKR");
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPolicySnapshot(
                hotel.getCancellationPolicy() != null && !hotel.getCancellationPolicy().isBlank()
                        ? hotel.getCancellationPolicy()
                        : "Free cancellation up to 48 hours before check-in. Late cancellations incur a 1-night charge.");

        Booking saved = bookingRepository.save(booking);

        if (couponCode != null && !couponCode.isBlank() && discount.compareTo(BigDecimal.ZERO) > 0) {
            try {
                couponService.validateAndRedeemCoupon(couponCode.trim(), touristUid, basePriceTotal, saved.getId());
            } catch (Exception ignored) {
            }
        }

        enrichBooking(saved, hotel, roomType);
        return saved;
    }

    /**
     * Cancels a confirmed or pending booking and releases its daily room inventory.
     */
    @Transactional
    public Booking cancelBooking(String touristUid, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: Booking not found: " + bookingId));

        if (!booking.getTouristUid().equals(touristUid)) {
            throw new IllegalStateException("FORBIDDEN: You do not have permission to cancel this booking");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("ALREADY_CANCELLED: Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("CANNOT_CANCEL_COMPLETED: Completed bookings cannot be cancelled");
        }

        releaseInventory(booking.getRoomTypeId(), booking.getCheckIn(), booking.getCheckOut(), booking.getRoomCount());

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);
        enrichSingleBooking(saved);
        return saved;
    }

    /**
     * Reschedules an existing confirmed booking to new check-in and check-out dates,
     * releasing old inventory and atomically reserving the new dates.
     */
    @Transactional
    public Booking rescheduleBooking(String touristUid, String bookingId, LocalDate newCheckIn, LocalDate newCheckOut) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: Booking not found: " + bookingId));

        if (!booking.getTouristUid().equals(touristUid)) {
            throw new IllegalStateException("FORBIDDEN: You do not have permission to reschedule this booking");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("INVALID_STATUS: Only active bookings can be rescheduled");
        }
        if (newCheckIn == null || newCheckOut == null || !newCheckIn.isBefore(newCheckOut)) {
            throw new IllegalArgumentException("INVALID_DATES: New check-out date must be after new check-in date");
        }
        long nights = ChronoUnit.DAYS.between(newCheckIn, newCheckOut);
        if (nights < 1 || nights > 30) {
            throw new IllegalArgumentException("INVALID_DATES: Stay duration must be between 1 and 30 nights");
        }

        RoomType roomType = roomTypeRepository.findById(booking.getRoomTypeId())
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: Room type not found"));

        releaseInventory(booking.getRoomTypeId(), booking.getCheckIn(), booking.getCheckOut(), booking.getRoomCount());
        allocateInventory(roomType, newCheckIn, newCheckOut, booking.getRoomCount());

        BigDecimal newPrice = calculateRoomPrice(roomType.getBasePrice(), nights, booking.getRoomCount());
        booking.setCheckIn(newCheckIn);
        booking.setCheckOut(newCheckOut);
        booking.setPriceTotal(newPrice);

        Booking saved = bookingRepository.save(booking);
        enrichBooking(saved, roomType.getHotel(), roomType);
        return saved;
    }

    /**
     * Marks a confirmed booking as completed so the guest can submit a verified review.
     */
    @Transactional
    public Booking markBookingCompleted(String callerUid, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: Booking not found: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("INVALID_STATUS: Cancelled bookings cannot be marked completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        Booking saved = bookingRepository.save(booking);
        enrichSingleBooking(saved);
        return saved;
    }

    /**
     * Returns all bookings for a tourist ordered by newest first, enriched with hotel and room details.
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsForTourist(String touristUid) {
        List<Booking> bookings = bookingRepository.findByTouristUidOrderByCreatedAtDesc(touristUid);
        enrichBookings(bookings);
        return bookings;
    }

    /**
     * Returns all bookings across hotels owned by the specified owner UID, enriched with hotel and room details.
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsForOwner(String ownerUid) {
        List<Hotel> ownerHotels = hotelRepository.findByOwnerUidOrderByCreatedAtDesc(ownerUid);
        if (ownerHotels.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> hotelIds = ownerHotels.stream()
                .map(Hotel::getId)
                .toList();
        List<Booking> bookings = bookingRepository.findByHotelIdInOrderByCreatedAtDesc(hotelIds);
        enrichBookings(bookings);
        return bookings;
    }

    /**
     * Retrieves a single booking by ID with enriched hotel and room metadata.
     */
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(String bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        bookingOpt.ifPresent(this::enrichSingleBooking);
        return bookingOpt;
    }

    private void allocateInventory(RoomType roomType, LocalDate checkIn, LocalDate checkOut, int roomCount) {
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            final LocalDate targetDate = date;
            RoomInventory inventory = roomInventoryRepository
                    .findByRoomTypeIdAndDate(roomType.getId(), targetDate)
                    .orElseGet(() -> new RoomInventory(roomType.getId(), targetDate, roomType.getTotalRooms(), 0));

            if (inventory.getBookedCount() + roomCount > inventory.getTotalCount()) {
                throw new IllegalStateException("SOLD_OUT: No rooms available for " + targetDate);
            }
            inventory.setBookedCount(inventory.getBookedCount() + roomCount);
            roomInventoryRepository.save(inventory);
        }
    }

    private void releaseInventory(String roomTypeId, LocalDate checkIn, LocalDate checkOut, int roomCount) {
        if (checkIn == null || checkOut == null) {
            return;
        }
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            roomInventoryRepository.findByRoomTypeIdAndDate(roomTypeId, date).ifPresent(inventory -> {
                int updatedCount = Math.max(0, inventory.getBookedCount() - roomCount);
                inventory.setBookedCount(updatedCount);
                roomInventoryRepository.save(inventory);
            });
        }
    }

    private void enrichSingleBooking(Booking booking) {
        Hotel hotel = hotelRepository.findById(booking.getHotelId()).orElse(null);
        RoomType roomType = roomTypeRepository.findById(booking.getRoomTypeId()).orElse(null);
        enrichBooking(booking, hotel, roomType);
    }

    private void enrichBookings(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return;
        }
        Map<String, Hotel> hotelMap = hotelRepository.findAllById(
                bookings.stream().map(Booking::getHotelId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Hotel::getId, Function.identity()));

        Map<String, RoomType> roomTypeMap = roomTypeRepository.findAllById(
                bookings.stream().map(Booking::getRoomTypeId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(RoomType::getId, Function.identity()));

        for (Booking booking : bookings) {
            enrichBooking(booking, hotelMap.get(booking.getHotelId()), roomTypeMap.get(booking.getRoomTypeId()));
        }
    }

    private void enrichBooking(Booking booking, Hotel hotel, RoomType roomType) {
        if (hotel != null) {
            booking.setHotelName(hotel.getName());
            booking.setCity(hotel.getCity());
            booking.setFeaturedImage(hotel.getFeaturedImage());
        } else {
            booking.setHotelName("Ceylon Boutique Property");
            booking.setCity("Sri Lanka");
        }
        if (roomType != null) {
            booking.setRoomTypeName(roomType.getName());
        } else {
            booking.setRoomTypeName("Deluxe Suite");
        }
        if (booking.getGuestName() == null || booking.getGuestName().isBlank()) {
            booking.setGuestName("Tourist (" + booking.getTouristUid() + ")");
        }
    }
}
