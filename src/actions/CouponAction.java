package com.waycation.actions;

import com.waycation.model.Coupon;
import com.waycation.service.CouponService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Module 6: Admin Coupon Management Actions
 * Provides server action entry points for creating promotional discount codes,
 * deactivating coupons, querying admin redemption analytics, and verifying promo eligibility.
 */
@Component
    // Facade Design Pattern: wraps coupon and discount operations into clean action methods
public class CouponAction {

    private final CouponService couponService;

    public CouponAction(CouponService couponService) {
        this.couponService = couponService;
    }

    public Coupon createCouponAction(String adminUid, Coupon coupon) {
        return couponService.createCoupon(adminUid, coupon);
    }

    public Coupon deactivateCouponAction(String adminUid, String couponId) {
        return couponService.deactivateCoupon(adminUid, couponId);
    }

    public List<Coupon> getAdminCouponsAction(String adminUid) {
        return couponService.getCouponsForAdmin(adminUid);
    }

    public Optional<Coupon> verifyCouponAction(String code) {
        return couponService.getCouponByCode(code);
    }

    public BigDecimal redeemCouponAction(
            String code,
            String touristUid,
            BigDecimal orderAmount,
            String bookingId
    ) {
        return couponService.validateAndRedeemCoupon(code, touristUid, orderAmount, bookingId);
    }
}

