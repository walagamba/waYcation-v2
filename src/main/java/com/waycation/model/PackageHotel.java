package com.waycation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

    // JPA join entity mapping partner hotels included in the package itinerary
@Entity
@Table(name = "package_hotels")
public class PackageHotel {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private TravelPackage travelPackage;

    @Column(name = "hotel_id", nullable = false)
    private String hotelId;

    @Column(name = "stop_order", nullable = false)
    private int stopOrder;

    @Column(nullable = false)
    private int nights = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private String hotelName;

    @Transient
    private String city;

    public PackageHotel() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public PackageHotel(String hotelId, int stopOrder, int nights) {
        this();
        this.hotelId = hotelId;
        this.stopOrder = stopOrder;
        this.nights = nights > 0 ? nights : 1;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.nights <= 0) {
            this.nights = 1;
        }
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TravelPackage getTravelPackage() {
        return travelPackage;
    }

    public void setTravelPackage(TravelPackage travelPackage) {
        this.travelPackage = travelPackage;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

