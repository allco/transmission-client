package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.IconButton as MaterialIconButton

/** Selects the colours of an [IconButton]. */
public enum class IconButtonStyle {
    /** Shows only the icon, with no container. */
    Standard,

    /** Shows the icon on the primary container colour. */
    Tonal,

    /** Shows the icon on the primary colour. Use it for the main action. */
    Filled,
}

/**
 * Shows a round 40 dp button that holds one icon.
 *
 * Give a [contentDescription] for screen readers. Use null only when other text near the button
 * tells its action.
 */
@Composable
public fun IconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: IconButtonStyle = IconButtonStyle.Standard,
    enabled: Boolean = true,
) {
    val buttonModifier = modifier.size(IconButtonSize)
    val content: @Composable () -> Unit = {
        Icon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(IconSize))
    }
    when (style) {
        IconButtonStyle.Standard -> MaterialIconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(contentColor = AppTheme.colors.onSurfaceVariant),
            content = content,
        )
        IconButtonStyle.Tonal -> FilledIconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = AppTheme.colors.primaryContainer,
                contentColor = AppTheme.colors.onPrimaryContainer,
            ),
            content = content,
        )
        IconButtonStyle.Filled -> FilledIconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = AppTheme.colors.primary,
                contentColor = AppTheme.colors.onPrimary,
            ),
            content = content,
        )
    }
}

private val IconButtonSize = 40.dp
private val IconSize = 24.dp

private val dummyIconButtonEnabled = true

@Composable
private fun IconButtonPreviewContent(enabled: Boolean) {
    Surface {
        Row(
            modifier = Modifier.padding(Spacing.s16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            IconButtonStyle.entries.forEach { style ->
                IconButton(
                    icon = AppIcons.Pause,
                    contentDescription = "Pause",
                    onClick = {},
                    style = style,
                    enabled = enabled,
                )
            }
        }
    }
}

@Preview
@Composable
private fun IconButtonPreview() {
    AppTheme(darkTheme = false) { IconButtonPreviewContent(enabled = dummyIconButtonEnabled) }
}

@Preview
@Composable
private fun IconButtonDarkPreview() {
    AppTheme(darkTheme = true) { IconButtonPreviewContent(enabled = dummyIconButtonEnabled) }
}

@Preview
@Composable
private fun IconButtonDisabledPreview() {
    AppTheme(darkTheme = false) { IconButtonPreviewContent(enabled = !dummyIconButtonEnabled) }
}
