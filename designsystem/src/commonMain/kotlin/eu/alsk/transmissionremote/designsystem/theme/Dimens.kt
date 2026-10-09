package eu.alsk.transmissionremote.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// The values are the "Spacing" and "Radius" variables of the Figma file.

/** Holds the spacing scale for padding and gaps. */
public object Spacing {
    public val s2: Dp = 2.dp
    public val s4: Dp = 4.dp
    public val s8: Dp = 8.dp
    public val s12: Dp = 12.dp
    public val s16: Dp = 16.dp
    public val s20: Dp = 20.dp
    public val s24: Dp = 24.dp
    public val s32: Dp = 32.dp
    public val s48: Dp = 48.dp
}

/** Holds the corner radius scale. Use [full] for pills and circles. */
public object Radius {
    public val xs: Dp = 4.dp
    public val sm: Dp = 8.dp
    public val md: Dp = 12.dp
    public val lg: Dp = 16.dp
    public val xl: Dp = 20.dp
    public val xxl: Dp = 28.dp
    public val full: Dp = 999.dp
}

/** Holds the shadow elevations. They match the effect styles Elevation/1 to Elevation/3. */
public object Elevation {
    public val level1: Dp = 1.dp
    public val level2: Dp = 3.dp
    public val level3: Dp = 6.dp
}

internal val AppShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.xs),
    small = RoundedCornerShape(Radius.sm),
    medium = RoundedCornerShape(Radius.md),
    large = RoundedCornerShape(Radius.lg),
    extraLarge = RoundedCornerShape(Radius.xxl),
)
