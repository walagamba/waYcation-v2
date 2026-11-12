# Module 2: Hotel & Room Catalog Management — Code & OOP Architecture

## 1. Module Overview
Module 2 is the core accommodation engine of waYcation 2.0. It handles hotel listings, owner onboarding, room type inventories, photo galleries, city/price search filtering, and IDOR-protected property management.

---

## 2. Classes in This Module & Their Specific Uses

### 1. `HotelStatus` (Enum)
- **Use & Purpose in System**: Enforces the operational lifecycle of a hotel property (`DRAFT`, `ACTIVE`, `UNDER_REVIEW`, `INACTIVE`, `ARCHIVED`, `SUSPENDED`).
- **Specific Responsibilities**: Controls visibility in search results and prevents public tourists from reserving rooms in hotels undergoing inspection or deactivation.

### 2. `Hotel` (JPA Entity Model - Aggregate Root)
- **Use & Purpose in System**: The central accommodation entity storing hotel identity, location, and owner relationships.
- **Specific Responsibilities**: Maps to the `hotels` table, tracks the owning host (`ownerUid`), full street address, star rating, hero image, and audit timestamps.

### 3. `HotelPhoto` (JPA Entity Model)
- **Use & Purpose in System**: Manages high-resolution gallery images for each hotel.
- **Specific Responsibilities**: Associates image URLs with a parent `hotelId`, tracks display ordering, and designates hero cover banners (`isHero`).

### 4. `RoomType` (JPA Entity Model)
- **Use & Purpose in System**: Defines distinct room categories (e.g. Deluxe Suite, Ocean Villa) offered by a hotel.
- **Specific Responsibilities**: Holds base nightly rates (`basePricePerNight`), guest capacity rules, and amenities lists.

### 5. `RoomInventory` (JPA Entity Model)
- **Use & Purpose in System**: Tracks day-by-day room counts to prevent double-booking.
- **Specific Responsibilities**: Associates a room type with a calendar date and an available count. Decremented during reservation checkout.

### 6. `HotelRepository`, `RoomTypeRepository`, `RoomInventoryRepository` (Data Access Layer)
- **Use & Purpose in System**: Execute optimized database operations for hotels, rooms, and daily schedules using Spring Data JPA queries.

### 7. `HotelService` (Business Logic & Transactional Service)
- **Use & Purpose in System**: Orchestrates hotel catalog search, pricing bounds validation, and owner property administration.
- **Specific Responsibilities**: Enforces that room prices are non-negative, room capacities $\ge 1$, and normalizes search queries.

### 8. `HotelController` (Web & Route Presentation Layer)
- **Use & Purpose in System**: Manages public catalog browsing, hotel detail pages, and host dashboard management.
- **Specific Responsibilities**: Prevents Insecure Direct Object Reference (IDOR) attacks by verifying that a modifying host owns the target hotel before saving changes.

### 9. `HotelAction` (Facade Adapter Layer)
- **Use & Purpose in System**: Acts as the single-call facade bridging controller routes with hotel domain services.

---

## 3. Attributes (Fields & Data Types)

### `Hotel.java`:
- `private UUID id`: Primary key.
- `private String ownerUid`: Foreign reference to the owning user's session UID (`profiles.firebase_uid`).
- `private String name`: Hotel name.
- `private String city`: Destination city (e.g. Galle, Kandy, Colombo).
- `private String address`: Full street address.
- `private String description`: Property summary.
- `private HotelStatus status`: Enum (`ACTIVE`, `INACTIVE`, `DRAFT`, `UNDER_REVIEW`, `SUSPENDED`).
- `private String heroImage`: Primary cover photo URL.
- `private double starRating`: Average rating (e.g. 4.8).
- `private Instant createdAt`, `updatedAt`: Audit timestamps.

### `RoomType.java`:
- `private UUID id`: Primary key.
- `private UUID hotelId`: Foreign key to parent `Hotel`.
- `private String name`: Room type label (e.g. "Deluxe Ocean Villa").
- `private BigDecimal basePricePerNight`: Base nightly rate.
- `private int capacity`: Standard guest capacity.
- `private int maxOccupancy`: Maximum allowed occupants.
- `private String amenities`: Comma-separated or serialized feature list.

### `RoomInventory.java`:
- `private UUID id`: Primary key.
- `private UUID roomTypeId`: Foreign key to `RoomType`.
- `private LocalDate inventoryDate`: Specific calendar date.
- `private int availableCount`: Number of unreserved rooms for that specific day.
- `private BigDecimal priceModifier`: Surge or discount modifier.

---

## 4. Object-Oriented Programming (OOP) Principles Applied

### A. Encapsulation
- All entity attributes are `private`. Data modifications must pass through methods where business rules are enforced (e.g., verifying that room prices are non-negative and capacity $\ge 1$).

### B. Composition & Aggregation
- **Composition**: A `Hotel` consists of multiple `HotelPhoto` and `RoomType` objects. If a hotel is archived or deleted, its room configurations are directly dependent on it.
- **Aggregation**: A `RoomType` aggregates multiple daily `RoomInventory` date snapshots.

### C. Abstraction & Interface-Driven Design
- `HotelRepository`, `RoomTypeRepository`, and `RoomInventoryRepository` inherit from Spring Data's `JpaRepository`. Client classes (`HotelService`) depend on abstract repository interfaces rather than concrete SQL drivers.

---

## 5. Constructors
- **No-Argument Constructors**: Required by Hibernate for entity instantiation.
- **Overloaded Constructors**:
  - `Hotel(ownerUid, name, city, address, description, heroImage)` initializes properties on onboarding.
  - `RoomType(hotelId, name, description, basePricePerNight, capacity, maxOccupancy, amenities)` initializes complete room specifications with price bounds validation.

---

## 6. Getters & Setters
- Implemented across all entities for standard JavaBean compliance, Thymeleaf binding (`th:text="${hotel.name}"`), and JSON serialization.

---

## 7. Key Methods & Business Logic
1. `getActiveHotels()`: Retrieves all public active accommodations.
2. `searchHotels(String city, Double minPrice, Double maxPrice)`: Normalizes city search input, strips whitespace, and filters accommodations.
3. `createRoomType(...)`: Validates that nightly pricing $> 0$ and guest capacity $\ge 1$.
4. `updateHotel(...)`: Enforces IDOR security guard by checking `hotel.getOwnerUid().equals(sessionOwnerUid)` before committing changes.

---

## 8. CRUD Operations Mapping

| Operation | HTTP / Route | Service Method | Database Operation |
| :--- | :--- | :--- | :--- |
| **Create** | `POST /owner/hotels/new` | `createHotel(...)`, `createRoomType(...)` | `INSERT INTO hotels`, `INSERT INTO room_types` |
| **Read** | `GET /hotels`, `GET /hotels/{id}` | `getActiveHotels()`, `getHotel(id)` | `SELECT * FROM hotels WHERE status = 'ACTIVE'` |
| **Update** | `POST /owner/hotels/{id}/edit`| `updateHotel(...)` | `UPDATE hotels SET name = ..., address = ...` |
| **Delete (Soft)** | `POST /owner/hotels/{id}/status` | `updateHotelStatus(...)` | Sets `status = INACTIVE` preserving bookings |

---

## 9. Database Access Points & Relational Tables
- **Tables**: `hotels`, `room_types`, `hotel_photos`, `room_inventory`
- **Foreign Keys**: `room_types.hotel_id -> hotels.id`, `hotel_photos.hotel_id -> hotels.id`
