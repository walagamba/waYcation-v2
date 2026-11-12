package com.waycation.repository;

import com.waycation.model.Review;
import com.waycation.model.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    // Spring Data JPA repository for saving and querying hotel reviews
@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    Optional<Review> findByBookingId(String bookingId);

    List<Review> findByTouristUidOrderByCreatedAtDesc(String touristUid);

    List<Review> findByHotelIdAndStatusOrderByCreatedAtDesc(String hotelId, ReviewStatus status);
}

