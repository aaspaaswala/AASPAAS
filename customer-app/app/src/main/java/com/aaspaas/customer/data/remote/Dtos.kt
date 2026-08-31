package com.aaspaas.customer.data.remote

import com.google.gson.annotations.SerializedName

// ── Auth ──────────────────────────────────────────────────────────────────────

data class CustomerAuthRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("name") val name: String?
)
data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("user") val user: UserDto
)
data class UserDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String?
)

// ── Store ─────────────────────────────────────────────────────────────────────

data class StoreDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("distanceKm") val distanceKm: Double?,
    @SerializedName("rating") val rating: Float?,
    @SerializedName("reviewCount") val reviewCount: Int,
    @SerializedName("openingHours") val openingHours: String,
    @SerializedName("isOpen") val isOpen: Boolean,
    @SerializedName("phone") val phone: String?,
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("verificationStatus") val verificationStatus: String
)

// ── Product ───────────────────────────────────────────────────────────────────

data class ProductDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrls") val imageUrls: List<String>,
    @SerializedName("category") val category: String,
    @SerializedName("variants") val variants: List<ProductVariantDto>,
    @SerializedName("store") val store: StoreDto
)

data class ProductVariantDto(
    @SerializedName("_id") val id: String,
    @SerializedName("size") val size: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("inventory") val inventory: InventoryDto
)

data class InventoryDto(
    @SerializedName("_id") val id: String,
    @SerializedName("totalStock") val totalStock: Int,
    @SerializedName("availableStock") val availableStock: Int,
    @SerializedName("reservedStock") val reservedStock: Int,
    @SerializedName("status") val status: String
)

// ── Reservation ───────────────────────────────────────────────────────────────

data class CreateReservationRequest(@SerializedName("variantId") val variantId: String)

data class ReservationDto(
    @SerializedName("_id") val id: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerMobile") val customerMobile: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("productImage") val productImage: String?,
    @SerializedName("variantSku") val variantSku: String,
    @SerializedName("variantDescription") val variantDescription: String,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("storeAddress") val storeAddress: String,
    @SerializedName("storeLocation") val storeLocation: StoreLocation?,
    @SerializedName("storePhone") val storePhone: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("status") val status: String,
    @SerializedName("reservationCode") val reservationCode: String,
    @SerializedName("durationHours") val durationHours: Int,
    @SerializedName("expiresAt") val expiresAt: String,
    @SerializedName("completedAt") val completedAt: String?,
    @SerializedName("cancelledAt") val cancelledAt: String?
)

data class StoreLocation(
    @SerializedName("type") val type: String,
    @SerializedName("coordinates") val coordinates: List<Double>
)

data class ReservationsListResponse(
    @SerializedName("reservations") val reservations: List<ReservationDto>,
    @SerializedName("count") val count: Int
)

data class ReservationWrapper(
    @SerializedName("reservation") val reservation: ReservationDto
)

// ── Search ────────────────────────────────────────────────────────────────────

data class SearchResultDto(
    @SerializedName("products") val products: List<ProductDto>,
    @SerializedName("stores") val stores: List<StoreDto>,
    @SerializedName("totalCount") val totalCount: Int
)

// ── Generic wrapper ───────────────────────────────────────────────────────────

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?
)
