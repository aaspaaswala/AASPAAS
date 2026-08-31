package com.aaspaas.customer.data.remote

import com.google.gson.annotations.SerializedName

// ── Auth ──────────────────────────────────────────────────────────────────────

data class SendOtpRequest(@SerializedName("mobile") val mobile: String)
data class VerifyOtpRequest(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("otp") val otp: String
)
data class AuthResponse(
    @SerializedName("token") val token: String,
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
    @SerializedName("product") val product: ProductDto,
    @SerializedName("variant") val variant: ProductVariantDto,
    @SerializedName("store") val store: StoreDto,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("expiresAt") val expiresAt: String,
    @SerializedName("completedAt") val completedAt: String?,
    @SerializedName("cancelledAt") val cancelledAt: String?,
    @SerializedName("durationHours") val durationHours: Int
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
