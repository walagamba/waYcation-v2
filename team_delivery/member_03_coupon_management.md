# Member 3: Coupon & Promotions Module Guide

## 📌 Module Overview & Responsibilities
* **Assigned Member / Area:** Admin Coupon Creation, Lifecycle Control (Pause / Activate / Delete), Live Validation & Redemptions.
* **Feature Branch:** `feature/coupon-module` (or `main`)
* **Repository Path to Commit In:** Your local repository root directory (e.g., `C:\Users\<YourUsername>\...\waYcation-v2` or `...\waYcation2.0`).

---

## 📂 Changed Files Included in `member_03_coupon_management.zip`
1. `src/main/java/com/waycation/service/CouponService.java`
   * Added `calculateCouponDiscountPreview(code, touristUid, orderAmount)` for live status, validity, and spend checks.
   * Implemented `activateCoupon(...)`, `updateCoupon(...)`, and `deleteCoupon(...)` for full admin CRUD.
   * Records audit entries in `coupon_redemptions`.
2. `src/main/java/com/waycation/controller/CouponController.java`
   * Added missing endpoints that were throwing 404:
     - `@PostMapping("/admin/coupons/{id}/activate")`
     - `@PostMapping("/admin/coupons/{id}/update")`
     - `@PostMapping("/admin/coupons/{id}/delete")`
3. `src/main/resources/templates/admin/coupons.html`
   * Aligned form actions with controller routes, added modal update bindings.

---

## 💻 PowerShell Commands (Step-by-Step)

Open PowerShell and run the following commands from **your repository root**:

```powershell
# 1. Ensure you are in your project repository root
cd "C:\Path\To\Your\waYcation"

# 2. Extract the zip package into your project (replaces and drops files into exact locations)
Expand-Archive -Path "C:\Path\To\member_03_coupon_management.zip" -DestinationPath . -Force

# 3. Verify git status
git status

# 4. Stage ONLY the Coupon Module files
git add src/main/java/com/waycation/service/CouponService.java
git add src/main/java/com/waycation/controller/CouponController.java
git add src/main/resources/templates/admin/coupons.html

# 5. Commit with a clear commit message
git commit -m "feat(coupon): expose activate, update, and delete endpoints and enable live redemption preview"

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
