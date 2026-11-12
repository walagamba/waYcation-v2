# Module 3: Booking & Reservation Management — Code & OOP Architecture

## 1. Module Overview
Module 3 is the transactional core that processes guest checkouts, date-range availability calculations, atomic room inventory decrements, reservation status tracking, and cancellation refunds without double-booking.

---

## 2. Classes in This Module & Their Specific Uses

### 1. `BookingStatus` (Enum)
- **Use & Purpose in System**: Governs the reservation state machine (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, `REFUNDED`).
- **Specific Responsibilities**: Dictates whether room inventory can be released and whether customer reviews are permitted.

### 2. `Booking` (JPA Entity Model)
- **Use & Purpose in System**: The core transactional record linking a tourist to a hotel and room type for a specified date range.
- **Specific Responsibilities**: Stores check-in/out dates, guest contact details, total price calculation, and status flags.

### 3. `BookingRepository` (Data Access Layer)
- **Use & Purpose in System**: Provides query methods for customer booking histories, owner check-in dashboards, and date conflict lookups.

### 4. `BookingService` (Transactional Service)
- **Use & Purpose in System**: Executes atomic room reservations under database transaction locks (`@Transactional`).
- **Specific Responsibilities**: Validates that check-in dates are not in the past, ensures check-out is after check-in, calculates stay duration, and atomically reserves inventory per night.

### 5. `BookingController` (Web & Route Presentation Layer)
- **Use & Purpose in System**: Manages checkout summary screens, reservation submission forms, and cancellation actions.
- **Specific Responsibilities**: Retains user-selected dates in flash attributes when validation fails so guests do not have to re-enter inputs.

### 6. `BookingAction` (Facade Adapter Layer)
- **Use & Purpose in System**: Provides a unified entry point for booking operations and status updates.

---

## 3. Attributes (Fields & Data Types)

### `Booking.java`:
- `private UUID id`: Primary key.
- `private String touristUid`: UID of the tourist placing the reservation.
- `private UUID hotelId`: Foreign key to `Hotel`.
- `private UUID roomTypeId`: Foreign key to `RoomType`.
- `private LocalDate checkInDate`: Beginning of stay.
- `private LocalDate checkOutDate`: Departure date.
- `private int roomQuantity`: Number of rooms reserved.
- `private int totalGuests`: Guest count.
- `private BigDecimal totalPrice`: Computed total reservation cost.
- `private BookingStatus status`: Enum (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, `REFUNDED`).
- `private String guestName`, `guestEmail`: Primary guest contact info.
- `private Instant createdAt`: Audit timestamp.

### `BookingStatus.java`:
- Values: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, `REFUNDED`.

---

## 4. Object-Oriented Programming (OOP) Principles Applied

### A. Encapsulation & State Invariant Protection
- Attributes like `totalPrice` and `status` cannot be mutated directly from outside. `BookingService` enforces state transition rules (e.g., a booking can only transition from `CONFIRMED` to `CANCELLED` if it is not already in the past).

### B. Association & Relational Mapping
- A `Booking` maintains a Many-to-One conceptual association with `Profile` (via `touristUid`), `Hotel` (via `hotelId`), and `RoomType` (via `roomTypeId`).

### C. Polymorphism
- Handled in status transitions where different `BookingStatus` values alter the behavior of reservation actions (e.g. `REFUNDED` restores room inventory while `COMPLETED` unlocks customer review rights).

---

## 5. Constructors
- **`public Booking()`**: Default constructor for JPA entity reconstruction.
- **`public Booking(touristUid, hotelId, roomTypeId, checkIn, checkOut, quantity, guests, totalPrice, status)`**: Full constructor ensuring all relational and monetary invariants are initialized at creation.

---

## 6. Getters & Setters
- Standard JavaBeans getters/setters allow Thymeleaf to render booking confirmation cards (`th:text="${booking.totalPrice}"`) and controllers to extract user data safely.

---

## 7. Key Methods & Business Logic
1. `createBooking(...)`:
   - Validates that `checkInDate >= LocalDate.now()` (prevents past-date booking).
   - Validates that `checkOutDate > checkInDate` (prevents zero-night or negative-duration stays).
   - Calculates exact nights between check-in and checkout.
   - Atomically locks and decrements available inventory for every night of the stay.
2. `cancelBooking(UUID bookingId, String userUid)`:
   - Verifies that the cancelling user is either the original tourist or the hotel owner.
   - Transitions status to `CANCELLED` and restores daily room inventory counts.
3. `getTouristBookings(String touristUid)`: Fetches past and upcoming reservations for a tourist.

---

## 8. CRUD Operations Mapping

| Operation | HTTP / Route | Service Method | Database Action |
| :--- | :--- | :--- | :--- |
| **Create** | `POST /bookings/confirm` | `createBooking(...)` | `INSERT INTO bookings`, `UPDATE room_inventory` |
| **Read** | `GET /tourist/bookings`, `GET /owner/bookings` | `getTouristBookings()`, `getOwnerBookings()` | `SELECT * FROM bookings WHERE tourist_uid = ...` |
| **Update** | `POST /bookings/{id}/reschedule` | `rescheduleBooking(...)` | `UPDATE bookings SET check_in_date = ...` |
| **Delete / Cancel**| `POST /bookings/{id}/cancel` | `cancelBooking(...)` | `UPDATE bookings SET status = 'CANCELLED'` |

---

## 9. Database Access Points & Relational Tables
- **Tables**: `bookings`, `room_inventory`
- **Transactions**: Annotated with `@Transactional` so that if room inventory cannot be decremented for even one night, the entire reservation is rolled back to prevent incomplete records.
