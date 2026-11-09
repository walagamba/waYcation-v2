package com.waycation.service;

import com.waycation.model.Coupon;
import com.waycation.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public BigDecimal calculateDiscount(BigDecimal discountValue, BigDecimal orderAmount) {
        return discountValue.min(orderAmount);
    }

    public Coupon createCoupon(String adminUid, Coupon coupon) {
        coupon.setCreatedByUid(adminUid);
        return couponRepository.save(coupon);
    }
}
