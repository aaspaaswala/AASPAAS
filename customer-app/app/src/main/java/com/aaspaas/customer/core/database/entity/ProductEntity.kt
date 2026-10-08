package com.aaspaas.customer.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ── TypeConverters ────────────────────────────────────────────────────────────

class StringListConverter {
    private val gson = Gson()
    @TypeConverter fun fromList(list: List<String>): String = gson.toJson(list)
    @TypeConverter fun toList(json: String): List<String> =
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
}

class VariantListConverter {
    private val gson = Gson()
    @TypeConverter fun fromList(list: List<ProductVariantEntity>): String = gson.toJson(list)
    @TypeConverter fun toList(json: String): List<ProductVariantEntity> =
        gson.fromJson(json, object : TypeToken<List<ProductVariantEntity>>() {}.type) ?: emptyList()
}

// ── Product ───────────────────────────────────────────────────────────────────

@Entity(tableName = "products")
@TypeConverters(StringListConverter::class, VariantListConverter::class)
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String?,
    val description: String?,
    val imageUrls: List<String>,
    val category: String,
    val variants: List<ProductVariantEntity>,
    // Denormalized store fields for offline display
    val storeId: String?,
    val storeName: String?,
    val storeAddress: String? = null,
    val storeLatitude: Double? = null,
    val storeLongitude: Double? = null,
    val storeDistanceKm: Double?,
    val storeRating: Float?,
    val storeIsOpen: Boolean,
    val storePhone: String? = null,
    val storeOpeningHours: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)

// ── ProductVariant (embedded via JSON in ProductEntity) ───────────────────────

data class ProductVariantEntity(
    val id: String,
    val productId: String,
    val size: String?,
    val color: String?,
    val price: Double,
    val inventoryStatus: String,
    val totalStock: Int,
    val availableStock: Int,
    val reservedStock: Int
)

// ── Store ─────────────────────────────────────────────────────────────────────

@Entity(tableName = "stores")
@TypeConverters(StringListConverter::class)
data class StoreEntity(
    @PrimaryKey val id: String,
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
    val verificationStatus: String,
    val cachedAt: Long = System.currentTimeMillis()
)

// ── Reservation ───────────────────────────────────────────────────────────────

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val productName: String,
    val productImage: String?,
    val variantId: String,
    val variantDescription: String,
    val variantSize: String?,
    val variantColor: String?,
    val storeId: String,
    val storeName: String,
    val storeAddress: String,
    val storeLatitude: Double,
    val storeLongitude: Double,
    val storePhone: String?,
    val price: Double,
    val quantity: Int,
    val status: String,
    val reservationCode: String,
    val durationHours: Int,
    val policy: String,
    val createdAt: Long,
    val expiresAt: Long,
    val completedAt: Long?,
    val cancelledAt: Long?
)
