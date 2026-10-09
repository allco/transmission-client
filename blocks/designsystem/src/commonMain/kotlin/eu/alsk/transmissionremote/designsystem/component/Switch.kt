package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.Switch as MaterialSwitch

/**
 * Shows a switch that turns one setting on or off.
 *
 * Set [onCheckedChange] to null when a parent row handles the click. Then the switch does not
 * take clicks itself.
 */
@Composable
public fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    MaterialSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = AppTheme.colors.onPrimary,
            checkedTrackColor = AppTheme.colors.primary,
            uncheckedThumbColor = AppTheme.colors.outline,
            uncheckedTrackColor = AppTheme.colors.surfaceContainerHighest,
            uncheckedBorderColor = AppTheme.colors.outline,
        ),
    )
}

private val dummySwitchChecked = true

@Composable
private fun SwitchPreviewContent() {
    Surface {
        Row(
            modifier = Modifier.padding(Spacing.s16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            Switch(checked = dummySwitchChecked, onCheckedChange = {})
            Switch(checked = !dummySwitchChecked, onCheckedChange = {})
            Switch(checked = dummySwitchChecked, onCheckedChange = {}, enabled = false)
            Switch(checked = !dummySwitchChecked, onCheckedChange = {}, enabled = false)
        }
    }
}

@Preview
@Composable
private fun SwitchPreview() {
    AppTheme(darkTheme = false) { SwitchPreviewContent() }
}

@Preview
@Composable
private fun SwitchDarkPreview() {
    AppTheme(darkTheme = true) { SwitchPreviewContent() }
}
