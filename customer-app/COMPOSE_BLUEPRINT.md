# AasPaasWala Customer App — Jetpack Compose Implementation Blueprint

> This document maps the Figma UI Specification directly to the existing Jetpack Compose codebase.
> Each section references exact file paths and line numbers for implementation.

---

## 1. Architecture Overview

```
com.aaspaas.customer
├── core/
│   ├── database/          # Room entities, DAOs, mappers
│   ├── location/          # FusedLocationProvider wrapper
│   ├── network/           # ApiResult, AuthInterceptor, token provider
│   ├── notifications/     # Firebase messaging
│   └── ui/
│       ├── components/    # CommonComponents.kt (shimmer, badges, states)
│       └── theme/         # Color.kt, Typography.kt, Theme.kt
├── data/
│   ├── model/             # Mappers.kt (DTO → Domain)
│   ├── remote/            # API service interfaces + DTOs
│   └── repository/        # Repository implementations
├── domain/
│   ├── model/             # Models.kt (domain entities)
│   └── repository/        # Repository interfaces
├── di/                    # Hilt modules
├── feature/
│   ├── auth/              # Login, OTP
│   ├── home/              # Splash, Onboarding, Home
│   ├── location/          # Location permission
│   ├── product/           # Product detail
│   ├── reservation/       # Confirm, Detail, History
│   ├── search/            # Search, Filter, Sort
│   ├── settings/          # Settings screen
│   ├── store/             # Store detail
│   ├── wishlist/          # Wishlist
│   └── notifications/     # Notifications list
└── navigation/            # AppNavGraph.kt, Screen.kt
```

---

## 2. Design System → Compose Mapping

### 2.1 Colors

**Spec: §4 — Brand Colors**

Existing implementation in `core/ui/theme/Color.kt`:

| Figma Token       | Spec Value   | Compose Constant | File:Line                  |
|-------------------|-------------|------------------|---------------------------|
| Primary           | `#30306F`   | `Primary`        | Color.kt:6                |
| Primary Dark      | `#22234F`   | `PrimaryDark`    | Color.kt:7                |
| Accent            | `#C8664D`   | `Accent`          | Color.kt:8                |
| Background        | `#FFFDF8`   | `Background`      | Color.kt:11               |
| Surface           | `#FFFFFF`   | `Surface`        | Color.kt:12               |
| Text Primary      | `#171717`   | `TextPrimary`    | Color.kt:13               |
| Text Secondary    | `#6B6B6B`   | `TextSecondary`  | Color.kt:14               |
| Border            | `#E7E4DE`   | `Border`          | Color.kt:15               |
| Success           | `#25855A`   | `Success`         | Color.kt:18               |
| Warning           | `#D89024`   | `Warning`         | Color.kt:19               |
| Error             | `#D64545`   | `Error`           | Color.kt:20               |

**Compose usage** — import `com.aaspaas.customer.core.ui.theme.*`:

```kotlin
// CTA buttons
Button(colors = ButtonDefaults.buttonColors(containerColor = Primary))

// Accent for offers/reservation highlights
Surface(color = Accent.copy(alpha = 0.08f))

// Text
Text("label", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
```

### 2.2 Typography

**Spec: §5 — Typography (Inter)**

Existing implementation in `core/ui/theme/Typography.kt`:

```kotlin
// Current: uses FontFamily.Default; swap with Inter font when available
val InterFontFamily = FontFamily.Default

val AppTypography = Typography(
    displayLarge  = 32.sp / Bold   // Display
    displayMedium = 28.sp / Bold   // H1
    displaySmall  = 24.sp / Bold   // H2
    headlineMedium = 20.sp / SemiBold  // H3
    headlineSmall  = 18.sp / SemiBold  // Title
    bodyLarge      = 16.sp / Regular   // Body
    bodyMedium     = 14.sp / Regular   // Body Small
    bodySmall      = 12.sp / Medium    // Caption
    labelLarge     = 14.sp / SemiBold  // Button
)
```

**Action required:** Add Inter font files to `res/font/` and update `InterFontFamily`:

```kotlin
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)
```

### 2.3 Spacing & Radius

**Spec: §6 — Spacing System (4px grid)**

Existing in `core/ui/theme/Theme.kt`:

```kotlin
val LocalSpacing = staticCompositionLocalOf { AppSpacing() }

data class AppSpacing(
    xs: 4.dp, sm: 8.dp, md: 12.dp, lg: 16.dp, xl: 20.dp,
    xxl: 24.dp, xxxl: 32.dp, huge: 40.dp, massive: 48.dp, giant: 64.dp
)

data class AppRadius(
    sm: 8.dp, md: 12.dp, lg: 16.dp, xl: 20.dp, full: 9999.dp
)
```

Usage in Compose:

```kotlin
val spacing = LocalSpacing.current
val radius = LocalRadius.current
Modifier.padding(horizontal = spacing.lg, vertical = spacing.md)
RoundedCornerShape(radius.md)  // 12dp card radius
```

---

## 3. Global Components → Compose Mapping

**Spec: §53 — Global Components**

All components live in `core/ui/components/CommonComponents.kt`.

| Figma Component         | Spec Section | Compose Function      | File:Line         |
|------------------------|-------------|----------------------|-------------------|
| Button / Primary       | §54        | `Button(colors=...)` | (use Material3)  |
| Button / Secondary     | §54        | `OutlinedButton(...)`| (use Material3)  |
| Button / Text          | §54        | `TextButton(...)`    | (use Material3)  |
| Input / Default        | §5        | `OutlinedTextField`  | (use Material3)  |
| SearchBar / Default    | —          | `HomeSearchBar()`    | HomeScreen.kt:303 |
| SearchBar / Active     | —          | Search bar in SearchScreen | SearchScreen.kt:65 |
| Badge / Available       | —          | `AvailableBadge()`   | CommonComponents.kt:227 |
| Badge / BestPrice       | —          | `BestPriceBadge()`   | CommonComponents.kt:239 |
| Badge / PRO             | —          | `ProBadge()`         | CommonComponents.kt:251 |
| Badge / Closed          | —          | `ClosedBadge()`      | CommonComponents.kt:263 |
| Skeleton / Product      | —          | `ProductCardShimmer()`| CommonComponents.kt:300 |
| Skeleton / Store        | —          | `StoreCardShimmer()`  | CommonComponents.kt:323 |
| Skeleton / Reservation  | —          | `ReservationCardShimmer()`| CommonComponents.kt:345 |

**BottomSheet** — use `ModalBottomSheet` from Material3:

```kotlin
// See: feature/search/ui/SearchFilterSheet.kt:28
@OptIn(ExperimentalMaterial3Api::class)
ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = Surface,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
) { /* sheet content */ }
```

---

## 4. Screen-by-Screen Mapping

### 4.1 Splash Screen

**Spec: §8**

**File:** `feature/home/ui/SplashScreen.kt:22`

```kotlin
@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
)
```

| Spec Element        | Implementation              |
|---------------------|----------------------------|
| Logo icon (72x72)   | `Box(Modifier.size(72.dp), Primary, RoundedCornerShape(20.dp))` |
| "AasPaasWala"       | `Text("AasPaasWala", style = headlineMedium, color = Primary)` |
| "Find it nearby."   | `Text("Find it nearby.", style = bodyLarge, color = onSurfaceVariant)` |
| 1.5s delay           | `LaunchedEffect(destination) { delay(1500) }` |
| Navigation logic    | Checks auth state → HOME or ONBOARDING→AUTH |

### 4.2 Onboarding

**Spec: §9**

**File:** `feature/home/ui/OnboardingScreen.kt:28`

Three pages with emoji visuals + dot indicators:
- Page 1: "Find what you need nearby." → emoji `🔍`
- Page 2: "Compare nearby stores" → emoji `🏪` (with store comparison visual)
- Page 3: "Reserve. Visit. Done." → emoji `✅` (with reservation concept)

CTA button text changes by page: "Get Started" → "Next" → "Start Exploring"

### 4.3 Login / OTP

**Spec: §10–§11**

**File:** `feature/auth/ui/AuthScreens.kt`

**Gap:** Spec uses phone input `+91 | Enter mobile number`, but current implementation uses email input.

**Spec: §10 Login** maps to `PhoneLoginScreen()` (AuthScreens.kt:55):
```
← (back button — not shown in current for new user flow)
"Welcome to AasPaasWala"
"Discover products available near you."
Email input field (should be phone: "+91  Enter mobile number")
"Continue" (primary, 52dp height)
"──────── OR ────────"
"Continue with Google" (social)
"By continuing, you agree to Terms & Privacy Policy."
```

**Spec: §11 OTP** maps to `OtpScreen()` (AuthScreens.kt:174):
```
← (back arrow)
"Verify your number"
"Enter the 6-digit code sent to +91 XXXXX XXXXX"
[ _ ][ _ ][ _ ][ _ ][ _ ][ _ ]  — OtpInputRow()
"Resend code in 00:24" or "Resend Code" link
"Verify" (primary button)
```

**To implement phone login per spec:** Replace email input with phone field using `CountryCodeTextInput` or masked input. Update `AuthViewModel` to call `requestPhoneOtp` / `verifyPhoneOtp` instead of email variants.

### 4.4 Location Permission

**Spec: §12**

**File:** `feature/location/ui/LocationPermissionScreen.kt:15`

```kotlin
@Composable
fun LocationPermissionScreen(
    onEnable: () -> Unit,
    onSkip: () -> Unit
)
```

| Spec Element          | Implementation |
|----------------------|----------------|
| Location illustration | `Surface(160dp, Primary.copy(0.08f), RoundedCornerShape(32.dp))` with `📍` |
| Heading              | "Find products around you" |
| Description          | "Allow location access to discover stores..." |
| "Enable Location"    | Primary button, `Modifier.height(52.dp)` |
| "Maybe later"        | TextButton |

### 4.5 Home Screen

**Spec: §13–§17**

**File:** `feature/home/ui/HomeScreen.kt:41`

This is the most important screen. Current implementation includes:

**Header (§13):** `HomeHeader()` (HomeScreen.kt:252)
```
Good morning, 👋                    🔔
📍 Jaipur
```

**Search Bar:** `HomeSearchBar()` (HomeScreen.kt:303)
```
🔍 Search products, brands or stores
```

**Quick Categories:** `QuickCategories()` (HomeScreen.kt:343)
```
Fashion  Electronics  Grocery  Beauty  Home  Sports  More
```

**Offers Banner:** `OffersBanner()` (HomeScreen.kt:389)
```
🏷️ Up to 30% OFF   Local stores near you   Explore →
```

**Available near you:** `NearbyProductCard()` (HomeScreen.kt:441)
```
┌───────────────────────────┐
│       PRODUCT IMAGE       │
├───────────────────────────┤
│ Nike Running Shoes        │
│ ₹2,499                    │
│ 3 stores nearby           │
│ [ View Stores ]           │
└───────────────────────────┘
```

**Stores around you:** `NearbyStoreCard()` (HomeScreen.kt:518)
```
┌────────────────────────────┐
│ ABC Fashion                │
│ ⭐ 4.4   •  1.2 km • Open  │
│ 243 products available     │
│ View Store →               │
└────────────────────────────┘
```

**Bottom Navigation:** `HomeBottomNav()` (HomeScreen.kt:87)
```
Home  Explore  Reservations  Wishlist  Profile
```

**Gap:** "Recently Viewed" section (§17) is not implemented. Add conditionally when user history exists:

```kotlin
// Add after Stores section in HomeContent()
if (state.recentlyViewed.isNotEmpty()) {
    item {
        HomeSectionHeader("Recently viewed")
        LazyRow(...) { items(state.recentlyViewed) { ProductCard.Horizontal(...) } }
    }
}
```

### 4.6 Search

**Spec: §18–§22**

**File:** `feature/search/ui/SearchScreen.kt`

**Search Bar (as top nav):** `SearchScreen()` (SearchScreen.kt:32)
```
←  🔍 Search products...    (persistent search bar at top)
```

**Recent Searches:** `RecentSearches()` (SearchScreen.kt:128)
```
Recent searches
👟 Running shoes       ×
👕 Black shirt         ×
🎧 Earphones           ×
Clear all
```
Uses `SearchHistoryDao` for persistence (see `core/database/dao/SearchHistoryDao.kt`).

**Search Suggestions:** `SearchSuggestions()` (SearchScreen.kt:183)
```
running
🔍 running shoes
🔍 running jacket
🔍 running shorts
Products   |  Stores
Nike Running Shoes
```

**Gap:** Spec distinguishes Products / Brands / Stores / Categories. Current implementation only shows Products and Stores. Add Brands and Categories suggestion sections.

**Search Results:** `SearchResults()` (SearchScreen.kt:209)
```
← Search
"Running Shoes"      243 results
Filter  Sort  Distance  (horizontal chips)
┌──────────┐ ┌──────────┐  ← 2-column grid
│ Product  │ │ Product  │
└──────────┘ └──────────┘
```

**Gap:** Search bar header shows `← Search` but current SearchScreen is a separate route without back context in the header. The results use a LazyColumn list, not a 2-column grid as per spec. Update `SearchResults` to use `LazyVerticalGrid(GridCells.Fixed(2))`.

### 4.7 Filter & Sort Bottom Sheets

**Spec: §21–§22**

**File:** `feature/search/ui/SearchFilterSheet.kt`

**FilterBottomSheet** (SearchFilterSheet.kt:28) — includes:
- Category radio buttons
- Price RangeSlider (₹0 → ₹10,000)
- Distance radio buttons (1km, 3km, 5km, 10km)
- Availability checkbox ("Available now")
- Reset button
- Apply: "Show N Results"

**SortBottomSheet** (SearchFilterSheet.kt:197) — options:
- Relevance (default)
- Distance
- Price: Low to High
- Price: High to Low
- Rating
- Recently Added

**SearchFilterBar** (SearchFilterSheet.kt:243) — the inline filter bar shown above results:
```
Filter  Sort  Distance
```

### 4.8 Product Detail

**Spec: §23–§27**

**File:** `feature/product/ui/ProductDetailScreen.kt:31`

```kotlin
@Composable
fun ProductDetailScreen(
    productId: String,
    variantId: String,
    onReserveClick: (String) -> Unit,
    onStoreClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
)
```

**Top Bar (§23):**
```
←                         ♡   ⋮
```
Implemented in `Scaffold(topBar=...)` (ProductDetailScreen.kt:106).

**Image Carousel (§23):**
```
        PRODUCT IMAGE
       ● ○ ○ ○
```
Currently uses placeholder icon with dots. To implement with real images, use `Pager` + `Coil`'s `AsyncImage`.

**Product Info (§23):**
```
Nike Running Shoes
⭐ 4.5 (124)
₹2,499
✓ Available nearby
```
Implemented in `ProductDetailContent` (ProductDetailScreen.kt:93).

**Variants (§24):** `VariantSelector()` (ProductDetailScreen.kt:414)
- Size chips: `[ 6 ] [ 7 ] [ 8 ] [ 9 ] [ 10 ]`
- Color chips: `[ Black ] [ White ] [ Blue ]`
- Selected uses `Primary` border + `Primary.copy(0.08f)` background

**Available Near You (§25 — Signature Section):** `AvailableNearYouSection()` (ProductDetailScreen.kt:254)

```
Available near you
┌──────────────────────────┐
│ ABC Sports               │
│ ⭐ 4.5                    │
│                            │
│ ₹2,399                    │
│ 📍 0.8 km                 │
│ ✓ Available now           │
│ [ Reserve ]               │
└──────────────────────────┘
```

**Price Comparison (§26):** Not yet implemented as a dedicated horizontal list. Add after "Available Near You":

```kotlin
// TODO: Implement PriceComparisonSection showing 3-4 stores with BEST PRICE badge
```

**Expandable Details (§27):** `ExpandableSection()` (ProductDetailScreen.kt:484)
```
Product Details       ›
Specifications        ›
Description            ›
```

### 4.9 Store Detail

**Spec: §28–§31**

**File:** `feature/store/ui/StoreDetailScreen.kt:31`

```kotlin
@Composable
fun StoreDetailScreen(
    storeId: String,
    onProductClick: (String, String) -> Unit,
    onBack: () -> Unit,
    viewModel: StoreDetailViewModel = hiltViewModel()
)
```

**Store Hero (§28):**
```
[ Store Cover Image ]
ABC Sports
⭐ 4.5
Open • Closes 9 PM    📍 1.2 km
```
Implemented in `StoreDetailContent` (StoreDetailScreen.kt:97).

**Action Buttons (§28):**
```
[ Directions ] [ Call ]
```
Implemented in StoreDetailScreen.kt:191. Uses `Intent(ACTION_VIEW, geo:...)` and `Intent(ACTION_DIAL, tel:...)`.

**Store Information (§29):** About text + hours. Partially implemented.

**Gap:** Weekly hours expand → `View weekly hours` is not implemented. Add `ExpandableSection` for hours.

**Store Products (§30):**
```
Products (N)
🔍 Search in this store
Shoes  Clothing  Accessories  Sports  (category chips)
[ Product grid ]
```
Currently shows product list as `LazyColumn` items. Update to support store-internal search and category chips.

**Store Map (§31):** Not implemented. Add a "Store Location" screen:

```kotlin
// TODO: feature/store/ui/StoreMapScreen.kt
@Composable
fun StoreMapScreen(store: Store, onBack: () -> Unit) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberCameraPositionState {
            center = LatLng(store.latitude, store.longitude)
        }
    ) {
        Marker(position = LatLng(store.latitude, store.longitude))
    }
}
```

### 4.10 Reservation Flow

**Spec: §32–§34**

**File:** `feature/reservation/ui/ReservationScreens.kt`

**Reservation Confirm (§32):** `ReservationConfirmScreen()` (ReservationScreens.kt:36)

**Spec requires:** Product name + store name + price shown dynamically.
**Gap:** Current implementation hardcodes "Nike Running Shoes" / "ABC Sports" / "₹2,399". Wire to actual product/variant data from `ReservationConfirmViewModel`.

```kotlin
// ReservationConfirmViewModel.load() should fetch product by variantId
// Then display dynamic data:
Text(state.product?.name ?: "Loading...", style = MaterialTheme.typography.titleMedium)
Text(state.product?.store?.name ?: "", style = MaterialTheme.typography.bodySmall)
Text("₹${state.product?.variants?.find { it.id == variantId }?.price?.toInt() ?: 0}", ...)
```

**Reservation Confirmation (§33):** `ReservationSuccessScreen()` (ReservationScreens.kt:247)

```
✓
Reservation confirmed
Nike Running Shoes
ABC Sports
Reserved until  Today, 8:30 PM
[ View Reservation ] [ Get Directions ]
```

**Gap:** Hardcoded values. Pass actual reservation data via parameter.

**Reservation Detail (§34):** `ReservationDetailScreen()` (ReservationScreens.kt:313)

```
Reservation #AW12345
● RESERVED
Nike Running Shoes
Size 8  Qty 1
ABC Sports  •  1.2 km
⏰ Reservation expires in  04:32:17
[ Get Directions ]
[ Cancel Reservation ] (secondary/outline)
```

Implemented with `CountdownCard()`, `ReservationProductCard()`, `ReservationStoreCard()`, `ReservationStatusBadge()`.

**Reservation History (§35):** `MyReservationsScreen()` (ReservationScreens.kt:520)

Tabs: Active | Completed | Cancelled
```
Nike Running Shoes
ABC Sports
₹2,399
Reserved  Expires 6:30 PM
View Details →
```

**Gap:** "Expired" tab is missing from the tab row. Backend returns expired reservations; add 4th tab or merge into "Cancelled".

### 4.11 Wishlist

**Spec: §36**

**File:** `feature/wishlist/ui/WishlistScreen.kt:41`

**Gap:** Uses placeholder local data models (`WishlistProduct`, `WishlistStore`). Connect to actual repository:

```kotlin
// Add ViewModel + Repository for wishlist items
// WishlistScreen should use hiltViewModel() and collect state
```

Empty state:
```
♡
Your wishlist is empty
Save products you want to check later.
[ Explore Products ]
```

### 4.12 Profile

**Spec: §37–§42**

**File:** `feature/profile/ui/ProfileScreen.kt:23`

User header with avatar (initial letter), name, email, phone, "Edit" button.

Menu sections:
- My Activity → My Reservations >
- My Activity → Wishlist >
- Account → Saved Locations > (not yet navigable)
- Account → Notifications >
- Account → Settings >
- Subscription → AasPaasWala Plus (PRO badge)
- Support → Help & Support >
- Support → Terms & Privacy >
- Support → About AasPaasWala >
- Logout (with confirmation dialog)

**Gap:** "Saved Locations" menu item has empty `onClick`. Wire to a SavedLocations screen.

### 4.13 System States

**Spec: §43–§48, §62**

Most states already in `CommonComponents.kt`:

| State             | Spec Section | Composables        | File:Line             |
|-------------------|-------------|-------------------|----------------------|
| Loading           | §47         | `LoadingScreen()` | CommonComponents.kt:25 |
| Skeleton          | §48         | `ProductCardShimmer()`, `StoreCardShimmer()` | CommonComponents.kt:300 |
| Empty             | §46         | `EmptyScreen()`   | CommonComponents.kt:71 |
| Error             | §45         | `ErrorScreen()`   | CommonComponents.kt:34 |
| Offline           | §44         | `OfflineScreen()` | CommonComponents.kt:113 |
| No Location       | §44         | `NoLocationScreen()` | CommonComponents.kt:143 |
| No Search Results | §43         | `NoResultsScreen()` | CommonComponents.kt:178 |

**Missing states to add:**

```kotlin
// In CommonComponents.kt
@Composable
fun SessionExpiredScreen(onLoginAgain: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔒", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("Session expired", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Please log in again to continue.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onLoginAgain, Modifier.height(52.dp), RoundedCornerShape(12.dp)) {
            Text("Log In Again")
        }
    }
}

@Composable
fun ProductUnavailableScreen(productName: String, onBrowse: () -> Unit) { ... }

@Composable
fun StoreClosedScreen(storeName: String, opensAt: String?, onReserveLater: () -> Unit) { ... }

@Composable
fun ReservationExpiredScreen(onFindStores: () -> Unit) { ... }

@Composable
fun NetworkErrorScreen(message: String, onRetry: () -> Unit) { ... }
```

---

## 5. Navigation Graph

**Spec: §60–§61, §68**

**File:** `navigation/AppNavGraph.kt:25`, `navigation/Screen.kt:3`

### Route Map

| Screen                  | Route Pattern                              | File                         |
|------------------------|--------------------------------------------|------------------------------|
| Splash                 | `"splash"`                                 | Screen.kt:5                  |
| Onboarding             | `"onboarding"`                             | Screen.kt:6                  |
| Login (phone/email)    | `"auth/phone"`                             | Screen.kt:9                  |
| OTP                    | `"auth/otp/{mobile}"`                      | Screen.kt:10                 |
| Location Permission    | `"location/permission"`                    | Screen.kt:15                 |
| Home                   | `"home"`                                   | Screen.kt:18                 |
| Search                 | `"search?query={query}"`                   | Screen.kt:19                 |
| Product Detail         | `"product/{productId}/variant/{variantId}"`| Screen.kt:24                 |
| Store Detail           | `"store/{storeId}"`                        | Screen.kt:29                 |
| Reservation Confirm    | `"reservation/confirm/{variantId}"`        | Screen.kt:34                 |
| Reservation Detail     | `"reservation/{reservationId}"`            | Screen.kt:37                 |
| My Reservations        | `"reservations"`                           | Screen.kt:40                 |
| Profile                | `"profile"`                                | Screen.kt:43                 |
| Wishlist               | `"wishlist"`                               | Screen.kt:44                 |
| Notifications          | `"notifications"`                          | Screen.kt:45                 |
| Settings               | `"settings"`                               | Screen.kt:46                 |
| Saved Locations        | `"saved-locations"`                        | Screen.kt:47                 |

### Missing Routes to Add

```kotlin
// Screen.kt — add these
object EditProfile : Screen("edit-profile")
object AddLocation : Screen("add-location")
object StoreMap : Screen("store/{storeId}/map") {
    fun createRoute(storeId: String) = "store/$storeId/map"
}
object SessionExpired : Screen("session-expired")
object NetworkError : Screen("network-error")
```

### Prototype Flows

**Flow 1 — New User (§61):**
```
Splash → Onboarding → Login → Location Permission → Home
```

**Flow 2 — Search (§61):**
```
Home → Search → Search Suggestions → Results → Filter → Product
```

**Flow 3 — Reservation (§61):**
```
Product → Reserve → Confirmation → Reservation Detail
```

**Flow 4 — Existing User (§61):**
```
Splash → Home
```

---

## 6. Data Layer Mapping

### 6.1 API Contract

**Spec references:** Backend returns `{ success: boolean, data: any, message: string }`

**File:** `data/remote/Dtos.kt`

| Endpoint                     | API Service              | DTO              | File:Line      |
|-----------------------------|--------------------------|------------------|----------------|
| POST `/auth/request-otp`    | `AuthApiService`         | `EmailOtpRequest`| Dtos.kt:14      |
| POST `/auth/verify-otp`     | `AuthApiService`         | `EmailOtpVerifyRequest` | Dtos.kt:20 |
| GET `/products/nearby`      | `ProductApiService`      | `List<ProductDto>` | ProductApiService.kt:23 |
| GET `/products/search`      | `ProductApiService`      | `SearchResultDto` | ProductApiService.kt:8  |
| GET `/products/{id}`        | `ProductApiService`      | `ProductDto`     | ProductApiService.kt:19 |
| GET `/stores/nearby`        | `StoreApiService`        | `List<StoreDto>` | ProductApiService.kt:34 |
| GET `/stores/{id}`          | `StoreApiService`        | `StoreDto`       | ProductApiService.kt:40 |
| POST `/reservations`        | `ReservationApiService`  | `ReservationDto` | ReservationApiService.kt:9 |
| GET `/reservations`         | `ReservationApiService`  | `List<ReservationDto>` | ReservationApiService.kt:13 |

### 6.2 Domain Models

**File:** `domain/model/Models.kt`

Key entities: `User`, `Store`, `Product`, `ProductVariant`, `Inventory`, `Reservation`, `SearchQuery`, `SearchResult`

Reservation status enum: `PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED, EXPIRED`

Reservation policy (§50):
- `FreeUser` → 6 hours
- `Subscriber(24h)` / `Subscriber(48h)` → requires subscription

### 6.3 Repository Interfaces

**File:** `domain/repository/`

| Repository           | Methods                                              | File                          |
|---------------------|------------------------------------------------------|-------------------------------|
| `AuthRepository`    | login, register, requestEmailOtp, verifyEmailOtp, socialLogin | AuthRepository.kt     |
| `ProductRepository` | search, getProductById, getNearbyProducts, getProductsByStore | ProductRepository.kt |
| `StoreRepository`   | getNearbyStores, getStoreById                        | ProductRepository.kt          |
| `ReservationRepository` | createReservation, cancelReservation, getReservationById, getMyReservations, fetchMyReservations, getActiveReservations | ReservationRepository.kt:7 |

---

## 7. State Management Pattern

All screens follow the **MVVM + unidirectional data flow** pattern:

```kotlin
// ViewModel
data class ScreenUiState(
    val isLoading: Boolean = false,
    val data: Data? = null,
    val error: String? = null
)

@HiltViewModel
class ScreenViewModel @Inject constructor(
    private val repository: XRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScreenUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _uiState.value = ScreenUiState(isLoading = true)
            repository.getData(id)
                .onSuccess { _uiState.value = ScreenUiState(data = it) }
                .onFailure { _uiState.value = ScreenUiState(error = it.message) }
        }
    }
}

// Composable
@Composable
fun Screen(viewModel: ScreenViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> ErrorScreen(state.error!!) { viewModel.load(id) }
        state.data != null -> Content(state.data!!)
    }
}
```

---

## 8. Key Business Logic

### 8.1 Reservation Duration (Spec §50)

```kotlin
// domain/model/Models.kt:88
sealed class ReservationPolicy {
    abstract val durationHours: Int
    object FreeUser : ReservationPolicy() { override val durationHours = 6 }
    data class Subscriber(override val durationHours: Int) : ReservationPolicy()
}
```

In `ReservationConfirmScreen`:
- 6 Hours → Free (default selected)
- 24 Hours → PRO badge (requires Plus subscription)
- 48 Hours → PRO badge (requires Plus subscription)

If non-subscriber taps 24h/48h option, show bottom sheet:
```
Longer reservations
Get 24/48 hour reservations with AasPaasWala Plus.
[ Explore Plus ]
```

### 8.2 Availability States

```kotlin
// domain/model/Models.kt:70
enum class InventoryStatus { AVAILABLE, LOW_STOCK, OUT_OF_STOCK, RESERVED }
```

Color mapping in UI:
- `AVAILABLE` → `Success` (green) → "Available" / "Available nearby"
- `LOW_STOCK` → `Warning` (amber) → "Only N left nearby"
- `OUT_OF_STOCK` → `Error` (red) → "Out of stock" / "Currently unavailable"

### 8.3 Store Open/Closed

```kotlin
// Used in StoreCard, NearbyStoreCard, StoreDetail
if (store.isOpen) {
    Text("Open", color = Success)  // also show closing time
} else {
    Text("Closed", color = Error)  // also show reopening time
}
```

### 8.4 Favorite Animation (Spec §49)

```kotlin
// In ProductCard / WishlistScreen
var isFilled by remember { mutableStateOf(false) }
Icon(
    imageVector = if (isFilled) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
    tint = if (isFilled) Accent else TextSecondary,
    modifier = Modifier
        .size(20.dp)
        .graphicsLayer(scaleX = animateScale, scaleY = animateScale)
)
// Trigger scale animation on click
val scale by animateFloatAsState(
    if (isFilled) 1.2f else 1f,
    animationSpec = tween(150),
    label = "favorite_scale"
)
```

---

## 9. Implementation Priority

**MVP — 12 Core Screens (Spec §71):**

| Priority | Screen               | Status  | File |
|----------|---------------------|---------|------|
| 1        | Home                | ✅ Done | HomeScreen.kt |
| 1        | Search              | ✅ Done | SearchScreen.kt |
| 1        | Search Suggestions  | ⚠ Partial| SearchScreen.kt:183 |
| 1        | Search Results      | ⚠ Partial| SearchScreen.kt:209 (list, not grid) |
| 1        | Filter              | ✅ Done | SearchFilterSheet.kt |
| 1        | Product Details     | ✅ Done | ProductDetailScreen.kt |
| 1        | Available Near You  | ✅ Done | ProductDetailScreen.kt:254 |
| 1        | Store Details       | ✅ Done | StoreDetailScreen.kt |
| 1        | Reservation Sheet   | ⚠ Partial| ReservationScreens.kt:36 (hardcoded data) |
| 1        | Reservation Confirmation | ✅ Done | ReservationScreens.kt:247 |
| 1        | Reservation Detail  | ✅ Done | ReservationScreens.kt:313 |
| 1        | Profile             | ✅ Done | ProfileScreen.kt |

**Phase 2 — Remaining Screens & Gaps:**
- Phone login (replace email login)
- Search results 2-column grid
- Search suggestions with Brands/Categories distinction
- Store map screen
- Price comparison section on product detail
- Store weekly hours
- Store products internal search + category chips
- Wishlist ViewModel + repository integration
- Saved Locations screen + navigation wiring
- Edit Profile screen
- All missing system states (session expired, network error, store closed, product unavailable, reservation expired)

---

## 10. Component Reusability Guide

Components that should be extracted to `core/ui/components/` for reuse across features:

```kotlin
// Recommended additions to CommonComponents.kt

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(LocalRadius.current.md),
        colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(LocalRadius.current.md),
        border = BorderStroke(1.dp, Border)
    ) {
        icon?.let { Icon(it, null, Modifier.size(18.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
    }
}

@Composable
fun StarRating(rating: Float, reviewCount: Int?, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Star, null, Modifier.size(14.dp), tint = Warning)
        Text(String.format("%.1f", rating), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        reviewCount?.let {
            Text(" ($it)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun DistanceChip(distanceKm: Double?, modifier: Modifier = Modifier) {
    distanceKm?.let {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), tint = TextSecondary)
            Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun TopAppBarWithActions(
    title: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = { title?.let { Text(it, color = TextPrimary) } },
        navigationIcon = navigationIcon,
        actions = { actions?.invoke() },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
    )
}
```

---

## 11. Backend API Compatibility Notes

### Auth Response (§API Contract)

Backend returns `accessToken` + `refreshToken` + `user`/`retailer` object. DTOs already match:

```kotlin
data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("user") val user: UserDto
)
```

### Reservation Response

Backend returns flat denormalized data — DTOs already match:

```kotlin
data class ReservationDto(
    val customerName: String, customerMobile: String,
    productName: String, productImage: String?,
    variantSku: String, variantDescription: String,
    storeName: String, storeAddress: String, storeLocation: StoreLocationDto?,
    storePhone: String?, price: Double, quantity: Int,
    status: String, reservationCode: String, durationHours: Int,
    createdAt: String, expiresAt: String, completedAt: String?, cancelledAt: String?
)
```

Mapper in `data/model/Mappers.kt:63` correctly handles the flat response.

### Nearby Products

`ProductApiService.getNearbyProducts()` returns `List<ProductDto>` — these don't include store info. Repository must inject store data:

```kotlin
// ProductRepositoryImpl.kt:107 — currently returns products without store
// Fix: fetch store info separately, or modify API to include store per product
```
