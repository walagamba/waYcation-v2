package com.waycation;

import com.waycation.model.Hotel;
import com.waycation.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WaycationApplicationTests {

    @Autowired
    private HotelRepository hotelRepository;

    @Test
    void contextLoadsAndFetchesHotels() {
        assertNotNull(hotelRepository, "HotelRepository should be injected");
        List<Hotel> hotels = hotelRepository.findAll();
        System.out.println(">>> Verified Accommodations in Spring Boot JPA:");
        for (Hotel h : hotels) {
            System.out.println(" - Hotel: " + h.getName() + " in " + h.getCity() + " (Rooms: " + (h.getRoomTypes() != null ? h.getRoomTypes().size() : 0) + ")");
        }
        assertFalse(hotels.isEmpty(), "Should have accommodations in database");
    }
}
