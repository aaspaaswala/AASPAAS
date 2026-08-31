package com.aaspaas.customer.navigation

sealed class Screen(val route: String) {
    // Root
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")

    // Auth
    object AuthPhone : Screen("auth/phone")
    object AuthOtp : Screen("auth/otp/{mobile}") {
        fun createRoute(mobile: String) = "auth/otp/$mobile"
    }

    // Main
    object Home : Screen("home")
    object Search : Screen("search?query={query}") {
        fun createRoute(query: String = "") = "search?query=$query"
    }

    // Product
    object ProductDetail : Screen("product/{productId}/variant/{variantId}") {
        fun createRoute(productId: String, variantId: String) = "product/$productId/variant/$variantId"
    }

    // Store
    object StoreDetail : Screen("store/{storeId}") {
        fun createRoute(storeId: String) = "store/$storeId"
    }

    // Reservation
    object ReservationConfirm : Screen("reservation/confirm/{variantId}") {
        fun createRoute(variantId: String) = "reservation/confirm/$variantId"
    }
    object ReservationDetail : Screen("reservation/{reservationId}") {
        fun createRoute(id: String) = "reservation/$id"
    }
    object MyReservations : Screen("reservations")

    // Profile
    object Profile : Screen("profile")
}
