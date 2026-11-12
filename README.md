# waYcation 2.0 — Boutique Hotel Discovery, Curated Travel Packages & Reservation Platform

**waYcation 2.0** is a full-stack enterprise web application built with **Java 21**, **Spring Boot 3.4.3**, **Spring Data JPA (Hibernate)**, **PostgreSQL**, **Thymeleaf HTML5**, **Tailwind CSS**, and **Vanilla ES6 JavaScript**.

---

## 🛠️ Approved Technology Stack

| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Language & Runtime** | **Java 21** | Object-oriented domain modeling, strict type safety, and enterprise concurrency |
| **Backend Framework** | **Spring Boot 3.4.3** (`spring-boot-starter-web`, `validation`) | Jakarta Servlet MVC controllers, HTTP session RBAC governance, and multipart file uploads |
| **Persistence & ORM** | **Spring Data JPA / Hibernate** | Transactional (`@Transactional`) repositories, entity lifecycle management, and SQL locking |
| **Relational Database** | **PostgreSQL** (Supabase / Local PostgreSQL + H2 Test Profile) | ACID-compliant relational persistence (`schema.sql` & `data.sql`) |
| **Server-Side Views** | **Thymeleaf HTML5** | Server-side rendered templates with role-aware fragment composition (`layout.html`) |
| **Styling & Interactivity** | **Tailwind CSS + Custom CSS (`waycation.css`) + Vanilla JS (`ui-enhancements.js`)** | Nothing OS & Apple Minimalist dual-theme UI, interactive date-range calendar, and live filtering |
| **Build Automation** | **Apache Maven** (`pom.xml`, `mvnw.cmd`) | Dependency management, `src/actions` source helper, and executable JAR packaging |

---

## 📦 Core Functional CRUD Modules

1. **Module 1 — Account, Profile & Role Governance CRUD**:
   - Multi-role authentication (`TOURIST`, `HOTEL_OWNER`, `STAFF`, `ADMIN`, `SUPER_ADMIN`).
   - Public registration restricted to `TOURIST` and `HOTEL_OWNER` (prevents admin self-registration).
   - Admin User Role Governance Console (`/admin/users`) with strict `SUPER_ADMIN` vs. `ADMIN` permission hierarchy.
   - GDPR-compliant account anonymization preserving historical booking foreign-key integrity.
2. **Module 2 — Hotel, Room Catalog & Multipart Photo Upload CRUD**:
   - Property onboarding and editing (`/owner/hotels/new`, `/owner/hotels/{id}/edit`) with multipart featured & gallery photo uploads stored in `/uploads/hotels/`.
   - Strict owner-only edit authorization (`verifyOwnershipOrAdmin`) preventing cross-tenant modifications.
   - Soft deactivation (`INACTIVE`) and reactivation (`ACTIVE`) of accommodations.
3. **Module 3 — Tourist Booking & Atomic Room Inventory CRUD**:
   - Authenticated reservation lock guard (guests must register/sign in before locking room inventory).
   - Atomic per-night `RoomInventory` availability verification and deduction across `[checkIn, checkOut)`.
   - Reservation rescheduling, cancellation with inventory rollback, and host stay-completion marking.
4. **Module 4 — Verified Stay Review & Rating Moderation CRUD**:
   - Verified-stay gate: only tourists who have stayed at the accommodation (`CONFIRMED` or `COMPLETED` booking) can publish a review.
   - Strict 1-review-per-booking uniqueness constraint, 1–5 star rating validation, review editing/deletion, and admin moderation.
5. **Module 5 — Curated Multi-Hotel Travel Packages & Start-Date CRUD**:
   - Ordered multi-hotel itinerary bundles (`TravelPackage` + `PackageHotel`) managed by `STAFF`, `ADMIN`, and `SUPER_ADMIN`.
   - Full CRUD (Create, Read, Update modal, Archive, Delete) and tourist package checkout with custom starting-day (`startDate`) selection and immutable JSON itinerary snapshots.
6. **Module 6 — Promotional Coupons & Redemption Audit CRUD**:
   - Percentage and fixed-amount discount codes (`Coupon`) with minimum spend thresholds, maximum discount caps, global usage limits, and per-user redemption limits.
   - Full Admin CRUD (`/admin/coupons` Create, Read, Update modal, Pause/Activate, Delete) and immutable `CouponRedemption` audit tracking.

---

## 🚀 Running the Application Locally

```powershell
# Start the Spring Boot server on http://localhost:8085
.\mvnw.cmd spring-boot:run
```
