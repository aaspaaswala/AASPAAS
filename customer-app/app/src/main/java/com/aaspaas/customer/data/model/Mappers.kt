package com.aaspaas.customer.data.model

import com.aaspaas.customer.core.database.entity.*
import com.aaspaas.customer.core.database.mapper.toEntity
import com.aaspaas.customer.data.remote.*
import com.aaspaas.customer.domain.model.*
import java.time.Instant

// ── DTO → Domain ──────────────────────────────────────────────────────────────

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    mobile = phone.orEmpty(),
    email = email,
    dob = dob
)

fun StoreDto.toDomain() = Store(
    id = id, name = name, imageUrl = imageUrl, address = address,
    latitude = latitude, longitude = longitude, distanceKm = distanceKm,
    rating = rating, reviewCount = reviewCount, openingHours = openingHours,
    isOpen = isOpen, phone = phone, categories = categories,
    verificationStatus = when (verificationStatus) {
        "VERIFIED" -> VerificationStatus.VERIFIED
        "REJECTED" -> VerificationStatus.REJECTED
        else -> VerificationStatus.PENDING
    }
)

fun InventoryDto.toDomain() = Inventory(
    id = id, totalStock = totalStock, availableStock = availableStock,
    reservedStock = reservedStock,
    status = when (status) {
        "LOW_STOCK" -> InventoryStatus.LOW_STOCK
        "OUT_OF_STOCK" -> InventoryStatus.OUT_OF_STOCK
        "RESERVED" -> InventoryStatus.RESERVED
        else -> InventoryStatus.AVAILABLE
    }
)

fun ProductVariantDto.toDomain() = ProductVariant(
    id = id, size = size, color = color, price = price,
    inventory = inventory?.toDomain() ?: Inventory(
        id = "", totalStock = 0, availableStock = 0,
        reservedStock = 0, status = InventoryStatus.OUT_OF_STOCK
    )
)

fun ProductDto.toDomain(storeOverride: Store? = null) = Product(
    id = id, name = name, brand = brand, description = description,
    imageUrls = imageUrls, category = category,
    variants = variants.map { it.toDomain() },
    store = storeOverride ?: store?.toDomain() ?: Store(
        id = "", name = "", imageUrl = null, address = "",
        latitude = 0.0, longitude = 0.0, distanceKm = null,
        rating = null, reviewCount = 0, openingHours = "",
        isOpen = true, phone = null, categories = emptyList(),
        verificationStatus = VerificationStatus.PENDING
    )
)

fun ReservationDto.toDomain(): Reservation {
    val lat = storeLocation?.coordinates?.getOrNull(1) ?: 0.0
    val lng = storeLocation?.coordinates?.getOrNull(0) ?: 0.0
    val store = Store(
        id = id, name = storeName, imageUrl = null,
        address = storeAddress, latitude = lat, longitude = lng,
        distanceKm = null, rating = null, reviewCount = 0,
        openingHours = "", isOpen = true, phone = storePhone,
        categories = emptyList(), verificationStatus = VerificationStatus.VERIFIED
    )
    val inventory = Inventory(
        id = id, totalStock = quantity, availableStock = quantity,
        reservedStock = 0, status = InventoryStatus.AVAILABLE
    )
    val variant = ProductVariant(
        id = id, size = null,
        color = variantDescription.ifBlank { null },
        price = price, inventory = inventory
    )
    val product = Product(
        id = id, name = productName, brand = null, description = null,
        imageUrls = productImage?.let { listOf(it) } ?: emptyList(),
        category = "", variants = listOf(variant), store = store
    )
    return Reservation(
        id = id, product = product, variant = variant, store = store,
        status = when (status) {
            "CONFIRMED" -> ReservationStatus.CONFIRMED
            "ACTIVE" -> ReservationStatus.ACTIVE
            "COMPLETED" -> ReservationStatus.COMPLETED
            "CANCELLED" -> ReservationStatus.CANCELLED
            "EXPIRED" -> ReservationStatus.EXPIRED
            else -> ReservationStatus.PENDING
        },
        createdAt = Instant.parse(createdAt),
        expiresAt = Instant.parse(expiresAt),
        completedAt = completedAt?.let { Instant.parse(it) },
        cancelledAt = cancelledAt?.let { Instant.parse(it) },
        policy = if (durationHours <= 6) ReservationPolicy.FreeUser
                 else ReservationPolicy.Subscriber(durationHours)
    )
}

// ── Domain → Entity (for caching) ────────────────────────────────────────────

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id, name = name, brand = brand, description = description,
    imageUrls = imageUrls, category = category,
    variants = variants.map {
        ProductVariantEntity(
            id = it.id, productId = id, size = it.size, color = it.color,
            price = it.price,
            inventoryStatus = it.inventory.status.name,
            totalStock = it.inventory.totalStock,
            availableStock = it.inventory.availableStock,
            reservedStock = it.inventory.reservedStock
        )
    },
    storeId = store.id, storeName = store.name,
    storeAddress = store.address,
    storeLatitude = store.latitude, storeLongitude = store.longitude,
    storeDistanceKm = store.distanceKm, storeRating = store.rating,
    storeIsOpen = store.isOpen, storePhone = store.phone,
    storeOpeningHours = store.openingHours
)

fun Store.toEntity(): StoreEntity = StoreEntity(
    id = id, name = name, imageUrl = imageUrl, address = address,
    latitude = latitude, longitude = longitude, distanceKm = distanceKm,
    rating = rating, reviewCount = reviewCount, openingHours = openingHours,
    isOpen = isOpen, phone = phone, categories = categories,
    verificationStatus = verificationStatus.name
)

fun PriceComparisonDto.toDomain(): PriceComparisonEntry {
    val store = this.store?.toDomain() ?: Store(
        id = "", name = "Unknown", imageUrl = null, address = "",
        latitude = 0.0, longitude = 0.0, distanceKm = null,
        rating = null, reviewCount = 0, openingHours = "",
        isOpen = true, phone = null, categories = emptyList(),
        verificationStatus = VerificationStatus.PENDING
    )
    return PriceComparisonEntry(
        store = store,
        variantId = variantId,
        price = price,
        availableStock = availableStock,
        distanceKm = distanceKm
    )
}
