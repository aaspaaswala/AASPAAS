package com.aaspaas.customer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aaspaas.customer.core.ui.components.EmptyScreen
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.feature.auth.ui.LoginScreen
import com.aaspaas.customer.feature.home.ui.HomeScreen
import com.aaspaas.customer.feature.home.ui.OnboardingScreen
import com.aaspaas.customer.feature.home.ui.SplashScreen
import com.aaspaas.customer.feature.location.ui.LocationPermissionScreen
import com.aaspaas.customer.feature.notifications.ui.NotificationsScreen
import com.aaspaas.customer.feature.product.ui.ProductDetailScreen
import com.aaspaas.customer.feature.profile.ui.ProfileScreen
import com.aaspaas.customer.feature.reservation.ui.MyReservationsScreen
import com.aaspaas.customer.feature.reservation.ui.ReservationConfirmScreen
import com.aaspaas.customer.feature.reservation.ui.ReservationDetailScreen
import com.aaspaas.customer.feature.search.ui.SearchScreen
import com.aaspaas.customer.feature.settings.ui.SettingsScreen
import com.aaspaas.customer.feature.store.ui.StoreDetailScreen
import com.aaspaas.customer.feature.store.ui.StoreMapScreen
import com.aaspaas.customer.feature.wishlist.ui.WishlistScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        // ── Root ──────────────────────────────────────────────────────────────

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.AuthPhone.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Auth ──────────────────────────────────────────────────────────────

        composable(Screen.AuthPhone.route) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Screen.LocationPermission.route) {
                        popUpTo(Screen.AuthPhone.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Location ──────────────────────────────────────────────────────────

        composable(Screen.LocationPermission.route) {
            LocationPermissionScreen(
                onEnable = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationPermission.route) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationPermission.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────────

        composable(Screen.Home.route) {
            HomeScreen(
                onSearchClick = { query -> navController.navigate(Screen.Search.createRoute(query)) },
                onProductClick = { productId, variantId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId, variantId))
                },
                onStoreClick = { storeId -> navController.navigate(Screen.StoreDetail.createRoute(storeId)) },
                onReservationsClick = { navController.navigate(Screen.MyReservations.route) },
                onWishlistClick = { navController.navigate(Screen.Wishlist.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        // ── Search ────────────────────────────────────────────────────────────

        composable(
            route = Screen.Search.route,
            arguments = listOf(navArgument("query") { type = NavType.StringType; defaultValue = "" })
        ) { backStack ->
            val query = backStack.arguments?.getString("query") ?: ""
            SearchScreen(
                initialQuery = query,
                onProductClick = { productId, variantId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId, variantId))
                },
                onStoreClick = { storeId -> navController.navigate(Screen.StoreDetail.createRoute(storeId)) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Product ───────────────────────────────────────────────────────────

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("variantId") { type = NavType.StringType }
            )
        ) { backStack ->
            val productId = backStack.arguments?.getString("productId") ?: ""
            val variantId = backStack.arguments?.getString("variantId") ?: ""
            ProductDetailScreen(
                productId = productId,
                variantId = variantId,
                onReserveClick = { vid ->
                    navController.navigate(Screen.ReservationConfirm.createRoute(productId, vid))
                },
                onStoreClick = { storeId -> navController.navigate(Screen.StoreDetail.createRoute(storeId)) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Store ─────────────────────────────────────────────────────────────

        composable(
            route = Screen.StoreDetail.route,
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStack ->
            val storeId = backStack.arguments?.getString("storeId") ?: ""
            StoreDetailScreen(
                storeId = storeId,
                onProductClick = { productId, variantId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId, variantId))
                },
                onStoreMapClick = { navController.navigate(Screen.StoreMap.createRoute(storeId)) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Reservation ───────────────────────────────────────────────────────

        composable(
            route = Screen.ReservationConfirm.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("variantId") { type = NavType.StringType }
            )
        ) { backStack ->
            val productId = backStack.arguments?.getString("productId") ?: ""
            val variantId = backStack.arguments?.getString("variantId") ?: ""
            ReservationConfirmScreen(
                productId = productId,
                variantId = variantId,
                onConfirmed = { reservationId ->
                    navController.navigate(Screen.ReservationDetail.createRoute(reservationId)) {
                        popUpTo(Screen.ReservationConfirm.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StoreMap.route,
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStack ->
            val storeId = backStack.arguments?.getString("storeId") ?: ""
            StoreMapScreen(
                storeId = storeId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ReservationDetail.route,
            arguments = listOf(navArgument("reservationId") { type = NavType.StringType })
        ) { backStack ->
            val reservationId = backStack.arguments?.getString("reservationId") ?: ""
            ReservationDetailScreen(
                reservationId = reservationId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MyReservations.route) {
            MyReservationsScreen(
                onReservationClick = { id -> navController.navigate(Screen.ReservationDetail.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Profile & sub-screens ─────────────────────────────────────────────

        composable(Screen.Profile.route) {
            ProfileScreen(
                onReservationsClick = { navController.navigate(Screen.MyReservations.route) },
                onWishlistClick = { navController.navigate(Screen.Wishlist.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.AuthPhone.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Wishlist.route) {
            WishlistScreen(
                onProductClick = { productId, variantId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId, variantId))
                },
                onStoreClick = { storeId -> navController.navigate(Screen.StoreDetail.createRoute(storeId)) },
                onExplore = { navController.navigate(Screen.Search.createRoute()) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        // ── Profile sub-screens ──────────────────────────────────────────────────

        composable(Screen.SavedLocations.route) {
            EmptyScreen(
                message = "Saved Locations",
                subtitle = "Saved locations will appear here.",
                onCta = { navController.popBackStack() },
                ctaLabel = "Back"
            )
        }

        composable(Screen.EditProfile.route) {
            EmptyScreen(
                message = "Edit Profile",
                subtitle = "Edit your profile details here.",
                onCta = { navController.popBackStack() },
                ctaLabel = "Back"
            )
        }

        composable(Screen.AddLocation.route) {
            EmptyScreen(
                message = "Add Location",
                subtitle = "Add a new location here.",
                onCta = { navController.popBackStack() },
                ctaLabel = "Back"
            )
        }
    }
}
