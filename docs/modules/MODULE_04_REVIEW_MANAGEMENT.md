# Module 4: Review & Rating Management — Code & OOP Architecture

## 1. Module Overview
Module 4 handles customer feedback, star rating aggregation (1 to 5 stars), content moderation (FLAGGED/REJECTED), and enforces the verified completed-stay restriction (preventing fake or duplicate reviews).

---

## 2. Classes in This Module & Their Specific Uses

### 1. `ReviewStatus` (Enum)
- **Use & Purpose in System**: Defines content moderation states (`PENDING`, `PUBLISHED`, `FLAGGED`, `REJECTED`, `HIDDEN`).
- **Specific Responsibilities**: Shields the public site from spam, abusive language, or fraudulent feedback.

### 2. `Review` (JPA Entity Model)
- **Use & Purpose in System**: Stores customer feedback, verified stay associations, and star ratings.
- **Specific Responsibilities**: Links an author UID to a specific hotel and a completed booking ID.

### 3. `ReviewRepository` (Data Access Layer)
- **Use & Purpose in System**: Queries published reviews for hotel pages and checks for prior reviews by booking ID.

### 4. `ReviewService` (Business Logic & Moderation Service)
- **Use & Purpose in System**: Enforces verified stay constraints (only completed reservations can leave reviews, strictly 1 review per booking).
- **Specific Responsibilities**: Computes real-time average star ratings using Java streams and provides admin moderation toggles.

### 5. `ReviewController` (Web & Route Presentation Layer)
- **Use & Purpose in System**: Serves customer review submission forms and admin moderation queues.

### 6. `ReviewAction` (Facade Adapter Layer)
- **Use & Purpose in System**: Unifies review operations and feedback queries into a clean facade.

---

## 3. Attributes (Fields & Data Types)

### `Review.java`:
- `private UUID id`: Primary key.
- `private String authorUid`: UID of the tourist submitting the review.
- `private UUID hotelId`: Foreign key to `Hotel`.
- `private UUID bookingId`: Foreign key to the specific completed `Booking`.
- `private int rating`: Integer star rating bounded between 1 and 5.
- `private String comment`: Customer textual feedback.
- `private ReviewStatus status`: Enum (`PENDING`, `PUBLISHED`, `FLAGGED`, `REJECTED`, `HIDDEN`).
- `private Instant createdAt`: Submission timestamp.

### `ReviewStatus.java`:
- Values: `PENDING`, `PUBLISHED`, `FLAGGED`, `REJECTED`, `HIDDEN`.

---

## 4. Object-Oriented Programming (OOP) Principles Applied

### A. Encapsulation
- The `rating` field is encapsulated with validation in `ReviewService` and the entity setters to guarantee that ratings outside 1–5 stars are rejected with an `IllegalArgumentException`.

### B. Association
- A `Review` has a strict One-to-One conceptual association with a verified `Booking` (one review per completed reservation), and a Many-to-One association with `Hotel`.

### C. Abstraction
- `ReviewRepository` abstracts SQL query filtering (`findByHotelIdAndStatus`, `existsByBookingId`) behind clean Java interfaces.

---

## 5. Constructors
- **`public Review()`**: Mandatory no-arg constructor for Hibernate.
- **`public Review(authorUid, hotelId, bookingId, rating, comment, status)`**: Overloaded constructor initializing the complete review record.

---

## 6. Getters & Setters
- Provided for all fields to enable Thymeleaf dynamic template binding (`th:text="${review.comment}"`, star loops) and JSON serialization.

---

## 7. Key Methods & Business Logic
1. `submitReview(...)`:
   - Checks that the user actually completed a stay at the hotel.
   - Enforces the rule that **only 1 review can ever be submitted per `bookingId`** to prevent review spam.
   - Validates rating boundaries ($1 \le rating \le 5$).
2. `calculateAverageRating(UUID hotelId)`:
   - Uses Java Streams to compute the real-time arithmetic mean of all `PUBLISHED` reviews for that property.
3. `moderateReview(UUID reviewId, ReviewStatus newStatus)`:
   - Allows platform admins to flag or hide inappropriate content.

---

## 8. CRUD Operations Mapping

| Operation | HTTP / Route | Service Method | Database Action |
| :--- | :--- | :--- | :--- |
| **Create** | `POST /tourist/reviews` | `submitReview(...)` | `INSERT INTO reviews` |
| **Read** | `GET /hotels/{id}`, `GET /tourist/reviews` | `getHotelReviews()`, `getTouristReviews()` | `SELECT * FROM reviews WHERE hotel_id = ...` |
| **Update** | `POST /tourist/reviews/{id}/edit` | `updateReview(...)` | `UPDATE reviews SET comment = ..., rating = ...` |
| **Delete / Moderate** | `POST /admin/reviews/{id}/moderate`| `moderateReview(...)` | `UPDATE reviews SET status = 'HIDDEN'` |

---

## 9. Database Access Points & Relational Table
- **Table**: `reviews`
- **Unique Constraint**: `UNIQUE(booking_id)` ensures that even under concurrent clicks, the database prevents duplicate review records.
