package com.aaspaas.business.feature.dashboard.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.business.core.ui.components.BusinessAvatar
import com.aaspaas.business.core.ui.components.EmptyState
import com.aaspaas.business.core.ui.components.ErrorState
import com.aaspaas.business.core.ui.components.ShimmerItem
import com.aaspaas.business.core.ui.theme.BusinessAccent
import com.aaspaas.business.core.ui.theme.BusinessBrand
import com.aaspaas.business.core.ui.theme.SurfaceVariant
import com.aaspaas.business.core.ui.theme.White
import com.aaspaas.business.domain.model.DashboardStats
import com.aaspaas.business.feature.dashboard.viewmodel.DashboardViewModel
import com.aaspaas.business.feature.profile.viewmodel.BusinessProfileViewModel
import com.aaspaas.business.navigation.BusinessScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onProductsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onReservationsClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onCustomersClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSupportClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onStoreClick: () -> Unit,
    onLogout: () -> Unit,
    currentRoute: String? = BusinessScreen.Dashboard.route,
    viewModel: DashboardViewModel = hiltViewModel(),
    profileViewModel: BusinessProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val retailer by profileViewModel.retailer.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(16.dp), color = BusinessBrand.copy(alpha = 0.12f)) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = BusinessBrand, modifier = Modifier.padding(12.dp).size(22.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AasPaasWala", style = MaterialTheme.typography.titleMedium, color = BusinessBrand)
                            Text(retailer?.businessName ?: "Business profile", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(16.dp), color = SurfaceVariant, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BusinessAvatar(retailer?.ownerName, modifier = Modifier.size(42.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(retailer?.ownerName ?: "Business owner", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(retailer?.mobile ?: "Contact unavailable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    DrawerItem("Home", Icons.Default.Home, selected = currentRoute == BusinessScreen.Dashboard.route, onClick = { scope.launch { drawerState.close() } })
                    DrawerItem("Products", Icons.Default.Inventory2, selected = currentRoute == BusinessScreen.Products.route, onClick = { scope.launch { drawerState.close() }; onProductsClick() })
                    DrawerItem("Reservations", Icons.Default.Bookmark, selected = currentRoute == BusinessScreen.Reservations.route, onClick = { scope.launch { drawerState.close() }; onReservationsClick() })
                    DrawerItem("Analytics", Icons.Default.BarChart, selected = currentRoute == BusinessScreen.Analytics.route, onClick = { scope.launch { drawerState.close() }; onAnalyticsClick() })
                    DrawerItem("Store", Icons.Default.Store, selected = currentRoute == BusinessScreen.StoreProfile.route, onClick = { scope.launch { drawerState.close() }; onStoreClick() })
                    DrawerItem("Notifications", Icons.Default.Notifications, selected = currentRoute == BusinessScreen.Notifications.route, onClick = { scope.launch { drawerState.close() }; onNotificationsClick() })
                    DrawerItem("Settings", Icons.Default.Settings, selected = currentRoute == BusinessScreen.Settings.route, onClick = { scope.launch { drawerState.close() }; onSettingsClick() })
                    DrawerItem("Help & Support", Icons.Default.HeadsetMic, selected = currentRoute == BusinessScreen.Support.route, onClick = { scope.launch { drawerState.close() }; onSupportClick() })
                    Spacer(Modifier.height(8.dp))
                    Divider()
                    DrawerItem("Logout", Icons.Default.Logout, selected = false, onClick = {
                        scope.launch { drawerState.close() }
                        profileViewModel.logout(onDone = onLogout)
                    })
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Hi, ${retailer?.ownerName ?: "Business Owner"}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(retailer?.businessName ?: "Business profile", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open navigation")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
                when {
                    state.isLoading -> DashboardShimmerScreen()
                    state.error != null -> ErrorState(
                        message = state.error ?: "Unable to load dashboard",
                        onRetry = viewModel::loadStats,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    state.stats != null -> DashboardContent(
                        retailer = retailer,
                        stats = state.stats!!,
                        onProductsClick = onProductsClick,
                        onInventoryClick = onInventoryClick,
                        onReservationsClick = onReservationsClick,
                        onAddProductClick = onAddProductClick,
                        onAnalyticsClick = onAnalyticsClick,
                        onNotificationsClick = onNotificationsClick
                    )
                    else -> DashboardContent(
                        retailer = retailer,
                        stats = DashboardStats(totalProducts = 0, availableProducts = 0, lowStockProducts = 0, activeReservations = 0, todayReservations = 0),
                        onProductsClick = onProductsClick,
                        onInventoryClick = onInventoryClick,
                        onReservationsClick = onReservationsClick,
                        onAddProductClick = onAddProductClick,
                        onAnalyticsClick = onAnalyticsClick,
                        onNotificationsClick = onNotificationsClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardShimmerScreen() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShimmerItem(Modifier.weight(1f).height(120.dp), shape = RoundedCornerShape(20.dp))
                ShimmerItem(Modifier.weight(1f).height(120.dp), shape = RoundedCornerShape(20.dp))
                ShimmerItem(Modifier.weight(1f).height(120.dp), shape = RoundedCornerShape(20.dp))
            }
        }
        item { ShimmerItem(Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(24.dp)) }
        item { ShimmerItem(Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(24.dp)) }
    }
}

@Composable
private fun DashboardContent(
    retailer: com.aaspaas.business.domain.model.Retailer?,
    stats: DashboardStats,
    onProductsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onReservationsClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceVariant
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BusinessAvatar(retailer?.ownerName, modifier = Modifier.size(40.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hi, ${retailer?.ownerName ?: "Business Owner"}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Text(retailer?.businessName ?: "Business profile", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(shape = RoundedCornerShape(50), color = BusinessBrand.copy(alpha = 0.12f)) {
                            Text(
                                text = retailer?.verificationStatus?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.titlecase() } ?: "Verification pending",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = BusinessBrand,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Today's Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardMetricCard("Total Sales", "Data unavailable", BusinessBrand, Modifier.weight(1f))
                DashboardMetricCard("Orders", "Data unavailable", BusinessAccent, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardMetricCard("Reservations", stats.activeReservations.takeIf { it > 0 }?.toString() ?: "Data unavailable", BusinessBrand, Modifier.weight(1f))
                DashboardMetricCard("Low Stock", stats.lowStockProducts.takeIf { it > 0 }?.toString() ?: "Data unavailable", Color(0xFFF59E0B), Modifier.weight(1f))
            }
        }

        item {
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionButton("Add Product", Icons.Default.AddCircle, Modifier.weight(1f), onAddProductClick)
                QuickActionButton("Update Stock", Icons.Default.Inventory2, Modifier.weight(1f), onInventoryClick)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionButton("Reservations", Icons.Default.Bookmark, Modifier.weight(1f), onReservationsClick)
                QuickActionButton("View Reports", Icons.Default.BarChart, Modifier.weight(1f), onAnalyticsClick)
            }
        }

        item {
            Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        item {
            if (stats.activeReservations == 0 && stats.lowStockProducts == 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BusinessBrand.copy(alpha = 0.15f))
                ) {
                    EmptyState(title = "No recent activity", description = "New reservations and stock updates will appear here.", modifier = Modifier.fillMaxWidth())
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BusinessBrand.copy(alpha = 0.12f))
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActivityItem("Reservation received", "${stats.activeReservations} active reservation(s) pending action", Icons.Default.Notifications, BusinessBrand)
                        ActivityItem("Low stock alert", "${stats.lowStockProducts} item(s) need attention", Icons.Default.Warning, Color(0xFFF59E0B))
                        ActivityItem("Business update", "Inventory and reservations data synced", Icons.Default.CheckCircle, BusinessAccent)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = accent, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = White),
        border = BorderStroke(1.dp, BusinessBrand.copy(alpha = 0.15f))
    ) {
        Icon(icon, null, tint = BusinessBrand, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun DrawerItem(label: String, icon: ImageVector, selected: Boolean = false, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = BusinessBrand.copy(alpha = 0.12f),
            selectedTextColor = BusinessBrand,
            selectedIconColor = BusinessBrand
        )
    )
}

@Composable
private fun ActivityItem(label: String, detail: String, icon: ImageVector, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(12.dp), color = tint.copy(alpha = 0.12f)) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(8.dp).size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
