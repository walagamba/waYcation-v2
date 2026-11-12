# Member 1: Hotel Management Module Guide

## 📌 Module Overview & Responsibilities
* **Assigned Member / Area:** Hotelier Portfolio, Multi-Room Category Creator & Editor, Hotel Detail View.
* **Feature Branch:** `feature/hotel-module` (or `main`)
* **Repository Path to Commit In:** Your local repository root directory (e.g., `C:\Users\<YourUsername>\...\waYcation-v2` or `...\waYcation2.0`).

---

## 📂 Changed Files Included in `member_01_hotel_management.zip`
1. `src/main/java/com/waycation/service/HotelService.java`
   * Added `updateHotelWithRooms(...)` and `deleteRoomType(...)` for dynamic multi-room categories.
2. `src/main/java/com/waycation/controller/HotelController.java`
   * Added routes: `@GetMapping("/owner/hotels/{id}/edit")`, `@PostMapping("/owner/hotels/{id}/edit")`, and delete room endpoint.
   * Connected `reviewService.getPublishedReviewsForHotel(id)` for verified hotel review cards.
3. `src/main/resources/templates/hotels/detail.html`
   * Replaced SpEL `BigDecimal * Integer` arithmetic crash (`hotel.startingPrice * 2` -> `hotel.startingPrice.add(hotel.startingPrice)`).
   * Fixed 500 error when viewing public hotel detail pages.
4. `src/main/resources/templates/owner/hotel-new.html`
   * Implemented dynamic multi-tier room category builder (`addRoomCategory()`).
5. `src/main/resources/templates/owner/hotel-edit.html`
   * Added dedicated property & room category edit console for hotel owners.

---

## 💻 PowerShell Commands (Step-by-Step)

Open PowerShell and run the following commands from **your repository root**:

```powershell
# 1. Ensure you are in your project repository root
cd "C:\Path\To\Your\waYcation"

# 2. Extract the zip package into your project (replaces and drops files into exact locations)
Expand-Archive -Path "C:\Path\To\member_01_hotel_management.zip" -DestinationPath . -Force

# 3. Verify git status
git status

# 4. Stage ONLY the Hotel Module files
git add src/main/java/com/waycation/service/HotelService.java
git add src/main/java/com/waycation/controller/HotelController.java
git add src/main/resources/templates/hotels/detail.html
git add src/main/resources/templates/owner/hotel-new.html
git add src/main/resources/templates/owner/hotel-edit.html

# 5. Commit with a clear commit message
git commit -m "feat(hotel): enable multi-room categories on create/edit, fix detail page SpEL 500 error"

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
