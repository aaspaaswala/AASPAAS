package com.aaspaas.customer.data.model

import com.aaspaas.customer.data.remote.*
import com.aaspaas.customer.domain.model.*
import java.time.Instant

fun UserDto.toDomain() = User(
    id = id, name = name, mobile = mobile, email = email
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
    inventory = inventory.toDomain()
)

fun ProductDto.toDomain() = Product(
    id = id, name = name, brand = brand, description = description,
    imageUrls = imageUrls, category = category,
    variants = variants.map { it.toDomain() },
    store = store.toDomain()
)

fun ReservationDto.toDomain(): Reservation {
    val lat = storeLocation?.coordinates?.getOrNull(1) ?: 0.0
    val lng = storeLocation?.coordinates?.getOrNull(0) ?: 0.0
    return Reservation(
        id = id,
        product = Product(
            id = id,
            name = productName,
            brand = null,
            description = null,
            imageUrls = productImage?.let { listOf(it) } ?: emptyList(),
            category = "",
            variants = emptyList(),
            store = Store(
                id = id,
                name = storeName,
                imageUrl = null,
                address = storeAddress,
                latitude = lat,
                longitude = lng,
                distanceKm = null,
                rating = null,
                reviewCount = 0,
                openingHours = "",
                isOpen = true,
                phone = storePhone,
                categories = emptyList(),
                verificationStatus = com.aaspaas.customer.domain.model.VerificationStatus.PENDING
            )
        ),
        variant = ProductVariant(
            id = id,
            size = null,
            color = null,
            price = price,
            inventory = Inventory(
                id = id,
                totalStock = quantity,
                availableStock = quantity,
                reservedStock = 0,
                status = com.aaspaas.customer.domain.model.InventoryStatus.AVAILABLE
            )
        ),
        store = Store(
            id = id,
            name = storeName,
            imageUrl = null,
            address = storeAddress,
            latitude = lat,
            longitude = lng,
            distanceKm = null,
            rating = null,
            reviewCount = 0,
            openingHours = "",
            isOpen = true,
            phone = storePhone,
            categories = emptyList(),
            verificationStatus = com.aaspaas.customer.domain.model.VerificationStatus.PENDING
        ),
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
