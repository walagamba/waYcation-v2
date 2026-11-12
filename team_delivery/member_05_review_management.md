# Member 5: Review Management Module Guide

## 📌 Module Overview & Responsibilities
* **Assigned Member / Area:** Verified Guest Reviews, 5-Star Ratings Ledger, Tourist Stay Feedback.
* **Feature Branch:** `feature/review-module` (or `main`)
* **Repository Path to Commit In:** Your local repository root directory (e.g., `C:\Users\<YourUsername>\...\waYcation-v2` or `...\waYcation2.0`).

---

## 📂 Changed Files Included in `member_05_review_management.zip`
1. `src/main/java/com/waycation/service/ReviewService.java`
   * Cleaned up methods and ensured `getPublishedReviewsForHotel(hotelId)` is available for hotel page rendering.
2. `src/main/resources/templates/tourist/reviews.html`
   * Removed all Git merge conflict markers (`<<<<<<< HEAD`, `=======`) and restored verified review submission form.

---

## 💻 PowerShell Commands (Step-by-Step)

Open PowerShell and run the following commands from **your repository root**:

```powershell
# 1. Ensure you are in your project repository root
cd "C:\Path\To\Your\waYcation"

# 2. Extract the zip package into your project (replaces and drops files into exact locations)
Expand-Archive -Path "C:\Path\To\member_05_review_management.zip" -DestinationPath . -Force

# 3. Verify git status
git status

# 4. Stage ONLY the Review Module files
git add src/main/java/com/waycation/service/ReviewService.java
git add src/main/resources/templates/tourist/reviews.html

# 5. Commit with a clear commit message
git commit -m "fix(review): clean conflict markers and connect hotel published reviews"

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
