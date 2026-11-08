package com.waycation.service;

import com.waycation.model.*;
import com.waycation.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    public BookingService(BookingRepository bookingRepository) { this.bookingRepository = bookingRepository; }
    public List<Booking> getTouristBookings(String uid) { return bookingRepository.findByTouristUid(uid); }
}
