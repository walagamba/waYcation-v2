package com.waycation.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

    // JPA Entity tracking tourist package purchase orders in 'package_purchases' table
@Entity
@Table(name = "package_purchases")
public class PackagePurchase {

    @Id
    private String id;

    @Column(name = "package_id", nullable = false)
    private String packageId;

    @Column(name = "tourist_uid", nullable = false)
    private String touristUid;

    @Column(name = "price_snapshot", nullable = false)
    private BigDecimal priceSnapshot;

    @Column(nullable = false)
    private String currency = "LKR";

    @Column(name = "package_snapshot", columnDefinition = "TEXT", nullable = false)
    private String packageSnapshot;

    @Column(nullable = false)
    private String status = "CONFIRMED";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PackagePurchase() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.currency == null) {
            this.currency = "LKR";
        }
        if (this.status == null) {
            this.status = "CONFIRMED";
        }
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getTouristUid() {
        return touristUid;
    }

    public void setTouristUid(String touristUid) {
        this.touristUid = touristUid;
    }

    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPackageSnapshot() {
        return packageSnapshot;
    }

    public void setPackageSnapshot(String packageSnapshot) {
        this.packageSnapshot = packageSnapshot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

