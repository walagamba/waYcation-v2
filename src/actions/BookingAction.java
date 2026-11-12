package com.waycation.actions;

import com.waycation.model.Booking;
import com.waycation.service.BookingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Module 3: Tourist Booking Management Actions
 * Provides server action entry points for atomic room reservations, cancellations,
 * date rescheduling, stay completion marking, and tourist/owner booking retrieval.
 */
@Component
public class BookingAction {

    private final BookingService bookingService;

    public BookingAction(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Booking createBookingAction(
            String touristUid,
            String roomTypeId,
            LocalDate checkIn,
            LocalDate checkOut,
            int guestCount,
            int roomCount,
            String couponCode
    ) {
        return bookingService.createBooking(
                touristUid, roomTypeId, checkIn, checkOut, guestCount, roomCount, couponCode
        );
    }

    public Booking cancelBookingAction(String touristUid, String bookingId) {
        return bookingService.cancelBooking(touristUid, bookingId);
    }

    public Booking rescheduleBookingAction(
            String touristUid,
            String bookingId,
            LocalDate newCheckIn,
            LocalDate newCheckOut
    ) {
        return bookingService.rescheduleBooking(touristUid, bookingId, newCheckIn, newCheckOut);
    }

    public List<Booking> getTouristBookingsAction(String touristUid) {
        return bookingService.getBookingsForTourist(touristUid);
    }

    public List<Booking> getOwnerBookingsAction(String ownerUid) {
        return bookingService.getBookingsForOwner(ownerUid);
    }

    public Booking markBookingCompletedAction(String callerUid, String bookingId) {
        return bookingService.markBookingCompleted(callerUid, bookingId);
    }
}
