package com.waycation.controller;

import com.waycation.model.Coupon;
import com.waycation.model.CouponStatus;
import com.waycation.model.DiscountType;
import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.repository.CouponRepository;
import com.waycation.repository.ProfileRepository;
import com.waycation.service.CouponService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Web MVC controller for Admin Coupon Management (Screen 19),
 * Tourist Group Trips & Voting Hub (Screen 06), and Tinder-Style Group Decision Room (Screen 07).
 */
    // MVC web controller for administrative coupon management and checkout promo codes
@Controller
public class CouponController {

    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final ProfileRepository profileRepository;

    public CouponController(
            CouponService couponService,
            CouponRepository couponRepository,
            ProfileRepository profileRepository) {
        this.couponService = couponService;
        this.couponRepository = couponRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Resolves an administrator UID, creating a fallback default admin profile if none exists
     * during local development/demo flows.
     */
    private String resolveAdminUid(String requestedAdminUid) {
        if (requestedAdminUid != null && !requestedAdminUid.isBlank()) {
            return requestedAdminUid;
        }
        return profileRepository.findAll().stream()
                .filter(p -> p.getRole() == Role.ADMIN || p.getRole() == Role.SUPER_ADMIN)
                .map(Profile::getFirebaseUid)
                .findFirst()
                .orElseGet(() -> {
                    Profile defaultAdmin = new Profile(
                            "admin-default-uid",
                            "admin@waycation.lk",
                            "System Administrator",
                            Role.SUPER_ADMIN,
                            null
                    );
                    return profileRepository.save(defaultAdmin).getFirebaseUid();
                });
    }

    // Admin console for managing promo codes and discount rules
    @GetMapping("/admin/coupons")
    public String showAdminCoupons(
            @RequestParam(name = "adminUid", required = false) String adminUid,
            Model model) {
        String effectiveAdminUid = resolveAdminUid(adminUid);
        List<Coupon> coupons = couponService.getCouponsForAdmin(effectiveAdminUid);

        long activeCount = coupons.stream()
                .filter(c -> c.getStatus() == CouponStatus.ACTIVE)
                .count();
        long totalRedemptions = coupons.stream()
                .mapToLong(Coupon::getRedemptionCount)
                .sum();

        model.addAttribute("coupons", coupons);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("totalCoupons", coupons.size());
        model.addAttribute("totalRedemptions", totalRedemptions);
        model.addAttribute("discountTypes", DiscountType.values());
        model.addAttribute("adminUid", effectiveAdminUid);

        return "admin/coupons";
    }

    @PostMapping("/admin/coupons/create")
    public String createCoupon(
            @RequestParam(name = "adminUid", required = false) String adminUid,
            @RequestParam("code") String code,
            @RequestParam("discountType") DiscountType discountType,
            @RequestParam("discountValue") BigDecimal discountValue,
            @RequestParam(name = "currency", defaultValue = "LKR") String currency,
            @RequestParam("startsAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startsAt,
            @RequestParam("expiresAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiresAt,
            @RequestParam(name = "minimumSpend", defaultValue = "0") BigDecimal minimumSpend,
            @RequestParam(name = "maximumDiscount", required = false) BigDecimal maximumDiscount,
            @RequestParam(name = "globalUsageLimit", defaultValue = "100") int globalUsageLimit,
            @RequestParam(name = "perUserLimit", defaultValue = "1") int perUserLimit,
            RedirectAttributes redirectAttributes) {
        try {
            String effectiveAdminUid = resolveAdminUid(adminUid);

            Coupon coupon = new Coupon();
            coupon.setCode(code);
            coupon.setDiscountType(discountType);
            coupon.setDiscountValue(discountValue);
            coupon.setCurrency(currency);
            coupon.setStartsAt(startsAt);
            coupon.setExpiresAt(expiresAt);
            coupon.setMinimumSpend(minimumSpend);
            coupon.setMaximumDiscount(maximumDiscount);
            coupon.setGlobalUsageLimit(globalUsageLimit);
            coupon.setPerUserLimit(perUserLimit);
            coupon.setStatus(CouponStatus.ACTIVE);

            Coupon created = couponService.createCoupon(effectiveAdminUid, coupon);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Promotional coupon '" + created.getCode() + "' created and activated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    @PostMapping("/admin/coupons/{id}/deactivate")
    public String deactivateCoupon(
            @PathVariable("id") String couponId,
            @RequestParam(name = "adminUid", required = false) String adminUid,
            RedirectAttributes redirectAttributes) {
        try {
            String effectiveAdminUid = resolveAdminUid(adminUid);
            Coupon deactivated = couponService.deactivateCoupon(effectiveAdminUid, couponId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Coupon '" + deactivated.getCode() + "' has been deactivated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    @GetMapping("/tourist/trips")
    public String showTouristTrips(Model model) {
        List<Map<String, Object>> activeSessions = List.of(
                Map.of(
                        "title", "South Coast Surf & Villa Escape",
                        "destination", "Ahangama, Weligama & Mirissa",
                        "dates", "Dec 14 â€“ Dec 19, 2026",
                        "token", "SOUTH-SURF-2026",
                        "participants", 5,
                        "votesCast", 18,
                        "status", "VOTING_OPEN",
                        "topPick", "Palmstone Retreat Kithulgala & Cape Weligama"
                ),
                Map.of(
                        "title", "Hill Country Tea Trails & Nine Arch",
                        "destination", "Nuwara Eliya & Ella",
                        "dates", "Jan 08 â€“ Jan 12, 2027",
                        "token", "ELLA-MIST-884",
                        "participants", 4,
                        "votesCast", 16,
                        "status", "CONSENSUS_REACHED",
                        "topPick", "98 Acres Resort & Spa"
                ),
                Map.of(
                        "title", "Cultural Triangle Heritage Expedition",
                        "destination", "Sigiriya, Dambulla & Kandy",
                        "dates", "Feb 02 â€“ Feb 06, 2027",
                        "token", "SIGIRIYA-CREW-7",
                        "participants", 6,
                        "votesCast", 9,
                        "status", "VOTING_OPEN",
                        "topPick", "Water Garden Sigiriya"
                )
        );

        model.addAttribute("groupSessions", activeSessions);
        return "tourist/trips";
    }

    @GetMapping("/group/{token}")
    public String showGroupVotingRoom(@PathVariable("token") String token, Model model) {
        List<Map<String, Object>> candidateCards = List.of(
                Map.of(
                        "id", "pkg-cape-weligama",
                        "name", "Cape Weligama Cliffside Sanctuary",
                        "location", "Weligama, Southern Province",
                        "category", "LUXURY COASTAL RESORT",
                        "pricePerNightLkr", "145,000",
                        "rating", "4.9",
                        "reviews", 312,
                        "highlights", List.of("Crescent Infinity Pool", "Private Surf Butler", "Oceanfront Dining"),
                        "imageUrl", "https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&w=1000&q=80",
                        "yesVotes", 4,
                        "totalMembers", 5
                ),
                Map.of(
                        "id", "pkg-98-acres",
                        "name", "98 Acres Tea Estate Eco Resort",
                        "location", "Ella, Uva Province",
                        "category", "BOUTIQUE MOUNTAIN CHALET",
                        "pricePerNightLkr", "98,500",
                        "rating", "4.8",
                        "reviews", 428,
                        "highlights", List.of("Little Adam's Peak View", "Organic Tea Tasting", "Heated Plunge Pool"),
                        "imageUrl", "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1000&q=80",
                        "yesVotes", 3,
                        "totalMembers", 5
                ),
                Map.of(
                        "id", "pkg-water-garden",
                        "name", "Water Garden Sigiriya",
                        "location", "Sigiriya, Cultural Triangle",
                        "category", "HERITAGE LUXURY VILLA",
                        "pricePerNightLkr", "122,000",
                        "rating", "4.9",
                        "reviews", 275,
                        "highlights", List.of("Unobstructed Lion Rock Vista", "Private Plunge Villas", "Ayurvedic Spa"),
                        "imageUrl", "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1000&q=80",
                        "yesVotes", 2,
                        "totalMembers", 5
                )
        );

        model.addAttribute("token", token);
        model.addAttribute("sessionTitle", "Sri Lanka Group Escape (" + token + ")");
        model.addAttribute("candidates", candidateCards);
        return "group/voting-room";
    }
}


