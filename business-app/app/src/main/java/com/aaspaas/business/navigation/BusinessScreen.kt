package com.aaspaas.business.navigation

sealed class BusinessScreen(val route: String) {
    object Splash : BusinessScreen("splash")
    object AuthPhone : BusinessScreen("auth/phone")
    object AuthOtp : BusinessScreen("auth/otp/{mobile}") {
        fun createRoute(mobile: String) = "auth/otp/$mobile"
    }
    object Register : BusinessScreen("auth/register")
    object Dashboard : BusinessScreen("dashboard")
    object Products : BusinessScreen("products")
    object AddProduct : BusinessScreen("products/add")
    object EditProduct : BusinessScreen("products/edit/{productId}") {
        fun createRoute(id: String) = "products/edit/$id"
    }
    object Inventory : BusinessScreen("inventory")
    object Reservations : BusinessScreen("reservations")
    object ReservationDetail : BusinessScreen("reservations/{reservationId}") {
        fun createRoute(id: String) = "reservations/$id"
    }
    object StoreProfile : BusinessScreen("store/profile")
    object Profile : BusinessScreen("profile")
}
