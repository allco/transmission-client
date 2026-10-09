package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
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
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
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
        },
        modifier = modifier,
        dismissButton = dismissText?.let { text ->
            @Composable { TextButton(onClick = onDismiss) { Text(text) } }
        },
        title = { Text(text = title, style = AppTheme.typography.headlineSmall) },
        text = content?.let { body ->
            @Composable { Column(verticalArrangement = Arrangement.spacedBy(Spacing.s16), content = body) }
        },
        shape = RoundedCornerShape(Radius.xxl),
        containerColor = AppTheme.colors.surfaceContainerHigh,
        titleContentColor = AppTheme.colors.onSurface,
        textContentColor = AppTheme.colors.onSurfaceVariant,
    )
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

private const val dummyDialogText = "The torrent \"ubuntu-24.04-desktop-amd64.iso\" goes out of the list."
private const val dummyDialogDeleteFiles = true

@Preview
@Composable
private fun DialogLightPreview() {
    AppTheme(darkTheme = false) {
        Dialog(title = "Remove torrent?", confirmText = "Remove", onConfirm = {}, onDismiss = {}, danger = true) {
            DialogText(dummyDialogText)
            DialogCheckboxOption(label = "Also delete the downloaded files", checked = dummyDialogDeleteFiles, onCheckedChange = {})
        }
    }
}

@Preview
@Composable
private fun DialogDarkPreview() {
    AppTheme(darkTheme = true) {
        Dialog(title = "Pause all torrents?", confirmText = "Pause", onConfirm = {}, onDismiss = {}) {
            DialogText(dummyDialogText)
        }
    }
}
