package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Holds the label and the optional icon of one segment of [SegmentedButtons].
 *
 * When [icon] is null, the selected segment shows a check icon.
 */
public data class SegmentOption(val label: String, val icon: ImageVector? = null)

/**
 * Shows a row of connected buttons. The user selects exactly one of the [options].
 *
 * The segment at [selectedIndex] shows the primary container colour. A click on a segment calls
 * [onSelect] with the index of that segment.
 */
@Composable
public fun SegmentedButtons(
    options: List<SegmentOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            SegmentedButton(
                selected = selected,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = AppTheme.colors.primaryContainer,
                    activeContentColor = AppTheme.colors.onPrimaryContainer,
                    activeBorderColor = AppTheme.colors.outline,
                    inactiveContainerColor = AppTheme.colors.surface,
                    inactiveContentColor = AppTheme.colors.onSurface,
                    inactiveBorderColor = AppTheme.colors.outline,
                ),
                icon = {
                    if (option.icon != null) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            modifier = Modifier.size(IconSize),
                        )
                    } else {
                        SegmentedButtonDefaults.Icon(active = selected)
                    }
                },
                label = { Text(text = option.label, style = AppTheme.typography.labelLarge) },
            )
        }
    }
}

private val IconSize = 18.dp

private val dummySegmentOptions = listOf(
    SegmentOption("All"),
    SegmentOption("Active"),
    SegmentOption("Done"),
)
private val dummySegmentOptionsWithIcons = listOf(
    SegmentOption("Down", AppIcons.ArrowDown),
    SegmentOption("Up", AppIcons.ArrowUp),
)
private val dummySegmentSelectedIndex = 1

@Composable
private fun SegmentedButtonsPreviewContent() {
    Surface {
        Column(
            modifier = Modifier.padding(Spacing.s16),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            SegmentedButtons(
                options = dummySegmentOptions,
                selectedIndex = dummySegmentSelectedIndex,
                onSelect = {},
            )
            SegmentedButtons(
                options = dummySegmentOptionsWithIcons,
                selectedIndex = dummySegmentSelectedIndex,
                onSelect = {},
            )
        }
    }
}

@Preview
@Composable
private fun SegmentedButtonsPreview() {
    AppTheme(darkTheme = false) { SegmentedButtonsPreviewContent() }
}

@Preview
@Composable
private fun SegmentedButtonsDarkPreview() {
    AppTheme(darkTheme = true) { SegmentedButtonsPreviewContent() }
}
