package com.aaspaas.customer.feature.home.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.theme.Accent
import com.aaspaas.customer.core.ui.theme.Background
import com.aaspaas.customer.core.ui.theme.Primary
import com.aaspaas.customer.feature.home.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(destination) {
        if (destination != null) {
            delay(1500)
            when (destination) {
                SplashViewModel.Destination.HOME -> onNavigateToHome()
                SplashViewModel.Destination.AUTH -> onNavigateToAuth()
                SplashViewModel.Destination.ONBOARDING -> onNavigateToAuth()
                null -> Unit
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            // Logo mark
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Primary, androidx.compose.foundation.shape.RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "APW",
                    style = MaterialTheme.typography.headlineMedium,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "AasPaasWala",
                style = MaterialTheme.typography.displayMedium,
                color = Primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Find it nearby.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
