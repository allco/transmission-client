package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.Checkbox as MaterialCheckbox

/**
 * Shows a checkbox that selects or clears one item.
 *
 * Set [onCheckedChange] to null when a parent row handles the click. Then the checkbox does not
 * take clicks itself.
 */
@Composable
public fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    MaterialCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = AppTheme.colors.primary,
            uncheckedColor = AppTheme.colors.onSurfaceVariant,
            checkmarkColor = AppTheme.colors.onPrimary,
        ),
    )
}

private val dummyCheckboxChecked = true

@Composable
private fun CheckboxPreviewContent() {
    Surface {
        Row(
            modifier = Modifier.padding(Spacing.s16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            Checkbox(checked = dummyCheckboxChecked, onCheckedChange = {})
            Checkbox(checked = !dummyCheckboxChecked, onCheckedChange = {})
            Checkbox(checked = dummyCheckboxChecked, onCheckedChange = {}, enabled = false)
            Checkbox(checked = !dummyCheckboxChecked, onCheckedChange = {}, enabled = false)
        }
    }
}

@Preview
@Composable
private fun CheckboxPreview() {
    AppTheme(darkTheme = false) { CheckboxPreviewContent() }
}

@Preview
@Composable
private fun CheckboxDarkPreview() {
    AppTheme(darkTheme = true) { CheckboxPreviewContent() }
}
