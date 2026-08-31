package com.aaspaas.business.data.remote

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?
)

data class RetailerDto(
    @SerializedName("_id") val id: String,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("businessName") val businessName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String?,
    @SerializedName("verificationStatus") val verificationStatus: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("retailer") val retailer: RetailerDto
)

data class SendOtpRequest(@SerializedName("mobile") val mobile: String)
data class VerifyOtpRequest(@SerializedName("mobile") val mobile: String, @SerializedName("otp") val otp: String)

data class RegisterBusinessRequest(
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("businessName") val businessName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String?,
    @SerializedName("category") val category: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("openingHours") val openingHours: String
)

data class StoreDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("phone") val phone: String?,
    @SerializedName("openingHours") val openingHours: String,
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("verificationStatus") val verificationStatus: String
)

data class InventoryDto(
    @SerializedName("_id") val id: String,
    @SerializedName("totalStock") val totalStock: Int,
    @SerializedName("availableStock") val availableStock: Int,
    @SerializedName("reservedStock") val reservedStock: Int,
    @SerializedName("status") val status: String
)

data class ProductVariantDto(
    @SerializedName("_id") val id: String,
    @SerializedName("size") val size: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("inventory") val inventory: InventoryDto
)

data class ProductDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrls") val imageUrls: List<String>,
    @SerializedName("category") val category: String,
    @SerializedName("variants") val variants: List<ProductVariantDto>
)

data class ReservationDto(
    @SerializedName("_id") val id: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerMobile") val customerMobile: String?,
    @SerializedName("productName") val productName: String,
    @SerializedName("variantDescription") val variantDescription: String,
    @SerializedName("price") val price: Double,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("expiresAt") val expiresAt: String,
    @SerializedName("completedAt") val completedAt: String?,
    @SerializedName("cancelledAt") val cancelledAt: String?
)

data class DashboardStatsDto(
    @SerializedName("totalProducts") val totalProducts: Int,
    @SerializedName("availableProducts") val availableProducts: Int,
    @SerializedName("lowStockProducts") val lowStockProducts: Int,
    @SerializedName("activeReservations") val activeReservations: Int,
    @SerializedName("todayReservations") val todayReservations: Int
)

data class AddVariantRequest(
    @SerializedName("size") val size: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("stock") val stock: Int
)

data class AddProductRequest(
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("variants") val variants: List<AddVariantRequest>
)

data class UpdateInventoryRequest(@SerializedName("stock") val stock: Int)
