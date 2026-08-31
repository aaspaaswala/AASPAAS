package com.aaspaas.customer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aaspaas.customer.feature.auth.ui.OtpScreen
import com.aaspaas.customer.feature.auth.ui.PhoneAuthScreen
import com.aaspaas.customer.feature.home.ui.HomeScreen
import com.aaspaas.customer.feature.home.ui.OnboardingScreen
import com.aaspaas.customer.feature.home.ui.SplashScreen
import com.aaspaas.customer.feature.product.ui.ProductDetailScreen
import com.aaspaas.customer.feature.profile.ui.ProfileScreen
import com.aaspaas.customer.feature.reservation.ui.MyReservationsScreen
import com.aaspaas.customer.feature.reservation.ui.ReservationConfirmScreen
import com.aaspaas.customer.feature.reservation.ui.ReservationDetailScreen
import com.aaspaas.customer.feature.search.ui.SearchScreen
import com.aaspaas.customer.feature.store.ui.StoreDetailScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = { navController.navigate(Screen.AuthPhone.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }
            )
        }

        composable(Screen.AuthPhone.route) {
            PhoneAuthScreen(
                onOtpSent = { mobile -> navController.navigate(Screen.AuthOtp.createRoute(mobile)) }
            )
        }

        composable(
            route = Screen.AuthOtp.route,
            arguments = listOf(navArgument("mobile") { type = NavType.StringType })
        ) { backStack ->
            val mobile = backStack.arguments?.getString("mobile") ?: ""
            OtpScreen(
                mobile = mobile,
                onVerified = { navController.navigate(Screen.Home.route) { popUpTo(Screen.AuthPhone.route) { inclusive = true } } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onSearchClick = { query -> navController.navigate(Screen.Search.createRoute(query)) },
                onProductClick = { productId, variantId -> navController.navigate(Screen.ProductDetail.createRoute(productId, variantId)) },
                onStoreClick = { storeId -> navController.navigate(Screen.StoreDetail.createRoute(storeId)) },
                onReservationsClick = { navController.navigate(Screen.MyReservations.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.Search.route,
            arguments = listOf(navArgument("query") { type = NavType.StringType; defaultValue = "" })
        ) { backStack ->
            val query = backStack.arguments?.getString("query") ?: ""
            SearchScreen(
                initialQuery = query,
                onProductClick = { productId, variantId -> navController.navigate(Screen.ProductDetail.createRoute(productId, variantId)) },
                onStoreClick = { storeId -> navController.navigate(Screen.StoreDetail.createRoute(storeId)) },
                onBack = { navController.popBackStack() }
            )
        }

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
                onReserveClick = { vid -> navController.navigate(Screen.ReservationConfirm.createRoute(vid)) },
                onStoreClick = { storeId -> navController.navigate(Screen.StoreDetail.createRoute(storeId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StoreDetail.route,
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStack ->
            val storeId = backStack.arguments?.getString("storeId") ?: ""
            StoreDetailScreen(
                storeId = storeId,
                onProductClick = { productId, variantId -> navController.navigate(Screen.ProductDetail.createRoute(productId, variantId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ReservationConfirm.route,
            arguments = listOf(navArgument("variantId") { type = NavType.StringType })
        ) { backStack ->
            val variantId = backStack.arguments?.getString("variantId") ?: ""
            ReservationConfirmScreen(
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

        composable(Screen.Profile.route) {
            ProfileScreen(
                onReservationsClick = { navController.navigate(Screen.MyReservations.route) },
                onLogout = {
                    navController.navigate(Screen.AuthPhone.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
