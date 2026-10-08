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

    // Location
    object LocationPermission : Screen("location/permission")

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
    object StoreMap : Screen("store/{storeId}/map") {
        fun createRoute(storeId: String) = "store/$storeId/map"
    }

    // Reservation
    object ReservationConfirm : Screen("reservation/confirm/{productId}/{variantId}") {
        fun createRoute(productId: String, variantId: String) = "reservation/confirm/$productId/$variantId"
    }
    object ReservationDetail : Screen("reservation/{reservationId}") {
        fun createRoute(id: String) = "reservation/$id"
    }
    object MyReservations : Screen("reservations")

    // Profile & sub-screens
    object Profile : Screen("profile")
    object Wishlist : Screen("wishlist")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object SavedLocations : Screen("saved-locations")
    object EditProfile : Screen("edit-profile")
    object AddLocation : Screen("add-location")
}
