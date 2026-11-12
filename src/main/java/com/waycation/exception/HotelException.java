package com.waycation.exception;

/**
 * ABSTRACT PARENT for every error the Hotel module throws on purpose.
 *
 * INHERITANCE: HotelException extends RuntimeException (a Java class), and
 * HotelNotFoundException / HotelAccessDeniedException extend HotelException.
 *
 * Each child MUST say which error code it is (abstract getErrorCode()), and the
 * parent puts that code in front of every message (overridden getMessage()).
 */
public abstract class HotelException extends RuntimeException {

    // CONSTRUCTOR OVERLOADING: same name, different parameter lists.
    protected HotelException(String message) {
        super(message);
    }

    protected HotelException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Every child class must provide its own short code, e.g. "ACCESS_DENIED". */
    public abstract String getErrorCode();

    /**
     * METHOD OVERRIDING: replaces RuntimeException's getMessage().
     * Calls getErrorCode(), so the text depends on which child object this is.
     */
    @Override
    public String getMessage() {
        return "[" + getErrorCode() + "] " + super.getMessage();
    }
}