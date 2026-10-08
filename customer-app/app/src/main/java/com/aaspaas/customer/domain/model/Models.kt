package com.aaspaas.customer.domain.model

import java.time.Instant

data class User(
    val id: String,
    val name: String,
    val mobile: String,
    val email: String?,
    val dob: String? = null,
    val savedLocations: List<SavedLocation> = emptyList()
)

data class SavedLocation(
    val id: String,
    val label: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class Store(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double?,
    val rating: Float?,
    val reviewCount: Int,
    val openingHours: String,
    val isOpen: Boolean,
    val phone: String?,
    val categories: List<String>,
    val verificationStatus: VerificationStatus
)

enum class VerificationStatus { PENDING, VERIFIED, REJECTED }

data class Product(
    val id: String,
    val name: String,
    val brand: String?,
    val description: String?,
    val imageUrls: List<String>,
    val category: String,
    val variants: List<ProductVariant>,
    val store: Store
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
) {
    val soldStock: Int get() = totalStock - availableStock - reservedStock
}

enum class InventoryStatus { AVAILABLE, LOW_STOCK, OUT_OF_STOCK, RESERVED }

data class Reservation(
    val id: String,
    val product: Product,
    val variant: ProductVariant,
    val store: Store,
    val status: ReservationStatus,
    val createdAt: Instant,
    val expiresAt: Instant,
    val completedAt: Instant?,
    val cancelledAt: Instant?,
    val policy: ReservationPolicy
)

enum class ReservationStatus { PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED, EXPIRED }

// Extensible policy — Phase 2 will add SubscriberPolicy
sealed class ReservationPolicy {
    abstract val durationHours: Int

    object FreeUser : ReservationPolicy() {
        override val durationHours: Int = 6
    }

    data class Subscriber(override val durationHours: Int) : ReservationPolicy()
}

data class SearchQuery(
    val text: String,
    val latitude: Double?,
    val longitude: Double?,
    val radiusKm: Double = 10.0,
    val category: String? = null,
    val maxPrice: Double? = null,
    val minPrice: Double? = null
)

data class SearchResult(
    val products: List<Product>,
    val stores: List<Store>,
    val totalCount: Int
)

data class PriceComparisonEntry(
    val store: Store,
    val variantId: String,
    val price: Double,
    val availableStock: Int,
    val distanceKm: Double?
)
