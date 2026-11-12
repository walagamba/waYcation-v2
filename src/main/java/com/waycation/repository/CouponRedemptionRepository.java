package com.waycation.repository;

import com.waycation.model.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, String> {

    long countByCouponId(String couponId);

    long countByCouponIdAndTouristUid(String couponId, String touristUid);
}
