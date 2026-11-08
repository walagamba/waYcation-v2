package com.waycation.service;

import com.waycation.model.Review;
import com.waycation.model.ReviewStatus;
import com.waycation.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(String touristUid, String hotelId, String bookingId, int rating, String comment) {
        Review review = new Review();
        review.setTouristUid(touristUid);
        review.setHotelId(hotelId);
        review.setBookingId(bookingId);
        review.setRating(rating);
        review.setComment(comment);
        review.setStatus(ReviewStatus.PUBLISHED);
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForTourist(String touristUid) {
        return reviewRepository.findByTouristUidOrderByCreatedAtDesc(touristUid);
    }
}
