# Module 6: Promotional Coupon & Redemption Management — Code & OOP Architecture

## 1. Module Overview
Module 6 manages promotional marketing discounts (percentage and fixed-amount rules), minimum spend thresholds, maximum discount caps, global and per-user redemption limits, and an immutable `CouponRedemption` audit ledger.

---

## 2. Classes in This Module & Their Specific Uses

### 1. `DiscountType` (Enum)
- **Use & Purpose in System**: Dictates the calculation strategy for promotional codes (`PERCENTAGE` vs `FIXED`).

### 2. `CouponStatus` (Enum)
- **Use & Purpose in System**: Tracks coupon availability (`ACTIVE`, `INACTIVE`, `EXPIRED`).

### 3. `Coupon` (JPA Entity Model)
- **Use & Purpose in System**: Stores marketing rules, promo codes, minimum spend thresholds, and maximum discount caps.
- **Specific Responsibilities**: Enforces platform-wide usage quotas (`globalUsageLimit`) and per-tourist usage limits (`perUserLimit`).

### 4. `CouponRedemption` (JPA Entity Model)
- **Use & Purpose in System**: Serves as an immutable audit ledger recording every time a tourist redeems a promo code during booking or package checkout.
- **Specific Responsibilities**: Prevents single-user promo code abuse by recording individual redemption records linked to `bookingId` or `packagePurchaseId`.

### 5. `CouponRepository` & `CouponRedemptionRepository` (Data Access Layer)
- **Use & Purpose in System**: Execute case-insensitive promo code lookups, date window verifications, and user redemption quota counts via Spring Data JPA.

### 6. `CouponService` (Business Logic & Transactional Service)
- **Use & Purpose in System**: Validates coupon eligibility, computes discounted totals, records redemptions atomically, and powers Admin CRUD operations (`createCoupon`, `updateCoupon`, `deactivateCoupon`, `activateCoupon`, `deleteCoupon`).

### 7. `CouponController` (Web & Route Presentation Layer)
- **Use & Purpose in System**: Serves the `/admin/coupons` promotional management console and handles Create, Update, Pause/Activate, and Delete form submissions.

### 8. `CouponAction` (Facade Adapter Layer)
- **Use & Purpose in System**: Spring `@Component` facade for promotional coupon creation, deactivation, verification, and discount redemption.

---

## 3. Attributes (Fields & Data Types)

### `Coupon.java`:
- `private String id`: Primary key UUID.
- `private String code`: Uppercase promo code string (e.g. `WAYCATION10`, `WELCOME5000`, `LANKA10`, `SOUTH5000`).
- `private DiscountType discountType`: Enum (`PERCENTAGE`, `FIXED`).
- `private BigDecimal discountValue`: Discount amount or percentage.
- `private BigDecimal minimumSpend`: Minimum order subtotal required to apply coupon.
- `private BigDecimal maximumDiscount`: Optional monetary cap for percentage discounts.
- `private LocalDateTime startsAt`, `expiresAt`: Validity window timestamps.
- `private Integer globalUsageLimit`: Total times coupon can be redeemed across the platform.
- `private Integer perUserLimit`: Maximum times a single tourist can redeem the coupon.
- `private CouponStatus status`: Enum (`ACTIVE`, `INACTIVE`, `EXPIRED`).

### `CouponRedemption.java`:
- `private String id`: Primary key UUID.
- `private String couponId`: Foreign key to `Coupon`.
- `private String touristUid`: Tourist who redeemed the code.
- `private String bookingId`: Associated hotel reservation ID.
- `private BigDecimal discountAmount`: Exact currency saved.
- `private LocalDateTime redeemedAt`: Audit timestamp.

---

## 4. Object-Oriented Programming (OOP) Principles Applied

### A. Strategy Pattern (Polymorphism via Enum)
- `DiscountType` dictates how discounts are calculated:
  - If `PERCENTAGE`: `orderAmount * (discountValue / 100)`, capped by `maximumDiscount`.
  - If `FIXED`: Flat deduction, capped by `orderAmount` (prevents negative checkout totals).

### B. Encapsulation & Integrity Guards
- Global and per-user usage counts are verified directly against the `coupon_redemptions` table within a `@Transactional` boundary before any redemption record is inserted.

---

## 5. CRUD Operations Mapping

| Operation | HTTP / Route | Service Method | Database Action |
| :--- | :--- | :--- | :--- |
| **Create** | `POST /admin/coupons` | `createCoupon(...)` | `INSERT INTO coupons` |
| **Read / Validate** | `GET /admin/coupons` | `getCouponsForAdmin(...)`, `getCouponByCode(...)` | `SELECT * FROM coupons` |
| **Update** | `POST /admin/coupons/{id}/edit`, `/activate`, `/deactivate` | `updateCoupon(...)`, `activateCoupon(...)`, `deactivateCoupon(...)` | `UPDATE coupons SET ...` |
| **Delete** | `POST /admin/coupons/{id}/delete` | `deleteCoupon(...)` | `DELETE FROM coupon_redemptions`, `DELETE FROM coupons` |
