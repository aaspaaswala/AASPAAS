package com.aaspaas.business.feature.inventory.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.business.core.ui.theme.*
import com.aaspaas.business.core.ui.components.BusinessSearchBar
import com.aaspaas.business.core.ui.components.EmptyState
import com.aaspaas.business.core.ui.components.ErrorState
import com.aaspaas.business.core.ui.components.InventoryRowShimmer
import com.aaspaas.business.core.ui.components.LoadingState
import com.aaspaas.business.core.ui.components.StatusChip
import com.aaspaas.business.core.ui.components.ShimmerItem
import com.aaspaas.business.domain.model.InventoryStatus
import com.aaspaas.business.domain.model.Product
import com.aaspaas.business.domain.model.ProductVariant
import com.aaspaas.business.feature.inventory.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    onBack: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts = remember(state.products, searchQuery) {
        state.products.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.brand.orEmpty().contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { LoadingState("Loading inventory") }
                items(4) {
                    Column {
                        ShimmerItem(modifier = Modifier.height(20.dp).fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                        Spacer(Modifier.height(8.dp))
                        InventoryRowShimmer()
                        InventoryRowShimmer()
                    }
                }
            }
            state.error != null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                ErrorState(state.error ?: "Unable to load inventory", onRetry = viewModel::load)
            }
            else -> LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    BusinessSearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search inventory"
                    )
                }
                if (filteredProducts.isEmpty()) {
                    item {
                        EmptyState(
                            title = if (state.products.isEmpty()) "Inventory is empty" else "No matching products",
                            description = if (state.products.isEmpty()) "Product stock will appear here after products are added." else "Try another product name or brand.",
                            icon = Icons.Default.Inventory2
                        )
                    }
                }
                items(filteredProducts) { product ->
                    InventoryProductCard(product = product, onUpdateStock = { inventoryId, stock ->
                        viewModel.updateStock(inventoryId, stock)
                    })
                }
            }
        }
    }
}

@Composable
private fun InventoryProductCard(product: Product, onUpdateStock: (String, Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            product.brand?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Spacer(Modifier.height(8.dp))
            product.variants.forEach { variant ->
                VariantInventoryRow(variant = variant, onUpdateStock = { stock -> onUpdateStock(variant.inventory.id, stock) })
            }
        }
    }
}

@Composable
private fun VariantInventoryRow(variant: ProductVariant, onUpdateStock: (Int) -> Unit) {
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        StockEditDialog(
            currentStock = variant.inventory.availableStock,
            onConfirm = { newStock -> onUpdateStock(newStock); showEditDialog = false },
            onDismiss = { showEditDialog = false }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            val label = listOfNotNull(variant.size?.let { "Size: $it" }, variant.color?.let { "Color: $it" }).joinToString(" • ")
            if (label.isNotEmpty()) Text(label, style = MaterialTheme.typography.bodySmall)
            Text("₹${variant.price.toInt()}", style = MaterialTheme.typography.labelMedium, color = BusinessAccent)
        }
        Column(horizontalAlignment = Alignment.End) {
            val inv = variant.inventory
            val statusColor = when (inv.status) {
                InventoryStatus.OUT_OF_STOCK -> OutOfStock
                InventoryStatus.LOW_STOCK -> LowStock
                InventoryStatus.RESERVED -> com.aaspaas.business.core.ui.theme.Warning
                else -> Available
            }
            StatusChip("Available ${inv.availableStock}", statusColor)
            Text("Reserved ${inv.reservedStock}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = { showEditDialog = true }) {
            Icon(Icons.Default.Edit, null, tint = BusinessBrand, modifier = Modifier.size(18.dp))
        }
    }
    HorizontalDivider(thickness = 0.5.dp)
}

@Composable
private fun StockEditDialog(currentStock: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var stockText by remember { mutableStateOf(currentStock.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Stock") },
        text = {
            OutlinedTextField(
                value = stockText,
                onValueChange = { stockText = it.filter { c -> c.isDigit() } },
                label = { Text("Available Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = stockText.toIntOrNull() != null,
                onClick = { onConfirm(stockText.toIntOrNull() ?: currentStock) }
            ) { Text("Update", color = BusinessBrand) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
