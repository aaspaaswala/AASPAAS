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
import com.aaspaas.customer.core.ui.theme.Brand
import com.aaspaas.customer.core.ui.theme.BrandAccent
import com.aaspaas.customer.core.ui.theme.White
import com.aaspaas.customer.feature.home.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()
    val alpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "alpha")

    LaunchedEffect(destination) {
        if (destination != null) {
            delay(1200)
            when (destination) {
                SplashViewModel.Destination.HOME -> onNavigateToHome()
                SplashViewModel.Destination.ONBOARDING -> onNavigateToOnboarding()
                null -> Unit
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Brand),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AAS PAAS WALA",
                style = MaterialTheme.typography.displayLarge,
                color = White,
                modifier = Modifier.alpha(alpha)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Find Nearby. Reserve Online. Buy Offline.",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandAccent,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}
