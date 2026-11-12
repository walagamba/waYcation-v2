package com.waycation.actions;

import com.waycation.model.Review;
import com.waycation.model.ReviewStatus;
import com.waycation.service.ReviewService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Module 4: Tourist Review Management Actions
 * Provides server action entry points for creating verified-stay reviews,
 * updating ratings and comments, deleting reviews, and administrator moderation.
 */
@Component
    // Facade Design Pattern: wraps review actions into clean, reusable action methods
public class ReviewAction {

    private final ReviewService reviewService;

    public ReviewAction(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public Review createReviewAction(
            String touristUid,
            String hotelId,
            String bookingId,
            int rating,
            String comment
    ) {
        return reviewService.createReview(touristUid, hotelId, bookingId, rating, comment);
    }

    public Review updateReviewAction(
            String touristUid,
            String reviewId,
            Integer rating,
            String comment
    ) {
        return reviewService.updateReview(touristUid, reviewId, rating, comment);
    }

    public void deleteReviewAction(String touristUid, String reviewId) {
        reviewService.deleteReview(touristUid, reviewId);
    }

    public List<Review> getTouristReviewsAction(String touristUid) {
        return reviewService.getReviewsForTourist(touristUid);
    }

    public Review moderateReviewAction(String adminUid, String reviewId, ReviewStatus status) {
        return reviewService.moderateReview(adminUid, reviewId, status);
    }
}

