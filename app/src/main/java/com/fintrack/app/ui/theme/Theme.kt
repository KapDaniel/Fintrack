package com.fintrack.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LightScheme = lightColorScheme(
    primary = FinNav600,
    onPrimary = FinTextInverse,
    primaryContainer = FinSurfaceAlt,
    onPrimaryContainer = FinNav700,
    secondary = FinOrange500,
    onSecondary = FinTextInverse,
    background = FinBg,
    onBackground = FinTextPrimary,
    surface = FinSurface,
    onSurface = FinTextPrimary,
    surfaceVariant = FinSurfaceAlt2,
    onSurfaceVariant = FinTextSecondary,
    outline = FinBorder,
    outlineVariant = FinBorder,
    error = FinDanger,
    onError = FinTextInverse,
)

private val DarkScheme = darkColorScheme(
    primary = FinNav500,
    onPrimary = FinTextInverse,
    primaryContainer = FinDarkSurfaceAlt,
    onPrimaryContainer = FinSurfaceAlt,
    secondary = FinOrange500,
    onSecondary = FinTextInverse,
    background = FinDarkBg,
    onBackground = FinDarkTextPrimary,
    surface = FinDarkSurface,
    onSurface = FinDarkTextPrimary,
    surfaceVariant = FinDarkSurfaceAlt2,
    onSurfaceVariant = FinDarkTextSecondary,
    outline = FinDarkBorder,
    outlineVariant = FinDarkBorder,
    error = FinDarkExpenseFg,
    onError = FinTextPrimary,
)

val LocalFinColors = staticCompositionLocalOf { LightFinColors }

/**
 * Тема приложения. Dynamic color намеренно выключен: фирменные оранжевый и синий
 * из макета не должны подменяться обоями пользователя.
 */
@Composable
fun FinTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val finColors = if (darkTheme) DarkFinColors else LightFinColors
    CompositionLocalProvider(LocalFinColors provides finColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = FinTypography,
            shapes = FinShapes,
            content = content,
        )
    }
}

/** Короткий доступ к семантическим цветам: `FinTheme.colors.incomeFg`. */
object FinTheme {
    val colors: FinColors
        @Composable get() = LocalFinColors.current
}
