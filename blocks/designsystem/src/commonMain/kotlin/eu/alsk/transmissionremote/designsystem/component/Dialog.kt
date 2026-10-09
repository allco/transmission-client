package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog as ComposeDialog
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Elevation
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows a modal dialog with a title, an optional [content] and the confirm and dismiss buttons.
 * When [danger] is true, the confirm button is a filled button in the error colours. Set
 * [dismissText] to null to show no dismiss button.
 */
@Composable
public fun Dialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = "Cancel",
    danger: Boolean = false,
    confirmEnabled: Boolean = true,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    ComposeDialog(onDismissRequest = onDismiss) {
        DialogPanel(
            title = title,
            confirmText = confirmText,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            modifier = modifier,
            dismissText = dismissText,
            danger = danger,
            confirmEnabled = confirmEnabled,
            content = content,
        )
    }
}

// The visible panel of the dialog. Dialog shows it in a dialog window. The previews show it
// directly, because the preview tools do not draw dialog windows.
@Composable
private fun DialogPanel(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = "Cancel",
    danger: Boolean = false,
    confirmEnabled: Boolean = true,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.widthIn(min = 280.dp, max = 560.dp),
        shape = RoundedCornerShape(Radius.xxl),
        color = AppTheme.colors.surfaceContainerHigh,
        shadowElevation = Elevation.level3,
    ) {
        Column(
            modifier = Modifier.padding(Spacing.s24),
            verticalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            Text(text = title, style = AppTheme.typography.headlineSmall, color = AppTheme.colors.onSurface)
            if (content != null) {
                CompositionLocalProvider(LocalContentColor provides AppTheme.colors.onSurfaceVariant) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s16), content = content)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s8, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (dismissText != null) {
                    TextButton(onClick = onDismiss) { Text(dismissText) }
                }
                if (danger) {
                    Button(
                        onClick = onConfirm,
                        enabled = confirmEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.error,
                            contentColor = AppTheme.colors.onError,
                        ),
                    ) { Text(confirmText) }
                } else {
                    TextButton(onClick = onConfirm, enabled = confirmEnabled) { Text(confirmText) }
                }
            }
        }
    }
}

/** Shows the body text of a [Dialog]. */
@Composable
public fun DialogText(text: String) {
    Text(text = text, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant)
}

/**
 * Shows a checkbox with a label in a [Dialog], for example "Also delete the downloaded files". A
 * click on the label also toggles the checkbox.
 */
@Composable
public fun DialogCheckboxOption(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(
            text = label,
            modifier = Modifier.weight(1f).padding(start = Spacing.s12),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurface,
        )
    }
}

private const val dummyDialogText = "The server removes \"ubuntu-24.04.1-desktop-amd64.iso\" from the list."
private const val dummyDialogDeleteFiles = true

@Preview
@Composable
private fun DialogLightPreview() {
    AppTheme(darkTheme = false) {
        DialogPanel(title = "Remove torrent?", confirmText = "Remove", onConfirm = {}, onDismiss = {}, danger = true) {
            DialogText(dummyDialogText)
            DialogCheckboxOption(
                label = "Also delete the downloaded files",
                checked = dummyDialogDeleteFiles,
                onCheckedChange = {},
            )
        }
    }
}

@Preview
@Composable
private fun DialogDarkPreview() {
    AppTheme(darkTheme = true) {
        DialogPanel(title = "Pause all torrents?", confirmText = "Pause", onConfirm = {}, onDismiss = {}) {
            DialogText(dummyDialogText)
        }
    }
}
