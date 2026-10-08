package com.aaspaas.customer.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aaspaas.customer.core.ui.theme.*

// ── Loading ───────────────────────────────────────────────────────────────────

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Primary)
    }
}

// ── Error ─────────────────────────────────────────────────────────────────────

@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("😕", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text(
            "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Try Again")
            }
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
fun EmptyScreen(
    message: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    emoji: String? = null,
    ctaLabel: String? = null,
    onCta: (() -> Unit)? = null
) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        emoji?.let {
            Text(it, style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(16.dp))
        }
        icon?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(56.dp), tint = TextSecondary)
            Spacer(Modifier.height(16.dp))
        }
        Text(message, style = MaterialTheme.typography.titleMedium, color = TextPrimary, textAlign = TextAlign.Center)
        subtitle?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
        }
        if (ctaLabel != null && onCta != null) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onCta,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(ctaLabel)
            }
        }
    }
}

// ── Offline State ─────────────────────────────────────────────────────────────

@Composable
fun OfflineScreen(onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📡", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("You're offline", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "Some information may be unavailable right now.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border)
        ) {
            Text("Try Again", color = TextPrimary)
        }
    }
}

// ── No Location State ─────────────────────────────────────────────────────────

@Composable
fun NoLocationScreen(onEnable: () -> Unit, onManual: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📍", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("Turn on location", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "We need your location to find nearby stores and products.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onEnable,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Enable Location")
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onManual) {
            Text("Choose location manually", color = Primary)
        }
    }
}

// ── No Search Results ─────────────────────────────────────────────────────────

@Composable
fun NoResultsScreen(query: String, onBrowse: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔍", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("No products found", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "We couldn't find \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text("Try:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("• Different keywords", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("• Nearby categories", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("• Broader search terms", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onBrowse,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
        ) {
            Text("Browse Categories", color = Primary)
        }
    }
}

// ── Session Expired ───────────────────────────────────────────────────────────

@Composable
fun SessionExpiredScreen(onLogin: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔒", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("Session expired", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "Your session has expired. Please log in again to continue.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onLogin,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Log In")
        }
    }
}

// ── Product Unavailable ───────────────────────────────────────────────────────

@Composable
fun ProductUnavailableScreen(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📦", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("Product unavailable", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "This product is currently out of stock at all nearby stores.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onBack,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Go Back")
        }
    }
}

// ── Store Closed ──────────────────────────────────────────────────────────────

@Composable
fun StoreClosedScreen(storeName: String, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⏰", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("$storeName is closed", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "This store is currently closed. Check back during business hours.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onBack,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Go Back")
        }
    }
}

// ── Reservation Expired ───────────────────────────────────────────────────────

@Composable
fun ReservationExpiredScreen(reservationId: String, onBrowse: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⏳", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("Reservation expired", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "Reservation #$reservationId has expired. Products have been returned to inventory.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onBrowse,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Browse Products")
        }
    }
}

// ── Network Error ─────────────────────────────────────────────────────────────

@Composable
fun NetworkErrorScreen(onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📡", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text("Connection failed", style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "No internet connection. Check your network and try again.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Try Again")
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// ── Badges ────────────────────────────────────────────────────────────────────

@Composable
fun AvailableBadge() {
    Surface(shape = RoundedCornerShape(4.dp), color = Success.copy(alpha = 0.1f)) {
        Text(
            "Available",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Success
        )
    }
}

@Composable
fun BestPriceBadge() {
    Surface(shape = RoundedCornerShape(4.dp), color = Success.copy(alpha = 0.1f)) {
        Text(
            "BEST PRICE",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Success
        )
    }
}

@Composable
fun ProBadge() {
    Surface(shape = RoundedCornerShape(4.dp), color = Accent.copy(alpha = 0.12f)) {
        Text(
            "PRO",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Accent
        )
    }
}

@Composable
fun ClosedBadge() {
    Surface(shape = RoundedCornerShape(4.dp), color = Error.copy(alpha = 0.1f)) {
        Text(
            "Closed",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Error
        )
    }
}

// ── Shimmer ───────────────────────────────────────────────────────────────────

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
            Color.LightGray.copy(alpha = 0.6f),
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
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column {
            ShimmerItem(Modifier.fillMaxWidth().height(130.dp), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            Column(Modifier.padding(12.dp)) {
                ShimmerItem(Modifier.height(14.dp).fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                ShimmerItem(Modifier.height(18.dp).width(80.dp))
                Spacer(Modifier.height(8.dp))
                ShimmerItem(Modifier.height(12.dp).width(100.dp))
                Spacer(Modifier.height(8.dp))
                ShimmerItem(Modifier.height(32.dp).fillMaxWidth(), RoundedCornerShape(8.dp))
            }
        }
    }
}

@Composable
fun StoreCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerItem(Modifier.size(56.dp), RoundedCornerShape(12.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                ShimmerItem(Modifier.height(16.dp).fillMaxWidth(0.6f))
                Spacer(Modifier.height(6.dp))
                ShimmerItem(Modifier.height(12.dp).fillMaxWidth(0.8f))
                Spacer(Modifier.height(6.dp))
                ShimmerItem(Modifier.height(12.dp).width(80.dp))
            }
        }
    }
}

@Composable
fun ReservationCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerItem(Modifier.height(16.dp).fillMaxWidth(0.7f))
                ShimmerItem(Modifier.height(12.dp).fillMaxWidth(0.5f))
                ShimmerItem(Modifier.height(14.dp).width(60.dp))
            }
            ShimmerItem(Modifier.height(24.dp).width(70.dp), RoundedCornerShape(20.dp))
        }
    }
}
