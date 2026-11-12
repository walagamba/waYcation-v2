# waYcation 2.0 — Stitch UI/UX Design Specification & Action Guide

This document provides a screen-by-screen breakdown of all pages, interactive components, user actions, state variations, and copy-pasteable **Stitch Prompts** for **waYcation 2.0** (a Booking.com-inspired travel discovery, group decision, and hotel reservation platform).

Use this specification to generate initial UI screens and prototypes in Stitch, then export or reference them for refinement in the Spring Boot + Thymeleaf + Tailwind CSS codebase.

---

## 🎨 Global Design System & Theme Directives

When designing in Stitch, keep these visual conventions consistent across all screens:

* **Brand Color Palette:**
  * **Primary Brand Blue:** `#003580` / `#00224f` (Deep trusted navy blue, Booking.com inspired)
  * **Accent Brand Yellow:** `#febb02` / `#e5a800` (Warm energetic call-to-action yellow)
  * **Backgrounds:** `#f8fafc` (Slate 50 cool neutral background) and `#ffffff` (Card white)
  * **Text:** `#0f172a` (Slate 900 headings), `#334155` (Slate 700 body), `#64748b` (Slate 500 captions)
  * **Semantic Accents:**
    * Success / Active: `#10b981` (Emerald green)
    * Danger / Cancel / Pass: `#f43f5e` (Rose / Crimson red)
    * Owner Portal: `#4338ca` (Indigo accent)
    * Admin Portal: `#b91c1c` (Crimson / Ruby red accent)
    * Group / Tinder Feature: `#d97706` / `#f59e0b` (Amber / Gold sparkle)
* **Typography:**
  * Sans-serif (Inter / Plus Jakarta Sans / Roboto)
  * Headings: Extra bold / Black (`font-black`), tracking tight
  * Labels & Badges: Small uppercase tracking wider (`text-[10px] font-bold uppercase tracking-wider`)
* **Corner Radii & Shadows:**
  * Cards: `rounded-2xl` or `rounded-3xl` with subtle borders (`border-slate-200`) and soft elevation (`shadow-sm hover:shadow-md`)
  * Buttons: `rounded-lg` or `rounded-xl`
  * Modals & Popovers: `rounded-2xl` or `rounded-3xl` with `shadow-2xl`

---

## 🗺️ Master Information Architecture & Screen Directory

| # | Screen Name | Route / View | Target Role | Key Highlight / Feature |
|---|---|---|---|---|
| **01** | Landing / Home Page | `/` | Public | Booking.com-style search bar, destination cards, group teaser |
| **02** | Hotel Discovery Catalog | `/hotels` | Public / Tourist | City filter, price sorting, live PostgreSQL cards |
| **03** | Hotel Details & Booking | `/hotels/[id]` | Tourist | Photo gallery, room type selector, sticky reservation card |
| **04** | Curated Packages Catalog | `/packages` | Public / Tourist | Multi-hotel curated bundles with stop order breakdown |
| **05** | Package Detail & Purchase | `/packages/[id]` | Tourist | Step-by-step itinerary sequence, immutable snapshot purchase |
| **06** | Group Trips & Voting Hub | `/tourist/trips` | Tourist | Temporary invite token generation, active trip cards |
| **07** | Tinder Decision Room | `/group/[token]` | Group Invitees | Swipeable hotel cards (Like / Pass), majority consensus result |
| **08** | Tourist Sign-In | `/tourist/sign-in` | Tourist | Clean authentication card, password recovery trigger |
| **09** | Hotel Owner Sign-In | `/owner/sign-in` | Hotel Owner | Indigo-themed hotelier portal authentication |
| **10** | Platform Admin Sign-In | `/admin/sign-in` | Admin | Security-hardened console entry point |
| **11** | User Registration | `/sign-up` | Public | Role toggle (Tourist vs Hotel Owner), password validation |
| **12** | Account & Profile Settings | `/account` | All Roles | Profile edit, Firebase password reset, compliant data anonymization |
| **13** | Tourist Bookings Manager | `/tourist/bookings` | Tourist | Status badges, reschedule modal, atomic cancellation |
| **14** | Tourist Verified Reviews | `/tourist/reviews` | Tourist | 1-per-booking reviews, star ratings, edit/delete modal |
| **15** | Owner Hotels Dashboard | `/owner/hotels` | Hotel Owner | Property list, room count summaries, soft deactivation toggle |
| **16** | Owner New Hotel Form | `/owner/hotels/new` | Hotel Owner | Multi-step property info, dynamic room types, policy editor |
| **17** | Owner Guest Bookings | `/owner/bookings` | Hotel Owner | Incoming guest reservations, check-in schedules, payouts |
| **18** | Admin Packages Builder | `/admin/packages` | Admin | Multi-hotel itinerary creator, stop-order editor |
| **19** | Admin Coupons Dashboard | `/admin/coupons` | Admin | Promo codes, discount rules, usage limits, live toggle |
| **20** | Global Navigation & Footer | Component | Global | Brand header, role switcher menu, responsive drawer |

---

## 📱 Detailed Screen Specifications & Stitch Prompts

---

### Screen 01: Landing & Travel Discovery (`/`)

#### 1. Purpose & Layout
The public entry point inspired by Booking.com, establishing trust, highlighting destination options, and introducing waYcation's unique Tinder-style group decision mechanism.

#### 2. Key UI Components
* **Global Navigation Bar:** waYcation logo with compass badge, links to Hotels, Packages, Group Discovery, "Sign In As" role dropdown, and "Register" button.
* **Hero Section (Brand Blue):**
  * Badge: `✨ Tinder-style Discovery & Group Decisions`
  * Heading: "Find your next stay, decide together in seconds."
  * Subtitle: Handpicked Sri Lankan boutique hotels with atomic reservation locks.
* **Floating Search Box (Brand Yellow border with White inner grid):**
  * Destination input (e.g. "Galle, Sri Lanka") with map pin icon.
  * Check-in / Check-out dates picker placeholder.
  * Guests & Rooms selector ("2 adults · 1 room").
  * "Search Stays" CTA button (Brand Blue, search icon).
* **Trust & Architecture Banner (3 Cards):**
  * Card 1: Server-Enforced Role Security (Tourist, Owner, Admin).
  * Card 2: Tinder Discovery & Temporary Group Links.
  * Card 3: Atomic SQL Integrity (Zero double-booking guarantee).
* **Featured Destinations Grid (4 Visual Cards):**
  * Galle Fort, Ella, Mirissa, Kandy with high-res photography, property counts, and category tags.
* **Interactive Group Decision Feature Teaser Banner:**
  * Gradient banner (Blue to Indigo) with copy: "Can't agree on a hotel with friends?"
  * "Start a Group Session" yellow button.
  * Tilted interactive preview card showing live vote tally (e.g. "3 Likes 👍").
* **Role Portals Section (3 Entry Cards):**
  * Tourist Space (`/tourist/sign-in`)
  * Hotel Owner Space (`/owner/sign-in`)
  * Platform Administration (`/admin/sign-in`)

#### 3. User Actions & Interactions
* `Click "Search Stays"` ➔ Navigates to `/hotels`.
* `Click Destination Card` ➔ Navigates to `/hotels?city={city}`.
* `Click "Start a Group Session"` ➔ Navigates to `/tourist/trips`.
* `Click Role Portal Sign-in` ➔ Navigates to respective sign-in route.

#### 4. Stitch Prompt
```text
Create a modern travel discovery homepage inspired by Booking.com called "waYcation 2.0".
Color scheme: deep navy blue (#003580), bright warm yellow (#febb02), white cards, and subtle slate gray borders.
Top header: navy background with white logo "waYcation." with yellow period, navigation links (Hotels, Travel Packages, Group Discovery), a "Sign In As" dropdown button, and a yellow "Register" button.
Hero section: deep blue background, bold white typography "Find your next stay, decide together in seconds.", yellow sub-headline, and an elevated search bar pill with 4 sections: Destination, Dates, Guests/Rooms, and a navy "Search Stays" button.
Below hero: 3 horizontal trust cards highlighting Role-based Security, Tinder-style Group Voting, and Atomic SQL Double-Booking Protection.
Grid section: 4 scenic cards for popular destinations (Galle Fort, Ella, Mirissa, Kandy) with dark gradient overlays, white text, and tag labels.
Feature highlight banner: dark indigo card showcasing the "Group Decision Link" with a floating tilted smartphone card showing friends voting with likes and a "Start a Group Session" button.
Footer: clean 4-column footer with links and security badges.
```

---

### Screen 02: Hotel Catalog & Search Results (`/hotels`)

#### 1. Purpose & Layout
A high-converting accommodation catalog listing real PostgreSQL records with search filtering, sorting, price indicators, and instant availability navigation.

#### 2. Key UI Components
* **Search Header Banner:**
  * Heading: "Explore Accommodations in Sri Lanka"
  * Filter Bar: Live search input for city/region ("Filter by city..."), Sort dropdown ("Sort by Price: Lowest to Highest, Highest to Lowest").
* **Results Meta Bar:**
  * Counter: "Showing X available properties"
  * System Badge: `✓ Live PostgreSQL Database Records` (green pill).
* **Hotel Cards List (Horizontal Layout):**
  * Property Photo (left 1/3) with floating City badge.
  * Information Block (right 2/3):
    * Hotel title (clickable link to details) and full street address.
    * Rating pill: Star icon, numeric score (e.g. `4.8`), and review count `(24)`.
    * Description excerpt (2 lines truncated).
    * Amenities tags pills (e.g. "Oceanfront", "Infinity Pool", "Ayurvedic Spa").
  * Pricing & Booking Footer:
    * "Starting from" label with large currency & price (e.g. `LKR 45,000 / night`).
    * "Includes taxes & service fees" badge.
    * "Check Availability →" primary button.
* **Empty State:**
  * Clean centered card with "No accommodations found matching your filter" and "Clear city filter" text link.

#### 3. User Actions & Interactions
* `Type in City Input` ➔ Real-time client-side filter of the card list.
* `Select Price Sort Option` ➔ Ascending or descending reordering of cards.
* `Click Hotel Card Title or "Check Availability"` ➔ Navigates to `/hotels/[id]`.

#### 4. Stitch Prompt
```text
Design a hotel search and catalog results page for a travel booking web app.
Header: Navy blue banner with white text "Explore Accommodations in Sri Lanka" containing a modern search input for filtering by city and a dropdown to sort by price.
Sub-header: Shows result count "Showing 12 available properties" and an emerald green pill badge "✓ Live PostgreSQL Database Records".
Main content: Stack of clean horizontal hotel cards (white background, slate-200 border, rounded-2xl).
Each card includes: Left-side property image (16:9 ratio) with a red map-pin city pill over the image. Right side has hotel title in bold slate-900, address, star rating badge (yellow star, 4.8 rating, review count), 2-line description, and tags pills ("Ocean View", "Free Breakfast").
Bottom right of each card displays large price text "LKR 42,000 / night" in bold black, "Includes taxes & fees" in green, and a navy blue "Check Availability →" button.
```

---

### Screen 03: Hotel Detail & Atomic Booking (`/hotels/[id]`)

#### 1. Purpose & Layout
Detailed hotel information display with multi-photo gallery, room type radio cards, guest reviews list, and a sticky reservation sidebar that applies coupons and executes atomic SQL bookings.

#### 2. Key UI Components
* **Top Navigation:** "Back to All Hotels" link, Hotel Title, Address with MapPin, Star Rating + Review count badge.
* **Photo Gallery Grid:** 3-image mosaic (1 large hero photo on left, 2 stacked photos on right).
* **Left Column (2/3 width):**
  * "About this accommodation" narrative description.
  * Quick Info Box: Check-in time, Check-out time, Cancellation Policy text.
  * **Room Type Selector:** Interactive cards for each room tier:
    * Room name (e.g., "Deluxe Ocean Suite"), Max guest count badge, description.
    * Price per night in LKR.
    * Active Selection indicator: Blue border, light blue tint, checkmark badge "Selected ✓".
  * **Verified Guest Reviews Section:**
    * Badge: "1 review per completed stay".
    * Review cards with author name, star rating (1-5 yellow stars), and comment.
* **Right Column (1/3 width - Sticky Reservation Sidebar):**
  * Card title: "Price Summary" with dynamic total calculation for selected nights.
  * Date Pickers: Check-in Date and Check-out Date inputs.
  * Guest count and Room count number steppers.
  * **Coupon Discount Row:** Input for promo code (e.g., `WAYCATION10`) + "Apply" button.
    * Success state: Green checkmark with discount deducted (e.g., `-LKR 9,000`).
  * Primary Action: "Reserve Now" (or "Reserving with Atomic Lock..." loading state).
  * Trust Footer: Shield icon with text "Zero double-booking guarantee via PostgreSQL".

#### 3. User Actions & Interactions
* `Click Room Card` ➔ Switches selected room type, recalculates total price.
* `Change Dates / Guests` ➔ Re-computes nights and pricing breakdown.
* `Click "Apply Coupon"` ➔ Validates coupon code and applies percentage/fixed discount.
* `Click "Reserve Now"` ➔ Invokes server action to lock inventory atomically; shows green confirmation banner and redirects to `/tourist/bookings`.

#### 4. Stitch Prompt
```text
Design a hotel detail and booking page with a 2-column layout.
Top: Breadcrumb link "Back to All Hotels", hotel name "The Fortress Resort & Spa", city location Galle Fort, and a 4.9 star rating badge.
Below header: Photo gallery grid with 1 large landscape photo on the left and 2 stacked photos on the right.
Left 2/3 column:
1. About section with paragraph description.
2. Check-in (14:00) and Check-out (11:00) info box with cancellation policy.
3. "Select your room type" section containing 2 selectable room cards. Selected card has blue border and "Selected ✓" button; unselected has gray border.
4. "Verified Guest Reviews" section with user reviews showing 5-star ratings and review text.
Right 1/3 column:
Sticky floating booking summary card (white, rounded-3xl, shadow-xl). Displays large total price "LKR 76,000 for 2 nights". Includes Check-in and Check-out date pickers, Guests and Rooms selectors, a Coupon Code input with "Apply" button showing green "-10% Applied", a prominent navy blue "Reserve Now" button, and an atomic security guarantee badge with a green shield.
```

---

### Screen 04: Curated Packages Catalog (`/packages`)

#### 1. Purpose & Layout
Displays pre-arranged, multi-hotel travel itineraries curated by platform administrators, showing ordered stops, total duration, and package bundle pricing.

#### 2. Key UI Components
* **Hero Banner:** Dark gradient (Blue to Slate) with Sparkles badge, title "Curated Multi-Hotel Travel Packages", and description explaining seamless multi-destination trips.
* **Package Cards Grid (2-column layout):**
  * Full-bleed landscape cover photo with Duration Badge (e.g. `4 Days / 3 Nights`).
  * Package Title (e.g. "Southern Heritage & Coastal Serenity").
  * Narrative description.
  * **Itinerary Sequence Box (Light Gray / Slate 50 container):**
    * Label: `ITINERARY SEQUENCE (STOP ORDER):`
    * Ordered stops list with numbered blue circle badges (`1`, `2`), hotel names, destination cities, and allocated nights (e.g. "Stop 1: Jetwing Lighthouse (Galle · 2 nights)").
  * **Card Footer:**
    * Bundle Price in LKR.
    * "View & Purchase →" action button (Brand Blue with yellow arrow).

#### 3. User Actions & Interactions
* `Click "View & Purchase"` ➔ Navigates to `/packages/[id]`.

#### 4. Stitch Prompt
```text
Design a curated multi-hotel travel package catalog page.
Header: Dark blue to black gradient banner with a golden sparkles pill badge, bold white text "Curated Multi-Hotel Travel Packages", and description.
Content: 2-column grid of spacious package cards (white, rounded-3xl, shadow-md).
Each card features:
- High quality cover photo of Sri Lanka with a floating glassmorphism pill badge "4 Days / 3 Nights".
- Bold title "Southern Heritage & Coastal Escape" and short description.
- An itinerary sequence container with a gray background displaying numbered stops: Step 1 (blue circle #1) Jetwing Lighthouse (2 nights in Galle), Step 2 (blue circle #2) Fortress Resort (2 nights in Koggala).
- Card footer with "Package Bundle" label, large price "LKR 115,000", and a navy blue "View & Purchase →" button.
```

---

### Screen 05: Package Detail & Immutable Purchase (`/packages/[id]`)

#### 1. Purpose & Layout
Presents full itinerary specifics for an administrator-curated package and allows the tourist to purchase the bundle, recording an immutable JSON snapshot in PostgreSQL.

#### 2. Key UI Components
* **Header:** "Back to All Packages", Category badge "Curated Travel Bundle", Title, and Overview description.
* **Hero Image:** Full-width high-resolution panoramic image with overlay pill for Duration.
* **Ordered Itinerary Timeline:**
  * Vertical or stacked list of stops.
  * Each stop card:
    * Large numbered badge `#1`, `#2`.
    * Hotel thumbnail image.
    * Hotel name, nights count badge (e.g. `2 Nights`), city with map pin, and description.
* **Purchase Summary Container (Slate 50 background, rounded-3xl):**
  * Total Price in bold display type with "Includes all nights, resort taxes, and service fees".
  * "Purchase Curated Package" action button (with loading state "Recording Snapshot...").
  * Trust guarantees: "Immutable purchase snapshot preserved in Supabase" and "Atomic confirmation across all partner accommodations".

#### 3. User Actions & Interactions
* `Click "Purchase Curated Package"` ➔ Executes server transaction, locks package details snapshot in database, and redirects to `/tourist/bookings`.

#### 4. Stitch Prompt
```text
Design a travel package detail and checkout screen for a multi-hotel itinerary.
Top: Back button "Back to All Packages", badge "Curated Travel Bundle", headline "Wildlife & Coastal Escape - 5 Days", and hero photo.
Main section: "Ordered Multi-Hotel Itinerary" timeline with 2 detailed stop cards.
Stop Card 1: Bold blue square "#1", thumbnail photo of luxury hotel, title "Jetwing Lighthouse, Galle", "2 Nights" pill, and description.
Stop Card 2: Bold blue square "#2", thumbnail photo of safari lodge, title "Cinnamon Wild, Yala", "2 Nights" pill, and description.
Bottom section: Slate-50 checkout card with large price "LKR 165,000 Total", a large navy blue "Purchase Curated Package" button, and two security checkmarks: "Immutable purchase snapshot preserved" and "Atomic reservation across all hotels".
```

---

### Screen 06: Group Trips & Voting Hub (`/tourist/trips`)

#### 1. Purpose & Layout
The tourist's group planning hub where users view their group decision trips, generate temporary expiring invite links, and launch the Tinder-style voting room.

#### 2. Key UI Components
* **Header:**
  * Badge: `✨ Tinder-Style Discovery & Group Sessions`
  * Heading: "Group Trip Voting Hub"
  * Primary Button: `+ New Group Trip Session` (opens creation modal).
* **Trip Cards Grid (2-column layout):**
  * Destination pill (e.g. `Galle`, `Ella`) + Status pill (`OPEN` in green).
  * Trip Title (e.g. "Ella Mountain Getaway").
  * Trip Meta: Dates range, Group size & voted members count ("4 Friends · 3 Voted").
  * **Shareable Invite Box:**
    * Truncated URL in monospace font.
    * "Copy Link" / "Copied ✓" clipboard action button.
  * Card Footer: "Open Tinder-style Voting Room →" link.
* **"Create New Group Decision Session" Modal:**
  * Title input ("e.g. Ella Mountain Getaway").
  * Destination area input ("e.g. Ella").
  * Cancel and "Create & Generate Link" submit buttons.

#### 3. User Actions & Interactions
* `Click "+ New Group Trip Session"` ➔ Opens creation modal.
* `Submit Modal Form` ➔ Persists new Trip and GroupSession in database, refreshes list.
* `Click "Copy Link"` ➔ Copies `/group/{token}` to clipboard; button transforms to "Copied ✓".
* `Click "Open Tinder-style Voting Room"` ➔ Navigates to `/group/[token]`.

#### 4. Stitch Prompt
```text
Design a group trip planning and voting dashboard for friends traveling together.
Header: Amber sparkles badge "Tinder-Style Discovery & Group Sessions", title "Group Trip Voting Hub", and a navy blue button with a yellow plus icon "+ New Group Trip Session".
Content: 2-column grid of trip cards.
Each trip card has:
- Top row: Purple pill "Ella" and green pill "OPEN".
- Trip title "Ella Mountain Retreat with College Friends".
- Details: Dates "Nov 15 - Nov 18" and "4 Friends (3 Voted)".
- Shareable invitation box with a gray background displaying the link "waycation.lk/group/tkn-984..." and a navy "Copy Link" button with a share icon.
- Bottom link: "Open Tinder-style Voting Room →" in bold blue.
Modal overlay state: Centered popup dialog titled "Create New Group Decision Session" with fields for Trip Title and Destination Area, with Cancel and "Create & Generate Link" buttons.
```

---

### Screen 07: Tinder-Style Group Decision Room (`/group/[token]`)

#### 1. Purpose & Layout
A fun, intuitive swipe deck where group members vote "Like" or "Pass" on candidate hotels. Once all cards are evaluated, the system tabulates votes and displays the consensus winner.

#### 2. Key UI Components
* **View A: Swipe Card Deck:**
  * Header Badge: `✨ Tinder-Style Decision Session · Expiry in 24 Hours`.
  * Heading: "Swipe Your Preference".
  * Subtitle: "Vote Like (👍) or Pass (❌) on each candidate hotel."
  * **Candidate Hotel Card (Vertical Smartphone / Card format, rounded-3xl, shadow-2xl):**
    * Full image (top 2/3) with bottom gradient fade.
    * Overlay info: Map pin + City name in yellow, Hotel Name in large bold white, Price, and Star rating pill.
    * Card body: 3-line description snippet.
    * Card footer meta: "Card 1 of 4 · One vote per member".
  * **Floating Action Buttons (Centered below card):**
    * **Pass Button:** Circular (64px), white with rose-300 border, rose-500 `X` icon.
    * **Like Button:** Circular (80px), vibrant emerald green background, white heart `♥` icon, with hover scale effect.
* **View B: Results & Winner View (Shown after voting completes):**
  * Gold Trophy icon in circular amber badge.
  * Label: `MAJORITY CONSENSUS ACHIEVED!`.
  * Heading: "Group Decision Winner".
  * **Winning Hotel Card:**
    * Image thumbnail, "Top Voted Choice" blue tag, Hotel name, city, price.
  * **Actions:**
    * Primary CTA: "Proceed to Booking Winner →" (Brand Blue).
    * Secondary: "Vote Again" outline button.

#### 3. User Actions & Interactions
* `Click Pass (X)` ➔ Records "PASS" vote, smoothly advances to next hotel card.
* `Click Like (Heart)` ➔ Records "LIKE" vote, advances to next card.
* `Final Card Swiped` ➔ Switches instantly to Results view showing the majority winner.
* `Click "Proceed to Booking Winner"` ➔ Navigates directly to `/hotels/[winnerId]` to finalize dates and rooms.

#### 4. Stitch Prompt
```text
Design a Tinder-style group voting room for choosing a hotel with friends.
Screen header: Amber pill "Tinder-Style Decision Session · Expiry in 24 Hours", title "Swipe Your Preference", and instructions.
Centerpiece: A large vertical swipeable hotel card (white, rounded-3xl, heavy shadow, max width 420px).
Inside the card: Top half is a stunning photo of a beach resort with dark gradient overlay at the bottom showing yellow location "Galle Fort", bold white title "The Fortress Resort & Spa", price "LKR 45,000 / night", and "★ 4.8". Bottom half has descriptive text and "Card 1 of 4".
Below the card: Two large circular action buttons: A circular white button with a pink/red 'X' icon (Pass) and a larger circular emerald green button with a white Heart icon (Like).
Alternative state view (Results): A celebration card featuring a golden trophy, green text "Majority Consensus Achieved!", a card displaying the winning hotel with 3 likes, and a navy button "Proceed to Booking Winner →".
```

---

### Screen 08, 09, 10: Multi-Role Sign-In Portals (`/tourist/sign-in`, `/owner/sign-in`, `/admin/sign-in`)

#### 1. Purpose & Layout
Three distinct, clearly branded entry points for Tourist, Hotel Owner, and Platform Administrator. All authenticate securely while visually reinforcing the user's role context.

#### 2. Key UI Components & Role Styling
* **Tourist Sign-In (`/tourist/sign-in`):**
  * Brand Blue theme with Compass icon (`🎒 Tourist Portal Entry`).
  * Heading: "Welcome back, Traveller".
  * Inputs: Email Address, Password.
  * Forgot password link.
  * Submit button: "Sign In to Tourist Account" (Brand Blue with yellow arrow).
* **Hotel Owner Sign-In (`/owner/sign-in`):**
  * Indigo theme (`#4338ca`) with Hotel building icon (`🏨 Hotel Owner Portal`).
  * Heading: "Hotelier & Property Manager Access".
  * Inputs: Email Address, Password.
  * Submit button: "Access Hotelier Console" (Indigo).
* **Platform Admin Sign-In (`/admin/sign-in`):**
  * Security Red theme (`#b91c1c`) with Shield icon (`🛡️ Platform Admin Console`).
  * Heading: "Administrative Sign-In".
  * Security Notice: "Privileged console access. Accounts require server-verified admin claims."
  * Submit button: "Sign In to Admin Portal" (Dark Red).

#### 3. User Actions & Interactions
* `Submit Sign-In Form` ➔ Validates credentials, sets session cookie, redirects to role home (`/tourist/bookings`, `/owner/hotels`, or `/admin/packages`).
* `Click "Forgot password?"` ➔ Triggers Firebase password reset flow.
* `Click "Register here"` ➔ Navigates to `/sign-up`.

#### 4. Stitch Prompt
```text
Design three themed authentication login cards for a multi-role web platform:
Card 1 (Tourist): Blue theme, compass icon, title "Welcome back, Traveller", email and password inputs, forgot password link, and a navy blue "Sign In to Tourist Account" button with a yellow arrow.
Card 2 (Hotel Owner): Deep indigo theme, hotel icon, title "Hotelier & Property Manager Access", subtitle "Manage accommodations & reservations", email and password fields, and an indigo button "Access Hotelier Console".
Card 3 (Admin): Crimson red theme, security shield icon, title "Administrative Sign-In", security advisory banner "Server-verified admin credentials required", email and password fields, and a red button "Sign In to Admin Portal".
All cards should be clean white boxes with rounded-2xl corners, subtle borders, and demo credential helpers.
```

---

### Screen 11: Public User Registration (`/sign-up`)

#### 1. Purpose & Layout
Public registration page with role-selection cards allowing users to register as either a Tourist or a Hotel Owner. (Admin role is intentionally omitted to prevent unauthorized privilege escalation).

#### 2. Key UI Components
* **Header:** Compass icon, title "Create your waYcation Account", subtitle "Choose whether you are travelling or listing an accommodation."
* **Role Selection Selector (2 Cards Side-by-Side):**
  * **Option A: Tourist:**
    * Compass icon, title "Tourist", subtitle "Discover hotels & book stays".
    * Active state: Blue border, light blue tint, checkmark.
  * **Option B: Hotel Owner:**
    * Hotel icon, title "Hotel Owner", subtitle "List and manage properties".
    * Active state: Indigo border, light indigo tint, checkmark.
* **Registration Form Fields:**
  * Full Name / Display Name input.
  * Email Address input.
  * Secure Password input (with helper "At least 8 characters").
* **Submit Action:**
  * Dynamic button: "Complete Tourist Registration" (Yellow) or "Complete Hotelier Registration" (Yellow).
* **Footer:** "Already have an account? Sign In" link.

#### 3. User Actions & Interactions
* `Click Role Option` ➔ Toggles between Tourist and Hotel Owner mode.
* `Submit Registration Form` ➔ Creates Firebase Auth credentials and Postgres profile, then redirects to dashboard.

#### 4. Stitch Prompt
```text
Design a modern sign-up registration screen with role selection.
Header: Clean centered layout with compass badge and title "Create your waYcation Account".
Role selector: 2 selectable cards side-by-side:
- Card 1 (Tourist): Compass icon, title "Tourist", text "Discover hotels & book stays".
- Card 2 (Hotel Owner): Hotel icon, title "Hotel Owner", text "List and manage properties".
Selected card has a prominent colored border and checkmark circle.
Form inputs below:
- Full Name / Display Name
- Email Address
- Secure Password
Primary button: Bright warm yellow button (#febb02) with black bold text "Complete Tourist Registration".
Footer: "Already have an account? Sign In" link.
```

---

### Screen 12: Account & Profile Management (`/account`)

#### 1. Purpose & Layout
Module 1 user profile and security console. Allows users of all roles to update display names, initiate password changes, and exercise data privacy rights via compliant account deletion.

#### 2. Key UI Components
* **Page Header:** "Account & Profile Settings" with Module 1 indicator.
* **Card 1: Personal Profile:**
  * Display Name: Editable text input.
  * Email Address: Read-only disabled field with caption "Managed securely by Firebase Authentication".
  * Assigned Role: Read-only badge (e.g. `TOURIST` or `HOTEL_OWNER` or `ADMIN`) with disclaimer that self-service role escalation is strictly blocked.
  * "Save Profile Changes" primary action button.
* **Card 2: Security & Password Flow:**
  * KeyRound icon, explanation that passwords are never stored in plain application tables.
  * "Send Password Reset Email" secondary action button.
* **Card 3: Danger Zone — Account Deletion:**
  * Red-tinted warning container with alert octagon icon.
  * Explanation: Deletion permanently anonymizes personal data while preserving historical booking and audit records.
  * "Request Permanent Account Deletion" red button.

#### 3. User Actions & Interactions
* `Edit Display Name + Save` ➔ Updates profile in Supabase; shows green success toast.
* `Click "Send Password Reset Email"` ➔ Triggers Firebase recovery email.
* `Click "Request Permanent Account Deletion"` ➔ Prompts confirmation dialog; upon confirmation, anonymizes records, clears auth cookies, and redirects to home.

#### 4. Stitch Prompt
```text
Design an Account & Profile Settings page with 3 stacked cards.
Card 1 (Personal Profile): White card with editable "Display Name" field, disabled "Email Address" field with lock icon, a read-only role badge "TOURIST" in blue, and a navy blue "Save Profile Changes" button.
Card 2 (Security & Password): White card explaining that passwords are encrypted in Firebase Authentication, with a gray outline button "Send Password Reset Email".
Card 3 (Danger Zone): Soft red background (#fef2f2) with red border (#fecdd3). Features a warning icon, header "Danger Zone — Account Deletion", explanation that personal info will be anonymized while financial audit logs are preserved, and a crimson button "Request Permanent Account Deletion".
```

---

### Screen 13: Tourist Booking Management (`/tourist/bookings`)

#### 1. Purpose & Layout
Module 3 tourist reservations manager. Displays booking history, live reservation statuses, cancellation policy snapshots, and provides reschedule and cancel workflows.

#### 2. Key UI Components
* **Header:** "My Bookings & Reservations" with "Explore More Hotels" link.
* **Booking Cards List:**
  * Left: Property photo with floating status badge:
    * `CONFIRMED` (Green)
    * `COMPLETED` (Blue)
    * `CANCELLED` (Red)
  * Right:
    * Hotel name and Room type name.
    * Dates range: `2026-11-01 → 2026-11-03`.
    * Total price paid/reserved (e.g. `LKR 76,000`).
    * Guests count and city.
    * Policy snapshot callout box: "Free cancellation up to 48 hours prior...".
  * **Card Actions (Status-Dependent):**
    * When `CONFIRMED`: "Reschedule Dates" outline button and "Cancel Booking" red-tinted button.
    * When `COMPLETED`: "Leave Verified Review →" yellow button.
* **Reschedule Modal:**
  * Dialog titled "Reschedule Booking Dates".
  * New Check-in date picker, New Check-out date picker.
  * "Cancel" and "Confirm Reschedule" buttons.

#### 3. User Actions & Interactions
* `Click "Cancel Booking"` ➔ Displays confirmation prompt; upon confirmation, executes atomic cancellation in database and restores room inventory.
* `Click "Reschedule Dates"` ➔ Opens reschedule modal.
* `Submit Reschedule Modal` ➔ Validates room availability for new dates and recalculates pricing.
* `Click "Leave Verified Review"` ➔ Navigates to `/tourist/reviews`.

#### 4. Stitch Prompt
```text
Design a Tourist Bookings and Reservations list page.
Header: "My Bookings & Reservations" with an "Explore More Hotels →" button.
Content: Stack of horizontal booking cards.
Card 1 (Active/Confirmed): Left side shows hotel photo with a green pill badge "CONFIRMED". Right side shows hotel name "The Fortress Resort", room type "Deluxe Ocean Suite", dates "Nov 01 - Nov 03", guests "2 Adults", price "LKR 76,000", and a gray policy snapshot note. Action buttons on bottom right: "Reschedule Dates" (white outline) and "Cancel Booking" (soft red button).
Card 2 (Completed): Shows hotel photo with blue badge "COMPLETED". Bottom right has a bright yellow button "Leave Verified Review →".
Modal state: Small popup for rescheduling with New Check-in and Check-out date inputs.
```

---

### Screen 14: Tourist Review Management (`/tourist/reviews`)

#### 1. Purpose & Layout
Module 4 verified reviews manager. Enforces the business rule that reviews are exclusively available for completed stays (1 review per booking) with editing and deletion capabilities.

#### 2. Key UI Components
* **Header:** "My Hotel Reviews" with badge "Module 4: Tourist Review Management".
* **Review Cards List:**
  * Top bar: Hotel Name, Booking reference tag (`#b4e9...`), submission date, and status pill (`PUBLISHED` in green).
  * 5-Star Rating visualization (Gold stars).
  * Review comment box (italicized text in rounded slate container).
  * Actions: "Edit Review" button (with pencil icon) and "Delete" button (with trash icon).
* **Edit Review Modal:**
  * Star rating selector dropdown (1 to 5 stars).
  * Comment textarea.
  * "Cancel" and "Update in Supabase" buttons.

#### 3. User Actions & Interactions
* `Click "Edit Review"` ➔ Opens edit modal with pre-populated rating and comment.
* `Submit Edit Form` ➔ Updates review record in database and refreshes public rating aggregates.
* `Click "Delete"` ➔ Removes review with user confirmation.

#### 4. Stitch Prompt
```text
Design a verified hotel reviews management page for a traveler.
Header: "My Hotel Reviews" with subtitle "Manage ratings and comments for your completed stays. 1 review per verified booking."
Content: List of review cards (white, rounded-2xl, shadow-sm).
Each review card displays:
- Hotel title "Jetwing Lighthouse", verified stay reference "#a89f32", date "October 2026", and a green pill badge "PUBLISHED".
- 5 yellow stars display.
- Comment quote box with a light gray background: "Exceptional ocean views, courteous staff, and the infinity pool was pristine."
- Bottom right buttons: "Edit Review" (gray outline with edit icon) and "Delete" (soft red pill with trash icon).
Modal: "Edit Your Review" dialog with a 1-5 star dropdown and textarea for the comment.
```

---

### Screen 15: Hotel Owner Accommodations List (`/owner/hotels`)

#### 1. Purpose & Layout
Module 2 management dashboard for hotel owners to inspect their properties, review total room capacities, check booking numbers, and safely deactivate or reactivate listings.

#### 2. Key UI Components
* **Header:** Indigo category badge `Module 2: Hotel Owner Listing Management`, Title "My Accommodations", and action buttons: "Guest Bookings" and `+ Add New Hotel` (Indigo button).
* **Accommodations Grid (2-column layout):**
  * Property Photo with status badge (`ACTIVE` green / `INACTIVE` red).
  * Hotel name, address, and city.
  * Stats Box (2 columns): "Total Rooms" (e.g. `11 Rooms`) and "Bookings" (e.g. `18 Total`).
  * Card Footer:
    * "Public View" link (eye icon).
    * Deactivation toggle button: "Deactivate (Archive)" in soft red or "Reactivate" in soft green.

#### 3. User Actions & Interactions
* `Click "+ Add New Hotel"` ➔ Navigates to `/owner/hotels/new`.
* `Click "Guest Bookings"` ➔ Navigates to `/owner/bookings`.
* `Click "Deactivate (Archive)"` ➔ Sets hotel status to `INACTIVE`; safely prevents new bookings while retaining all past reservation history.

#### 4. Stitch Prompt
```text
Design a hotel owner accommodation management dashboard.
Header: Indigo badge "Module 2: Hotel Owner Listing Management", title "My Accommodations", and two buttons: "Guest Bookings" (outline) and "+ Add New Hotel" (vibrant indigo button #4338ca).
Content: 2-column grid of property cards.
Each card includes:
- Top photo with a green pill badge "ACTIVE".
- Hotel title "The Fortress Resort & Spa" and address "Galle, Sri Lanka".
- Two statistic metric boxes side-by-side: "Total Rooms: 14 Rooms" and "Bookings: 24 Total".
- Card footer: "Public View" link with an eye icon, and a button "Deactivate (Archive)" in soft red to unpublish the listing.
```

---

### Screen 16: Hotel Owner Add New Hotel Form (`/owner/hotels/new`)

#### 1. Purpose & Layout
Comprehensive property registration form for hotel owners to define hotel details, check-in/out hours, cancellation policies, photo URLs, and dynamic room types with pricing.

#### 2. Key UI Components
* **Navigation:** "Back to My Hotels" text link.
* **Form Container (White, rounded-2xl, shadow-sm):**
  * **Section 1: General Information:**
    * Hotel Name input ("e.g. Jetwing Lighthouse").
    * City / Region input ("e.g. Galle").
    * Street Address input.
    * Property Description textarea.
  * **Section 2: Room Types & Indicative Pricing (Dynamic List):**
    * Header with `+ Add Another Room Type` text button.
    * Room Type Row: Room Name, Max Guests stepper, Price per Night (LKR), Total Rooms count, and Delete trash button.
  * **Section 3: Policies & Times:**
    * Check-in Time input (`14:00`) and Check-out Time input (`11:00`).
    * Cancellation Policy snapshot textarea.
  * **Section 4: Media:**
    * Featured Cover Image URL input with preview icon.
  * **Submit Button:** Full-width "Publish Hotel Listing" button in indigo.

#### 3. User Actions & Interactions
* `Click "+ Add Another Room Type"` ➔ Appends a new editable room tier row.
* `Click Trash Icon on Room Row` ➔ Removes that room tier.
* `Submit Form` ➔ Validates inputs, writes hotel + room types + inventory to database, and redirects to `/owner/hotels`.

#### 4. Stitch Prompt
```text
Design an "Add New Hotel Listing" form for hotel owners.
Header: Breadcrumb "Back to My Hotels", title "Add New Hotel Listing", and subtitle.
Form container (white card with clean sections):
1. General Information: Fields for Hotel Name, City, Street Address, and Description textarea.
2. Room Types & Pricing: Section showing an editable row with Room Name ("Deluxe Ocean Suite"), Max Guests (2), Price per Night (LKR 38,000), and Total Rooms (8), with a "+ Add Another Room Type" button.
3. Policies & Times: 2-column inputs for Check-in Time ("14:00") and Check-out Time ("11:00"), followed by Cancellation Policy text.
4. Featured Cover Image URL input.
Bottom: Full-width vibrant indigo button "Publish Hotel Listing".
```

---

### Screen 17: Hotel Owner Guest Bookings Dashboard (`/owner/bookings`)

#### 1. Purpose & Layout
Allows hotel owners to review incoming guest reservations across their properties, inspect dates, guest names, total payouts, and fulfillment statuses.

#### 2. Key UI Components
* **Header:** "Back to My Properties" link, Title "Guest Reservations", subtitle.
* **Bookings Table / Card List:**
  * Property badge (e.g. `Jetwing Lighthouse`) + Room Type name.
  * Guest info: User icon, Guest Name (e.g. `Kasun Fernando`), Guest Count.
  * Dates: Calendar icon with Check-in and Check-out dates.
  * Financials: Total Payout amount in bold black (e.g. `LKR 76,000`).
  * Status badge: Emerald pill `CONFIRMED`.

#### 3. User Actions & Interactions
* `Read-only overview` of all customer bookings assigned to the owner's properties.

#### 4. Stitch Prompt
```text
Design a hotel owner's Guest Bookings dashboard.
Header: Back link "Back to My Properties", title "Guest Reservations", and subtitle.
Main card: Table-style list of guest reservations.
Each row displays:
- Property name in an indigo pill "The Fortress Resort" and room name "Ocean Suite".
- Guest name "Kasun Fernando (2 guests)" with user icon.
- Dates "Nov 01, 2026 to Nov 03, 2026" with calendar icon.
- Payout amount "LKR 76,000" in bold.
- Status pill "CONFIRMED" in green.
Clean, spacious layout with subtle divider lines.
```

---

### Screen 18: Admin Curated Packages Builder (`/admin/packages`)

#### 1. Purpose & Layout
Module 5 administrator console for creating, managing, and publishing multi-hotel travel itineraries with ordered stops, pricing, and purchase metrics.

#### 2. Key UI Components
* **Header:** Red category badge `Module 5: Admin Travel Package Management`, Title "Curated Travel Packages", "Manage Coupons →" link, and `+ Create Travel Package` red button.
* **Packages List:**
  * Package Title, Status pill (`PUBLISHED` in green).
  * Meta: Duration in Days/Nights, Tourist Purchases Count.
  * Bundle Price in large bold text.
  * **Ordered Itinerary Stops Box:**
    * Numbered badges showing itinerary order: `#1 Hotel A (2 nights)`, `#2 Hotel B (2 nights)`.
  * Card Footer: "View Public Package Page →" link.
* **Create Package Modal:**
  * Package Title input.
  * Duration (Days) input.
  * Price Total (LKR) input.
  * "Save to Supabase" button.

#### 3. User Actions & Interactions
* `Click "+ Create Travel Package"` ➔ Opens creation modal.
* `Submit Modal` ➔ Saves package and joins hotel stops in PostgreSQL; refreshes catalog.

#### 4. Stitch Prompt
```text
Design an administrative Travel Packages Management dashboard.
Header: Crimson red badge "Module 5: Admin Travel Package Management", title "Curated Travel Packages", and a red button "+ Create Travel Package".
Content: Stack of package cards.
Each card displays:
- Title "Southern Wildlife & Coastal Escape" with green pill "PUBLISHED".
- Subtitle: "4 Days / 3 Nights · 12 Tourist Purchases".
- Package Bundle Price "LKR 115,000".
- Itinerary Sequence container showing ordered stops: Step 1 (red circle #1) Jetwing Lighthouse (2 nights), Step 2 (red circle #2) Fortress Resort (2 nights).
- Public preview link.
Modal dialog state: "Create Curated Multi-Hotel Package" with title, duration, and price inputs.
```

---

### Screen 19: Admin Coupons & Discounts Dashboard (`/admin/coupons`)

#### 1. Purpose & Layout
Module 6 administrator console for configuring promotional discount codes, setting global usage limits, minimum order spend thresholds, and deactivating expired promotions.

#### 2. Key UI Components
* **Header:** Red badge `Module 6: Admin Coupon Management`, Title "Promotional Coupons & Discounts", "Manage Packages →" link, and `+ Create Coupon Code` red button.
* **Coupons Table / List:**
  * Coupon Code badge: Monospace bold font in light gray box (e.g. `WAYCATION10`).
  * Status badge (`ACTIVE` green / `INACTIVE` gray).
  * Discount Value: `10%` (PERCENTAGE) or `LKR 5,000` (FIXED).
  * Minimum Spend threshold (e.g. `Min Spend: LKR 20,000`).
  * Expiration date.
  * **Usage Progress Metric:** Displaying redemption tally (e.g. `14 / 50 Used`).
  * Action: "Deactivate" / "Activate" toggle button.
* **Create Coupon Modal:**
  * Coupon Code text input (automatically converts to uppercase).
  * Discount Type dropdown (Percentage `%` vs Fixed Amount `LKR`).
  * Discount Value input.
  * Global Usage Limit number input.
  * "Register Coupon Code" submit button.

#### 3. User Actions & Interactions
* `Click "+ Create Coupon Code"` ➔ Opens creation modal.
* `Submit Coupon Modal` ➔ Registers code in database with atomic redemption constraints.
* `Click "Deactivate / Activate"` ➔ Toggles coupon status immediately.

#### 4. Stitch Prompt
```text
Design an administrative Coupon and Discount management dashboard.
Header: Crimson red badge "Module 6: Admin Coupon Management", title "Promotional Coupons & Discounts", and a red button "+ Create Coupon Code".
Content: Clean card containing a table of coupons.
Row 1: Code badge "WAYCATION10" in bold monospace font, green pill "ACTIVE", details "Discount: 10% (PERCENTAGE) · Min Spend: LKR 20,000 · Expires: 12/31/2026", usage progress "18 / 50 Used", and a "Deactivate" outline button.
Row 2: Code badge "WELCOME5000", green pill "ACTIVE", details "Discount: LKR 5,000 (FIXED)", usage progress "5 / 100 Used", and "Deactivate" button.
Modal overlay state: "Register New Coupon Code" with inputs for Code, Type (Percentage/Fixed), Value, and Global Usage Limit.
```

---

### Screen 20: Global Navigation Header & Footer Component

#### 1. Purpose & Layout
Reusable header and footer components providing consistent branding, role switching, mobile drawer navigation, and platform legal information.

#### 2. Key UI Components
* **Navbar (`Navbar.tsx`):**
  * Left: Brand logo with compass icon in yellow box: `waYcation.` with "2.0 Relational" tag.
  * Center: Navigation links: "Hotels", "Travel Packages", "Group Discovery".
  * Right:
    * "Sign In As" dropdown button with hover popover revealing:
      * 🎒 Tourist Sign In
      * 🏨 Hotel Owner Sign In
      * 🛡️ Admin Console
    * "Register" prominent yellow button.
  * Mobile view: Hamburger menu expanding to full drawer navigation.
* **Footer (`Footer.tsx`):**
  * Brand column: Summary of Booking.com-inspired experience with atomic SQL integrity.
  * Navigation column: Direct links to discovery, packages, and group tools.
  * Portals column: Direct entry points for all three user roles.
  * Copyright & Tech Stack notice: Next.js App Router, Firebase Auth, Supabase PostgreSQL, Firebase App Hosting.

#### 3. Stitch Prompt
```text
Design a global website header and footer for "waYcation 2.0".
Header:
- Navy blue bar (#003580), height 64px, sticky top with shadow.
- Left: Logo with yellow compass square icon and bold white text "waYcation.".
- Center: Clean white text links with hover underline: "Hotels", "Travel Packages", "Group Discovery".
- Right: A "Sign In As" dropdown pill button with a user icon that shows a menu for Tourist, Hotel Owner, and Admin, and a bright yellow "Register" button.
Footer:
- Dark slate navy footer (#0f172a) with 4 columns: Brand mission, Explore links, Portals, and Technical Architecture summary (Next.js, Firebase Auth, Supabase PostgreSQL).
```

---

## 🚀 How to Use These Specs in Stitch & Bring Over to Code

1. **Step 1: Feed Prompts to Stitch**
   * Copy the exact prompt block from any screen above into Stitch.
   * Review the generated layout and tweak spacing or visual components.
2. **Step 2: Export Assets & Layout Specs**
   * Export the component hierarchy, color codes, and visual placements.
3. **Step 3: Map to Existing Codebase Routes**
   * Use the Route / URL column in the Master Table to match each Stitch screen directly to its corresponding Next.js route in `src/app/`.
4. **Step 4: Refine & Connect State**
   * The server actions for all 6 CRUD modules, group voting, and bookings already exist in `src/app/actions/` and `src/server/services/`.
   * Bind the UI inputs and buttons to the existing server actions (`createBookingAction`, `createTripAction`, etc.) for instant end-to-end functionality.
