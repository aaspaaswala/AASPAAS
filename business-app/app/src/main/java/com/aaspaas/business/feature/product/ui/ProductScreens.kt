package com.aaspaas.business.feature.product.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.business.core.ui.components.BrandButton
import com.aaspaas.business.core.ui.components.BusinessSearchBar
import com.aaspaas.business.core.ui.components.EmptyState
import com.aaspaas.business.core.ui.components.ErrorState
import com.aaspaas.business.core.ui.components.LoadingState
import com.aaspaas.business.core.ui.components.ProductCard
import com.aaspaas.business.core.ui.components.ProductCardShimmer
import com.aaspaas.business.core.ui.theme.*
import com.aaspaas.business.feature.product.viewmodel.AddEditProductViewModel
import com.aaspaas.business.feature.product.viewmodel.ProductListViewModel

@Composable
fun ProductListScreen(
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts = remember(state.products, searchQuery) {
        state.products.filter { product ->
            product.name.contains(searchQuery, ignoreCase = true) ||
                product.category.contains(searchQuery, ignoreCase = true) ||
                product.brand.orEmpty().contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Products") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { viewModel.loadProducts() }) { Icon(Icons.Default.Refresh, null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = BusinessAccent
            ) { Icon(Icons.Default.Add, contentDescription = "Add Product") }
        }
    ) { padding ->
        when {
            state.isLoading -> LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { LoadingState("Loading products") }
                items(5) { ProductCardShimmer() }
            }
            state.error != null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                ErrorState(state.error ?: "Unable to load products", onRetry = viewModel::loadProducts)
            }
            state.products.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    title = "No products yet",
                    description = "Add your first product to start building your catalog.",
                    icon = Icons.Default.Inventory2,
                    actionLabel = "Add product",
                    onAction = onAddClick
                )
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
                        placeholder = "Search products"
                    )
                }
                if (filteredProducts.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No matching products",
                            description = "Try another product name, category, or brand.",
                            icon = Icons.Default.Search
                        )
                    }
                }
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onEdit = { onEditClick(product.id) },
                        onDelete = { viewModel.deleteProduct(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditProductScreen(
    productId: String?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    LaunchedEffect(productId) { if (productId != null) viewModel.loadProduct(productId) }
    LaunchedEffect(state.product) {
        state.product?.let { p ->
            name = p.name; brand = p.brand ?: ""; description = p.description ?: ""
            category = p.category
            p.variants.firstOrNull()?.let { v ->
                size = v.size ?: ""; color = v.color ?: ""
                price = v.price.toInt().toString()
                stock = v.inventory.availableStock.toString()
            }
        }
    }
    LaunchedEffect(state.isSaved) { if (state.isSaved) onSaved() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "Add Product" else "Edit Product") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when {
            productId != null && state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                LoadingState("Loading product")
            }
            productId != null && state.error != null && state.product == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                ErrorState(state.error ?: "Unable to load product", onRetry = { viewModel.loadProduct(productId) })
            }
            else -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Product Info", style = MaterialTheme.typography.headlineSmall, color = BusinessBrand)
                Surface(
                    modifier = Modifier.fillMaxWidth().height(136.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceVariant
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = BusinessBrand, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Product image", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), minLines = 2, maxLines = 4)

                HorizontalDivider()
                Text("Variant & Inventory", style = MaterialTheme.typography.titleLarge, color = BusinessBrand)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Size") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), singleLine = true)
                    OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), singleLine = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = price, onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Price (₹) *") }, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text("₹") }
                    )
                    OutlinedTextField(
                        value = stock, onValueChange = { stock = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock *") }, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

                BrandButton(
                    text = if (productId == null) "Add Product" else "Save Changes",
                    onClick = {
                        viewModel.saveProduct(
                            productId = productId, name = name, brand = brand, description = description,
                            category = category, size = size, color = color,
                            price = price.toDoubleOrNull() ?: 0.0,
                            stock = stock.toIntOrNull() ?: 0
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && category.isNotBlank() && price.isNotBlank() && stock.isNotBlank(),
                    isLoading = state.isLoading
                )
            }
        }
    }
}
