package com.waycation.exception;

/**
 * Thrown when a hotel or room category id does not exist.
 * INHERITANCE: a HotelNotFoundException "is a" HotelException.
 */
public class HotelNotFoundException extends HotelException {

    // CONSTRUCTOR OVERLOADING: one for a hotel id ...
    public HotelNotFoundException(String hotelId) {
        this("Hotel", hotelId);   // calls the other constructor below (constructor chaining)
    }

    // ... and one for any kind of record (e.g. "Room category").
    public HotelNotFoundException(String recordType, String id) {
        super(recordType + " not found: " + id);   // calls the parent constructor
    }

    // METHOD OVERRIDING: gives the parent's abstract method a body.
    @Override
    public String getErrorCode() {
        return "NOT_FOUND";
    }
}