# Aas Paas Wala — Implementation Plan

## Phase One — Core Backend Foundation (Complete)

Phase One delivers the working backend contract used by the customer, business,
and admin clients:

- JWT customer, retailer, and admin authentication
- Product, store, inventory, and category APIs
- Customer reservation creation, listing, detail, cancellation, and expiry
- Business dashboard, product, inventory, and reservation operations
- Role-based access control, validation, rate limiting, CORS, and error handling
- MongoDB-backed integration tests for reservation, business, and category flows
- Atomic reservation cancellation and expiry stock release to protect inventory
  from duplicate releases during concurrent requests

Validation baseline: backend build passes and the backend test suite passes.

## Phase Two — Production Authentication (In Progress)

Completed in this phase:

- Email OTP request and verification for customer and business apps
- SMTP delivery support for production email OTPs
- Google and Facebook server-side token verification endpoints
- Android email OTP API, repository, ViewModel, and UI wiring
- Keyboard-safe, scrollable authentication UX in both Android apps
- Environment documentation for SMTP and social provider configuration

Remaining: native Google/Facebook Android SDK sign-in, persistent admin users,
OpenAPI documentation, Android UI verification, and CI.

## Current Architecture

### Backend
- **Framework**: Node.js + Express + TypeScript
- **Database**: MongoDB + Mongoose (with in-memory fallback for dev)
- **Auth**: JWT (access + refresh), OTP-based (mock in dev)
- **Validation**: Zod
- **Logging**: Winston + Morgan
- **Security**: Helmet, CORS, rate limiting
- **Pattern**: MVC-like with services, controllers, routes, models

### Customer App
- **Framework**: Android (Kotlin + Jetpack Compose)
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Retrofit + Gson
- **Local**: DataStore (tokens), Room (configured but not actively used)
- **Location**: FusedLocationProvider

### Business App
- **Framework**: Android (Kotlin + Jetpack Compose)
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Retrofit + Gson
- **Local**: DataStore (tokens)

## Existing Relevant Files

### Backend
- `src/server.ts` — Entry point with reservation expiry scheduler
- `src/app.ts` — Express app with all route registration
- `src/modules/reservations/` — Complete reservation module (model, service, controller, routes, schema)
- `src/modules/business/` — Complete business API (auth, store, products, inventory, reservations, dashboard)
- `src/modules/categories/` — Complete category API (public + admin)
- `src/models/` — User, Retailer, Store, Product, ProductVariant, Inventory
- `src/middleware/authenticate.ts` — JWT + role guards
- `tests/modules/reservations.test.ts` — Reservation + business + category API tests

### Customer App
- `data/remote/AuthApiService.kt` — Customer auth API
- `data/remote/ReservationApiService.kt` — Reservation API
- `data/remote/ProductApiService.kt` — Product/store API
- `data/remote/Dtos.kt` — DTOs (has auth mismatch)
- `data/model/Mappers.kt` — DTO → domain mappers (reservation mapper wrong)
- `data/repository/ReservationRepositoryImpl.kt` — In-memory cache
- `feature/reservation/viewmodel/ReservationViewModels.kt` — Reservation ViewModels

### Business App
- `data/remote/ApiServices.kt` — Business API interfaces
- `data/remote/Dtos.kt` — Business DTOs (has auth mismatch)
- `data/repository/RepositoryImpls.kt` — Repository implementations
- `data/model/Mappers.kt` — DTO → domain mappers
- `feature/*/viewmodel/*.kt` — Business ViewModels

## Missing Functionality

### Backend
- None critical — all P0 modules implemented
- Tests fail on Windows due to `mongodb-memory-server` startup timeout (needs longer timeout)

### Customer App
1. **Auth DTO mismatch**: `AuthResponse` expects `token` but backend returns `accessToken` + `refreshToken`
2. **Reservation DTO mismatch**: `ReservationDto` expects nested `product`, `variant`, `store` but backend returns flat denormalized fields
3. **Reservation mapper wrong**: Maps nested DTOs that don't exist in backend response

### Business App
1. **Auth DTO mismatch**: `AuthResponse` expects `token` but backend returns `accessToken` + `refreshToken`

## Files to Modify

### Backend
1. `package.json` — Increase `mongodbMemoryServer.version` or add `mongodbMemoryServer.launchTimeout` config
2. `tests/modules/reservations.test.ts` — Increase MongoDB startup timeout

### Customer App
1. `data/remote/Dtos.kt` — Fix `AuthResponse` to match backend (`accessToken`, `refreshToken`, `user`/`retailer`)
2. `data/remote/Dtos.kt` — Fix `ReservationDto` to match backend flat response
3. `data/model/Mappers.kt` — Fix reservation mapper for flat response

### Business App
1. `data/remote/Dtos.kt` — Fix `AuthResponse` to match backend (`accessToken`, `refreshToken`, `retailer`)

## API Contract

All APIs use consistent wrapper: `{ success: boolean, data: any, message: string }`

### Customer Auth
```json
{
  "success": true,
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "user": { "_id": "...", "name": "...", "mobile": "...", "email": "..." }
  },
  "message": "Authenticated successfully"
}
```

### Business Auth
```json
{
  "success": true,
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "retailer": { "_id": "...", "ownerName": "...", "businessName": "...", ... }
  },
  "message": "Authenticated successfully"
}
```

### Reservation Create
```json
{
  "success": true,
  "data": {
    "_id": "...",
    "customerName": "...",
    "customerMobile": "...",
    "productName": "...",
    "productImage": "...",
    "variantSku": "...",
    "variantDescription": "...",
    "storeName": "...",
    "storeAddress": "...",
    "storeLocation": { "type": "Point", "coordinates": [...] },
    "storePhone": "...",
    "price": 1499,
    "quantity": 2,
    "status": "PENDING",
    "reservationCode": "RES-XXXX-XXXX",
    "durationHours": 6,
    "expiresAt": "2026-08-30T21:34:26.000Z",
    "createdAt": "...",
    "updatedAt": "..."
  },
  "message": "Reservation created"
}
```

### Business Reservation
```json
{
  "success": true,
  "data": {
    "_id": "...",
    "customerName": "...",
    "customerMobile": "...",
    "productName": "...",
    "variantDescription": "...",
    "price": 1499,
    "status": "PENDING",
    "createdAt": "...",
    "expiresAt": "...",
    "completedAt": "...",
    "cancelledAt": "..."
  },
  "message": "Reservation confirmed"
}
```

## Database Changes
- None — all schemas already exist with proper indexes

## Testing Strategy
- Fix existing test timeout issues
- Add tests for reservation expiry simulation
- Add tests for business reservation status transitions
- Add tests for concurrent reservation scenarios

## Risks
- `mongodb-memory-server` startup timeout on Windows (mitigation: increase timeout)
- Android app builds may fail if Gradle dependencies not cached
- Customer app reservation mapper changes require UI verification
