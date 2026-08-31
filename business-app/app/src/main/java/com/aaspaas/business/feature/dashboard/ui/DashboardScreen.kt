package com.aaspaas.business.feature.dashboard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.business.core.ui.components.ErrorScreen
import com.aaspaas.business.core.ui.components.LoadingScreen
import com.aaspaas.business.core.ui.components.SectionHeader
import com.aaspaas.business.core.ui.components.ShimmerItem
import com.aaspaas.business.core.ui.components.StatCardShimmer
import com.aaspaas.business.core.ui.theme.*
import com.aaspaas.business.domain.model.DashboardStats
import com.aaspaas.business.feature.dashboard.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onProductsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onReservationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStoreClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("APW Business", style = MaterialTheme.typography.titleLarge)
                        Text("Dashboard", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) { Icon(Icons.Default.Person, null) }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Dashboard, null) }, label = { Text("Dashboard") })
                NavigationBarItem(selected = false, onClick = onProductsClick, icon = { Icon(Icons.Default.Inventory, null) }, label = { Text("Products") })
                NavigationBarItem(selected = false, onClick = onReservationsClick, icon = { Icon(Icons.Default.BookmarkBorder, null) }, label = { Text("Reservations") })
                NavigationBarItem(selected = false, onClick = onStoreClick, icon = { Icon(Icons.Default.Store, null) }, label = { Text("Store") })
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            when {
                state.isLoading -> DashboardShimmerScreen()
                state.stats != null -> DashboardContent(
                    stats = state.stats!!,
                    onProductsClick = onProductsClick,
                    onInventoryClick = onInventoryClick,
                    onReservationsClick = onReservationsClick
                )
                else -> {
                    DashboardContent(
                        stats = DashboardStats(0, 0, 0, 0, 0),
                        onProductsClick = onProductsClick,
                        onInventoryClick = onInventoryClick,
                        onReservationsClick = onReservationsClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardShimmerScreen() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(4) { StatCardShimmer() }
    }
    Spacer(Modifier.height(24.dp))
    Text("Quick Actions", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ShimmerItem(modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(12.dp))
        ShimmerItem(modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(12.dp))
    }
}

@Composable
private fun DashboardContent(
    stats: DashboardStats,
    onProductsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onReservationsClick: () -> Unit
) {
    Text("Overview", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(12.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            StatCard("Total Products", stats.totalProducts.toString(), Icons.Default.Inventory2, BusinessBrand, onProductsClick)
        }
        item {
            StatCard("Active Reservations", stats.activeReservations.toString(), Icons.Default.BookmarkAdded, StateActive, onReservationsClick)
        }
        item {
            StatCard("Today's Reservations", stats.todayReservations.toString(), Icons.Default.Today, BusinessAccent, onReservationsClick)
        }
        item {
            StatCard("Low Stock", stats.lowStockProducts.toString(), Icons.Default.Warning, LowStock, onInventoryClick)
        }
    }

    Spacer(Modifier.height(24.dp))
    Text("Quick Actions", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickActionButton("Add Product", Icons.Default.AddBox, Modifier.weight(1f), onProductsClick)
        QuickActionButton("View Reservations", Icons.Default.List, Modifier.weight(1f), onReservationsClick)
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineLarge, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, null, Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
