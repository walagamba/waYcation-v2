package com.waycation.exception;

/**
 * Thrown when someone who is not the hotel's owner tries to change it.
 * INHERITANCE: a HotelAccessDeniedException "is a" HotelException.
 */
public class HotelAccessDeniedException extends HotelException {

    private final String hotelId;

    // CONSTRUCTOR OVERLOADING: without a hotel id ...
    public HotelAccessDeniedException(String action) {
        this(action, null);
    }

    // ... and with a hotel id.
    public HotelAccessDeniedException(String action, String hotelId) {
        super("Only the owner of this hotel can " + action + ".");
        this.hotelId = hotelId;
    }

    public String getHotelId() {
        return hotelId;
    }

    // METHOD OVERRIDING
    @Override
    public String getErrorCode() {
        return "ACCESS_DENIED";
    }
}