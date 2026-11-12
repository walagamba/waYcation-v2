# Module 5: Travel Package & Bundle Management — Code & OOP Architecture

## 1. Module Overview
Module 5 manages curated multi-destination vacation bundles (e.g. "Sri Lanka Heritage & Beach Tour - 7 Days"). It controls multi-hotel stop sequences, fixed bundle pricing, and immutable purchase snapshots so future hotel modifications never alter purchased itineraries.

---

## 2. Classes in This Module & Their Specific Uses

### 1. `PackageStatus` (Enum)
- **Use & Purpose in System**: Governs vacation bundle availability (`DRAFT`, `ACTIVE`, `FEATURED`, `EXPIRED`, `ARCHIVED`).

### 2. `TravelPackage` (JPA Entity Model - Aggregate Root)
- **Use & Purpose in System**: Defines multi-day, curated travel experiences covering multiple destinations.
- **Specific Responsibilities**: Stores bundle pricing, duration in days, destination hubs, and maximum traveler quotas.

### 3. `PackageHotel` (JPA Entity Model - Join Entity)
- **Use & Purpose in System**: Maps hotels to a vacation package in a chronological itinerary sequence.
- **Specific Responsibilities**: Tracks stop numbers (`stopOrder`) and night allocations per hotel stop.

### 4. `PackagePurchase` (JPA Entity Model)
- **Use & Purpose in System**: Records customer bundle purchases and stores immutable JSON snapshots.
- **Specific Responsibilities**: Preserves the exact hotel itinerary and pricing at the time of purchase, preventing retroactive edits.

### 5. `TravelPackageRepository` & `PackagePurchaseRepository` (Data Access Layer)
- **Use & Purpose in System**: Provide database access for package catalogs, featured promotions, and traveler purchase orders.

### 6. `PackageService` (Business Logic & Transactional Service)
- **Use & Purpose in System**: Validates that packages contain at least 2 hotel stops and executes bundle transactions.

### 7. `PackageController` (Web & Route Presentation Layer)
- **Use & Purpose in System**: Displays the package discovery catalog, itinerary detail views, and admin package creation forms.

### 8. `PackageAction` (Facade Adapter Layer)
- **Use & Purpose in System**: Serves as the domain facade for package booking and administrative curation.

---

## 3. Attributes (Fields & Data Types)

### `TravelPackage.java`:
- `private UUID id`: Primary key.
- `private String title`: Bundle title (e.g. "Southern Coastline Luxury Escape").
- `private String description`: Comprehensive itinerary description.
- `private BigDecimal price`: Fixed bundle package price.
- `private int durationDays`: Duration in days.
- `private String heroImage`: Cover photo URL.
- `private String destinationCity`: Main hub region.
- `private int maximumCapacity`: Participant limit.
- `private PackageStatus status`: Enum (`ACTIVE`, `DRAFT`, `FEATURED`, `EXPIRED`, `ARCHIVED`).

### `PackageHotel.java`:
- `private UUID id`: Primary key.
- `private UUID packageId`: Foreign key to `TravelPackage`.
- `private UUID hotelId`: Foreign key to `Hotel`.
- `private int stopOrder`: Sequence number (Stop 1, Stop 2, etc.).
- `private int nights`: Number of nights assigned to this specific hotel stop.

### `PackagePurchase.java`:
- `private UUID id`: Primary key.
- `private String touristUid`: UID of the purchasing tourist.
- `private UUID packageId`: Reference to package.
- `private BigDecimal amountPaid`: Exact amount paid at checkout time.
- `private LocalDate travelStartDate`: Departure date.
- `private String itinerarySnapshot`: Serialized JSON snapshot preserving all included hotels and stop orders at the moment of payment.

---

## 4. Object-Oriented Programming (OOP) Principles Applied

### A. Composition (Aggregate Pattern)
- `TravelPackage` acts as the aggregate root, composing multiple ordered `PackageHotel` stops. A stop cannot exist independently of its parent package.

### B. Encapsulation & Immutability
- In `PackagePurchase`, `itinerarySnapshot` and `amountPaid` are immutable once written, shielding the customer from retroactive price spikes or hotel drops.

### C. Abstraction
- Repositories abstract complex multi-table joins between packages, package hotels, and hotel listings.

---

## 5. Constructors
- **No-Arg Constructors**: Required by Hibernate for all 3 entities.
- **Overloaded Constructors**:
  - `TravelPackage(...)` initializes complete package definitions.
  - `PackageHotel(packageId, hotelId, stopOrder, nights)` sets up ordered itinerary stops.
  - `PackagePurchase(...)` binds payment and snapshot data.

---

## 6. Getters & Setters
- Implemented across all entities for Thymeleaf data rendering (`th:text="${package.title}"`) and form submission binding.

---

## 7. Key Methods & Business Logic
1. `getActivePackages()`: Discovers published vacation packages.
2. `createPackage(...)`: Validates that every curated travel package contains **at least 2 distinct hotel stops** in chronological sequence (`stopOrder >= 1`).
3. `purchasePackage(...)`: Transactionally decrements package capacity, charges the fixed price, and locks in the JSON snapshot.

---

## 8. CRUD Operations Mapping

| Operation | HTTP / Route | Service Method | Database Action |
| :--- | :--- | :--- | :--- |
| **Create** | `POST /admin/packages/new` | `createPackage(...)` | `INSERT INTO travel_packages`, `INSERT INTO package_hotels` |
| **Read** | `GET /packages`, `GET /packages/{id}` | `getActivePackages()`, `getPackage()` | `SELECT * FROM travel_packages WHERE status = 'ACTIVE'` |
| **Update** | `POST /admin/packages/{id}/edit`| `updatePackage(...)` | `UPDATE travel_packages SET price = ...` |
| **Delete / Archive**| `POST /admin/packages/{id}/status` | `updatePackageStatus(...)` | `UPDATE travel_packages SET status = 'ARCHIVED'` |

---

## 9. Database Access Points & Relational Tables
- **Tables**: `travel_packages`, `package_hotels`, `package_purchases`
- **Foreign Keys**: `package_hotels.package_id -> travel_packages.id`, `package_hotels.hotel_id -> hotels.id`
