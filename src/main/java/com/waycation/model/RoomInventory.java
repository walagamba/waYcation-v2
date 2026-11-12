package com.waycation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA entity tracking daily room allocation and booked counts per room type.
 */
    // JPA Entity tracking date-specific room inventory quotas for booking checks
@Entity
@Table(name = "room_inventory")
public class RoomInventory {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "room_type_id", nullable = false)
    private String roomTypeId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "booked_count", nullable = false)
    private int bookedCount = 0;

    public RoomInventory() {
        this.id = UUID.randomUUID().toString();
    }

    public RoomInventory(String roomTypeId, LocalDate date, int totalCount, int bookedCount) {
        this();
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.totalCount = totalCount;
        this.bookedCount = bookedCount;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public int getAvailableCount() {
        return Math.max(0, totalCount - bookedCount);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(int bookedCount) {
        this.bookedCount = bookedCount;
    }
}

