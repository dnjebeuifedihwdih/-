package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

data class PaperColorTokens(
    val bg: Color,
    val surface: Color,
    val border: Color,
    val shadow: Color,
    val gridDot: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val stampGreen: Color,
    val stampRed: Color,
    val stickyYellow: Color
)

val LocalPaperColors = staticCompositionLocalOf {
    PaperColorTokens(
        bg = GlassBgBase,
        surface = GlassSurfaceBase,
        border = GlassCardBorder,
        shadow = Color.Black.copy(alpha = 0.5f),
        gridDot = Color.White.copy(alpha = 0.08f),
        textPrimary = GlassTextPrimary,
        textSecondary = GlassTextSecondary,
        accent = NeonCyan,
        stampGreen = NeonEmerald,
        stampRed = NeonCoral,
        stickyYellow = NeonPurple
    )
}

private val LiquidDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = GlassBgBase,
    surface = GlassSurfaceBase,
    onSurface = GlassTextPrimary,
    background = GlassBgBase,
    onBackground = GlassTextPrimary,
    secondary = NeonBlue,
    outline = GlassCardBorder
)

val LiquidShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun AetherPaperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val paperColors = PaperColorTokens(
        bg = GlassBgBase,
        surface = GlassSurfaceBase,
        border = GlassCardBorder,
        shadow = Color.Black.copy(alpha = 0.5f),
        gridDot = Color.White.copy(alpha = 0.08f),
        textPrimary = GlassTextPrimary,
        textSecondary = GlassTextSecondary,
        accent = NeonCyan,
        stampGreen = NeonEmerald,
        stampRed = NeonCoral,
        stickyYellow = NeonPurple
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GlassBgBase.toArgb()
            window.navigationBarColor = GlassBgBase.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    CompositionLocalProvider(LocalPaperColors provides paperColors) {
        MaterialTheme(
            colorScheme = LiquidDarkColorScheme,
            typography = AppTypography,
            shapes = LiquidShapes,
            content = content
        )
    }
}
