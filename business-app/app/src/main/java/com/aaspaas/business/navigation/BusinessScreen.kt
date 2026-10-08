package com.aaspaas.business.navigation

sealed class BusinessScreen(val route: String) {
    object Splash : BusinessScreen("splash")
    object AuthPhone : BusinessScreen("auth/phone")
    object Register : BusinessScreen("auth/register")
    object Onboarding : BusinessScreen("onboarding")
    object Dashboard : BusinessScreen("dashboard")
    object Analytics : BusinessScreen("analytics")
    object Customers : BusinessScreen("customers")
    object CustomerDetail : BusinessScreen("customers/{customerId}") {
        fun createRoute(id: String) = "customers/$id"
    }
    object Reviews : BusinessScreen("reviews")
    object Notifications : BusinessScreen("notifications")
    object Settings : BusinessScreen("settings")
    object BusinessSettings : BusinessScreen("settings/business")
    object ReservationSettings : BusinessScreen("settings/reservations")
    object SecuritySettings : BusinessScreen("settings/security")
    object Support : BusinessScreen("support")
    object Subscription : BusinessScreen("subscription")
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
    object StoreHours : BusinessScreen("store/hours")
    object StoreLocation : BusinessScreen("store/location")
    object Profile : BusinessScreen("profile")
}
