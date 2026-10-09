package eu.alsk.transmissionremote.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Applies the colours, the type scale and the shapes of the design system to [content].
 *
 * The type scale is the Material 3 default. It has the same sizes as the text styles of the Figma
 * file.
 */
@Composable
public fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalStatusColors provides if (darkTheme) DarkStatusColors else LightStatusColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography(),
            shapes = AppShapes,
            content = content,
        )
    }
}

/** Gives access to the tokens of the current [AppTheme]. */
public object AppTheme {
    public val colors: ColorScheme
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme

    public val statusColors: StatusColors
        @Composable @ReadOnlyComposable get() = LocalStatusColors.current

    public val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography
}
