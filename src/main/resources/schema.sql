-- ==========================================================================
-- waYcation 2.0 — PostgreSQL Relational Database Schema (DDL)
-- Defines all core entities, foreign key constraints, and lookup indices
-- across the 6 platform modules:
--   1. Profiles & Role Governance (profiles)
--   2. Hotel & Room Catalog (hotels, hotel_photos, room_types, room_inventory)
--   3. Tourist Bookings (bookings)
--   4. Verified Guest Reviews (reviews)
--   5. Curated Travel Packages (packages, package_hotels, package_purchases)
--   6. Promotional Coupons & Redemptions (coupons, coupon_redemptions)
-- ==========================================================================

-- 1. User Profiles & Role-Based Access Control (Module 1)
CREATE TABLE IF NOT EXISTS profiles (
    id VARCHAR(64) PRIMARY KEY,
    firebase_uid VARCHAR(128) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'TOURIST',
    avatar_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Hotel Accommodations & Owner Portfolio (Module 2)
CREATE TABLE IF NOT EXISTS hotels (
    id VARCHAR(64) PRIMARY KEY,
    owner_uid VARCHAR(128) NOT NULL REFERENCES profiles(firebase_uid) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(120) NOT NULL,
    country VARCHAR(120) NOT NULL DEFAULT 'Sri Lanka',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    check_in_time VARCHAR(16) NOT NULL DEFAULT '14:00',
    check_out_time VARCHAR(16) NOT NULL DEFAULT '11:00',
    cancellation_policy TEXT NOT NULL,
    featured_image TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_hotels_owner_uid ON hotels(owner_uid);
CREATE INDEX IF NOT EXISTS idx_hotels_city ON hotels(city);
CREATE INDEX IF NOT EXISTS idx_hotels_status ON hotels(status);

-- Hotel Photo Gallery (Supports uploaded images in /uploads/hotels/ and external URLs)
CREATE TABLE IF NOT EXISTS hotel_photos (
    id VARCHAR(64) PRIMARY KEY,
    hotel_id VARCHAR(64) NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    caption VARCHAR(255),
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Room Categories per Hotel
CREATE TABLE IF NOT EXISTS room_types (
    id VARCHAR(64) PRIMARY KEY,
    hotel_id VARCHAR(64) NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    capacity INT NOT NULL DEFAULT 2,
    base_price NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'LKR',
    total_rooms INT NOT NULL DEFAULT 5,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Per-Night Atomic Room Inventory Ledger
CREATE TABLE IF NOT EXISTS room_inventory (
    id VARCHAR(64) PRIMARY KEY,
    room_type_id VARCHAR(64) NOT NULL REFERENCES room_types(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    total_count INT NOT NULL,
    booked_count INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_room_inventory_date UNIQUE (room_type_id, date)
);

-- 3. Tourist Reservations & Stay Lifecycle (Module 3)
CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(64) PRIMARY KEY,
    tourist_uid VARCHAR(128) NOT NULL REFERENCES profiles(firebase_uid) ON DELETE RESTRICT,
    hotel_id VARCHAR(64) NOT NULL REFERENCES hotels(id) ON DELETE RESTRICT,
    room_type_id VARCHAR(64) NOT NULL REFERENCES room_types(id) ON DELETE RESTRICT,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guest_count INT NOT NULL DEFAULT 1,
    room_count INT NOT NULL DEFAULT 1,
    price_total NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'LKR',
    status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
    policy_snapshot TEXT NOT NULL,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_bookings_tourist_uid ON bookings(tourist_uid);
CREATE INDEX IF NOT EXISTS idx_bookings_hotel_id ON bookings(hotel_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);

-- 4. Verified Guest Reviews & Star Ratings (Module 4)
CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(64) PRIMARY KEY,
    tourist_uid VARCHAR(128) NOT NULL REFERENCES profiles(firebase_uid) ON DELETE CASCADE,
    hotel_id VARCHAR(64) NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    booking_id VARCHAR(64) NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Curated Multi-Hotel Travel Packages & Purchases (Module 5)
CREATE TABLE IF NOT EXISTS packages (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    duration_days INT NOT NULL,
    price_total NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'LKR',
    status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED',
    cover_image TEXT,
    created_by_uid VARCHAR(128) NOT NULL REFERENCES profiles(firebase_uid) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS package_hotels (
    id VARCHAR(64) PRIMARY KEY,
    package_id VARCHAR(64) NOT NULL REFERENCES packages(id) ON DELETE CASCADE,
    hotel_id VARCHAR(64) NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    stop_order INT NOT NULL,
    nights INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_package_hotel UNIQUE (package_id, hotel_id)
);

CREATE TABLE IF NOT EXISTS package_purchases (
    id VARCHAR(64) PRIMARY KEY,
    package_id VARCHAR(64) NOT NULL REFERENCES packages(id) ON DELETE RESTRICT,
    tourist_uid VARCHAR(128) NOT NULL REFERENCES profiles(firebase_uid) ON DELETE RESTRICT,
    price_snapshot NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'LKR',
    package_snapshot TEXT NOT NULL,
    start_date DATE,
    status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. Promotional Coupons & Redemption Audit Ledger (Module 6)
CREATE TABLE IF NOT EXISTS coupons (
    id VARCHAR(64) PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    discount_type VARCHAR(32) NOT NULL,
    discount_value NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'LKR',
    starts_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    minimum_spend NUMERIC(10, 2) NOT NULL DEFAULT 0,
    maximum_discount NUMERIC(10, 2),
    global_usage_limit INT NOT NULL,
    per_user_limit INT NOT NULL DEFAULT 1,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_by_uid VARCHAR(128) NOT NULL REFERENCES profiles(firebase_uid) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS coupon_redemptions (
    id VARCHAR(64) PRIMARY KEY,
    coupon_id VARCHAR(64) NOT NULL REFERENCES coupons(id) ON DELETE RESTRICT,
    tourist_uid VARCHAR(128) NOT NULL REFERENCES profiles(firebase_uid) ON DELETE RESTRICT,
    booking_id VARCHAR(64) REFERENCES bookings(id) ON DELETE SET NULL,
    package_purchase_id VARCHAR(64) REFERENCES package_purchases(id) ON DELETE SET NULL,
    discount_amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'LKR',
    redeemed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
