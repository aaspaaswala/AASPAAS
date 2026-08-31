package com.aaspaas.business.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aaspaas.business.feature.auth.ui.BusinessOtpScreen
import com.aaspaas.business.feature.auth.ui.BusinessPhoneAuthScreen
import com.aaspaas.business.feature.auth.ui.BusinessRegisterScreen
import com.aaspaas.business.feature.auth.ui.BusinessSplashScreen
import com.aaspaas.business.feature.dashboard.ui.DashboardScreen
import com.aaspaas.business.feature.inventory.ui.InventoryScreen
import com.aaspaas.business.feature.product.ui.AddEditProductScreen
import com.aaspaas.business.feature.product.ui.ProductListScreen
import com.aaspaas.business.feature.profile.ui.BusinessProfileScreen
import com.aaspaas.business.feature.reservation.ui.BusinessReservationDetailScreen
import com.aaspaas.business.feature.reservation.ui.BusinessReservationsScreen
import com.aaspaas.business.feature.store.ui.StoreProfileScreen

@Composable
fun BusinessNavGraph(
    navController: NavHostController,
    startDestination: String = BusinessScreen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(BusinessScreen.Splash.route) {
            BusinessSplashScreen(
                onNavigateToAuth = { navController.navigate(BusinessScreen.AuthPhone.route) { popUpTo(BusinessScreen.Splash.route) { inclusive = true } } },
                onNavigateToDashboard = { navController.navigate(BusinessScreen.Dashboard.route) { popUpTo(BusinessScreen.Splash.route) { inclusive = true } } }
            )
        }

        composable(BusinessScreen.AuthPhone.route) {
            BusinessPhoneAuthScreen(
                onOtpSent = { mobile -> navController.navigate(BusinessScreen.AuthOtp.createRoute(mobile)) },
                onRegisterClick = { navController.navigate(BusinessScreen.Register.route) }
            )
        }

        composable(
            route = BusinessScreen.AuthOtp.route,
            arguments = listOf(navArgument("mobile") { type = NavType.StringType })
        ) { back ->
            val mobile = back.arguments?.getString("mobile") ?: ""
            BusinessOtpScreen(
                mobile = mobile,
                onVerified = { navController.navigate(BusinessScreen.Dashboard.route) { popUpTo(BusinessScreen.AuthPhone.route) { inclusive = true } } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(BusinessScreen.Register.route) {
            BusinessRegisterScreen(
                onRegistered = { navController.navigate(BusinessScreen.Dashboard.route) { popUpTo(BusinessScreen.AuthPhone.route) { inclusive = true } } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(BusinessScreen.Dashboard.route) {
            DashboardScreen(
                onProductsClick = { navController.navigate(BusinessScreen.Products.route) },
                onInventoryClick = { navController.navigate(BusinessScreen.Inventory.route) },
                onReservationsClick = { navController.navigate(BusinessScreen.Reservations.route) },
                onProfileClick = { navController.navigate(BusinessScreen.Profile.route) },
                onStoreClick = { navController.navigate(BusinessScreen.StoreProfile.route) }
            )
        }

        composable(BusinessScreen.Products.route) {
            ProductListScreen(
                onAddClick = { navController.navigate(BusinessScreen.AddProduct.route) },
                onEditClick = { id -> navController.navigate(BusinessScreen.EditProduct.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(BusinessScreen.AddProduct.route) {
            AddEditProductScreen(productId = null, onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }

        composable(
            route = BusinessScreen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { back ->
            val productId = back.arguments?.getString("productId") ?: ""
            AddEditProductScreen(productId = productId, onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Inventory.route) {
            InventoryScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Reservations.route) {
            BusinessReservationsScreen(
                onReservationClick = { id -> navController.navigate(BusinessScreen.ReservationDetail.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = BusinessScreen.ReservationDetail.route,
            arguments = listOf(navArgument("reservationId") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("reservationId") ?: ""
            BusinessReservationDetailScreen(reservationId = id, onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.StoreProfile.route) {
            StoreProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Profile.route) {
            BusinessProfileScreen(
                onLogout = { navController.navigate(BusinessScreen.AuthPhone.route) { popUpTo(BusinessScreen.Dashboard.route) { inclusive = true } } },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
