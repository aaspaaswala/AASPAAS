package com.aaspaas.business.data.model

import com.aaspaas.business.data.remote.*
import com.aaspaas.business.domain.model.*
import java.time.Instant

fun RetailerDto.toDomain() = Retailer(
    id = id, ownerName = ownerName, businessName = businessName,
    mobile = mobile, email = email,
    verificationStatus = when (verificationStatus) {
        "VERIFIED" -> VerificationStatus.VERIFIED
        "REJECTED" -> VerificationStatus.REJECTED
        else -> VerificationStatus.PENDING
    }
)

fun StoreDto.toDomain() = Store(
    id = id, name = name, address = address, latitude = latitude, longitude = longitude,
    phone = phone, openingHours = openingHours, categories = categories,
    imageUrl = imageUrl,
    verificationStatus = when (verificationStatus) {
        "VERIFIED" -> VerificationStatus.VERIFIED
        "REJECTED" -> VerificationStatus.REJECTED
        else -> VerificationStatus.PENDING
    }
)

fun InventoryDto.toDomain() = Inventory(
    id = id, totalStock = totalStock, availableStock = availableStock, reservedStock = reservedStock,
    status = when (status) {
        "LOW_STOCK" -> InventoryStatus.LOW_STOCK
        "OUT_OF_STOCK" -> InventoryStatus.OUT_OF_STOCK
        "RESERVED" -> InventoryStatus.RESERVED
        else -> InventoryStatus.AVAILABLE
    }
)

fun ProductVariantDto.toDomain() = ProductVariant(
    id = id, size = size, color = color, price = price, inventory = inventory.toDomain()
)

fun ProductDto.toDomain() = Product(
    id = id, name = name, brand = brand, description = description,
    imageUrls = imageUrls, category = category, variants = variants.map { it.toDomain() }
)

fun ReservationDto.toDomain() = Reservation(
    id = id, customerName = customerName, customerMobile = customerMobile,
    productName = productName, variantDescription = variantDescription, price = price,
    quantity = quantity, reservationCode = reservationCode,
    status = when (status) {
        "CONFIRMED" -> ReservationStatus.CONFIRMED
        "READY" -> ReservationStatus.READY
        "COMPLETED" -> ReservationStatus.COMPLETED
        "CANCELLED" -> ReservationStatus.CANCELLED
        "EXPIRED" -> ReservationStatus.EXPIRED
        "REJECTED" -> ReservationStatus.REJECTED
        "NO_SHOW" -> ReservationStatus.NO_SHOW
        else -> ReservationStatus.PENDING
    },
    createdAt = Instant.parse(createdAt),
    expiresAt = Instant.parse(expiresAt),
    completedAt = completedAt?.let { Instant.parse(it) },
    cancelledAt = cancelledAt?.let { Instant.parse(it) },
    rejectedAt = rejectedAt?.let { Instant.parse(it) }
)

fun DashboardStatsDto.toDomain() = DashboardStats(
    totalProducts = totalProducts, availableProducts = availableProducts,
    lowStockProducts = lowStockProducts, activeReservations = activeReservations,
    todayReservations = todayReservations
)
