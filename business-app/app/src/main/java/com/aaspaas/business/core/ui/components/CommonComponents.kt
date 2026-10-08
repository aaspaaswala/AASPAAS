package com.aaspaas.business.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aaspaas.business.core.ui.theme.BusinessAccent
import com.aaspaas.business.core.ui.theme.BusinessBrand
import com.aaspaas.business.core.ui.theme.Outline
import com.aaspaas.business.core.ui.theme.SurfaceVariant
import com.aaspaas.business.core.ui.theme.Warning
import com.aaspaas.business.core.ui.theme.White
import com.aaspaas.business.domain.model.Product
import com.aaspaas.business.domain.model.Reservation
import com.aaspaas.business.domain.model.ReservationStatus

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingState() }
}

@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { ErrorState(message, onRetry) }
}

@Composable
fun EmptyScreen(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyState(message, icon = icon) }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun BrandButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BusinessBrand)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun BrandOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color = BusinessBrand,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val cardModifier = if (onClick == null) modifier else modifier.clickable(onClick = onClick)
    Card(
        modifier = cardModifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StatusChip(label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun BusinessSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search"
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Surface(shape = CircleShape, color = BusinessBrand.copy(alpha = 0.1f)) {
                Icon(icon, contentDescription = null, tint = BusinessBrand, modifier = Modifier.padding(16.dp).size(32.dp))
            }
            Spacer(Modifier.height(16.dp))
        }
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        description?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            BrandButton(actionLabel, onAction)
        }
    }
}

@Composable
fun LoadingState(message: String = "Loading…", modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(color = BusinessBrand)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ErrorState(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        if (onRetry != null) BrandOutlinedButton("Retry", onRetry)
    }
}

@Composable
fun BusinessAvatar(name: String?, modifier: Modifier = Modifier) {
    val initial = remember(name) { name?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "B" }
    Surface(modifier = modifier.size(48.dp), shape = CircleShape, color = BusinessBrand.copy(alpha = 0.12f)) {
        Box(contentAlignment = Alignment.Center) {
            Text(initial, color = BusinessAccent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete product?") },
            text = { Text("Delete \"${product.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = RoundedCornerShape(14.dp), color = SurfaceVariant, modifier = Modifier.size(64.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = "Product image placeholder",
                        tint = BusinessBrand,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                product.brand?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Text(product.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                val stock = product.variants.sumOf { it.inventory.availableStock }
                StatusChip(
                    label = "Stock $stock",
                    color = when {
                        stock == 0 -> MaterialTheme.colorScheme.error
                        stock <= 3 -> Warning
                        else -> BusinessBrand
                    }
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                product.variants.minOfOrNull { it.price }?.let {
                    Text("₹${it.toInt()}", style = MaterialTheme.typography.titleSmall, color = BusinessAccent)
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit product", tint = BusinessBrand)
                    }
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete product", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationCard(
    reservation: Reservation,
    onClick: () -> Unit,
    onConfirm: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (reservation.status) {
        ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED -> BusinessBrand
        ReservationStatus.PENDING -> Warning
        ReservationStatus.COMPLETED -> BusinessBrand
        ReservationStatus.CANCELLED -> MaterialTheme.colorScheme.error
        ReservationStatus.EXPIRED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val statusLabel = when (reservation.status) {
        ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED -> "Active"
        ReservationStatus.PENDING -> "Pending"
        ReservationStatus.COMPLETED -> "Completed"
        ReservationStatus.CANCELLED -> "Cancelled"
        ReservationStatus.EXPIRED -> "Expired"
    }
    val isActive = reservation.status in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING)

    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(reservation.productName, style = MaterialTheme.typography.titleMedium)
                    Text(reservation.variantDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusChip(statusLabel, statusColor)
            }
            Text("₹${reservation.price.toInt()}  ·  ${reservation.customerName}", style = MaterialTheme.typography.bodyMedium)
            if (isActive) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reservation.status == ReservationStatus.PENDING) {
                        BrandOutlinedButton("Confirm", onConfirm, Modifier.weight(1f))
                    }
                    BrandButton("Mark Sold", onComplete, Modifier.weight(1f))
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Cancel", style = MaterialTheme.typography.labelMedium) }
                }
            }
        }
    }
}

@Composable
fun ShimmerItem(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(4.dp)) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -500f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "shimmer_translate"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.7f),
            Color.LightGray.copy(alpha = 0.3f)
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 300f, 0f)
    )
    Box(modifier = modifier.background(brush, shape))
}

@Composable
fun ProductCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                ShimmerItem(modifier = Modifier.height(18.dp).fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                ShimmerItem(modifier = Modifier.height(14.dp).fillMaxWidth(0.6f))
                Spacer(Modifier.height(8.dp))
                ShimmerItem(modifier = Modifier.height(14.dp).width(80.dp))
            }
            Spacer(Modifier.width(8.dp))
            ShimmerItem(modifier = Modifier.height(24.dp).width(60.dp))
            Spacer(Modifier.width(8.dp))
            ShimmerItem(modifier = Modifier.size(32.dp), shape = RoundedCornerShape(8.dp))
        }
    }
}

@Composable
fun ReservationCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerItem(modifier = Modifier.height(20.dp).fillMaxWidth())
            ShimmerItem(modifier = Modifier.height(14.dp).fillMaxWidth(0.6f))
            ShimmerItem(modifier = Modifier.height(14.dp).fillMaxWidth(0.4f))
        }
    }
}

@Composable
fun StatCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            ShimmerItem(modifier = Modifier.size(28.dp), shape = RoundedCornerShape(4.dp))
            Spacer(Modifier.height(8.dp))
            ShimmerItem(modifier = Modifier.height(28.dp).width(60.dp))
            Spacer(Modifier.height(4.dp))
            ShimmerItem(modifier = Modifier.height(14.dp).width(100.dp))
        }
    }
}

@Composable
fun InventoryRowShimmer() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            ShimmerItem(modifier = Modifier.height(14.dp).fillMaxWidth(0.5f))
            Spacer(Modifier.height(4.dp))
            ShimmerItem(modifier = Modifier.height(12.dp).width(60.dp))
        }
        Spacer(Modifier.width(8.dp))
        ShimmerItem(modifier = Modifier.size(24.dp), shape = RoundedCornerShape(4.dp))
    }
    HorizontalDivider(thickness = 0.5.dp)
}
