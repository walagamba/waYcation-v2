package com.waycation.repository;

import com.waycation.model.Booking;
import com.waycation.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Booking} entities.
 */
    // Spring Data JPA repository for saving and finding reservations
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByTouristUidOrderByCreatedAtDesc(String touristUid);

    List<Booking> findByHotelIdInOrderByCreatedAtDesc(List<String> hotelIds);

    List<Booking> findByTouristUidAndStatusIn(String touristUid, List<BookingStatus> statuses);
}

