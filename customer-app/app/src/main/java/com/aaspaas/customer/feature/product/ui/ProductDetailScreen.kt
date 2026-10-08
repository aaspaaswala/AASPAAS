package com.aaspaas.customer.feature.product.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.ShimmerItem
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.InventoryStatus
import com.aaspaas.customer.domain.model.PriceComparisonEntry
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.ProductVariant
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.feature.product.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    variantId: String,
    onReserveClick: (String) -> Unit,
    onStoreClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId, variantId) { viewModel.load(productId, variantId) }

    when {
        state.isLoading -> ProductShimmerScreen(onBack)
        state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.load(productId, variantId) })
        state.product != null -> ProductDetailContent(
            product = state.product!!,
            selectedVariant = state.selectedVariant,
            priceComparison = state.priceComparison,
            onVariantSelect = { viewModel.selectVariant(it) },
            onReserveClick = { state.selectedVariant?.let { onReserveClick(it.id) } },
            onStoreClick = { onStoreClick(state.product!!.store.id) },
            onBack = onBack
        )
    }
}

@Composable
private fun ProductShimmerScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            item {
                ShimmerItem(Modifier.fillMaxWidth().height(300.dp), RoundedCornerShape(0.dp))
                Column(Modifier.padding(16.dp)) {
                    ShimmerItem(Modifier.height(14.dp).width(80.dp), RoundedCornerShape(4.dp))
                    Spacer(Modifier.height(8.dp))
                    ShimmerItem(Modifier.height(24.dp).fillMaxWidth(), RoundedCornerShape(4.dp))
                    Spacer(Modifier.height(8.dp))
                    ShimmerItem(Modifier.height(28.dp).width(100.dp), RoundedCornerShape(4.dp))
                    Spacer(Modifier.height(16.dp))
                    ShimmerItem(Modifier.height(24.dp).width(140.dp), RoundedCornerShape(20.dp))
                    Spacer(Modifier.height(24.dp))
                    ShimmerItem(Modifier.height(18.dp).width(160.dp), RoundedCornerShape(4.dp))
                    Spacer(Modifier.height(12.dp))
                    repeat(2) {
                        ShimmerItem(Modifier.fillMaxWidth().height(80.dp), RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    selectedVariant: ProductVariant?,
    priceComparison: List<PriceComparisonEntry>,
    onVariantSelect: (ProductVariant) -> Unit,
    onReserveClick: () -> Unit,
    onStoreClick: () -> Unit,
    onBack: () -> Unit
) {
    val isAvailable = selectedVariant?.inventory?.status == InventoryStatus.AVAILABLE ||
            (selectedVariant?.inventory?.availableStock ?: 0) > 0
    var wishlisted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { wishlisted = !wishlisted }) {
                        Icon(
                            imageVector = if (wishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (wishlisted) Accent else TextSecondary
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, null, tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Surface) {
                Button(
                    onClick = onReserveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp),
                    enabled = isAvailable && selectedVariant != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.BookmarkAdd, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Reserve Now", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            // Product image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingBag, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    // Image dots indicator
                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(4) { index ->
                            Box(
                                Modifier
                                    .size(if (index == 0) 20.dp else 6.dp, 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (index == 0) Primary else Color.White.copy(alpha = 0.6f))
                            )
                        }
                    }
                }
            }

            // Product info
            item {
                Column(Modifier.background(Surface).padding(16.dp)) {
                    product.brand?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(product.name, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, Modifier.size(16.dp), tint = Warning)
                            Spacer(Modifier.width(2.dp))
                            Text("4.5", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                            Text(" (124)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    selectedVariant?.let {
                        Text("₹${it.price.toInt()}", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
                    }
                    Spacer(Modifier.height(8.dp))
                    AvailabilityBadge(isAvailable, selectedVariant?.inventory?.availableStock ?: 0)
                }
            }

            // Variants
            if (product.variants.size > 1) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Surface(color = Surface) {
                        Column(Modifier.padding(16.dp)) {
                            VariantSelector(
                                variants = product.variants,
                                selected = selectedVariant,
                                onSelect = onVariantSelect
                            )
                        }
                    }
                }
            }

            // ── SIGNATURE SECTION: Available Near You ──────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                AvailableNearYouSection(
                    store = product.store,
                    price = selectedVariant?.price,
                    isAvailable = isAvailable,
                    onStoreClick = onStoreClick,
                    onReserveClick = onReserveClick
                )
            }

            // ── Price Comparison ──────────────────────────────────────────────
            if (priceComparison.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    PriceComparisonSection(
                        priceComparison = priceComparison,
                        currentVariantId = selectedVariant?.id,
                        onStoreClick = { onStoreClick() }
                    )
                }
            }

            // Product description
            product.description?.let { desc ->
                item {
                    Spacer(Modifier.height(8.dp))
                    Surface(color = Surface) {
                        Column(Modifier.padding(16.dp)) {
                            ExpandableSection(title = "Product Details") {
                                Text(desc, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ── Available Near You — AasPaasWala's Signature Section ─────────────────────

@Composable
private fun AvailableNearYouSection(
    store: Store,
    price: Double?,
    isAvailable: Boolean,
    onStoreClick: () -> Unit,
    onReserveClick: () -> Unit
) {
    Surface(color = Surface) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, Modifier.size(18.dp), tint = Primary)
                Spacer(Modifier.width(6.dp))
                Text("Available near you", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
            Spacer(Modifier.height(12.dp))

            // Store card with reserve CTA
            NearbyStoreReserveCard(
                store = store,
                price = price,
                isAvailable = isAvailable,
                isBestPrice = true,
                onStoreClick = onStoreClick,
                onReserveClick = onReserveClick
            )
        }
    }
}

@Composable
private fun NearbyStoreReserveCard(
    store: Store,
    price: Double?,
    isAvailable: Boolean,
    isBestPrice: Boolean,
    onStoreClick: () -> Unit,
    onReserveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(store.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        if (isBestPrice) {
                            BestPriceBadge()
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    store.rating?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, Modifier.size(12.dp), tint = Warning)
                            Spacer(Modifier.width(2.dp))
                            Text(String.format("%.1f", it), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
                price?.let {
                    Text("₹${it.toInt()}", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                store.distanceKm?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), tint = TextSecondary)
                        Spacer(Modifier.width(2.dp))
                        Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isAvailable) Success else Error)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (isAvailable) "Available now" else "Out of stock",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isAvailable) Success else Error
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onStoreClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Text("View Store", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                }
                Button(
                    onClick = onReserveClick,
                    modifier = Modifier.weight(1f),
                    enabled = isAvailable,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Reserve", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun BestPriceBadge() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Success.copy(alpha = 0.12f)
    ) {
        Text(
            "BEST PRICE",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Success
        )
    }
}

// ── Availability Badge ────────────────────────────────────────────────────────

@Composable
private fun AvailabilityBadge(isAvailable: Boolean, stock: Int) {
    val color = when {
        !isAvailable -> Error
        stock <= 3 -> Warning
        else -> Success
    }
    val label = when {
        !isAvailable -> "Currently unavailable"
        stock <= 3 -> "Only $stock left nearby"
        else -> "✓ Available nearby"
    }
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.1f)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

// ── Variant Selector ──────────────────────────────────────────────────────────

@Composable
private fun VariantSelector(
    variants: List<ProductVariant>,
    selected: ProductVariant?,
    onSelect: (ProductVariant) -> Unit
) {
    val sizes = variants.mapNotNull { it.size }.distinct()
    val colors = variants.mapNotNull { it.color }.distinct()

    if (sizes.isNotEmpty()) {
        Text("Size", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sizes.forEach { size ->
                val variant = variants.find { it.size == size && (selected?.color == null || it.color == selected.color) }
                    ?: variants.find { it.size == size }
                val isSelected = selected?.size == size
                VariantChipItem(
                    label = size,
                    isSelected = isSelected,
                    onClick = { variant?.let { onSelect(it) } }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }

    if (colors.isNotEmpty()) {
        Text("Color", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            colors.forEach { color ->
                val variant = variants.find { it.color == color && (selected?.size == null || it.size == selected.size) }
                    ?: variants.find { it.color == color }
                val isSelected = selected?.color == color
                VariantChipItem(
                    label = color,
                    isSelected = isSelected,
                    onClick = { variant?.let { onSelect(it) } }
                )
            }
        }
    }
}

@Composable
private fun VariantChipItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (isSelected) Primary else Border,
                RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) Primary.copy(alpha = 0.08f) else Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Primary else TextPrimary
        )
    }
}

// ── Expandable Section ────────────────────────────────────────────────────────

@Composable
private fun ExpandableSection(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                null,
                tint = TextSecondary
            )
        }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            content()
        }
        HorizontalDivider(color = Border, modifier = Modifier.padding(top = 12.dp))
    }
}

// ── Price Comparison Section ───────────────────────────────────────────────────

@Composable
private fun PriceComparisonSection(
    priceComparison: List<PriceComparisonEntry>,
    currentVariantId: String?,
    onStoreClick: (String) -> Unit
) {
    Surface(color = Surface) {
        Column(Modifier.padding(16.dp)) {
            Text("Price Comparison", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "Compare prices from nearby stores",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(12.dp))
            priceComparison.forEach { entry ->
                PriceComparisonRow(
                    entry = entry,
                    isLowest = entry.price == priceComparison.minOf { it.price },
                    onStoreClick = { onStoreClick(entry.store.id) }
                )
                if (entry != priceComparison.last()) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PriceComparisonRow(
    entry: PriceComparisonEntry,
    isLowest: Boolean,
    onStoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onStoreClick)
            .background(if (isLowest) Primary.copy(alpha = 0.05f) else Surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(entry.store.name, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                if (isLowest) {
                    Spacer(Modifier.width(6.dp))
                    BestPriceBadge()
                }
            }
            entry.store.distanceKm?.let {
                Spacer(Modifier.height(2.dp))
                Text("${String.format("%.1f", it)} km away", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₹${entry.price.toInt()}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            if (entry.availableStock <= 3) {
                Text("Only ${entry.availableStock} left", style = MaterialTheme.typography.labelSmall, color = Warning)
            } else {
                Text("${entry.availableStock} in stock", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}
