package com.waycation.service;

import com.waycation.model.Coupon;
import com.waycation.model.CouponRedemption;
import com.waycation.model.CouponStatus;
import com.waycation.model.DiscountType;
import com.waycation.model.Profile;
import com.waycation.model.Role;
import com.waycation.repository.CouponRedemptionRepository;
import com.waycation.repository.CouponRepository;
import com.waycation.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Business logic service for Module 6: Admin Coupon Management, promotional
 * code validation, discount calculations, and tourist redemptions.
 */
    // Business logic service managing promo code validation, discount calculations, and audit logs
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final ProfileRepository profileRepository;

    public CouponService(
            CouponRepository couponRepository,
            CouponRedemptionRepository couponRedemptionRepository,
            ProfileRepository profileRepository) {
        this.couponRepository = couponRepository;
        this.couponRedemptionRepository = couponRedemptionRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Verifies the caller has ADMIN or SUPER_ADMIN privileges via ProfileRepository.
     */
    private void verifyAdminAccess(String adminUid) {
        if (adminUid == null || adminUid.isBlank()) {
            throw new IllegalArgumentException("Admin UID is required.");
        }
        Optional<Profile> profileOpt = profileRepository.findByFirebaseUid(adminUid)
                .or(() -> profileRepository.findById(adminUid));

        if (profileOpt.isEmpty()) {
            throw new SecurityException("Unauthorized: Admin profile not found.");
        }

        Role role = profileOpt.get().getRole();
        if (role != Role.ADMIN && role != Role.SUPER_ADMIN) {
            throw new SecurityException("Forbidden: Only ADMIN or SUPER_ADMIN users can manage promotional coupons.");
        }
    }

    /**
     * Creates a new promotional coupon after verifying administrator privileges,
     * normalizing the promo code to uppercase, checking uniqueness, and validating
     * the promotion window.
     */
    @Transactional
    public Coupon createCoupon(String adminUid, Coupon coupon) {
        verifyAdminAccess(adminUid);

        if (coupon == null) {
            throw new IllegalArgumentException("Coupon payload cannot be null.");
        }
        if (coupon.getCode() == null || coupon.getCode().isBlank()) {
            throw new IllegalArgumentException("Coupon code is required.");
        }

        String normalizedCode = coupon.getCode().trim().toUpperCase(Locale.ROOT);
        if (couponRepository.findByCodeIgnoreCase(normalizedCode).isPresent()) {
            throw new IllegalArgumentException("Coupon code '" + normalizedCode + "' already exists.");
        }

        if (coupon.getStartsAt() == null || coupon.getExpiresAt() == null) {
            throw new IllegalArgumentException("Both startsAt and expiresAt timestamps are required.");
        }
        if (!coupon.getStartsAt().isBefore(coupon.getExpiresAt())) {
            throw new IllegalArgumentException("Coupon start date must be strictly before its expiration date.");
        }

        if (coupon.getDiscountType() == null) {
            throw new IllegalArgumentException("Discount type is required.");
        }
        if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than zero.");
        }
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE
                && coupon.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%.");
        }
        if (coupon.getGlobalUsageLimit() <= 0) {
            throw new IllegalArgumentException("Global usage limit must be at least 1.");
        }
        if (coupon.getPerUserLimit() <= 0) {
            coupon.setPerUserLimit(1);
        }

        coupon.setCode(normalizedCode);
        coupon.setCreatedByUid(adminUid);
        if (coupon.getCurrency() == null || coupon.getCurrency().isBlank()) {
            coupon.setCurrency("LKR");
        }
        if (coupon.getMinimumSpend() == null) {
            coupon.setMinimumSpend(BigDecimal.ZERO);
        }
        if (coupon.getStatus() == null) {
            coupon.setStatus(CouponStatus.ACTIVE);
        }

        return couponRepository.save(coupon);
    }

    /**
     * Calculates the exact integer-rounded discount amount for an order.
     * PERCENTAGE discounts are capped by maximumDiscount if present;
     * FIXED discounts are capped by orderAmount.
     */
    public BigDecimal calculateDiscount(
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal orderAmount,
            BigDecimal maximumDiscount) {
        if (discountType == null || discountValue == null || orderAmount == null) {
            return BigDecimal.ZERO;
        }
        if (orderAmount.compareTo(BigDecimal.ZERO) <= 0 || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal calculated;
        if (discountType == DiscountType.PERCENTAGE) {
            calculated = orderAmount
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            if (maximumDiscount != null
                    && maximumDiscount.compareTo(BigDecimal.ZERO) > 0
                    && calculated.compareTo(maximumDiscount) > 0) {
                calculated = maximumDiscount.setScale(0, RoundingMode.HALF_UP);
            }
        // Apply flat fixed-amount discount (e.g.  off)
        } else {
            calculated = discountValue.setScale(0, RoundingMode.HALF_UP);
        }

        BigDecimal roundedOrder = orderAmount.setScale(0, RoundingMode.HALF_UP);
        if (calculated.compareTo(roundedOrder) > 0) {
            calculated = roundedOrder;
        }

        return calculated;
    }

    /**
     * Validates that the order subtotal meets or exceeds the coupon's minimum spend threshold.
     */
    public boolean validateCouponOrderEligibility(BigDecimal minimumSpend, BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero.");
        }
        BigDecimal effectiveMinimum = minimumSpend != null ? minimumSpend : BigDecimal.ZERO;
        if (orderAmount.compareTo(effectiveMinimum) < 0) {
            throw new IllegalStateException(
                    "Order subtotal (" + orderAmount + ") does not meet minimum spend requirement of " + effectiveMinimum + ".");
        }
        return true;
    }

    /**
     * Validates active status, promotion window, minimum spend, global usage limit,
     * and per-user limit, records a CouponRedemption, and returns the applied discount.
     */
    @Transactional
    public BigDecimal validateAndRedeemCoupon(
            String code,
            String touristUid,
            BigDecimal orderAmount,
            String bookingId) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Coupon code is required.");
        }
        if (touristUid == null || touristUid.isBlank()) {
            throw new IllegalArgumentException("Tourist UID is required to redeem a coupon.");
        }

        String normalizedCode = code.trim().toUpperCase(Locale.ROOT);
        Coupon coupon = couponRepository.findByCodeIgnoreCase(normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code: " + normalizedCode));

        // Verify coupon is active and available
        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new IllegalStateException("Coupon '" + normalizedCode + "' is not active.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt())) {
            throw new IllegalStateException("Coupon '" + normalizedCode + "' promotion has not started yet.");
        }
        if (coupon.getExpiresAt() != null && now.isAfter(coupon.getExpiresAt())) {
            coupon.setStatus(CouponStatus.EXPIRED);
            couponRepository.save(coupon);
            throw new IllegalStateException("Coupon '" + normalizedCode + "' has expired.");
        }

        validateCouponOrderEligibility(coupon.getMinimumSpend(), orderAmount);

        long totalRedemptions = couponRedemptionRepository.countByCouponId(coupon.getId());
        if (coupon.getGlobalUsageLimit() > 0 && totalRedemptions >= coupon.getGlobalUsageLimit()) {
            throw new IllegalStateException("Coupon '" + normalizedCode + "' has reached its global usage limit.");
        }

        long userRedemptions = couponRedemptionRepository.countByCouponIdAndTouristUid(coupon.getId(), touristUid);
        if (coupon.getPerUserLimit() > 0 && userRedemptions >= coupon.getPerUserLimit()) {
            throw new IllegalStateException("You have already reached the per-user redemption limit for coupon '" + normalizedCode + "'.");
        }

        BigDecimal discountAmount = calculateDiscount(
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                orderAmount,
                coupon.getMaximumDiscount());

        // Create immutable audit log entry for this coupon redemption
        CouponRedemption redemption = new CouponRedemption();
        redemption.setCouponId(coupon.getId());
        redemption.setTouristUid(touristUid);
        redemption.setBookingId(bookingId);
        redemption.setDiscountAmount(discountAmount);
        redemption.setCurrency(coupon.getCurrency());
        redemption.setRedeemedAt(now);
        // Persist coupon redemption audit log into database
        couponRedemptionRepository.save(redemption);

        return discountAmount;
    }

    /**
     * Immediately deactivates an existing coupon.
     */
    @Transactional
    public Coupon deactivateCoupon(String adminUid, String couponId) {
        verifyAdminAccess(adminUid);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found with ID: " + couponId));

        coupon.setStatus(CouponStatus.INACTIVE);
        coupon.setUpdatedAt(LocalDateTime.now());
        return couponRepository.save(coupon);
    }

    /**
     * Retrieves all coupons ordered by creation date descending, populating
     * transient redemption counts for admin analytics display.
     */
    @Transactional(readOnly = true)
    public List<Coupon> getCouponsForAdmin(String adminUid) {
        if (adminUid != null && !adminUid.isBlank()) {
            Optional<Profile> profileOpt = profileRepository.findByFirebaseUid(adminUid)
                    .or(() -> profileRepository.findById(adminUid));
            if (profileOpt.isPresent()) {
                Role role = profileOpt.get().getRole();
                if (role != Role.ADMIN && role != Role.SUPER_ADMIN) {
                    throw new SecurityException("Forbidden: Admin access required.");
                }
            }
        }

        List<Coupon> coupons = couponRepository.findAllByOrderByCreatedAtDesc();
        for (Coupon coupon : coupons) {
            long count = couponRedemptionRepository.countByCouponId(coupon.getId());
            coupon.setRedemptionCount(count);
        }
        return coupons;
    }

    /**
     * Looks up a coupon by its code (case-insensitive) and populates its redemption count.
     */
    @Transactional(readOnly = true)
    public Optional<Coupon> getCouponByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCase(code.trim());
        couponOpt.ifPresent(c -> c.setRedemptionCount(couponRedemptionRepository.countByCouponId(c.getId())));
        return couponOpt;
    }
}





