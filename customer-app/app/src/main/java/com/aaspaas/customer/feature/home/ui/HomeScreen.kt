package com.aaspaas.customer.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.*
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.feature.home.viewmodel.HomeViewModel

// ── Bottom Navigation ─────────────────────────────────────────────────────────

private enum class HomeTab(val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    HOME("Home", Icons.Outlined.Home, Icons.Filled.Home),
    EXPLORE("Explore", Icons.Outlined.Explore, Icons.Filled.Explore),
    RESERVATIONS("Reservations", Icons.Outlined.BookmarkBorder, Icons.Filled.Bookmark),
    WISHLIST("Wishlist", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    PROFILE("Profile", Icons.Outlined.PersonOutline, Icons.Filled.Person)
}

@Composable
fun HomeScreen(
    onSearchClick: (String) -> Unit,
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit,
    onReservationsClick: () -> Unit,
    onWishlistClick: () -> Unit = {},
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }

    Scaffold(
        bottomBar = {
            HomeBottomNav(
                selected = selectedTab,
                onSelect = { tab ->
                    selectedTab = tab
                    when (tab) {
                        HomeTab.RESERVATIONS -> onReservationsClick()
                        HomeTab.PROFILE -> onProfileClick()
                        HomeTab.EXPLORE -> onSearchClick("")
                        HomeTab.WISHLIST -> onWishlistClick()
                        else -> {}
                    }
                }
            )
        },
        containerColor = Background
    ) { padding ->
        when {
            state.isLoading -> HomeShimmerScreen(modifier = Modifier.padding(padding))
            state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadHomeData() })
            else -> HomeContent(
                state = state,
                onSearchClick = onSearchClick,
                onProductClick = onProductClick,
                onStoreClick = onStoreClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun HomeBottomNav(selected: HomeTab, onSelect: (HomeTab) -> Unit) {
    NavigationBar(
        containerColor = Surface,
        tonalElevation = 0.dp
    ) {
        HomeTab.values().forEach { tab ->
            val isSelected = selected == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    indicatorColor = Primary.copy(alpha = 0.1f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

// ── Shimmer ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeShimmerScreen(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            // Header shimmer
            Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                ShimmerItem(Modifier.height(20.dp).width(160.dp), RoundedCornerShape(4.dp))
                Spacer(Modifier.height(4.dp))
                ShimmerItem(Modifier.height(16.dp).width(100.dp), RoundedCornerShape(4.dp))
            }
            // Search shimmer
            ShimmerItem(Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 16.dp), RoundedCornerShape(12.dp))
            Spacer(Modifier.height(20.dp))
            // Categories shimmer
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(6) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ShimmerItem(Modifier.size(52.dp), CircleShape)
                        Spacer(Modifier.height(6.dp))
                        ShimmerItem(Modifier.height(12.dp).width(48.dp), RoundedCornerShape(4.dp))
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(24.dp))
            ShimmerItem(Modifier.height(18.dp).width(160.dp).padding(horizontal = 16.dp), RoundedCornerShape(4.dp))
            Spacer(Modifier.height(12.dp))
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(3) { ProductCardShimmer() }
            }
        }
        item {
            Spacer(Modifier.height(24.dp))
            ShimmerItem(Modifier.height(18.dp).width(140.dp).padding(horizontal = 16.dp), RoundedCornerShape(4.dp))
            Spacer(Modifier.height(12.dp))
            repeat(2) {
                StoreCardShimmer()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Main Content ──────────────────────────────────────────────────────────────

@Composable
private fun HomeContent(
    state: com.aaspaas.customer.feature.home.viewmodel.HomeUiState,
    onSearchClick: (String) -> Unit,
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header: greeting + location + notification
        item { HomeHeader(locationText = state.location?.let { "Nearby" } ?: "Set location") }

        // Search bar
        item { HomeSearchBar(onSearchClick) }

        // Quick categories
        item {
            Spacer(Modifier.height(20.dp))
            QuickCategories(onCategoryClick = { onSearchClick(it) })
        }

        // Offers banner (subtle)
        item {
            Spacer(Modifier.height(20.dp))
            OffersBanner()
        }

        // Available near you — products
        item {
            Spacer(Modifier.height(24.dp))
            HomeSectionHeader(
                title = "Available near you",
                subtitle = "Products you can find nearby"
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            if (state.nearbyProducts.isEmpty()) {
                Text(
                    "No products found nearby",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.nearbyProducts) { product ->
                        NearbyProductCard(product = product, onClick = {
                            val variantId = product.variants.firstOrNull()?.id ?: return@NearbyProductCard
                            onProductClick(product.id, variantId)
                        })
                    }
                }
            }
        }

        // Stores around you
        item {
            Spacer(Modifier.height(24.dp))
            HomeSectionHeader(title = "Stores around you")
            Spacer(Modifier.height(12.dp))
        }
        if (state.nearbyStores.isEmpty()) {
            item {
                Text(
                    "No stores found nearby",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            items(state.nearbyStores) { store ->
                NearbyStoreCard(store = store, onClick = { onStoreClick(store.id) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(locationText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Good morning 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Change location",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        IconButton(onClick = {}) {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = TextPrimary
            )
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────

@Composable
private fun HomeSearchBar(onSearchClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onSearchClick("") },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Search products, brands or stores",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

// ── Quick Categories ──────────────────────────────────────────────────────────

private data class Category(val emoji: String, val name: String)

private val categories = listOf(
    Category("👗", "Fashion"),
    Category("📱", "Electronics"),
    Category("🛒", "Grocery"),
    Category("💄", "Beauty"),
    Category("🏠", "Home"),
    Category("⚽", "Sports"),
    Category("📦", "More")
)

@Composable
private fun QuickCategories(onCategoryClick: (String) -> Unit) {
    Column {
        Text(
            text = "Popular near you",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryChip(category = category, onClick = { onCategoryClick(category.name) })
            }
        }
    }
}

@Composable
private fun CategoryChip(category: Category, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = Primary.copy(alpha = 0.08f),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(category.emoji, style = MaterialTheme.typography.titleLarge)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary
        )
    }
}

// ── Offers Banner ─────────────────────────────────────────────────────────────

@Composable
private fun OffersBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Accent.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🏷️", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Up to 30% OFF",
                    style = MaterialTheme.typography.titleSmall,
                    color = Accent
                )
                Text(
                    "Local stores near you",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Text(
                "Explore →",
                style = MaterialTheme.typography.labelMedium,
                color = Accent
            )
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
private fun HomeSectionHeader(title: String, subtitle: String? = null) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        subtitle?.let {
            Spacer(Modifier.height(2.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

// ── Nearby Product Card ───────────────────────────────────────────────────────

@Composable
private fun NearbyProductCard(product: Product, onClick: () -> Unit) {
    val variant = product.variants.firstOrNull()
    val storeCount = 1 // single store per product in current model; extend when multi-store available

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(Modifier.padding(12.dp)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                if (variant != null) {
                    Text(
                        "₹${variant.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, null, Modifier.size(12.dp), tint = Primary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "$storeCount store nearby",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary
                    )
                }
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "View Stores",
                        modifier = Modifier.padding(vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

// ── Nearby Store Card ─────────────────────────────────────────────────────────

@Composable
private fun NearbyStoreCard(store: Store, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Store icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Store, null, Modifier.size(28.dp), tint = Primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    store.rating?.let {
                        Icon(Icons.Default.Star, null, Modifier.size(12.dp), tint = Warning)
                        Text(String.format("%.1f", it), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("•", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    store.distanceKm?.let {
                        Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("•", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Text(
                        text = if (store.isOpen) "Open" else "Closed",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (store.isOpen) Success else Error
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "View Store →",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary
                )
            }
        }
    }
}
