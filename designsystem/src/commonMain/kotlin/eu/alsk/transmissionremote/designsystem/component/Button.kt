package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.Button as MaterialButton

/** Selects the colours of a [Button]. */
public enum class ButtonStyle {
    /** Uses the primary colour. Use it for the main action of a screen. */
    Filled,

    /** Uses the primary container colour. Use it for a secondary action. */
    Tonal,

    /** Shows a border and no fill. Use it for an alternative action. */
    Outlined,

    /** Shows only the text. Use it for a low-priority action. */
    Text,

    /** Uses the error colour. Use it for an action that deletes or removes data. */
    Danger,
}

/**
 * Shows a pill-shaped button with a text label and an optional leading icon.
 *
 * The button is 40 dp high. When [enabled] is false, the button shows the Material disabled colours
 * and does not call [onClick].
 */
@Composable
public fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Filled,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    val buttonModifier = modifier.height(ButtonHeight)
    val shape = RoundedCornerShape(Radius.full)
    val contentPadding = if (leadingIcon != null) {
        ButtonDefaults.ButtonWithIconContentPadding
    } else {
        ButtonDefaults.ContentPadding
    }
    val content: @Composable RowScope.() -> Unit = { ButtonContent(text, leadingIcon) }
    when (style) {
        ButtonStyle.Filled -> MaterialButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colors.primary,
                contentColor = AppTheme.colors.onPrimary,
            ),
            contentPadding = contentPadding,
            content = content,
        )
        ButtonStyle.Tonal -> MaterialButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colors.primaryContainer,
                contentColor = AppTheme.colors.onPrimaryContainer,
            ),
            contentPadding = contentPadding,
            content = content,
        )
        ButtonStyle.Danger -> MaterialButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colors.error,
                contentColor = AppTheme.colors.onError,
            ),
            contentPadding = contentPadding,
            content = content,
        )
        ButtonStyle.Outlined -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppTheme.colors.primary),
            border = BorderStroke(
                width = OutlineWidth,
                color = if (enabled) {
                    AppTheme.colors.outline
                } else {
                    AppTheme.colors.onSurface.copy(alpha = DisabledBorderAlpha)
                },
            ),
            contentPadding = contentPadding,
            content = content,
        )
        ButtonStyle.Text -> TextButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.textButtonColors(contentColor = AppTheme.colors.primary),
            contentPadding = if (leadingIcon != null) {
                ButtonDefaults.TextButtonWithIconContentPadding
            } else {
                ButtonDefaults.TextButtonContentPadding
            },
            content = content,
        )
    }
}

@Composable
private fun ButtonContent(text: String, leadingIcon: ImageVector?) {
    if (leadingIcon != null) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize),
        )
        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
    }
    Text(text = text, style = AppTheme.typography.labelLarge)
}

private val ButtonHeight = 40.dp
private val OutlineWidth = 1.dp
private const val DisabledBorderAlpha = 0.12f

private val dummyButtonEnabled = true

@Composable
private fun ButtonPreviewContent(enabled: Boolean) {
    Surface {
        Column(
            modifier = Modifier.padding(Spacing.s16),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            ButtonStyle.entries.forEach { style ->
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s8)) {
                    Button(text = style.name, onClick = {}, style = style, enabled = enabled)
                    Button(
                        text = style.name,
                        onClick = {},
                        style = style,
                        enabled = enabled,
                        leadingIcon = AppIcons.Plus,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ButtonPreview() {
    AppTheme(darkTheme = false) { ButtonPreviewContent(enabled = dummyButtonEnabled) }
}

@Preview
@Composable
private fun ButtonDarkPreview() {
    AppTheme(darkTheme = true) { ButtonPreviewContent(enabled = dummyButtonEnabled) }
}

@Preview
@Composable
private fun ButtonDisabledPreview() {
    AppTheme(darkTheme = false) { ButtonPreviewContent(enabled = !dummyButtonEnabled) }
}
