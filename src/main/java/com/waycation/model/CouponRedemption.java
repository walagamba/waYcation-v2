package com.waycation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Audit record of a promotional coupon redeemed by a tourist against a booking
 * or travel package purchase.
 */
    // JPA Entity storing immutable audit history of each voucher redemption
@Entity
@Table(name = "coupon_redemptions")
public class CouponRedemption {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "coupon_id", nullable = false)
    private String couponId;

    @Column(name = "tourist_uid", nullable = false)
    private String touristUid;

    @Column(name = "booking_id")
    private String bookingId;

    @Column(name = "package_purchase_id")
    private String packagePurchaseId;

    @Column(name = "discount_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "currency", nullable = false, length = 8)
    private String currency = "LKR";

    @Column(name = "redeemed_at", nullable = false, updatable = false)
    private LocalDateTime redeemedAt;

    public CouponRedemption() {
        this.id = UUID.randomUUID().toString();
        this.redeemedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.currency == null || this.currency.isBlank()) {
            this.currency = "LKR";
        }
        if (this.redeemedAt == null) {
            this.redeemedAt = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getTouristUid() {
        return touristUid;
    }

    public void setTouristUid(String touristUid) {
        this.touristUid = touristUid;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPackagePurchaseId() {
        return packagePurchaseId;
    }

    public void setPackagePurchaseId(String packagePurchaseId) {
        this.packagePurchaseId = packagePurchaseId;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}

