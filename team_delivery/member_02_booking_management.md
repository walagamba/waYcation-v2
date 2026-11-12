# Member 2: Booking Management Module Guide

## 📌 Module Overview & Responsibilities
* **Assigned Member / Area:** Tourist Stay Checkout, Real-Time Coupon Discount Application, Inventory Booking Lifecycle.
* **Feature Branch:** `feature/booking-module` (or `main`)
* **Repository Path to Commit In:** Your local repository root directory (e.g., `C:\Users\<YourUsername>\...\waYcation-v2` or `...\waYcation2.0`).

---

## 📂 Changed Files Included in `member_02_booking_management.zip`
1. `src/main/java/com/waycation/service/BookingService.java`
   * Injected `CouponService` into `BookingService`.
   * Replaced hardcoded coupon checks with dynamic database queries (`couponService.calculateCouponDiscountPreview` and `couponService.validateAndRedeemCoupon`).
2. `src/main/java/com/waycation/controller/BookingController.java`
   * Wired coupon validation preview into `/checkout` model so tourists see live deductions.
   * Handles invalid coupon exceptions gracefully with flash error feedback.
3. `src/main/resources/templates/bookings/checkout.html`
   * Added dynamic coupon discount pill badge, discount subtraction line item, and coupon feedback.

---

## 💻 PowerShell Commands (Step-by-Step)

Open PowerShell and run the following commands from **your repository root**:

```powershell
# 1. Ensure you are in your project repository root
cd "C:\Path\To\Your\waYcation"

# 2. Extract the zip package into your project (replaces and drops files into exact locations)
Expand-Archive -Path "C:\Path\To\member_02_booking_management.zip" -DestinationPath . -Force

# 3. Verify git status
git status

# 4. Stage ONLY the Booking Module files
git add src/main/java/com/waycation/service/BookingService.java
git add src/main/java/com/waycation/controller/BookingController.java
git add src/main/resources/templates/bookings/checkout.html

# 5. Commit with a clear commit message
git commit -m "feat(booking): wire database coupon validation and live checkout discount preview"

# 6. Pull latest changes with rebase to prevent merge conflicts
git pull --rebase origin main

# 7. Push to GitHub
git push origin main
```

---

## 🔍 Verification Command
```powershell
mvn compile -DskipTests
```
Expected output: `BUILD SUCCESS`
