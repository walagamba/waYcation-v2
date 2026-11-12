# Member 4: Travel Package Management Module Guide

## 📌 Module Overview & Responsibilities
* **Assigned Member / Area:** Multi-Hotel Curated Vacation Packages, Itinerary Builder, Immutable Snapshot Purchase & Timeline.
* **Feature Branch:** `feature/package-module` (or `main`)
* **Repository Path to Commit In:** Your local repository root directory (e.g., `C:\Users\<YourUsername>\...\waYcation-v2` or `...\waYcation2.0`).

---

## 📂 Changed Files Included in `member_04_package_management.zip`
1. `src/main/java/com/waycation/service/PackageService.java`
   * Added overloaded `updatePackage(String adminUid, String packageId, TravelPackage updatedData)` for `PackageAction`.
   * Added overloaded `purchasePackage(touristUid, packageId, startDate)` for custom trip start dates.
   * Added duplicate hotel stop filtering to prevent `package_hotels_package_id_hotel_id_key` unique constraint violation crashes.
2. `src/main/java/com/waycation/controller/PackageController.java`
   * Added `@GetMapping("/admin/packages/{id}/edit")` fallback handler to prevent 404.
   * Connected `minStartDate` and `defaultStartDate` to `/packages/{id}` model.
3. `src/actions/PackageAction.java`
   * Synchronized update signature with `PackageService`.
4. `src/main/java/com/waycation/model/PackagePurchase.java`
   * Added `startDate` attribute with JPA mappings, getters, and setters.
5. `src/main/resources/templates/packages/detail.html`
   * Added tourist trip start date selector with min-date protection.
   * Replaced enum comparison with safe `.name() == 'PUBLISHED'` check.
6. `src/main/resources/templates/admin/packages.html`
   * Removed all Git merge conflict markers (`<<<<<<< HEAD`, `=======`).
   * Added JavaScript validation `validatePackageStops(form)` to enforce distinct hotel selection across stops.

---

## 💻 PowerShell Commands (Step-by-Step)

Open PowerShell and run the following commands from **your repository root**:

```powershell
# 1. Ensure you are in your project repository root
cd "C:\Path\To\Your\waYcation"

# 2. Extract the zip package into your project (replaces and drops files into exact locations)
Expand-Archive -Path "C:\Path\To\member_04_package_management.zip" -DestinationPath . -Force

# 3. Verify git status
git status

# 4. Stage ONLY the Package Module files
git add src/main/java/com/waycation/service/PackageService.java
git add src/main/java/com/waycation/controller/PackageController.java
git add src/actions/PackageAction.java
git add src/main/java/com/waycation/model/PackagePurchase.java
git add src/main/resources/templates/packages/detail.html
git add src/main/resources/templates/admin/packages.html

# 5. Commit with a clear commit message
git commit -m "feat(package): support trip start date, deduplicate hotel stops, and clean merge conflict markers"

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
