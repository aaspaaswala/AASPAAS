package com.aaspaas.customer.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E8F5),
    onPrimaryContainer = PrimaryDark,
    secondary = Accent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE8E0),
    onSecondaryContainer = Color(0xFF8B3E2A),
    tertiary = PrimaryDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0E0EB),
    onTertiaryContainer = PrimaryDark,
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF8B1A1A),
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF5F3EF),
    onSurfaceVariant = TextSecondary,
    outline = Border,
    outlineVariant = Color(0xFFD9D5D0),
    scrim = Color.Black,
    inverseSurface = Color(0xFF2E2E2E),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFFB8B8D8)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB8B8D8),
    onPrimary = PrimaryDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFE8A898),
    onSecondary = Color(0xFF4A2215),
    secondaryContainer = Color(0xFF6B3525),
    onSecondaryContainer = Color(0xFFFFE8E0),
    tertiary = Color(0xFFB8B8D8),
    onTertiary = PrimaryDark,
    tertiaryContainer = PrimaryDark,
    onTertiaryContainer = Color.White,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF171717),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3D3D3D),
    onSurfaceVariant = Color(0xFFB8B8B8),
    outline = Color(0xFF767676),
    outlineVariant = Color(0xFF4A4A4A),
    scrim = Color.Black,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    inversePrimary = Primary
)

data class AppSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val huge: Dp = 40.dp,
    val massive: Dp = 48.dp,
    val giant: Dp = 64.dp
)

data class AppRadius(
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val full: Dp = 9999.dp
)

val LocalSpacing = staticCompositionLocalOf { AppSpacing() }
val LocalRadius = staticCompositionLocalOf { AppRadius() }

@Composable
fun AasPaasWalaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(
        LocalSpacing provides AppSpacing(),
        LocalRadius provides AppRadius()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}