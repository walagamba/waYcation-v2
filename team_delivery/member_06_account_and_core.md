# Member 6: Account & Core Platform Module Guide

## 📌 Module Overview & Responsibilities
* **Assigned Member / Area:** Multi-Role Authentication Portals, Session Management, Account Settings, Sign-Out Handlers.
* **Feature Branch:** `feature/account-module` (or `main`)
* **Repository Path to Commit In:** Your local repository root directory (e.g., `C:\Users\<YourUsername>\...\waYcation-v2` or `...\waYcation2.0`).

---

## 📂 Changed Files Included in `member_06_account_and_core.zip`
1. `src/main/java/com/waycation/controller/AccountController.java`
   * Added `/sign-out` and `/logout` GET/POST endpoints that invalidate the session and redirect cleanly.
2. `src/main/java/com/waycation/service/AccountService.java`
   * Added docstrings, anonymization, and safe null-checking for display names and avatar URLs.
3. `src/main/java/com/waycation/WaycationApplication.java`
   * Added architectural documentation and transactional annotations.

---

## 💻 PowerShell Commands (Step-by-Step)

Open PowerShell and run the following commands from **your repository root**:

```powershell
# 1. Ensure you are in your project repository root
cd "C:\Path\To\Your\waYcation"

# 2. Extract the zip package into your project (replaces and drops files into exact locations)
Expand-Archive -Path "C:\Path\To\member_06_account_and_core.zip" -DestinationPath . -Force

# 3. Verify git status
git status

# 4. Stage ONLY the Account & Core files
git add src/main/java/com/waycation/controller/AccountController.java
git add src/main/java/com/waycation/service/AccountService.java
git add src/main/java/com/waycation/WaycationApplication.java

# 5. Commit with a clear commit message
git commit -m "feat(auth): add global sign-out endpoints and enhance account service safety"

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
