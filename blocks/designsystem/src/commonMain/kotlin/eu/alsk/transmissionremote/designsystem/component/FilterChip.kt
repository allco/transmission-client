package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.FilterChip as MaterialFilterChip

/**
 * Shows a chip that turns one filter on or off.
 *
 * When [selected] is true, the chip shows the primary container colour and a check icon. When
 * [selected] is false, the chip shows a border and no fill.
 */
@Composable
public fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MaterialFilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, style = AppTheme.typography.labelLarge) },
        modifier = modifier,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = AppIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        } else {
            null
        },
        shape = RoundedCornerShape(Radius.sm),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = AppTheme.colors.onSurfaceVariant,
            selectedContainerColor = AppTheme.colors.primaryContainer,
            selectedLabelColor = AppTheme.colors.onPrimaryContainer,
            selectedLeadingIconColor = AppTheme.colors.onPrimaryContainer,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = AppTheme.colors.outlineVariant,
            selectedBorderColor = Color.Transparent,
        ),
    )
}

private val dummyFilterChipSelected = true

@Composable
private fun FilterChipPreviewContent() {
    Surface {
        Row(
            modifier = Modifier.padding(Spacing.s16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            FilterChip(label = "Downloading", selected = dummyFilterChipSelected, onClick = {})
            FilterChip(label = "Seeding", selected = !dummyFilterChipSelected, onClick = {})
        }
    }
}

@Preview
@Composable
private fun FilterChipPreview() {
    AppTheme(darkTheme = false) { FilterChipPreviewContent() }
}

@Preview
@Composable
private fun FilterChipDarkPreview() {
    AppTheme(darkTheme = true) { FilterChipPreviewContent() }
}
