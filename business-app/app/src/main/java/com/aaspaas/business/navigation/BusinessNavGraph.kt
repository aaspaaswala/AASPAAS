package com.aaspaas.business.navigation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aaspaas.business.feature.auth.ui.BusinessLoginScreen
import com.aaspaas.business.feature.auth.ui.BusinessRegisterScreen
import com.aaspaas.business.feature.auth.ui.BusinessSplashScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessAnalyticsScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessBusinessSettingsScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessCustomerDetailScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessCustomersScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessNotificationsScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessOnboardingScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessReservationSettingsScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessReviewsScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessSecuritySettingsScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessSettingsScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessSubscriptionScreen
import com.aaspaas.business.feature.dashboard.ui.BusinessSupportScreen
import com.aaspaas.business.feature.dashboard.ui.DashboardScreen
import com.aaspaas.business.feature.inventory.ui.InventoryScreen
import com.aaspaas.business.feature.product.ui.AddEditProductScreen
import com.aaspaas.business.feature.product.ui.ProductListScreen
import com.aaspaas.business.feature.profile.ui.BusinessProfileScreen
import com.aaspaas.business.feature.reservation.ui.BusinessReservationDetailScreen
import com.aaspaas.business.feature.reservation.ui.BusinessReservationsScreen
import com.aaspaas.business.feature.store.ui.StoreProfileScreen
import com.aaspaas.business.core.ui.theme.BusinessBrand
import com.aaspaas.business.core.ui.theme.White

@Composable
fun BusinessNavGraph(
    navController: NavHostController,
    startDestination: String = BusinessScreen.Splash.route
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in setOf(
        BusinessScreen.Dashboard.route,
        BusinessScreen.Products.route,
        BusinessScreen.AddProduct.route,
        BusinessScreen.Profile.route,
        BusinessScreen.EditProduct.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = White,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == BusinessScreen.Dashboard.route,
                        onClick = { navigateTab(navController, BusinessScreen.Dashboard.route) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BusinessBrand,
                            selectedTextColor = BusinessBrand,
                            indicatorColor = BusinessBrand.copy(alpha = 0.12f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == BusinessScreen.Products.route,
                        onClick = { navigateTab(navController, BusinessScreen.Products.route) },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = null) },
                        label = { Text("Products") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BusinessBrand,
                            selectedTextColor = BusinessBrand,
                            indicatorColor = BusinessBrand.copy(alpha = 0.12f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == BusinessScreen.AddProduct.route || currentRoute == BusinessScreen.EditProduct.route,
                        onClick = { navigateTab(navController, BusinessScreen.AddProduct.route) },
                        icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                        label = { Text("Add item") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BusinessBrand,
                            selectedTextColor = BusinessBrand,
                            indicatorColor = BusinessBrand.copy(alpha = 0.12f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == BusinessScreen.Profile.route,
                        onClick = { navigateTab(navController, BusinessScreen.Profile.route) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BusinessBrand,
                            selectedTextColor = BusinessBrand,
                            indicatorColor = BusinessBrand.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { padding ->
      NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = androidx.compose.ui.Modifier.padding(padding)
      ) {

        composable(BusinessScreen.Splash.route) {
            BusinessSplashScreen(
                onNavigateToAuth = { navController.navigate(BusinessScreen.AuthPhone.route) { popUpTo(BusinessScreen.Splash.route) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(BusinessScreen.Register.route) { popUpTo(BusinessScreen.Splash.route) { inclusive = true } } }
            )
        }

        composable(BusinessScreen.AuthPhone.route) {
            BusinessLoginScreen(
                onLoggedIn = { navController.navigate(BusinessScreen.Dashboard.route) { popUpTo(BusinessScreen.AuthPhone.route) { inclusive = true } } },
                onRegisterClick = { navController.navigate(BusinessScreen.Register.route) }
            )
        }

        composable(BusinessScreen.Register.route) {
            BusinessRegisterScreen(
                onRegistered = { navController.navigate(BusinessScreen.Onboarding.route) { popUpTo(BusinessScreen.Register.route) { inclusive = true } } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(BusinessScreen.Onboarding.route) {
            BusinessOnboardingScreen(
                onContinue = { navController.navigate(BusinessScreen.Dashboard.route) { popUpTo(BusinessScreen.Onboarding.route) { inclusive = true } } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(BusinessScreen.Dashboard.route) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            DashboardScreen(
                onProductsClick = { navController.navigate(BusinessScreen.Products.route) },
                onInventoryClick = { navController.navigate(BusinessScreen.Inventory.route) },
                onReservationsClick = { navController.navigate(BusinessScreen.Reservations.route) },
                onAddProductClick = { navController.navigate(BusinessScreen.AddProduct.route) },
                onAnalyticsClick = { navController.navigate(BusinessScreen.Analytics.route) },
                onCustomersClick = { navController.navigate(BusinessScreen.Customers.route) },
                onReviewsClick = { navController.navigate(BusinessScreen.Reviews.route) },
                onNotificationsClick = { navController.navigate(BusinessScreen.Notifications.route) },
                onSettingsClick = { navController.navigate(BusinessScreen.Settings.route) },
                onSupportClick = { navController.navigate(BusinessScreen.Support.route) },
                onSubscriptionClick = { navController.navigate(BusinessScreen.Subscription.route) },
                onStoreClick = { navController.navigate(BusinessScreen.StoreProfile.route) },
                onLogout = {
                    navController.navigate(BusinessScreen.AuthPhone.route) {
                        popUpTo(BusinessScreen.Dashboard.route) { inclusive = true }
                    }
                },
                currentRoute = currentRoute
            )
        }

        composable(BusinessScreen.Analytics.route) {
            BusinessAnalyticsScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Customers.route) {
            BusinessCustomersScreen(
                onBack = { navController.popBackStack() },
                onCustomerClick = { id -> navController.navigate(BusinessScreen.CustomerDetail.createRoute(id)) }
            )
        }

        composable(
            route = BusinessScreen.CustomerDetail.route,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { back ->
            val customerId = back.arguments?.getString("customerId") ?: ""
            BusinessCustomerDetailScreen(customerId = customerId, onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Reviews.route) {
            BusinessReviewsScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Notifications.route) {
            BusinessNotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Settings.route) {
            BusinessSettingsScreen(
                onBack = { navController.popBackStack() },
                onBusinessSettings = { navController.navigate(BusinessScreen.BusinessSettings.route) },
                onReservationSettings = { navController.navigate(BusinessScreen.ReservationSettings.route) },
                onSecuritySettings = { navController.navigate(BusinessScreen.SecuritySettings.route) }
            )
        }

        composable(BusinessScreen.BusinessSettings.route) {
            BusinessBusinessSettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.ReservationSettings.route) {
            BusinessReservationSettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.SecuritySettings.route) {
            BusinessSecuritySettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Support.route) {
            BusinessSupportScreen(onBack = { navController.popBackStack() })
        }

        composable(BusinessScreen.Subscription.route) {
            BusinessSubscriptionScreen(onBack = { navController.popBackStack() })
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
                onBack = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(BusinessScreen.Settings.route) },
                onSupportClick = { navController.navigate(BusinessScreen.Support.route) },
                onSubscriptionClick = { navController.navigate(BusinessScreen.Subscription.route) },
                onStoreClick = { navController.navigate(BusinessScreen.StoreProfile.route) }
            )
        }
      }
    }
}

private fun navigateTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(BusinessScreen.Dashboard.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
