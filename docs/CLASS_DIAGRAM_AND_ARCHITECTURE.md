# waYcation 2.0: Complete System Class Diagram & OOP Architecture

This document contains the complete **UML Class Diagram** for all 6 modules of the waYcation 2.0 system.

---

## 1. Complete UML Class Diagram (Mermaid)

```mermaid
classDiagram
    %% MODULE 1: ACCOUNT & PROFILE
    class Role {
        <<enumeration>>
        TOURIST
        HOTEL_OWNER
        ADMIN
        SUPER_ADMIN
        STAFF
        +isAdministrative() boolean
    }

    class Profile {
        -UUID id
        -String firebaseUid
        -String email
        -String displayName
        -Role role
        -String avatarUrl
        -Instant createdAt
        -Instant updatedAt
        +isAnonymized() boolean
        +getDisplayName() String
        +setDisplayName(String name) void
    }

    %% MODULE 2: HOTEL & ROOM CATALOG
    class HotelStatus {
        <<enumeration>>
        DRAFT
        ACTIVE
        UNDER_REVIEW
        INACTIVE
        ARCHIVED
        SUSPENDED
    }

    class Hotel {
        -UUID id
        -String ownerUid
        -String name
        -String city
        -String address
        -String description
        -HotelStatus status
        -String heroImage
        -double starRating
        -Instant createdAt
        +getOwnerUid() String
        +getStatus() HotelStatus
    }

    class HotelPhoto {
        -UUID id
        -UUID hotelId
        -String url
        -String caption
        -boolean isHero
        -int displayOrder
    }

    class RoomType {
        -UUID id
        -UUID hotelId
        -String name
        -BigDecimal basePricePerNight
        -int capacity
        -int maxOccupancy
        -String amenities
        +getBasePricePerNight() BigDecimal
    }

    class RoomInventory {
        -UUID id
        -UUID roomTypeId
        -LocalDate inventoryDate
        -int availableCount
        -BigDecimal priceModifier
        +isAvailable(int count) boolean
    }

    %% MODULE 3: BOOKINGS
    class BookingStatus {
        <<enumeration>>
        PENDING
        CONFIRMED
        CANCELLED
        COMPLETED
        REFUNDED
    }

    class Booking {
        -UUID id
        -String touristUid
        -UUID hotelId
        -UUID roomTypeId
        -LocalDate checkInDate
        -LocalDate checkOutDate
        -int roomQuantity
        -int totalGuests
        -BigDecimal totalPrice
        -BookingStatus status
        -String guestName
        -String guestEmail
        +canBeCancelled() boolean
        +getStatus() BookingStatus
    }

    %% MODULE 4: REVIEWS
    class ReviewStatus {
        <<enumeration>>
        PENDING
        PUBLISHED
        FLAGGED
        REJECTED
        HIDDEN
    }

    class Review {
        -UUID id
        -String authorUid
        -UUID hotelId
        -UUID bookingId
        -int rating
        -String comment
        -ReviewStatus status
        -Instant createdAt
        +isVerifiedStay() boolean
    }

    %% MODULE 5: PACKAGES
    class PackageStatus {
        <<enumeration>>
        DRAFT
        ACTIVE
        FEATURED
        EXPIRED
        ARCHIVED
    }

    class TravelPackage {
        -UUID id
        -String title
        -String description
        -BigDecimal price
        -int durationDays
        -String heroImage
        -String destinationCity
        -int maximumCapacity
        -PackageStatus status
    }

    class PackageHotel {
        -UUID id
        -UUID packageId
        -UUID hotelId
        -int stopOrder
        -int nights
    }

    class PackagePurchase {
        -UUID id
        -String touristUid
        -UUID packageId
        -BigDecimal amountPaid
        -LocalDate travelStartDate
        -String itinerarySnapshot
    }

    %% MODULE 6: COUPONS & VOTING
    class DiscountType {
        <<enumeration>>
        PERCENTAGE
        FIXED_AMOUNT
    }

    class CouponStatus {
        <<enumeration>>
        ACTIVE
        EXPIRED
        EXHAUSTED
        DISABLED
    }

    class Coupon {
        -UUID id
        -String code
        -DiscountType discountType
        -BigDecimal discountValue
        -BigDecimal minSpend
        -BigDecimal maxDiscountAmount
        -LocalDate validFrom
        -LocalDate validUntil
        -int maxUsageLimit
        -int currentUsageCount
        -CouponStatus status
        +isValidNow() boolean
        +calculateDiscount(BigDecimal total) BigDecimal
    }

    class CouponRedemption {
        -UUID id
        -UUID couponId
        -String userUid
        -UUID bookingId
        -BigDecimal discountApplied
        -Instant redeemedAt
    }

    %% RELATIONSHIPS
    Profile "1" --> "1" Role : has role
    Hotel "1" --> "1" HotelStatus : lifecycle state
    Hotel "1" *-- "0..*" HotelPhoto : contains
    Hotel "1" *-- "1..*" RoomType : offers
    RoomType "1" *-- "0..*" RoomInventory : schedules
    Profile "1" --> "0..*" Booking : places
    Hotel "1" --> "0..*" Booking : booked in
    Booking "1" --> "1" BookingStatus : tracks status
    Booking "1" --> "0..1" Review : verified review
    Review "1" --> "1" ReviewStatus : moderated by
    TravelPackage "1" *-- "2..*" PackageHotel : sequence stops
    Hotel "1" <-- "0..*" PackageHotel : included stop
    TravelPackage "1" --> "0..*" PackagePurchase : bought as
    Profile "1" --> "0..*" PackagePurchase : orders
    Coupon "1" --> "1" DiscountType : strategy
    Coupon "1" --> "1" CouponStatus : state
    Coupon "1" <-- "0..*" CouponRedemption : redeemed audits
    Booking "1" <-- "0..1" CouponRedemption : applied to
```

---

## 2. How to Export or View This Class Diagram

You have 3 easy ways to get and display this diagram for your project submission:

### Method 1: Mermaid Live Editor (Free Online PNG/SVG Export)
1. Copy the code block starting from `classDiagram` above.
2. Go to: **[https://mermaid.live](https://mermaid.live)**
3. Paste the code into the left editor.
4. The diagram will render instantly on the right.
5. Click **Download PNG** or **Download SVG** to insert it directly into your report or slides!

### Method 2: Visual Studio Code
1. Install the extension: **Markdown Preview Mermaid Support** (by Matt Bierner).
2. Open this markdown file in VS Code and press `Ctrl + Shift + V`. The diagram renders directly!

### Method 3: Draw.io / PlantUML
You can also import Mermaid syntax into [draw.io](https://app.diagrams.net) via **Arrange $\to$ Insert $\to$ Advanced $\to$ Mermaid**.
