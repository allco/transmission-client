package eu.alsk.transmissionremote.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// The values are the colour variables of the Figma file (collection "Color", modes Light and
// Dark). Change a value here and in Figma in the same change.

internal val LightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFFB3261E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD5),
    onPrimaryContainer = Color(0xFF410001),
    secondary = Color(0xFFB3261E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD5),
    onSecondaryContainer = Color(0xFF410001),
    background = Color(0xFFFFF8F7),
    onBackground = Color(0xFF231918),
    surface = Color(0xFFFFF8F7),
    onSurface = Color(0xFF231918),
    surfaceVariant = Color(0xFFECE0DE),
    onSurfaceVariant = Color(0xFF534341),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFEF1EF),
    surfaceContainer = Color(0xFFF8EBE9),
    surfaceContainerHigh = Color(0xFFF2E5E3),
    surfaceContainerHighest = Color(0xFFECE0DE),
    outline = Color(0xFF857371),
    outlineVariant = Color(0xFFD8C2BF),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    inverseSurface = Color(0xFF392E2D),
    inverseOnSurface = Color(0xFFFFEDEA),
    inversePrimary = Color(0xFFFFB4AA),
)

internal val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4AA),
    onPrimary = Color(0xFF690003),
    primaryContainer = Color(0xFF930006),
    onPrimaryContainer = Color(0xFFFFDAD5),
    secondary = Color(0xFFFFB4AA),
    onSecondary = Color(0xFF690003),
    secondaryContainer = Color(0xFF930006),
    onSecondaryContainer = Color(0xFFFFDAD5),
    background = Color(0xFF1A1110),
    onBackground = Color(0xFFF1DFDC),
    surface = Color(0xFF1A1110),
    onSurface = Color(0xFFF1DFDC),
    surfaceVariant = Color(0xFF3D3231),
    onSurfaceVariant = Color(0xFFD8C2BF),
    surfaceContainerLowest = Color(0xFF140C0B),
    surfaceContainerLow = Color(0xFF231918),
    surfaceContainer = Color(0xFF271D1C),
    surfaceContainerHigh = Color(0xFF322826),
    surfaceContainerHighest = Color(0xFF3D3231),
    outline = Color(0xFFA08C8A),
    outlineVariant = Color(0xFF534341),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFF1DFDC),
    inverseOnSurface = Color(0xFF392E2D),
    inversePrimary = Color(0xFFB3261E),
)

/**
 * Holds the status colours that Material 3 does not have: success, info and warning. Each status
 * has a strong colour for icons and text, and a container colour for backgrounds.
 */
@Immutable
public data class StatusColors(
    val success: Color,
    val successContainer: Color,
    val info: Color,
    val infoContainer: Color,
    val warning: Color,
    val warningContainer: Color,
)

internal val LightStatusColors: StatusColors = StatusColors(
    success = Color(0xFF2E7D32),
    successContainer = Color(0xFFC8E6C9),
    info = Color(0xFF1565C0),
    infoContainer = Color(0xFFD6E3FF),
    warning = Color(0xFF8A5300),
    warningContainer = Color(0xFFFFDDB8),
)

internal val DarkStatusColors: StatusColors = StatusColors(
    success = Color(0xFF81C784),
    successContainer = Color(0xFF1B5E20),
    info = Color(0xFFA8C8FF),
    infoContainer = Color(0xFF004494),
    warning = Color(0xFFFFB95C),
    warningContainer = Color(0xFF693F00),
)

internal val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }
