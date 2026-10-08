package com.aaspaas.customer.core.database.mapper

import com.aaspaas.customer.core.database.entity.*
import com.aaspaas.customer.data.remote.*
import com.aaspaas.customer.domain.model.*
import java.time.Instant

// ── DTO → Entity ──────────────────────────────────────────────────────────────

fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    brand = brand,
    description = description,
    imageUrls = imageUrls,
    category = category,
    variants = variants.map { it.toEntity(productId = id) },
    storeId = store?.id,
    storeName = store?.name,
    storeAddress = store?.address,
    storeLatitude = store?.latitude,
    storeLongitude = store?.longitude,
    storeDistanceKm = store?.distanceKm,
    storeRating = store?.rating,
    storeIsOpen = store?.isOpen ?: true,
    storePhone = store?.phone,
    storeOpeningHours = store?.openingHours
)

fun ProductVariantDto.toEntity(productId: String): ProductVariantEntity = ProductVariantEntity(
    id = id,
    productId = productId,
    size = size,
    color = color,
    price = price,
    inventoryStatus = inventory?.status ?: "AVAILABLE",
    totalStock = inventory?.totalStock ?: 0,
    availableStock = inventory?.availableStock ?: 0,
    reservedStock = inventory?.reservedStock ?: 0
)

fun StoreDto.toEntity(): StoreEntity = StoreEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    address = address,
    latitude = latitude,
    longitude = longitude,
    distanceKm = distanceKm,
    rating = rating,
    reviewCount = reviewCount,
    openingHours = openingHours,
    isOpen = isOpen,
    phone = phone,
    categories = categories,
    verificationStatus = verificationStatus
)

fun ReservationDto.toEntity(): ReservationEntity {
    val lat = storeLocation?.coordinates?.getOrNull(1) ?: 0.0
    val lng = storeLocation?.coordinates?.getOrNull(0) ?: 0.0
    return ReservationEntity(
        id = id,
        productId = id,
        productName = productName,
        productImage = productImage,
        variantId = id,
        variantDescription = variantDescription,
        variantSize = null,
        variantColor = null,
        storeId = id,
        storeName = storeName,
        storeAddress = storeAddress,
        storeLatitude = lat,
        storeLongitude = lng,
        storePhone = storePhone,
        price = price,
        quantity = quantity,
        status = status,
        reservationCode = reservationCode,
        durationHours = durationHours,
        policy = if (durationHours <= 6) "FreeUser" else "Subscriber",
        createdAt = Instant.parse(createdAt).toEpochMilli(),
        expiresAt = Instant.parse(expiresAt).toEpochMilli(),
        completedAt = completedAt?.let { Instant.parse(it).toEpochMilli() },
        cancelledAt = cancelledAt?.let { Instant.parse(it).toEpochMilli() }
    )
}

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    brand = brand,
    description = description,
    imageUrls = imageUrls,
    category = category,
    variants = variants.map { it.toDomain() },
    store = Store(
        id = storeId ?: "",
        name = storeName ?: "",
        imageUrl = null,
        address = storeAddress ?: "",
        latitude = storeLatitude ?: 0.0,
        longitude = storeLongitude ?: 0.0,
        distanceKm = storeDistanceKm,
        rating = storeRating,
        reviewCount = 0,
        openingHours = storeOpeningHours ?: "",
        isOpen = storeIsOpen,
        phone = storePhone,
        categories = emptyList(),
        verificationStatus = VerificationStatus.PENDING
    )
)

fun ProductVariantEntity.toDomain(): ProductVariant = ProductVariant(
    id = id,
    size = size,
    color = color,
    price = price,
    inventory = Inventory(
        id = id,
        totalStock = totalStock,
        availableStock = availableStock,
        reservedStock = reservedStock,
        status = when (inventoryStatus) {
            "LOW_STOCK" -> InventoryStatus.LOW_STOCK
            "OUT_OF_STOCK" -> InventoryStatus.OUT_OF_STOCK
            "RESERVED" -> InventoryStatus.RESERVED
            else -> InventoryStatus.AVAILABLE
        }
    )
)

fun StoreEntity.toDomain(): Store = Store(
    id = id,
    name = name,
    imageUrl = imageUrl,
    address = address,
    latitude = latitude,
    longitude = longitude,
    distanceKm = distanceKm,
    rating = rating,
    reviewCount = reviewCount,
    openingHours = openingHours,
    isOpen = isOpen,
    phone = phone,
    categories = categories,
    verificationStatus = when (verificationStatus) {
        "VERIFIED" -> VerificationStatus.VERIFIED
        "REJECTED" -> VerificationStatus.REJECTED
        else -> VerificationStatus.PENDING
    }
)

fun ReservationEntity.toDomain(): Reservation {
    val store = Store(
        id = storeId,
        name = storeName,
        imageUrl = null,
        address = storeAddress,
        latitude = storeLatitude,
        longitude = storeLongitude,
        distanceKm = null,
        rating = null,
        reviewCount = 0,
        openingHours = "",
        isOpen = true,
        phone = storePhone,
        categories = emptyList(),
        verificationStatus = VerificationStatus.VERIFIED
    )
    val inventory = Inventory(
        id = variantId,
        totalStock = quantity,
        availableStock = quantity,
        reservedStock = 0,
        status = InventoryStatus.AVAILABLE
    )
    val variant = ProductVariant(
        id = variantId,
        size = variantSize,
        color = variantColor ?: variantDescription.ifBlank { null },
        price = price,
        inventory = inventory
    )
    val product = Product(
        id = productId,
        name = productName,
        brand = null,
        description = null,
        imageUrls = productImage?.let { listOf(it) } ?: emptyList(),
        category = "",
        variants = listOf(variant),
        store = store
    )
    return Reservation(
        id = id,
        product = product,
        variant = variant,
        store = store,
        status = when (status) {
            "CONFIRMED" -> ReservationStatus.CONFIRMED
            "ACTIVE" -> ReservationStatus.ACTIVE
            "COMPLETED" -> ReservationStatus.COMPLETED
            "CANCELLED" -> ReservationStatus.CANCELLED
            "EXPIRED" -> ReservationStatus.EXPIRED
            else -> ReservationStatus.PENDING
        },
        createdAt = Instant.ofEpochMilli(createdAt),
        expiresAt = Instant.ofEpochMilli(expiresAt),
        completedAt = completedAt?.let { Instant.ofEpochMilli(it) },
        cancelledAt = cancelledAt?.let { Instant.ofEpochMilli(it) },
        policy = if (policy == "Subscriber") ReservationPolicy.Subscriber(durationHours)
                 else ReservationPolicy.FreeUser
    )
}
