package com.waycation.controller;

import com.waycation.model.Booking;
import com.waycation.model.BookingStatus;
import com.waycation.model.Hotel;
import com.waycation.model.Review;
import com.waycation.model.ReviewStatus;
import com.waycation.repository.BookingRepository;
import com.waycation.repository.HotelRepository;
import com.waycation.repository.ReviewRepository;
import com.waycation.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ReviewController {

    private static final String DEFAULT_TOURIST_UID = "tourist-demo-001";
    private static final String DEFAULT_ADMIN_UID = "admin-demo-001";

    private final ReviewService reviewService;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;

    public ReviewController(
            ReviewService reviewService,
            BookingRepository bookingRepository,
            HotelRepository hotelRepository,
            ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/tourist/reviews")
    public String viewTouristReviews(
            @RequestParam(value = "uid", required = false) String uidParam,
            HttpSession session,
            Model model) {
        String touristUid = resolveTouristUid(uidParam, session);
        List<Review> reviews = reviewService.getReviewsForTourist(touristUid);

        Set<String> reviewedBookingIds = reviews.stream()
                .map(Review::getBookingId)
                .collect(Collectors.toSet());

        List<Booking> allTouristBookings = bookingRepository.findByTouristUidOrderByCreatedAtDesc(touristUid);
        for (Booking b : allTouristBookings) {
            if (b.getHotelName() == null && b.getHotelId() != null) {
                hotelRepository.findById(b.getHotelId()).map(Hotel::getName).ifPresent(b::setHotelName);
            }
        }

        List<Booking> eligibleBookings = allTouristBookings.stream()
                .filter(b -> !reviewedBookingIds.contains(b.getId()))
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED
                        || (b.getStatus() == BookingStatus.CONFIRMED
                            && b.getCheckOut() != null
                            && !b.getCheckOut().isAfter(LocalDate.now())))
                .collect(Collectors.toList());

        double averageRating = reviews.isEmpty()
                ? 0.0
                : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        model.addAttribute("touristUid", touristUid);
        model.addAttribute("reviews", reviews);
        model.addAttribute("eligibleBookings", eligibleBookings);
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("averageRating", String.format("%.1f", averageRating));
        return "tourist/reviews";
    }

    @PostMapping("/reviews/create")
    public String createReview(
            @RequestParam(value = "touristUid", required = false) String touristUidParam,
            @RequestParam(value = "hotelId", required = false) String hotelId,
            @RequestParam("bookingId") String bookingId,
            @RequestParam("rating") int rating,
            @RequestParam("comment") String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String touristUid = resolveTouristUid(touristUidParam, session);
        try {
            String resolvedHotelId = hotelId;
            if (resolvedHotelId == null || resolvedHotelId.isBlank()) {
                resolvedHotelId = bookingRepository.findById(bookingId)
                        .map(Booking::getHotelId)
                        .orElse("");
            }
            reviewService.createReview(touristUid, resolvedHotelId, bookingId, rating, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Your verified stay review has been published.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tourist/reviews";
    }

    @PostMapping("/reviews/{id}/update")
    public String updateReview(
            @PathVariable("id") String reviewId,
            @RequestParam(value = "touristUid", required = false) String touristUidParam,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "comment", required = false) String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String touristUid = resolveTouristUid(touristUidParam, session);
        try {
            reviewService.updateReview(touristUid, reviewId, rating, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Your review has been updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tourist/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(
            @PathVariable("id") String reviewId,
            @RequestParam(value = "touristUid", required = false) String touristUidParam,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String touristUid = resolveTouristUid(touristUidParam, session);
        try {
            reviewService.deleteReview(touristUid, reviewId);
            redirectAttributes.addFlashAttribute("successMessage", "Your review has been permanently removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tourist/reviews";
    }

    @PostMapping("/admin/reviews/{id}/moderate")
    public String moderateReview(
            @PathVariable("id") String reviewId,
            @RequestParam(value = "adminUid", required = false) String adminUidParam,
            @RequestParam("status") ReviewStatus status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String adminUid = adminUidParam;
        if (adminUid == null || adminUid.isBlank()) {
            Object sessionUid = session.getAttribute("uid");
            adminUid = sessionUid != null ? sessionUid.toString() : DEFAULT_ADMIN_UID;
        }
        try {
            reviewService.moderateReview(adminUid, reviewId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Review moderation status updated to " + status + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tourist/reviews";
    }

    private String resolveTouristUid(String uidParam, HttpSession session) {
        if (uidParam != null && !uidParam.isBlank()) {
            return uidParam;
        }
        if (session != null) {
            Object sessionUid = session.getAttribute("uid");
            if (sessionUid != null && !sessionUid.toString().isBlank()) {
                return sessionUid.toString();
            }
            Object userUid = session.getAttribute("touristUid");
            if (userUid != null && !userUid.toString().isBlank()) {
                return userUid.toString();
            }
        }
        return DEFAULT_TOURIST_UID;
    }
}
