package com.aaspaas.customer.feature.product.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.LoadingScreen
import com.aaspaas.customer.core.ui.components.ShimmerItem
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.InventoryStatus
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.ProductVariant
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
        state.isLoading -> ProductShimmerScreen()
        state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.load(productId, variantId) })
        state.product != null -> ProductDetailContent(
            product = state.product!!,
            selectedVariant = state.selectedVariant,
            onVariantSelect = { viewModel.selectVariant(it) },
            onReserveClick = { state.selectedVariant?.let { onReserveClick(it.id) } },
            onStoreClick = { onStoreClick(state.product!!.store.id) },
            onBack = onBack
        )
    }
}

@Composable
private fun ProductShimmerScreen() {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            ShimmerItem(modifier = Modifier.fillMaxWidth().height(280.dp), shape = RoundedCornerShape(0.dp))
        }
        item {
            Column(Modifier.padding(16.dp)) {
                ShimmerItem(modifier = Modifier.height(20.dp).fillMaxWidth(0.6f))
                Spacer(Modifier.height(8.dp))
                ShimmerItem(modifier = Modifier.height(28.dp).fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                ShimmerItem(modifier = Modifier.height(24.dp).width(100.dp))
                Spacer(Modifier.height(16.dp))
                ShimmerItem(modifier = Modifier.height(28.dp).fillMaxWidth(), shape = RoundedCornerShape(20.dp))
                Spacer(Modifier.height(16.dp))
                ShimmerItem(modifier = Modifier.height(16.dp).fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                repeat(2) {
                    ShimmerItem(modifier = Modifier.height(40.dp).fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    selectedVariant: ProductVariant?,
    onVariantSelect: (ProductVariant) -> Unit,
    onReserveClick: () -> Unit,
    onStoreClick: () -> Unit,
    onBack: () -> Unit
) {
    val isAvailable = selectedVariant?.inventory?.status == InventoryStatus.AVAILABLE ||
            (selectedVariant?.inventory?.availableStock ?: 0) > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = onReserveClick,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    enabled = isAvailable && selectedVariant != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reserve for 6 Hours", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                // Product image placeholder
                Box(
                    modifier = Modifier.fillMaxWidth().height(280.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item {
                Column(Modifier.padding(16.dp)) {
                    product.brand?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(product.name, style = MaterialTheme.typography.headlineMedium)
                    selectedVariant?.let {
                        Text("₹${it.price.toInt()}", style = MaterialTheme.typography.headlineLarge, color = BrandAccent)
                    }

                    Spacer(Modifier.height(8.dp))
                    AvailabilityChip(isAvailable, selectedVariant?.inventory?.availableStock ?: 0)

                    // Variants
                    if (product.variants.size > 1) {
                        Spacer(Modifier.height(16.dp))
                        Text("Select Variant", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        VariantSelector(
                            variants = product.variants,
                            selected = selectedVariant,
                            onSelect = onVariantSelect
                        )
                    }

                    // Description
                    product.description?.let {
                        Spacer(Modifier.height(16.dp))
                        Text("About this product", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Store info
                    Spacer(Modifier.height(16.dp))
                    StoreInfoCard(product = product, onStoreClick = onStoreClick)
                }
            }
        }
    }
}

@Composable
private fun AvailabilityChip(isAvailable: Boolean, stock: Int) {
    val color = when {
        !isAvailable -> OutOfStock
        stock <= 3 -> LowStock
        else -> Available
    }
    val label = when {
        !isAvailable -> "Out of Stock"
        stock <= 3 -> "Only $stock left"
        else -> "In Stock"
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = "● $label",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Composable
private fun VariantSelector(
    variants: List<ProductVariant>,
    selected: ProductVariant?,
    onSelect: (ProductVariant) -> Unit
) {
    // Group by size and color
    val sizes = variants.mapNotNull { it.size }.distinct()
    val colors = variants.mapNotNull { it.color }.distinct()

    if (sizes.isNotEmpty()) {
        Text("Size", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sizes.forEach { size ->
                val variant = variants.find { it.size == size && (selected?.color == null || it.color == selected.color) }
                    ?: variants.find { it.size == size }
                val isSelected = selected?.size == size
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, if (isSelected) BrandAccent else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .background(if (isSelected) BrandAccent.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                        .clickable { variant?.let { onSelect(it) } }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(size, style = MaterialTheme.typography.labelMedium, color = if (isSelected) BrandAccent else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }

    if (colors.isNotEmpty()) {
        Text("Color", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            colors.forEach { color ->
                val variant = variants.find { it.color == color && (selected?.size == null || it.size == selected.size) }
                    ?: variants.find { it.color == color }
                val isSelected = selected?.color == color
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, if (isSelected) BrandAccent else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .background(if (isSelected) BrandAccent.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                        .clickable { variant?.let { onSelect(it) } }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(color, style = MaterialTheme.typography.labelMedium, color = if (isSelected) BrandAccent else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun StoreInfoCard(product: Product, onStoreClick: () -> Unit) {
    val store = product.store
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onStoreClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(40.dp), tint = Brand)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleMedium)
                Text(store.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                Row {
                    store.distanceKm?.let { Text("${String.format("%.1f", it)} km  •  ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Text(store.openingHours, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
