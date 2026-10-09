package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.RadioButton as MaterialRadioButton

/**
 * Shows a radio button that selects one option from a group.
 *
 * Set [onClick] to null when a parent row handles the click. Then the radio button does not take
 * clicks itself.
 */
@Composable
public fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    MaterialRadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = RadioButtonDefaults.colors(
            selectedColor = AppTheme.colors.primary,
            unselectedColor = AppTheme.colors.onSurfaceVariant,
        ),
    )
}

private val dummyRadioButtonSelected = true

@Composable
private fun RadioButtonPreviewContent() {
    Surface {
        Row(
            modifier = Modifier.padding(Spacing.s16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            RadioButton(selected = dummyRadioButtonSelected, onClick = {})
            RadioButton(selected = !dummyRadioButtonSelected, onClick = {})
            RadioButton(selected = dummyRadioButtonSelected, onClick = {}, enabled = false)
            RadioButton(selected = !dummyRadioButtonSelected, onClick = {}, enabled = false)
        }
    }
}

@Preview
@Composable
private fun RadioButtonPreview() {
    AppTheme(darkTheme = false) { RadioButtonPreviewContent() }
}

@Preview
@Composable
private fun RadioButtonDarkPreview() {
    AppTheme(darkTheme = true) { RadioButtonPreviewContent() }
}
