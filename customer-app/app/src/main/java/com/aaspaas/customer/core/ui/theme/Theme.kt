package com.aaspaas.customer.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Brand,
    onPrimary = White,
    primaryContainer = BrandSecondary,
    onPrimaryContainer = White,
    secondary = BrandAccent,
    onSecondary = White,
    secondaryContainer = Color(0xFFFFE4E8),
    onSecondaryContainer = BrandAccent,
    background = White,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    error = Error,
    onError = White
)

// Custom spacing tokens
data class AppSpacing(
    val xs: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(4f),
    val sm: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(8f),
    val md: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(16f),
    val lg: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(24f),
    val xl: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(32f),
    val xxl: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(48f)
)

val LocalSpacing = staticCompositionLocalOf { AppSpacing() }

@Composable
fun AasPaasWalaTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpacing provides AppSpacing()) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
