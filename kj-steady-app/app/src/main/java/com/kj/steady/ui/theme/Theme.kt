package com.kj.steady.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = SurfaceWhite,
    primaryContainer = BrandBlueLight,
    onPrimaryContainer = BrandBlueDark,
    secondary = WaterBlue,
    onSecondary = SurfaceWhite,
    secondaryContainer = WaterBlueLight,
    onSecondaryContainer = WaterBlueDark,
    tertiary = StepsOrange,
    onTertiary = SurfaceWhite,
    tertiaryContainer = StepsOrangeLight,
    onTertiaryContainer = StepsOrangeDark,
    background = Slate50,
    onBackground = Slate900,
    surface = SurfaceWhite,
    onSurface = Slate900,
    outline = Slate200,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    error = ErrorRed,
    onError = SurfaceWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Slate950,
    primaryContainer = Slate800,
    onPrimaryContainer = Color(0xFF93C5FD),
    secondary = Color(0xFF38BDF8),
    onSecondary = Slate950,
    secondaryContainer = Slate800,
    onSecondaryContainer = Color(0xFF7DD3FC),
    tertiary = Color(0xFFFB923C),
    onTertiary = Slate950,
    background = DarkBg,
    onBackground = DarkInkText,
    surface = DarkSurface,
    onSurface = DarkInkText,
    outline = DarkBorder,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkMutedText,
    error = Color(0xFFF87171),
    onError = Slate950
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
            window.statusBarColor = (if (darkTheme) DarkBg else Slate50).toArgb()
            window.navigationBarColor = (if (darkTheme) DarkSurface else SurfaceWhite).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
