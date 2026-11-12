package com.waycation.controller;

import com.waycation.model.Booking;
import com.waycation.model.BookingStatus;
import com.waycation.model.Hotel;
import com.waycation.model.RoomType;
import com.waycation.repository.HotelRepository;
import com.waycation.repository.RoomTypeRepository;
import com.waycation.service.BookingService;
import com.waycation.service.CouponService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Spring MVC Controller for tourist booking checkout, reservation management,
 * rescheduling, cancellations, and hotel owner reservation views.
 */
@Controller
public class BookingController {

    private static final String DEFAULT_TOURIST_UID = "tourist-demo-1";
    private static final String DEFAULT_OWNER_UID = "owner-demo-1";

    private final BookingService bookingService;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final CouponService couponService;

    public BookingController(
            BookingService bookingService,
            HotelRepository hotelRepository,
            RoomTypeRepository roomTypeRepository,
            CouponService couponService) {
        this.bookingService = bookingService;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.couponService = couponService;
    }

    /**
     * Renders the booking checkout summary screen with atomic inventory lock details.
     */
    @GetMapping("/checkout")
    public String showCheckout(
            @RequestParam(name = "hotelId", required = false) String hotelId,
            @RequestParam(name = "roomTypeId", required = false) String roomTypeId,
            @RequestParam(name = "checkIn", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(name = "guests", defaultValue = "2") int guests,
            @RequestParam(name = "rooms", defaultValue = "1") int rooms,
            @RequestParam(name = "couponCode", required = false) String couponCode,
            HttpSession session,
            Model model) {

        LocalDate resolvedCheckIn = checkIn != null ? checkIn : LocalDate.now().plusDays(7);
        LocalDate resolvedCheckOut = (checkOut != null && checkOut.isAfter(resolvedCheckIn))
                ? checkOut
                : resolvedCheckIn.plusDays(3);
        int resolvedRooms = Math.max(1, rooms);
        int resolvedGuests = Math.max(1, guests);

        RoomType roomType = null;
        Hotel hotel = null;

        if (roomTypeId != null && !roomTypeId.isBlank()) {
            roomType = roomTypeRepository.findById(roomTypeId).orElse(null);
            if (roomType != null) {
                hotel = roomType.getHotel();
            }
        }
        if (hotel == null && hotelId != null && !hotelId.isBlank()) {
            hotel = hotelRepository.findById(hotelId).orElse(null);
            if (hotel != null && (roomType == null) && !hotel.getRoomTypes().isEmpty()) {
                roomType = hotel.getRoomTypes().get(0);
            }
        }
        if (hotel == null) {
            List<Hotel> allHotels = hotelRepository.findAll();
            if (!allHotels.isEmpty()) {
                hotel = allHotels.get(0);
                if (!hotel.getRoomTypes().isEmpty()) {
                    roomType = hotel.getRoomTypes().get(0);
                }
            }
        }

        long nights = Math.max(1, ChronoUnit.DAYS.between(resolvedCheckIn, resolvedCheckOut));
        BigDecimal basePrice = roomType != null ? roomType.getBasePrice() : BigDecimal.valueOf(35000);
        BigDecimal subtotal = bookingService.calculateRoomPrice(basePrice, nights, resolvedRooms);

        String touristUid = resolveTouristUid(session);
        BigDecimal discount = BigDecimal.ZERO;
        String couponError = null;

        if (couponCode != null && !couponCode.isBlank()) {
            try {
                discount = couponService.calculateCouponDiscountPreview(couponCode.trim(), touristUid, subtotal);
            } catch (Exception e) {
                couponError = e.getMessage();
            }
        }

        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO);

        model.addAttribute("hotel", hotel);
        model.addAttribute("roomType", roomType);
        model.addAttribute("checkIn", resolvedCheckIn);
        model.addAttribute("checkOut", resolvedCheckOut);
        model.addAttribute("nights", nights);
        model.addAttribute("guests", resolvedGuests);
        model.addAttribute("rooms", resolvedRooms);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("total", total);
        model.addAttribute("couponCode", couponCode != null ? couponCode.trim().toUpperCase() : "");
        model.addAttribute("couponError", couponError);
        model.addAttribute("touristUid", touristUid);
        return "bookings/checkout";
    }

    /**
     * Processes a reservation request and redirects to the Tourist Bookings dashboard.
     */
    @PostMapping("/bookings/create")
    public String createBooking(
            @RequestParam("roomTypeId") String roomTypeId,
            @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(name = "guestCount", defaultValue = "2") int guestCount,
            @RequestParam(name = "roomCount", defaultValue = "1") int roomCount,
            @RequestParam(name = "couponCode", required = false) String couponCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String touristUid = resolveTouristUid(session);
        try {
            Booking created = bookingService.createBooking(
                    touristUid, roomTypeId, checkIn, checkOut, guestCount, roomCount, couponCode);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Booking confirmed (" + created.getId().substring(0, 8).toUpperCase()
                            + ") for " + created.getHotelName() + "!");
            return "redirect:/tourist/bookings";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/checkout?roomTypeId=" + roomTypeId
                    + "&checkIn=" + checkIn
                    + "&checkOut=" + checkOut
                    + "&guests=" + guestCount
                    + "&rooms=" + roomCount
                    + (couponCode != null && !couponCode.isBlank() ? "&couponCode=" + couponCode : "");
        }
    }

    /**
     * Screen 13: Tourist Bookings list with status filters, reschedule modal, and cancellation actions.
     */
    @GetMapping("/tourist/bookings")
    public String listTouristBookings(
            @RequestParam(name = "status", required = false) String statusFilter,
            HttpSession session,
            Model model) {

        String touristUid = resolveTouristUid(session);
        List<Booking> allBookings = bookingService.getBookingsForTourist(touristUid);

        List<Booking> filteredBookings = allBookings;
        if (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter)) {
            try {
                BookingStatus targetStatus = BookingStatus.valueOf(statusFilter.toUpperCase());
                filteredBookings = allBookings.stream()
                        .filter(b -> b.getStatus() == targetStatus)
                        .toList();
            } catch (IllegalArgumentException ignored) {
            }
        }

        long confirmedCount = allBookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long completedCount = allBookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();
        long cancelledCount = allBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();

        model.addAttribute("bookings", filteredBookings);
        model.addAttribute("selectedStatus", statusFilter != null ? statusFilter.toUpperCase() : "ALL");
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("touristUid", touristUid);
        return "tourist/bookings";
    }

    /**
     * Cancels a tourist's reservation and restores room inventory.
     */
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(
            @PathVariable("id") String bookingId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String touristUid = resolveTouristUid(session);
        try {
            bookingService.cancelBooking(touristUid, bookingId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Booking cancelled and room inventory released back to the property.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tourist/bookings";
    }

    /**
     * Reschedules a tourist's reservation to new check-in and check-out dates.
     */
    @PostMapping("/bookings/{id}/reschedule")
    public String rescheduleBooking(
            @PathVariable("id") String bookingId,
            @RequestParam("newCheckIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newCheckIn,
            @RequestParam("newCheckOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newCheckOut,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String touristUid = resolveTouristUid(session);
        try {
            Booking updated = bookingService.rescheduleBooking(touristUid, bookingId, newCheckIn, newCheckOut);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Booking rescheduled to " + updated.getCheckIn() + " → " + updated.getCheckOut()
                            + " (New Total: LKR " + updated.getPriceTotal() + ").");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tourist/bookings";
    }

    /**
     * Marks a reservation as COMPLETED so the guest can leave a verified review or owner can close stay.
     */
    @PostMapping("/bookings/{id}/complete")
    public String completeBooking(
            @PathVariable("id") String bookingId,
            @RequestParam(name = "redirectTo", defaultValue = "/tourist/bookings") String redirectTo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String callerUid = resolveTouristUid(session);
        try {
            bookingService.markBookingCompleted(callerUid, bookingId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Stay marked as COMPLETED. Verified guest review eligibility is now unlocked.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:" + (redirectTo.startsWith("/") ? redirectTo : "/tourist/bookings");
    }

    /**
     * Screen 17: Hotel Owner Guest Reservations console.
     */
    @GetMapping("/owner/bookings")
    public String listOwnerBookings(HttpSession session, Model model) {
        String ownerUid = resolveOwnerUid(session);
        List<Booking> bookings = bookingService.getBookingsForOwner(ownerUid);

        BigDecimal confirmedRevenue = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .map(Booking::getPriceTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeReservations = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();

        model.addAttribute("bookings", bookings);
        model.addAttribute("confirmedRevenue", confirmedRevenue);
        model.addAttribute("activeReservations", activeReservations);
        model.addAttribute("ownerUid", ownerUid);
        return "owner/bookings";
    }

    private String resolveTouristUid(HttpSession session) {
        if (session != null) {
            Object uid = session.getAttribute("uid");
            if (uid instanceof String s && !s.isBlank()) {
                return s;
            }
            Object userUid = session.getAttribute("userUid");
            if (userUid instanceof String s && !s.isBlank()) {
                return s;
            }
        }
        return DEFAULT_TOURIST_UID;
    }

    private String resolveOwnerUid(HttpSession session) {
        if (session != null) {
            Object uid = session.getAttribute("uid");
            if (uid instanceof String s && !s.isBlank()) {
                return s;
            }
            Object ownerUid = session.getAttribute("ownerUid");
            if (ownerUid instanceof String s && !s.isBlank()) {
                return s;
            }
        }
        return DEFAULT_OWNER_UID;
    }
}
