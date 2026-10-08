package com.aaspaas.business.domain.model

import java.time.Instant

data class Retailer(
    val id: String,
    val ownerName: String,
    val businessName: String,
    val mobile: String,
    val email: String?,
    val verificationStatus: VerificationStatus
)

enum class VerificationStatus { PENDING, VERIFIED, REJECTED }

data class Store(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String?,
    val openingHours: String,
    val categories: List<String>,
    val imageUrl: String?,
    val verificationStatus: VerificationStatus
)

data class Product(
    val id: String,
    val name: String,
    val brand: String?,
    val description: String?,
    val imageUrls: List<String>,
    val category: String,
    val variants: List<ProductVariant>
)

data class ProductVariant(
    val id: String,
    val size: String?,
    val color: String?,
    val price: Double,
    val inventory: Inventory
)

data class Inventory(
    val id: String,
    val totalStock: Int,
    val availableStock: Int,
    val reservedStock: Int,
    val status: InventoryStatus
)

enum class InventoryStatus { AVAILABLE, LOW_STOCK, OUT_OF_STOCK, RESERVED }

data class Reservation(
    val id: String,
    val customerName: String,
    val customerMobile: String?,
    val productName: String,
    val variantDescription: String,
    val price: Double,
    val quantity: Int,
    val reservationCode: String?,
    val status: ReservationStatus,
    val createdAt: Instant,
    val expiresAt: Instant,
    val completedAt: Instant?,
    val cancelledAt: Instant?,
    val rejectedAt: Instant?
)

enum class ReservationStatus { PENDING, CONFIRMED, READY, COMPLETED, CANCELLED, EXPIRED, REJECTED, NO_SHOW }

data class DashboardStats(
    val totalProducts: Int,
    val availableProducts: Int,
    val lowStockProducts: Int,
    val activeReservations: Int,
    val todayReservations: Int
)

data class AddProductRequest(
    val name: String,
    val brand: String?,
    val description: String?,
    val category: String,
    val variants: List<AddVariantRequest>
)

data class AddVariantRequest(
    val size: String?,
    val color: String?,
    val price: Double,
    val stock: Int
)
