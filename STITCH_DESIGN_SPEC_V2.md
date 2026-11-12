# waYcation v2.0 — Comprehensive Stitch UI/UX Design Specification

> **Platform:** waYcation v2.0 — Luxury Hotel Reservations, Curated Vacation Packages & Smart Travel Management  
> **Architecture:** Spring Boot 3 + PostgreSQL (Supabase) + Thymeleaf + Tailwind CSS + Vanilla JS  
> **Target Audience:** Public Visitors, Verified Tourists, Hotel Owners, and Platform Administrators  
> **Key Simplification:** Group Tinder voting removed; Travel Packages now fully support **Creation, Editing, Deletion/Archiving by both Admins & Hotel Owners**, and direct purchase by Tourists.

---

## 🎨 1. Global Design System & Stitch Visual Directives

When feeding prompts into **Google Stitch**, Figma AI, or v0, enforce these exact design tokens:

### 1.1 Color Palette
* **Primary Brand Navy:** `#003580` (Deep authoritative navy blue, inspired by Booking.com)
* **Navy Dark / Header:** `#00224f` (Deep night blue)
* **Accent Brand Yellow / Gold:** `#febb02` / `#e5a800` (Warm, energetic call-to-action yellow)
* **Background Neutrals:**
  * Page Canvas: `#f8fafc` (Tailwind `slate-50`)
  * Surface / Card Background: `#ffffff` (Pure white)
  * Elevated Containers: `#f1f5f9` (Tailwind `slate-100`)
* **Typography & Borders:**
  * Primary Headings: `#0f172a` (Tailwind `slate-900`)
  * Body Text: `#334155` (Tailwind `slate-700`)
  * Muted Captions / Subtitles: `#64748b` (Tailwind `slate-500`)
  * Subtle Borders: `#e2e8f0` (Tailwind `slate-200`)
* **Role-Based Theme Accents:**
  * **Tourist Space:** Emerald Green (`#10b981`) & Brand Navy (`#003580`)
  * **Hotel Owner Portal:** Royal Indigo (`#4338ca` / `#4f46e5`)
  * **Platform Admin Console:** Deep Crimson (`#b91c1c` / `#dc2626`)
  * **Status & Feedback:**
    * Success / Active: `#10b981` (Emerald)
    * Warning / Pending: `#f59e0b` (Amber)
    * Danger / Cancel / Delete: `#ef4444` (Rose / Crimson)

### 1.2 Typography & Sizing
* **Font Family:** `Inter`, `Plus Jakarta Sans`, or `system-ui, sans-serif`
* **Headings:** Bold / Black (`font-bold` to `font-black`), tracking tight (`tracking-tight`)
* **Section Eyebrows / Badges:** Uppercase, tracking wider (`text-xs font-bold uppercase tracking-wider`)
* **Body:** Clean readable line heights (`text-sm` to `text-base`, `leading-relaxed`)

### 1.3 Radii, Borders & Shadows
* **Cards & Containers:** `rounded-2xl` or `rounded-3xl` with 1px border (`border border-slate-200/80`)
* **Form Inputs & Buttons:** `rounded-xl`
* **Shadows:** Soft modern elevation (`shadow-sm`, hover states `hover:shadow-md transition-all duration-200`)
* **Modal Windows:** `rounded-3xl shadow-2xl backdrop-blur-sm`

---

## 🗺️ 2. Master Screen Directory & Information Architecture

| # | Screen Name | URL Route | Target Role | Module Covered | Key Feature Highlight |
|---|---|---|---|---|---|
| **01** | Landing & Travel Discovery | `/` | Public | Global / M2 / M5 | Booking-style search bar, destination tiles, package teasers |
| **02** | Tourist Sign-In | `/tourist/sign-in` | Tourist | M1 (Account) | Clean auth card with demo credentials pill |
| **03** | Hotel Owner Sign-In | `/owner/sign-in` | Hotel Owner | M1 (Account) | Indigo-accented hotelier authentication console |
| **04** | Platform Admin Sign-In | `/admin/sign-in` | Admin | M1 (Account) | Crimson-accented superuser portal |
| **05** | User Registration | `/sign-up` | Public | M1 (Account) | Tourist vs Hotel Owner role toggle, secure signup form |
| **06** | Profile & Account Settings | `/account` | All Users | M1 (Account) | Profile update, password reset, GDPR data anonymization |
| **07** | Admin User Directory | `/admin/users` | Admin | M1 (Account) | Role modifier, user suspension, user search table |
| **08** | Hotel Discovery Catalog | `/hotels` | Public / Tourist | M2 (Hotels) | City filter, price range slider, real-time cards |
| **09** | Hotel Details & Room Booking | `/hotels/{id}` | Tourist | M2 / M3 | 3-photo mosaic, room selector cards, sticky booking sidebar |
| **10** | Owner Hotels Portfolio | `/owner/hotels` | Hotel Owner | M2 (Hotels) | Property cards, room count metrics, active/archive toggle |
| **11** | Owner Hotel Creator & Editor | `/owner/hotels/new` | Hotel Owner | M2 (Hotels) | Multi-tier room builder, amenity checklist, photo URLs |
| **12** | Tourist Bookings Dashboard | `/tourist/bookings` | Tourist | M3 (Bookings) | Active/completed/cancelled tabs, status pills, invoice action |
| **13** | Direct Reservation Modal | `/bookings/new` | Tourist | M3 (Bookings) | Date range picker, room counter, coupon code reducer |
| **14** | Owner Guest Reservations | `/owner/bookings` | Hotel Owner | M3 (Bookings) | Check-in calendar, guest table, revenue payout metrics |
| **15** | Reschedule & Cancel Modal | `/bookings/{id}/reschedule` | Tourist | M3 (Bookings) | Date switcher, refund policy calculation, atomic release |
| **16** | Tourist Reviews Dashboard | `/tourist/reviews` | Tourist | M4 (Reviews) | Verified stay badge, star rating history, edit review modal |
| **17** | Hotel Review Submission Modal | `/reviews/new` | Tourist | M4 (Reviews) | Interactive 5-star selector, verified booking ID check |
| **18** | Public Vacation Packages | `/packages` | Public / Tourist | M5 (Packages) | Multi-stop itinerary cards, bundle discount badges |
| **19** | Package Detail & Purchase | `/packages/{id}` | Tourist | M5 (Packages) | Ordered stop sequence, included hotels, instant checkout |
| **20** | Owner Packages Dashboard | `/owner/packages` | Hotel Owner | M5 (Packages) | Hotelier's custom packages, creation button, edit/delete |
| **21** | Admin All Packages Console | `/admin/packages` | Admin | M5 (Packages) | Platform-wide package management, toggle visibility |
| **22** | Package Builder & Editor Form | `/owner/packages/new` | Owner / Admin | M5 (Packages) | Step-by-step stops builder, hotel dropdown, pricing input |
| **23** | Admin Coupons Dashboard | `/admin/coupons` | Admin | M6 (Coupons) | Promo code table, usage counters, active/deactive switch |
| **24** | Admin Coupon Creator Modal | `/admin/coupons/new` | Admin | M6 (Coupons) | Percentage/fixed discount, expiration picker, min spend |
| **25** | Global Header & Nav Drawer | Component | All Roles | Global | Role-aware navigation, user avatar dropdown, responsive bar |
| **26** | Global Footer System | Component | All Roles | Global | Trust badges, currency switcher, quick links, copyright |

---

## 📱 3. Detailed Screen Specifications & Stitch Prompts

---

### Screen 01: Landing & Travel Discovery (`/`)

#### 1. Purpose & Layout
The public entry point inspired by Booking.com. It establishes brand trust, features an elevated multi-field search bar, highlights top Sri Lankan destinations, showcases curated vacation packages, and provides clear role-based sign-in portals.

#### 2. Key UI Elements
* **Top Header Bar:** Dark navy (`#00224f`), waYcation brand logo with gold compass icon, navigation links (`Hotels`, `Packages`, `About`), "Sign In" role selector dropdown, and gold "Register" button.
* **Hero Section:**
  * Background: Deep navy gradient (`#003580` to `#00224f`).
  * Headline: "Find your next stay, discover Sri Lanka effortlessly."
  * Subtitle: "Handpicked boutique hotels, verified guest reviews, and curated multi-hotel vacation packages."
* **Floating Search Box (White card with gold accent border, elevated shadow):**
  * Destination Input: Map pin icon with placeholder "Where are you going? (e.g. Galle, Kandy, Ella)".
  * Date Picker: Calendar icon with "Check-in — Check-out" range selector.
  * Occupancy Selector: User icon with "2 Adults · 1 Room".
  * Search CTA Button: Brand navy with magnifying glass icon "Search Stays".
* **Value Proposition Cards (3-Column Grid):**
  * Card 1: *Atomic Booking Locks* — Shield icon, guarantee against double bookings.
  * Card 2: *Curated Multi-Hotel Packages* — Luggage icon, handpicked itineraries across top destinations.
  * Card 3: *100% Verified Reviews* — Star icon, authentic feedback only from guests with completed stays.
* **Featured Destinations Grid (4 Visual Cards):**
  * Galle Fort, Ella Highlands, Mirissa Coast, and Cultural Kandy with high-resolution imagery, property counts, and category tags.
* **Featured Vacation Packages Teaser (3-Card Carousel/Grid):**
  * Cards showing "Southern Coast Explorer", "Hill Country Serenity", with itinerary stop pills, total days, and "From LKR 85,000" pricing.
* **Role Portals Access Section:**
  * 3 quick-access cards: Tourist Space, Hotel Owner Portal (Indigo), and Administrator Portal (Crimson).

#### 3. Stitch Prompt
```text
Create a modern, luxury travel booking homepage inspired by Booking.com named "waYcation".
Color palette: Deep Navy Blue (#003580), Night Navy (#00224f), Warm Gold/Yellow (#febb02), and crisp White cards on Slate-50 background.
Header: Dark night navy bar with white logo "waYcation" having a gold compass accent, navigation links (Hotels, Vacation Packages, Destinations), a "Sign In" dropdown button, and a vibrant gold "Register" button.
Hero Section: Navy blue background with bold white headline "Find your next stay, discover Sri Lanka effortlessly" and gold subtitle.
Search Widget: Floating white card with rounded-2xl corners, 1px border, and shadow-xl. Inside has 4 inline items: destination search with map-pin icon, date range picker with calendar icon, guests/rooms dropdown, and an authoritative navy blue "Search Stays" button.
Trust Bar: 3 horizontal white cards with subtle borders highlighting "Atomic Double-Booking Protection", "Curated Vacation Packages", and "100% Verified Guest Reviews".
Featured Destinations: 4 large aesthetic photography cards (Galle, Ella, Mirissa, Kandy) with dark gradient overlays, white titles, and property count badges.
Curated Packages Teaser: 3 package cards with duration badges ("4 Days / 3 Nights"), ordered stop pills (Stop 1: Galle, Stop 2: Mirissa), price tags in LKR, and "View Package →" buttons.
Clean, modern, and trustworthy layout.
```

---

### Screen 02: Tourist Sign-In (`/tourist/sign-in`)

#### 1. Purpose & Layout
Focused authentication screen for regular travelers and tourists to access their bookings, reviews, and profile.

#### 2. Key UI Elements
* **Brand Header:** Centered logo with link back to homepage.
* **Authentication Card (White, `rounded-3xl`, `shadow-xl`, `max-w-md`):**
  * Category Badge: Emerald green pill `Tourist Account`.
  * Title: "Welcome Back" with subtitle "Sign in to manage your bookings and verified reviews".
  * Email Input Field: Clean border with mail icon, floating label.
  * Password Input Field: Lock icon with "Show/Hide" toggle.
  * "Remember me" checkbox & "Forgot Password?" link.
  * Primary Action: Full-width navy button "Sign In as Tourist".
  * Divider: "or continue with" text.
  * Demo Credentials Quick-Fill Pill: "Click to autofill Tourist demo login".
  * Footer: "Don't have an account? Sign up as a Tourist".

#### 3. Stitch Prompt
```text
Design a clean, modern user sign-in page for a travel platform called "waYcation".
Background: Very light slate-50 with a subtle geometric background pattern.
Center Card: White background, rounded-3xl, 1px slate-200 border, soft deep shadow.
Card Header: waYcation logo at top, emerald green badge "Tourist Portal", bold headline "Welcome Back", and subtitle "Sign in to manage your hotel reservations".
Form Elements:
- Email address input with a mail icon and placeholder "tourist@example.com".
- Password input with a lock icon and an eye toggle button.
- Flex row with "Remember this device" checkbox and blue "Forgot password?" text.
- Full-width primary CTA button in deep navy blue (#003580) with text "Sign In to Your Account".
- A clickable demo badge pill with text "Quick Demo: tourist@waycation.lk".
Bottom Text: "Looking for property management? Switch to Hotel Owner Sign-in" with link to owner portal.
```

---

### Screen 03: Hotel Owner Sign-In (`/owner/sign-in`)

#### 1. Purpose & Layout
Dedicated portal entry for hoteliers and resort managers with an Indigo theme to clearly distinguish commercial management from tourist bookings.

#### 2. Key UI Elements
* **Visual Theme:** Indigo accents (`#4338ca`) and building/hotel badge icons.
* **Card Header:** Indigo badge `Hotelier & Property Portal`.
* **Form Elements:** Email, Password, "Keep me logged into management console".
* **CTA Button:** Deep Royal Indigo button "Access Property Dashboard →".
* **Notice Banner:** "Want to list your hotel or boutique resort on waYcation? Register your property".

#### 3. Stitch Prompt
```text
Design a specialized B2B Hotel Owner Sign-In page for the travel platform "waYcation".
Theme: Indigo and Slate professional enterprise look (#4338ca).
Card: Centered white card, rounded-3xl, subtle indigo top border highlight.
Badge: Royal indigo pill badge with a building icon "Hotelier & Owner Portal".
Headline: "Property Management Console" with subtitle "Manage your hotel catalog, room tiers, guest reservations, and vacation packages".
Inputs: Modern floating-label fields for Business Email and Password.
Button: Full-width royal indigo button (#4338ca) with text "Sign In to Owner Dashboard".
Bottom Callout: "New hotel partner? Register as a Hotel Owner to list your rooms and curate travel packages".
```

---

### Screen 04: Platform Admin Sign-In (`/admin/sign-in`)

#### 1. Purpose & Layout
Security-hardened administrator gateway with Crimson theme for managing system-wide accounts, verified hotels, travel packages, and promotional coupons.

#### 2. Key UI Elements
* **Theme:** Crimson / Ruby red accents (`#b91c1c`) with shield/lock emblems.
* **Security Notice:** "Authorized Personnel Only — Super Admin & Staff Portal".
* **Form:** Administrator Email, Security Key / Password.
* **CTA Button:** Deep Crimson button "Unlock Admin Console".

#### 3. Stitch Prompt
```text
Design an administrative login page for a travel reservation system.
Theme: High security, Crimson red (#b91c1c) and deep dark slate accents.
Card: Sleek elevated white card with a crimson security shield badge "System Administration".
Title: "Platform Admin Console", subtitle: "Restricted access for system administrators and managers".
Form: Administrator Email, Master Password, and a full-width crimson button "Unlock Management Console".
A small security footer: "All administrative access attempts are encrypted and audited".
```

---

### Screen 05: User Registration (`/sign-up` or `/register`)

#### 1. Purpose & Layout
Universal onboarding screen allowing users to choose their account type (**Tourist** vs. **Hotel Owner**) via a responsive segmented toggle switch.

#### 2. Key UI Elements
* **Role Switcher (Segmented Radio Pills):**
  * Option A: `Tourist / Traveler` (Book stays, buy vacation packages, post verified reviews).
  * Option B: `Hotel Owner / Partner` (List properties, manage room inventory, create vacation packages).
* **Form Inputs:**
  * Full Name / Display Name.
  * Email Address.
  * Password (with real-time strength bar).
  * Confirm Password.
  * Terms & Privacy agreement checkbox.
* **Conditional Fields (if Hotel Owner selected):**
  * Business / Property Group Name.
  * Contact Phone Number.
* **CTA:** Brand Navy / Indigo button "Create My Account".

#### 3. Stitch Prompt
```text
Design a user registration page with an interactive account type toggle.
Top: Segmented switch with two large visual options:
1. "Tourist / Traveler" (selected state: navy blue highlight, backpack icon).
2. "Hotel Owner" (unselected state: slate border, building icon).
Form Card:
- Full Name input with user icon.
- Email Address input with mail icon.
- Password input with a 4-level color strength meter (Weak, Fair, Good, Strong).
- Confirm Password input.
- Checkbox: "I agree to waYcation's Terms of Service and Cancellation Policies".
- Large primary CTA button: "Create My waYcation Account".
Footer: "Already have an account? Sign in here".
Clean, friendly, and conversion-focused.
```

---

### Screen 06: Profile & Account Settings (`/account`)

#### 1. Purpose & Layout
Self-service profile management for all authenticated users to manage personal details, view assigned role permissions, and exercise privacy rights.

#### 2. Key UI Elements
* **Profile Header:** User avatar with photo upload button, Display Name, Email, and Role Badge (`TOURIST`, `HOTEL_OWNER`, or `ADMIN`).
* **Section 1: Personal Details Form:** Full Name, Phone Number, Preferred Currency (LKR, USD, EUR).
* **Section 2: Security & Password:** Current Password, New Password, "Update Password" button.
* **Section 3: Privacy & GDPR Rights:**
  * Danger zone card with red outline.
  * "Request Account Anonymization" button (scrubs name and email in PostgreSQL while preserving historical booking financial records).

#### 3. Stitch Prompt
```text
Design an Account & Profile Settings page for a travel platform user.
Header: Modern profile header with a circular avatar image, user name "Kasun Perera", email, and an emerald green pill badge "Verified Tourist".
Left Sidebar / Tabs: "Personal Info", "Security & Password", "Preferences", and "Privacy & Data".
Main Content Area (Personal Info):
- Grid with inputs for First Name, Last Name, Email (disabled), Phone Number, and Preferred Currency dropdown.
- "Save Profile Changes" button in navy blue.
Danger Zone Container at bottom: Light red/rose card with warning icon, headline "Account Anonymization & Deletion", explanatory text about data privacy, and a red outline button "Anonymize My Account".
```

---

### Screen 07: Admin User Directory (`/admin/users`)

#### 1. Purpose & Layout
Administrative management table displaying all registered users with role-switching actions and account audit metrics.

#### 2. Key UI Elements
* **Dashboard Header:** Crimson banner with "User Directory & Role Administration", search bar, and role filter dropdown (`All`, `Tourist`, `Hotel Owner`, `Admin`).
* **Users Data Table:**
  * Columns: User, Email, Assigned Role (colored pills), Registration Date, Status, Actions.
  * Role Dropdown: Quick change between `TOURIST` and `HOTEL_OWNER`.
  * Actions Menu: View booking history, reset credentials, deactivate account.

#### 3. Stitch Prompt
```text
Design an Admin User Management dashboard screen.
Header: Dark slate with crimson accent, headline "User Directory & Role Management", search input "Search by name or email...", and a role filter dropdown.
Data Table: Clean modern table with subtle borders:
- Column 1: User avatar and full name.
- Column 2: Email address.
- Column 3: Role pill (Emerald for TOURIST, Indigo for HOTEL_OWNER, Crimson for ADMIN).
- Column 4: Date joined.
- Column 5: Status pill (Active green dot).
- Column 6: Action buttons: "Edit Role" dropdown and "..." options menu.
Pagination at bottom: "Showing 1 to 10 of 124 users" with Next/Previous buttons.
```

---

### Screen 08: Hotel Discovery Catalog (`/hotels`)

#### 1. Purpose & Layout
High-converting accommodation catalog listing real PostgreSQL hotel records with multi-criteria filtering, price sorting, and live availability cards.

#### 2. Key UI Elements
* **Catalog Filter Bar:**
  * Search input: "Filter by city or hotel name..."
  * Sort dropdown: "Price: Low to High", "Price: High to Low", "Top Rated".
  * City Quick-Pills: `All Cities`, `Galle`, `Ella`, `Kandy`, `Colombo`, `Mirissa`.
* **Results Meta Bar:** Result counter: "Showing 12 verified properties" with a green PostgreSQL sync badge.
* **Hotel Cards (Horizontal Responsive Cards):**
  * Left: High-resolution property image (16:9 ratio) with city badge overlay.
  * Center: Hotel title (clickable), location address with map pin, star rating pill (`★ 4.9 (48 reviews)`), 2-line description, and amenity tags (`Ocean View`, `Infinity Pool`, `Free WiFi`).
  * Right: "Starting from" label, price per night in bold black (`LKR 45,000`), "Includes taxes & fees", and navy CTA "View Rooms & Book →".
* **Empty State:** Centered card with search illustration and "Reset filters" button.

#### 3. Stitch Prompt
```text
Design a hotel discovery catalog and search results page for "waYcation".
Top Bar: Clean filter section with a search input "Search city or hotel...", a sort dropdown "Price: Low to High", and horizontal scrollable city filter pills (All, Galle, Ella, Kandy, Mirissa).
Results Info: Text "Showing 12 available properties in Sri Lanka" with a small green database status badge.
List of Hotel Cards (Horizontal layout, white card, rounded-2xl, subtle border):
Each card has:
1. Left side photo (rounded-xl) with a red map pin badge "Galle Fort".
2. Middle area: Hotel name in bold slate-900 "The Fortress Resort & Spa", full address, star rating badge (yellow star, 4.9 rating, 32 reviews), short description, and feature tag pills (Pool, Oceanfront, Free Breakfast).
3. Right side: Price container with "Starting from", prominent text "LKR 55,000 / night", and a deep navy blue button "View Rooms & Book →".
Polished, spacious, and responsive.
```

---

### Screen 09: Hotel Details & Room Booking (`/hotels/{id}`)

#### 1. Purpose & Layout
Detailed property view with photo mosaic, room category selector, verified reviews list, and a sticky booking sidebar calculating live totals and applying promotional coupons.

#### 2. Key UI Elements
* **Top Navigation Breadcrumb:** "← Back to Stays / Galle / The Fortress Resort & Spa".
* **Photo Mosaic Grid:** 3-photo grid (1 primary large photo on left, 2 stacked photos on right) with "View all photos" button.
* **Left Column (2/3 width):**
  * Property Overview & Amenities list.
  * Check-in / Check-out policy box (Check-in: 14:00, Check-out: 11:00).
  * **Room Tier Selector Cards:**
    * Tier 1: Deluxe Ocean Suite (Capacity: 2 Guests, LKR 45,000/night).
    * Tier 2: Presidential Villa (Capacity: 4 Guests, LKR 95,000/night).
    * Active Selection: Blue highlight border, checkmark badge "Selected ✓".
  * **Verified Guest Reviews Section:**
    * Summary score box: `4.9 / 5.0` based on 32 reviews.
    * Review cards with reviewer name, verified stay date, 5-star rating, and feedback comment.
* **Right Column (1/3 width - Sticky Reservation Card):**
  * Card header: "Reserve Your Stay".
  * Selected room summary with base price per night.
  * Check-in and Check-out date pickers.
  * Guest count selector.
  * Total nights calculation.
  * **Coupon Discount Row:** Input for promo code (e.g. `WAYCATION10`) + "Apply" button with green discount deduction line.
  * Total Price in bold LKR.
  * Primary Action: Full-width navy button "Confirm & Book Stay".
  * Security reassurance: Green shield icon "Atomic SQL Inventory Lock — Zero Double Bookings".

#### 3. Stitch Prompt
```text
Design a luxury hotel detail and reservation screen with a 2-column layout.
Top: Breadcrumb navigation and hotel header with title "The Fortress Resort & Spa", location, and 4.9 star rating badge.
Photo Gallery: 3-image mosaic layout (1 large hero photo on left, 2 stacked photos on right).
Left 2/3 Column:
1. "About this property" paragraph and a grid of amenity icons (Infinity Pool, Spa, Ocean View, Wi-Fi).
2. Policies box showing Check-in (14:00) and Check-out (11:00).
3. "Available Room Types" section with 2 selectable room cards. The active card has a prominent blue border and an emerald "Selected ✓" pill; the inactive card has a neutral gray border.
4. "Verified Guest Reviews" section displaying genuine guest reviews with yellow stars, author names, and verified stay badges.
Right 1/3 Column (Sticky Booking Widget):
Elevated white card with rounded-3xl and deep shadow.
- Displays calculated price "LKR 90,000 for 2 nights".
- Inputs for Check-in Date and Check-out Date.
- Number of guests dropdown.
- Coupon Code input with an "Apply" button and green badge showing "-10% Promo Applied".
- Final calculated Total Price.
- Full-width navy CTA button "Reserve Stay Now".
- Atomic database guarantee badge with green shield icon.
```

---

### Screen 10: Owner Hotels Portfolio (`/owner/hotels`)

#### 1. Purpose & Layout
Hotel owner's management dashboard listing all owned properties, room inventory counts, active/inactive statuses, and a quick button to add a new hotel.

#### 2. Key UI Elements
* **Dashboard Header:** Indigo accent banner with "Property Portfolio Management" and an "+ Add New Hotel" button.
* **Summary KPI Cards:**
  * Card 1: Total Properties Listed.
  * Card 2: Active Room Types.
  * Card 3: Total Bookings This Month.
* **Hotel Cards Grid:**
  * Property thumbnail, Title, City, Base Price.
  * Status indicator: `Active (Bookable)` in green or `Archived / Hidden` in gray.
  * Actions: "Edit Property Details", "Manage Rooms", "Deactivate Listing".

#### 3. Stitch Prompt
```text
Design a Hotel Owner Property Management dashboard screen.
Header: Royal indigo theme (#4338ca) with title "My Hotel Portfolio" and a top-right indigo button "+ Add New Property".
Top Stat Cards: 3 metric cards showing "Total Properties (3)", "Active Rooms (14)", and "Monthly Bookings (42)".
Main Content: Grid of property management cards:
Each card shows:
- Hotel photo with an "Active" green pill badge.
- Hotel name, address, and city.
- Room count indicator ("4 Room Types configured").
- Base nightly rate in LKR.
- Action buttons: "Edit Property", "Manage Rooms", and a toggle switch to enable/disable public visibility.
Professional, clean, and intuitive hotelier portal.
```

---

### Screen 11: Owner Hotel Creator & Editor (`/owner/hotels/new` & `.../edit`)

#### 1. Purpose & Layout
Multi-step form allowing hotel owners to create a new hotel listing or update an existing one with descriptions, photos, and room type tiers.

#### 2. Key UI Elements
* **Section 1: General Information:** Hotel Name, Destination City dropdown, Full Street Address, Contact Email & Phone.
* **Section 2: Descriptions & Amenities:** Narrative summary, Checkbox list for amenities (Pool, Spa, Beachfront, Air Conditioning, Free Breakfast, Gym).
* **Section 3: Photography:** Image URLs input list with live image preview thumbnails.
* **Section 4: Room Types Configuration:**
  * Dynamic list with "+ Add Room Tier" button.
  * Tier fields: Room Name (e.g. Deluxe Suite), Capacity (Guests), Price per Night (LKR), Total Units Available.
* **Form Actions:** "Cancel" button and "Save & Publish Hotel" primary button.

#### 3. Stitch Prompt
```text
Design a hotel creation form screen for property owners.
Header: "Create New Hotel Listing" with breadcrumb "← Back to Properties".
Form Container: Large white card with organized sections:
1. "Basic Information": Inputs for Hotel Name, City (dropdown: Galle, Ella, Kandy, etc.), and Full Address.
2. "Description & Highlights": Multi-line textarea for hotel overview.
3. "Amenities Checklist": 2-column checkbox grid (Infinity Pool, Free Wi-Fi, Spa, Beachfront, Restaurant, Parking).
4. "Room Tiers Builder": Section where owner can add room types. Shows one configured card (Room Name: "Ocean Deluxe", Max Guests: 2, Price per Night: "LKR 45,000") and a dashed "+ Add Another Room Type" button.
Footer Actions: Secondary "Discard Changes" button and primary royal indigo "Publish Hotel Listing" button.
```

---

### Screen 12: Tourist Bookings Dashboard (`/tourist/bookings`)

#### 1. Purpose & Layout
Personal reservation center for tourists to track upcoming stays, past completed trips, cancellation options, and invoice downloads.

#### 2. Key UI Elements
* **Dashboard Header:** "My Hotel Reservations" with active filter tabs: `All Bookings`, `Upcoming Stays`, `Completed`, `Cancelled`.
* **Reservation Cards List:**
  * Hotel Thumbnail & Title with link.
  * Booking Reference Number (e.g. `#WAY-88421`).
  * Dates: Check-in & Check-out with night count badge (`3 Nights`).
  * Room Tier & Guests.
  * Financial Summary: Total Paid, applied coupon code if any.
  * Status Badge:
    * `CONFIRMED` (Emerald Green)
    * `COMPLETED` (Slate Gray)
    * `CANCELLED` (Rose Red)
  * Action Buttons:
    * Upcoming: "Reschedule Dates" and "Cancel Reservation".
    * Completed: "Write a Verified Review ★" (links directly to review submission).
* **Empty State:** "No reservations found. Explore hotels or curated packages to book your next trip."

#### 3. Stitch Prompt
```text
Design a Tourist Reservations Management screen for a travel booking app.
Header: Headline "My Travel Bookings" with a filter tab bar (All Stays, Upcoming, Completed, Cancelled).
Booking Cards (White cards, rounded-2xl, border slate-200):
Card 1 (Upcoming):
- Left: Hotel image of "The Fortress Resort & Spa".
- Center: Booking Reference "#WAY-90412", stay dates "Nov 15, 2026 – Nov 18, 2026 (3 nights)", Room: "Deluxe Ocean Suite".
- Status Badge: Emerald green pill "CONFIRMED".
- Right: Total price "LKR 135,000", action buttons: "Reschedule Dates" and a red text link "Cancel Booking".
Card 2 (Completed):
- Past stay card with gray status pill "COMPLETED", and a prominent yellow-accented button "Write a Verified Review ★".
Clean and user-friendly.
```

---

### Screen 13: Direct Reservation Modal (`/bookings/new`)

#### 1. Purpose & Layout
Modal dialog or dedicated checkout step displaying a clear price breakdown, guest info, and coupon application before finalizing an atomic reservation.

#### 2. Key UI Elements
* **Modal Header:** "Complete Your Reservation" with close button.
* **Stay Summary:** Hotel Name, Room Type, Selected Dates, Nights Count.
* **Guest Contact Form:** Full Name, Email, Phone Number, Special Requests textarea.
* **Pricing Breakdown Box:**
  * Room Rate x Nights: `LKR 90,000`
  * Taxes & Service Fees (Included): `LKR 0`
  * Applied Coupon Code: `- LKR 9,000 (10% OFF)`
  * Final Total: `LKR 81,000`
* **CTA Button:** Navy "Confirm & Lock Reservation" with spinner loading state.

#### 3. Stitch Prompt
```text
Design a booking confirmation modal dialog for a hotel reservation.
Modal Window: Centered floating white card, rounded-3xl, shadow-2xl, max-w-xl.
Header: Title "Complete Your Reservation" with hotel name "Jetwing Lighthouse, Galle".
Content:
- Date summary pill: "Nov 20 – Nov 22, 2026 · 2 Nights · 2 Guests".
- Contact Details: Name and Email input fields.
- Pricing Table: Subtotal LKR 80,000, Coupon Discount (WAYCATION10) -LKR 8,000 in green, Final Total LKR 72,000 in bold black text.
- Trust Badge: Green lock icon "Guaranteed Atomic Inventory Lock".
Footer: "Cancel" button and a prominent navy blue "Confirm Reservation" button.
```

---

### Screen 14: Owner Guest Reservations (`/owner/bookings`)

#### 1. Purpose & Layout
Hotel owner's guest tracking console with check-in schedules, guest details, booking statuses, and revenue tallies.

#### 2. Key UI Elements
* **Header:** Royal Indigo banner with "Guest Bookings & Check-in Schedule".
* **Metrics Row:** Today's Check-ins, Active Guests In-House, Upcoming Reservations, Month-to-Date Revenue.
* **Guest Bookings Table:**
  * Columns: Booking ID, Guest Name, Room Tier, Check-in Date, Check-out Date, Total Amount, Status (`CONFIRMED`, `CHECKED_IN`, `COMPLETED`, `CANCELLED`).
  * Action: "Mark Checked In", "View Invoice".

#### 3. Stitch Prompt
```text
Design a Hotel Owner Guest Reservations dashboard screen.
Theme: Royal Indigo (#4338ca) and Slate.
Header: Headline "Guest Bookings & Front Desk", search bar "Search guest name or booking ID...", and date filter.
Summary Stats: 4 clean KPI cards (Today's Arrivals: 4, In-House Guests: 18, Upcoming: 29, Total Revenue: LKR 1,840,000).
Table: Modern data table with columns:
- Booking ID (#WAY-812)
- Guest Name (with email and phone)
- Assigned Room Tier
- Check-in & Check-out dates
- Total Price
- Status Badge (Confirmed green, Checked In blue, Cancelled red)
- Action buttons.
Clean, professional, and optimized for hotel staff.
```

---

### Screen 15: Reschedule & Cancel Modal (`/bookings/{id}/reschedule`)

#### 1. Purpose & Layout
Interactive modal dialog enabling tourists to either change their reservation dates without double-paying or cancel their booking with automated refund calculation.

#### 2. Key UI Elements
* **Reschedule Tab:**
  * Current Dates vs New Date Range Pickers.
  * Price difference indicator (if new dates have different rates).
  * "Confirm Date Change" button.
* **Cancel Tab:**
  * Warning icon with cancellation policy explanation.
  * Refund amount calculation.
  * Reason for cancellation dropdown.
  * Red "Confirm Cancellation & Release Room" button.

#### 3. Stitch Prompt
```text
Design a modal dialog for rescheduling or cancelling a hotel booking.
Modal: White card, rounded-3xl, centered with dark backdrop blur.
Top: Segmented tabs "Reschedule Dates" and "Cancel Booking".
Tab 1 (Reschedule): Shows current booking dates "Nov 15 - 18", two new date pickers for New Check-in and New Check-out, and an emerald button "Update Reservation".
Tab 2 (Cancel): Warning box with yellow caution icon, refund policy notice "Full refund eligible up to 48 hours before check-in", cancellation reason dropdown, and a bold red button "Cancel Booking & Process Refund".
```

---

### Screen 16: Tourist Verified Reviews Dashboard (`/tourist/reviews`)

#### 1. Purpose & Layout
Tourist review center displaying their verified feedback history and highlighting completed stays that are eligible for new reviews (enforcing the strict 1-review-per-completed-booking rule).

#### 2. Key UI Elements
* **Dashboard Header:** "My Verified Reviews & Feedback".
* **Eligible Stays Section (Pending Reviews):**
  * Banner: "You have 1 completed stay ready to be reviewed!"
  * Card showing Hotel Name, Stay Dates, and a gold "Write Review ★" CTA button.
* **Published Reviews Grid:**
  * Hotel Thumbnail & Name.
  * Star Rating (`★ ★ ★ ★ ★ 5/5`).
  * Review title and full comment text.
  * Date published.
  * Actions: "Edit Review" and "Delete Review".

#### 3. Stitch Prompt
```text
Design a Tourist Reviews & Ratings dashboard screen.
Header: "My Verified Reviews" with subtitle "Only verified guests who completed a stay can leave authentic reviews".
Callout Box at top: Warm amber card with star icon "You have 1 completed stay awaiting your review! The Fortress Resort & Spa (Stayed Oct 2026)" with a yellow button "Write Review Now".
Reviews History List: Stack of review cards:
Each card shows:
- Hotel name and thumbnail photo.
- Star rating widget displaying 5 solid yellow stars.
- Review date and "Verified Stay" green badge.
- Review comment paragraph: "Exceptional beachfront stay. Staff was courteous and breakfast was divine."
- Small text links: "Edit Review" and "Delete".
Polished, clean, and authentic.
```

---

### Screen 17: Hotel Review Submission Modal (`/reviews/new`)

#### 1. Purpose & Layout
Modal dialog allowing a verified tourist to rate and review their completed hotel stay.

#### 2. Key UI Elements
* **Modal Title:** "Review Your Stay at [Hotel Name]".
* **Interactive Star Rating Selector:** 5 clickable large star icons (hover states highlight stars gold).
* **Review Comment Area:** Textarea with placeholder "What did you enjoy most about your stay? Mention room comfort, service, location...".
* **Trust Reassurance:** "Your review will be marked as a Verified Stay".
* **Actions:** "Discard" and "Submit Verified Review" button.

#### 3. Stitch Prompt
```text
Design a hotel review submission modal dialog.
Modal Card: White, rounded-3xl, max-w-lg, elevated shadow.
Header: "Rate & Review Your Stay" with hotel name "Jetwing Lighthouse, Galle".
Content:
1. Star Rating Selector: 5 large, interactive golden stars with label "Click to rate your experience".
2. Textarea: Clean bordered input with placeholder "Share details about your room, service, cleanliness, and food...".
3. A subtle badge: "✓ Verified Completed Stay #WAY-8841".
Footer: "Cancel" button and a vibrant navy/gold button "Publish Verified Review".
```

---

### Screen 18: Public Vacation Packages Catalog (`/packages`)

#### 1. Purpose & Layout
Public discovery catalog showcasing multi-hotel, multi-day curated travel itineraries created by Admins and Hotel Owners, displaying ordered stops and bundled discounts.

#### 2. Key UI Elements
* **Catalog Hero Banner:**
  * Title: "Curated Multi-Hotel Vacation Packages"
  * Subtitle: "Handpicked Sri Lankan travel journeys with seamless multi-destination stays."
* **Packages Grid (2-Column Spacious Layout):**
  * Full-bleed landscape cover photo with Duration Badge (`4 Days / 3 Nights`).
  * Creator Badge: `Curated by Jetwing Hotels` (Owner) or `Official waYcation Curated` (Admin).
  * Package Title: e.g. "Southern Heritage & Coastal Serenity".
  * Narrative Overview (2 lines).
  * **Itinerary Sequence Container (Slate-50 background):**
    * Label: `ORDERED ITINERARY STOPS:`
    * Step 1: Blue circle `#1` — Jetwing Lighthouse (Galle · 2 nights).
    * Step 2: Blue circle `#2` — Fortress Resort (Koggala · 1 night).
  * **Card Footer:**
    * Bundle Price in LKR (`LKR 115,000`).
    * "View Itinerary & Book →" primary button.

#### 3. Stitch Prompt
```text
Design a curated multi-hotel travel packages catalog page.
Header: Dark navy banner with golden sparkles icon, bold white title "Curated Vacation Packages", and subtitle "Experience multiple Sri Lankan destinations with one seamless booking".
Grid Layout: 2-column grid of spacious package cards (white, rounded-3xl, shadow-md, slate-200 border).
Each Package Card contains:
1. High-resolution landscape cover photo of Sri Lanka with a floating glassmorphism badge "5 Days / 4 Nights".
2. Top badge indicating author: "Curated by Hotelier" or "waYcation Official".
3. Bold title "Southern Coast & Hill Country Splendor" and short description.
4. An Itinerary Stops Box with light gray background displaying numbered stops:
   - Step 1 (blue circle #1): Galle Fort Stay (2 nights).
   - Step 2 (blue circle #2): Ella Mountain Resort (2 nights).
5. Card footer with "Bundle Price" label, large price "LKR 145,000", and a navy blue "View Itinerary & Book →" button.
Polished, appealing, and inspiring travel aesthetic.
```

---

### Screen 19: Package Detail & Tourist Purchase Checkout (`/packages/{id}`)

#### 1. Purpose & Layout
Detailed itinerary breakdown for a vacation package, allowing the tourist to select their journey start date, review the hotel sequence, and purchase the package.

#### 2. Key UI Elements
* **Top Navigation:** "← Back to Packages" link, Package Title, Total Duration pill (`6 Days / 5 Nights`).
* **Left Column (2/3 width):**
  * Full narrative description and journey overview.
  * **Step-by-Step Stop Timeline:**
    * Vertical timeline with numbered nodes (`1`, `2`, `3`).
    * Each stop card includes: Hotel photo thumbnail, Hotel Name, City, Allocated Nights, and included room amenities.
* **Right Column (1/3 width - Purchase Sidebar):**
  * Sticky checkout card with Total Bundle Price in LKR.
  * Start Date Picker: "Select Journey Start Date".
  * Guest count selector.
  * Price breakdown displaying individual hotel rates vs package bundle savings (`Save LKR 24,000 with this bundle`).
  * Primary Action: Full-width navy button "Purchase Vacation Package".
  * Guarantee: "Immutable package snapshot stored in your account".

#### 3. Stitch Prompt
```text
Design a Vacation Package Detail and Purchase screen with a 2-column layout.
Top: Breadcrumb "← Back to Packages", package title "Sri Lanka Heritage & Coastal Voyage", and a duration badge "5 Days / 4 Nights".
Left 2/3 Column:
1. Overview story paragraph about the journey.
2. Vertical Itinerary Timeline:
   - Stop 1 (Node #1 in blue circle): "Jetwing Lighthouse, Galle" (2 nights) with thumbnail image, room description, and highlights.
   - Stop 2 (Node #2 in blue circle): "98 Acres Resort, Ella" (2 nights) with mountain view thumbnail and highlights.
Right 1/3 Column (Purchase Widget):
Sticky white card with rounded-3xl and deep shadow:
- Large price text "LKR 160,000 Total Bundle".
- Green savings pill: "Save 15% vs booking separately".
- Date input for "Select Journey Start Date".
- Guests selector dropdown.
- Full-width navy blue button "Purchase Package Now".
- Small reassurance text: "Includes reservations at all listed hotels in sequence".
Inspiring and trustworthy design.
```

---

### Screen 20: Hotel Owner Packages Dashboard (`/owner/packages`)

#### 1. Purpose & Layout
Dedicated dashboard where Hotel Owners can manage their customized vacation packages, see tourist purchase counts, and create new packages.

#### 2. Key UI Elements
* **Header:** Royal Indigo banner with "My Hotel Packages & Itineraries" and a "+ Create New Package" button.
* **Packages Table / Grid:**
  * Package Title, Cover Photo, Duration, Number of Stops, Price in LKR, Status (`Active` / `Draft`).
  * Action buttons:
    * "Edit Package" (opens editor form).
    * "Delete / Archive" (with confirmation dialog).
* **Empty State:** "You haven't created any vacation packages yet. Combine your hotel stays with partner destinations to offer bundled packages to travelers."

#### 3. Stitch Prompt
```text
Design a Hotel Owner Packages Management dashboard screen.
Theme: Royal Indigo (#4338ca) and Slate.
Header: Headline "Vacation Packages Management", subtitle "Create and manage curated multi-stay packages for travelers", and a top-right indigo button "+ Create New Package".
Content Table / Cards:
A list of owner-created packages:
Card 1:
- Thumbnail image of the package.
- Title "Southern Coastal Wellness Retreat".
- Duration "4 Days / 3 Nights" and Stops count "2 Hotel Stops".
- Price "LKR 110,000".
- Status pill: "Active (Visible to Tourists)" in green.
- Actions: Indigo "Edit" button, and red outline "Delete" button.
Clean and functional management interface.
```

---

### Screen 21: Platform Admin All Packages Console (`/admin/packages`)

#### 1. Purpose & Layout
Administrative console displaying all packages across the entire platform (both Admin-curated and Hotel Owner-submitted) with editing, deletion, and toggle controls.

#### 2. Key UI Elements
* **Header:** Crimson accent banner with "Platform Vacation Packages Directory" and "+ Create Admin Package" button.
* **Filter Tabs:** `All Packages`, `Admin Curated`, `Hotel Owner Created`.
* **Management Table:**
  * Columns: Package Name, Author/Owner, Stops, Duration, Price (LKR), Public Visibility Switch, Actions (`Edit`, `Delete`).

#### 3. Stitch Prompt
```text
Design a Platform Admin Vacation Packages console.
Theme: Crimson (#b91c1c) and Dark Slate.
Header: Title "All Platform Packages", filter tabs (All, Admin Created, Owner Created), and a crimson button "+ Create Platform Package".
Data Table:
- Column 1: Package Title and cover thumbnail.
- Column 2: Creator (Badge: "Super Admin" or "Hotel Owner: Jetwing").
- Column 3: Duration & Stops (4 Days · 2 Stops).
- Column 4: Bundle Price.
- Column 5: Visibility Toggle Switch (Active on / off).
- Column 6: Actions: Edit icon, Delete trash icon.
Clean enterprise admin table.
```

---

### Screen 22: Package Builder & Editor Form (`/owner/packages/new` & `.../edit`)

#### 1. Purpose & Layout
Intuitive multi-step builder allowing both Hotel Owners and Admins to create or edit a vacation package with ordered hotel stops and pricing.

#### 2. Key UI Elements
* **Header:** "Build Curated Travel Package" with "Back to Packages" link.
* **Basic Details:** Package Title, Total Duration (Days & Nights), Narrative Description, Cover Photo URL.
* **Dynamic Itinerary Stops Builder:**
  * Reorderable stops list with numbered pills (`Stop 1`, `Stop 2`, `Stop 3`).
  * For each stop:
    * Hotel selector dropdown (searches all active hotels).
    * Number of nights at this stop.
    * Stop order number input.
  * "+ Add Another Stop" button.
* **Pricing & Publishing:**
  * Bundle Price in LKR.
  * Active/Public checkbox.
  * "Save & Publish Package" button.

#### 3. Stitch Prompt
```text
Design a Package Builder form screen for creating multi-hotel travel itineraries.
Header: "Create Vacation Package" with subtitle "Combine hotels into an ordered itinerary for travelers".
Form Sections:
1. "Package Overview": Inputs for Package Title ("Southern Serenity"), Duration Days (4), Duration Nights (3), and a Cover Image URL.
2. "Itinerary Stops Builder":
   - Stop #1 Card: Hotel dropdown selector (e.g. "Jetwing Lighthouse, Galle"), Nights at this hotel (2), and a small trash icon to remove.
   - Stop #2 Card: Hotel dropdown selector (e.g. "The Fortress Resort, Koggala"), Nights (1).
   - A dashed button with plus icon "+ Add Another Itinerary Stop".
3. "Pricing & Publication":
   - Bundle Price in LKR input field ("115,000").
   - Checkbox "Publish immediately to public catalog".
Footer: "Cancel" button and primary "Save & Publish Package" button.
```

---

### Screen 23: Admin Coupons Dashboard (`/admin/coupons`)

#### 1. Purpose & Layout
Administrative discount management table displaying promotional coupon codes, percentage/fixed discounts, expiration dates, and live toggle switches.

#### 2. Key UI Elements
* **Header:** Crimson accent banner with "Promotions & Coupon Management" and "+ Create Coupon" button.
* **Coupons Data Table:**
  * Columns: Promo Code (e.g. `WAYCATION10`), Discount Type (`PERCENTAGE` / `FIXED`), Value (`10%` or `LKR 5,000`), Minimum Spend, Expiration Date, Times Used, Status Toggle (`Active` / `Inactive`).
  * Action: "Edit", "Deactivate", "Delete".

#### 3. Stitch Prompt
```text
Design an Admin Promotions & Coupons Management screen.
Theme: Crimson (#b91c1c) and Slate.
Header: Headline "Promotional Coupons & Discounts" and a top-right button "+ Create New Coupon".
Coupons Table:
- Column 1: Coupon Code in monospace bold badge (e.g., "WAYCATION10", "SUMMER5000").
- Column 2: Discount Type & Value (10% OFF or LKR 5,000 Fixed).
- Column 3: Minimum Spend requirement ("Min: LKR 40,000").
- Column 4: Expiration Date ("Dec 31, 2026").
- Column 5: Usage counter ("48 / 100 used").
- Column 6: Active Toggle Switch (Green active toggle).
- Column 7: Actions: Edit and Delete icons.
Clean, modern, and high-density dashboard.
```

---

### Screen 24: Admin Coupon Creator Modal (`/admin/coupons/new`)

#### 1. Purpose & Layout
Modal dialog for generating a new discount code with validation rules, expiration date, and redemption limits.

#### 2. Key UI Elements
* **Modal Title:** "Create Promotional Coupon".
* **Inputs:**
  * Coupon Code (uppercase auto-formatting, e.g. `EARLYBIRD15`).
  * Discount Type (Radio: `Percentage %` or `Fixed Amount LKR`).
  * Discount Value input.
  * Minimum Booking Amount (LKR).
  * Expiration Date picker.
  * Max Redemptions limit.
* **Actions:** "Cancel" and "Save Coupon" button.

#### 3. Stitch Prompt
```text
Design a modal dialog for creating a promotional coupon code.
Modal Card: White, rounded-3xl, max-w-lg, elevated shadow.
Header: "Create New Promotional Coupon".
Fields:
1. Coupon Code input (e.g. "DISCOUNT20") in uppercase text.
2. Discount Type radio options: "Percentage Discount (%)" and "Fixed Amount (LKR)".
3. Discount Value input field ("20" or "5000").
4. Minimum Spend Amount (LKR) input.
5. Expiration Date calendar picker.
6. Maximum Usage Limit input.
Footer: "Cancel" button and primary crimson button "Save & Activate Coupon".
```

---

### Screen 25: Global Navigation Bar & Footer System

#### 1. Purpose & Layout
Unified navigation system supporting public visitors, tourists, hotel owners, and administrators, with responsive mobile drawer support.

#### 2. Key UI Elements
* **Top Navbar:**
  * Left: Brand logo "waYcation" with gold compass icon.
  * Center: Links for "Hotels", "Vacation Packages", "Destinations".
  * Right (when logged in): Role pill badge, User Name, Avatar, and Dropdown Menu (`My Bookings`, `My Reviews`, `Account Settings`, `Sign Out`).
  * Right (when guest): "Sign In" role dropdown and yellow "Register" button.
* **Global Footer:**
  * 4 Columns: Company, Destinations, For Property Owners, Security & Compliance.
  * Trust badges: PostgreSQL Atomic Locking, Verified Reviews, 256-bit SSL.
  * Copyright & currency switcher.

#### 3. Stitch Prompt
```text
Design a global responsive Navigation Header and Footer system for "waYcation".
Navbar:
- Deep night navy background (#00224f) with white text.
- Left: "waYcation." logo with a warm gold period and compass icon.
- Center links: "Hotels", "Vacation Packages", "Sri Lanka Guide".
- Right logged-in user profile: Circular avatar, user name "Nimal Perera", an emerald "Tourist" badge, and a chevron dropdown icon.
Footer:
- Slate-900 background with clean 4-column link directory (Explore, Accommodations, For Hotel Partners, Legal & Privacy).
- Bottom row: Currency selector (LKR), copyright notice, and trust badges for "Atomic SQL Protection" and "Verified Stays".
```

---

## 🛠️ 4. Step-by-Step Guide: How to Generate & Implement UI with Stitch

### Step 1: Open Google Stitch / UI Generator
1. Open **Google Stitch** (or your preferred AI UI design tool such as v0 or Galileo).
2. Set your design workspace or project name to **waYcation v2.0**.

### Step 2: Paste the Screen Prompts
1. Copy the exact **Stitch Prompt** provided in Section 3 for the screen you want to generate (e.g., Screen 08 for `/hotels` or Screen 18 for `/packages`).
2. Paste the prompt into Stitch and click **Generate**.
3. Inspect the layout, spacing, and color accents against the **Global Design Directives** (Navy `#003580`, Gold `#febb02`, and role-based Indigo/Crimson accents).

### Step 3: Export HTML & Tailwind Code
1. Once satisfied with the visual design in Stitch, click **Export** -> **HTML / Tailwind CSS**.
2. Copy the exported HTML markup.

### Step 4: Map to Spring Boot Thymeleaf Templates
Open your `waYcation-v2` project and paste the generated UI into the corresponding template file in `src/main/resources/templates/`:

| Stitch Screen # | Spring Boot Template Location |
|---|---|
| **01 (Landing)** | `src/main/resources/templates/index.html` |
| **02 (Tourist Sign-in)** | `src/main/resources/templates/auth/tourist-signin.html` |
| **03 (Owner Sign-in)** | `src/main/resources/templates/auth/owner-signin.html` |
| **04 (Admin Sign-in)** | `src/main/resources/templates/auth/admin-signin.html` |
| **05 (Register)** | `src/main/resources/templates/auth/register.html` |
| **06 (Account)** | `src/main/resources/templates/account/profile.html` |
| **07 (Admin Users)** | `src/main/resources/templates/admin/users.html` |
| **08 (Hotel Catalog)** | `src/main/resources/templates/hotels/catalog.html` |
| **09 (Hotel Detail)** | `src/main/resources/templates/hotels/detail.html` |
| **10 (Owner Hotels)** | `src/main/resources/templates/owner/hotels.html` |
| **11 (Owner Hotel Form)** | `src/main/resources/templates/owner/hotel-form.html` |
| **12 (Tourist Bookings)** | `src/main/resources/templates/tourist/bookings.html` |
| **14 (Owner Bookings)** | `src/main/resources/templates/owner/bookings.html` |
| **16 (Tourist Reviews)** | `src/main/resources/templates/tourist/reviews.html` |
| **18 (Packages Catalog)** | `src/main/resources/templates/packages/catalog.html` |
| **19 (Package Detail)** | `src/main/resources/templates/packages/detail.html` |
| **20 (Owner Packages)** | `src/main/resources/templates/owner/packages.html` |
| **21 (Admin Packages)** | `src/main/resources/templates/admin/packages.html` |
| **22 (Package Form)** | `src/main/resources/templates/packages/package-form.html` |
| **23 (Admin Coupons)** | `src/main/resources/templates/admin/coupons.html` |

### Step 5: Replace Static Placeholders with Thymeleaf Expressions
Replace static text with standard Thymeleaf attributes:
* Text: `th:text="${hotel.name}"`
* Links: `th:href="@{/hotels/{id}(id=${hotel.id})}"`
* Loops: `th:each="hotel : ${hotels}"`
* Conditions: `th:if="${booking.status.name() == 'CONFIRMED'}"`
