package com.waycation.model;

/**
 * Reservation lifecycle state for tourist hotel bookings.
 */
    // Reservation lifecycle state machine enum (CONFIRMED, CANCELLED, COMPLETED, etc.)
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

