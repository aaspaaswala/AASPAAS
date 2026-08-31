package com.aaspaas.business.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        if (onRetry != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
fun EmptyScreen(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
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
