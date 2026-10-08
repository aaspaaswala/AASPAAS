package com.aaspaas.customer.feature.location.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aaspaas.customer.core.ui.theme.*

@Composable
fun LocationPermissionScreen(
    onEnable: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Primary.copy(alpha = 0.08f),
            modifier = Modifier.size(160.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("📍", style = MaterialTheme.typography.displayLarge)
            }
        }

        Spacer(Modifier.height(40.dp))

        Text(
            "Find products around you",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Allow location access to discover stores and products near you.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onEnable,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Enable Location", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onSkip) {
            Text(
                "Maybe later",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
        }
    }
}
