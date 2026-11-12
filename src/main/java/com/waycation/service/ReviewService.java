package com.waycation.service;

import com.waycation.model.Booking;
import com.waycation.model.BookingStatus;
import com.waycation.model.Hotel;
import com.waycation.model.Profile;
import com.waycation.model.Review;
import com.waycation.model.ReviewStatus;
import com.waycation.repository.BookingRepository;
import com.waycation.repository.HotelRepository;
import com.waycation.repository.ProfileRepository;
import com.waycation.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

    // Service layer managing review submission, verified stay checks, and star averages
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final ProfileRepository profileRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository,
            HotelRepository hotelRepository,
            ProfileRepository profileRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Review createReview(String touristUid, String hotelId, String bookingId, int rating, String comment) {
        if (touristUid == null || touristUid.isBlank()) {
            throw new IllegalArgumentException("UNAUTHORIZED: Tourist authentication is required.");
        }
        // Validate rating is between 1 and 5 stars
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("INVALID_RATING: Rating must be an integer between 1 and 5.");
        }
        if (comment == null || comment.trim().length() < 5) {
            throw new IllegalArgumentException("INVALID_COMMENT: Review comment must be at least 5 characters long.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND: Booking record could not be found."));

        if (!touristUid.equals(booking.getTouristUid())) {
            throw new IllegalArgumentException("FORBIDDEN_BOOKING: You can only review bookings associated with your account.");
        }

        if (hotelId != null && !hotelId.isBlank() && !hotelId.equals(booking.getHotelId())) {
            throw new IllegalArgumentException("HOTEL_MISMATCH: Booking does not belong to the specified hotel.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("CANCELLED_BOOKING: Cancelled bookings are not eligible for review.");
        }

        boolean isCompleted = booking.getStatus() == BookingStatus.COMPLETED;
        boolean isConfirmedPastCheckout = booking.getStatus() == BookingStatus.CONFIRMED
                && booking.getCheckOut() != null
                && !booking.getCheckOut().isAfter(LocalDate.now());

        if (!isCompleted && !isConfirmedPastCheckout) {
            throw new IllegalStateException("STAY_NOT_COMPLETED: Reviews can only be submitted for completed stays.");
        }

        if (reviewRepository.findByBookingId(bookingId).isPresent()) {
            throw new IllegalStateException("DUPLICATE_REVIEW: You have already submitted a review for this stay.");
        }

        Review review = new Review(
                touristUid,
                booking.getHotelId(),
                bookingId,
                rating,
                comment.trim()
        );
        // Set review status to PUBLISHED so it appears on the hotel page
        review.setStatus(ReviewStatus.PUBLISHED);
        // Persist verified review entity to database
        Review saved = reviewRepository.save(review);
        enrichReviewDisplayFields(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsForTourist(String touristUid) {
        List<Review> reviews = reviewRepository.findByTouristUidOrderByCreatedAtDesc(touristUid);
        reviews.forEach(this::enrichReviewDisplayFields);
        return reviews;
    }

    @Transactional(readOnly = true)
    public List<Review> getPublishedReviewsForHotel(String hotelId) {
        List<Review> reviews = reviewRepository.findByHotelIdAndStatusOrderByCreatedAtDesc(hotelId, ReviewStatus.PUBLISHED);
        reviews.forEach(this::enrichReviewDisplayFields);
        return reviews;
    }

    @Transactional
    public Review updateReview(String touristUid, String reviewId, Integer rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("REVIEW_NOT_FOUND: Review could not be found."));

        if (!review.getTouristUid().equals(touristUid)) {
            throw new IllegalArgumentException("FORBIDDEN: You can only update your own reviews.");
        }

        if (rating != null) {
            // Validate rating is between 1 and 5 stars
        if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("INVALID_RATING: Rating must be between 1 and 5.");
            }
            review.setRating(rating);
        }

        if (comment != null) {
            if (comment.trim().length() < 5) {
                throw new IllegalArgumentException("INVALID_COMMENT: Review comment must be at least 5 characters long.");
            }
            review.setComment(comment.trim());
        }

        review.setUpdatedAt(LocalDateTime.now());
        // Persist verified review entity to database
        Review saved = reviewRepository.save(review);
        enrichReviewDisplayFields(saved);
        return saved;
    }

    @Transactional
    public void deleteReview(String touristUid, String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("REVIEW_NOT_FOUND: Review could not be found."));

        if (!review.getTouristUid().equals(touristUid)) {
            throw new IllegalArgumentException("FORBIDDEN: You can only delete your own reviews.");
        }

        reviewRepository.delete(review);
    }

    @Transactional
    public Review moderateReview(String adminUid, String reviewId, ReviewStatus status) {
        Profile adminProfile = findProfileByUidOrId(adminUid)
                .orElseThrow(() -> new IllegalArgumentException("UNAUTHORIZED: Admin profile not found."));

        String roleValue = String.valueOf(adminProfile.getRole());
        if (!"ADMIN".equals(roleValue) && !"SUPER_ADMIN".equals(roleValue)) {
            throw new IllegalArgumentException("FORBIDDEN: Only administrators can moderate reviews.");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("REVIEW_NOT_FOUND: Review could not be found."));

        review.setStatus(status);
        review.setUpdatedAt(LocalDateTime.now());
        // Persist verified review entity to database
        Review saved = reviewRepository.save(review);
        enrichReviewDisplayFields(saved);
        return saved;
    }

    private Optional<Profile> findProfileByUidOrId(String uid) {
        if (uid == null || uid.isBlank()) {
            return Optional.empty();
        }
        return profileRepository.findByFirebaseUid(uid)
                .or(() -> profileRepository.findById(uid));
    }

    private void enrichReviewDisplayFields(Review review) {
        if (review.getHotelId() != null) {
            Optional<Hotel> hotelOpt = hotelRepository.findById(review.getHotelId());
            review.setHotelName(hotelOpt.map(Hotel::getName).orElse("waYcation Verified Property"));
        }
        if (review.getTouristUid() != null) {
            Optional<Profile> profileOpt = findProfileByUidOrId(review.getTouristUid());
            review.setTouristDisplayName(profileOpt.map(Profile::getDisplayName).orElse("Verified Guest"));
        }
    }
}




