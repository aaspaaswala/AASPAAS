package com.aaspaas.business.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BusinessBrand,
    onPrimary = White,
    secondary = BusinessAccent,
    onSecondary = White,
    tertiary = BusinessSecondary,
    background = Surface,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    error = Error,
    onError = White
)

data class AppSpacing(
    val xs: Dp = 4.dp, val sm: Dp = 8.dp, val md: Dp = 16.dp,
    val lg: Dp = 24.dp, val xl: Dp = 32.dp, val xxl: Dp = 48.dp
)

val LocalSpacing = staticCompositionLocalOf { AppSpacing() }

@Composable
fun AasPaasBusinessTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpacing provides AppSpacing()) {
        MaterialTheme(colorScheme = LightColorScheme, typography = BusinessTypography, content = content)
    }
}
