# Aas Paas Wala — Implementation Plan

## Current Architecture

### Backend
- **Framework:** Express.js + TypeScript
- **Database:** MongoDB + Mongoose
- **Auth:** JWT access + refresh tokens, OTP-based
- **Validation:** Zod
- **Logging:** Winston
- **Security:** Helmet, CORS, express-rate-limit
- **Response format:** `{ success: boolean, data?: any, message?: string, error?: { code: string, message: string } }`
- **Error classes:** AppError, ValidationError, UnauthorizedError, ForbiddenError, NotFoundError, ConflictError, InsufficientStockError, ReservationExpiredError

### Android (both apps)
- **Stack:** Kotlin + Jetpack Compose + Hilt + Retrofit + Gson
- **Response wrapper:** `ApiResponse<T>` with `{ success, data, message }`
- **Token:** DataStore + AuthInterceptor
- **Base URL:** `http://10.0.2.2:3000/api/v1/`

---

## Existing Relevant Files

### Backend
- `src/app.ts` — route registration
- `src/models/User.ts` — customer model
- `src/models/Retailer.ts` — business owner model
- `src/models/Store.ts` — store with 2dsphere index
- `src/models/Product.ts` — product with text search index, `categoryId` ref
- `src/models/ProductVariant.ts` — variant with compound unique index
- `src/models/Inventory.ts` — stock accounting, invariant: available + reserved = total
- `src/middleware/authenticate.ts` — JWT verification + role guards
- `src/utils/errors.ts` — custom error classes
- `src/utils/response.ts` — sendSuccess/sendError helpers
- `src/config/constants.ts` — ROLES, RESERVATION_STATUS, etc.

### Customer Android
- `data/remote/ReservationApiService.kt` — expects `POST reservations` with `{ variantId }`
- `data/remote/Dtos.kt` — ReservationDto expects nested product/variant/store + `durationHours`
- `domain/model/Models.kt` — Reservation domain model with FreeUser/Subscriber policy
- `domain/repository/ReservationRepository.kt` — createReservation, cancelReservation, getReservationById, getMyReservations(), getActiveReservations()
- `feature/reservation/viewmodel/ReservationViewModels.kt` — confirm, detail with countdown

### Business Android
- `data/remote/ApiServices.kt` — expects `business/*` endpoints
- `data/remote/Dtos.kt` — flat ReservationDto with customerName, productName, variantDescription, price
- `domain/repository/Repositories.kt` — confirm/complete/cancel reservations, getDashboardStats
- `feature/dashboard/viewmodel/DashboardViewModel.kt` — stats on init
- `feature/product/viewmodel/ProductViewModels.kt` — CRUD via AddProductRequest
- `feature/inventory/viewmodel/InventoryViewModel.kt` — update stock via updateInventory

---

## Missing Functionality

1. **Reservation backend module** — model, routes, controller, service, validation
2. **Business API namespace** — dashboard, store, products, inventory, reservations
3. **Category admin routes** — minimal CRUD
4. **Android integration** — customer + business apps currently use mock auth; no real reservation data flow
5. **SMS provider** — mock only
6. **Seed script** — referenced but missing

---

## Files to Create

### Backend
```
src/modules/reservations/
├── reservation.model.ts
├── reservation.schema.ts
├── reservation.service.ts
├── reservation.controller.ts
└── reservation.routes.ts

src/modules/business/
├── business.routes.ts
├── business.controller.ts
├── business.service.ts
└── business.validation.ts

src/modules/categories/
├── category.model.ts
├── category.routes.ts
├── category.controller.ts
└── category.service.ts
```

### Tests
```
tests/reservations/
└── reservation.test.ts

tests/business/
└── business.test.ts
```

---

## API Contract

### Customer Reservation APIs
```
POST   /api/v1/reservations
       Body: { variantId: string }
       Auth: Bearer (CUSTOMER)

GET    /api/v1/reservations
       Auth: Bearer (CUSTOMER)

GET    /api/v1/reservations/:id
       Auth: Bearer (CUSTOMER)

POST   /api/v1/reservations/:id/cancel
       Auth: Bearer (CUSTOMER)
```

### Business APIs
```
GET    /api/v1/business/dashboard
       Auth: Bearer (RETAILER)

GET    /api/v1/business/store
       Auth: Bearer (RETAILER)

PUT    /api/v1/business/store
       Body: { name, address, latitude, longitude, phone, openingHours, categories, imageUrl }
       Auth: Bearer (RETAILER)

GET    /api/v1/business/products
       Auth: Bearer (RETAILER)

POST   /api/v1/business/products
       Body: { name, brand?, description?, category, variants: [{ size?, color?, price, stock }] }
       Auth: Bearer (RETAILER)

PUT    /api/v1/business/products/:id
       Body: same as POST
       Auth: Bearer (RETAILER)

DELETE /api/v1/business/products/:id
       Auth: Bearer (RETAILER)

PUT    /api/v1/business/inventory/:id
       Body: { stock: number }
       Auth: Bearer (RETAILER)

GET    /api/v1/business/reservations
       Auth: Bearer (RETAILER)

POST   /api/v1/business/reservations/:id/confirm
       Auth: Bearer (RETAILER)

POST   /api/v1/business/reservations/:id/complete
       Auth: Bearer (RETAILER)

POST   /api/v1/business/reservations/:id/cancel
       Auth: Bearer (RETAILER)
```

### Category APIs
```
GET    /api/v1/categories
GET    /api/v1/categories/:id

POST   /api/v1/admin/categories
       Auth: Bearer (ADMIN)

PUT    /api/v1/admin/categories/:id
       Auth: Bearer (ADMIN)

DELETE /api/v1/admin/categories/:id
       Auth: Bearer (ADMIN)
```

---

## Database Changes

### New: Reservation Model
- `customerId` → ref User
- `storeId` → ref Store
- `productId` → ref Product
- `variantId` → ref ProductVariant
- `inventoryId` → ref Inventory
- `customerName`, `customerMobile`, `productName`, `variantSku`, `variantDescription`
- `storeName`, `storeAddress`, `storeLocation` (2dsphere for analytics)
- `price` (paise integer), `quantity`
- `status` enum, `reservationCode` unique index
- `durationHours`, `expiresAt`, `completedAt`, `cancelledAt`
- Indexes: customerId, storeId, status, expiresAt, reservationCode

### New: Category Model
- `name` unique, `description?`, `isActive`
- Used by Product.categoryId

### Modified: Store Model
- Add `retailerId` → already exists

---

## Testing Strategy

- Reservation tests: create (success, insufficient stock, invalid variant), cancel, expiry simulation, duplicate prevention
- Business tests: product CRUD, inventory update respecting invariant, reservation listing, status transitions, dashboard
- Category tests: public list, admin CRUD, unauthorized access rejection
- Use mongodb-memory-server like existing inventory tests

---

## Risks

1. **Atomic stock race conditions** — use MongoDB transactions or `findOneAndUpdate` with `$inc` and pre-condition checks
2. **Android mock auth** — both apps default to mock auth in debug; need `USE_MOCK_AUTH=false` to test real APIs
3. **Customer create reservation ambiguity** — app sends only `variantId`; backend must resolve store from available inventory
4. **Android data loss on app kill** — both reservation repos use in-memory cache; Room migration needed later

---

## Implementation Order

1. Reservation backend
2. Business API backend
3. Category backend
4. Customer Android integration
5. Business Android integration
6. Tests + build verification
