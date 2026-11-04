package com.kj.steady.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = ForestGreenLight,
    onPrimaryContainer = SurfaceWhite,
    secondary = RestrainedLime,
    onSecondary = ForestGreen,
    background = SandBg,
    onBackground = InkText,
    surface = SurfaceWhite,
    onSurface = InkText,
    outline = LineBorder,
    surfaceVariant = SandBg
)

private val DarkColorScheme = darkColorScheme(
    primary = RestrainedLime,
    onPrimary = ForestGreenDark,
    primaryContainer = ForestGreenLight,
    onPrimaryContainer = RestrainedLime,
    secondary = RestrainedLime,
    onSecondary = ForestGreenDark,
    background = DarkBg,
    onBackground = DarkInkText,
    surface = DarkSurface,
    onSurface = DarkInkText,
    outline = DarkBorder,
    surfaceVariant = DarkSurface
)

@Composable
fun KJSteadyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (darkTheme) DarkBg else ForestGreen).toArgb()
            window.navigationBarColor = (if (darkTheme) DarkBg else ForestGreen).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
