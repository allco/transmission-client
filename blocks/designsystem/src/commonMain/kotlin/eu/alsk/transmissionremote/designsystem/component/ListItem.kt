package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/** Holds the content at the end of a [ListItem]. */
public sealed interface ListItemTrailing {
    /** Shows no content at the end. */
    public data object None : ListItemTrailing

    /** Shows a chevron. Use it for a row that opens a new screen. */
    public data object Chevron : ListItemTrailing

    /** Shows the current value of a setting, and a chevron when [chevron] is true. */
    public data class Value(val text: String, val chevron: Boolean = false) : ListItemTrailing

    /** Shows a switch. The switch calls [onCheckedChange] when the user toggles it. */
    public data class Switch(val checked: Boolean, val onCheckedChange: (Boolean) -> Unit) : ListItemTrailing

    /** Shows a checkbox. The row click changes the value. */
    public data class Check(val checked: Boolean) : ListItemTrailing

    /** Shows a radio button. The row click changes the value. */
    public data class Radio(val selected: Boolean) : ListItemTrailing
}

/**
 * Shows one row of a list: an optional leading icon, a headline, an optional supporting text and
 * the [trailing] content. The row is at least 56 dp high. When [onClick] is set, the row is
 * clickable.
 */
@Composable
public fun ListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    leadingIcon: ImageVector? = null,
    trailing: ListItemTrailing = ListItemTrailing.None,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .heightIn(min = 56.dp)
            .padding(horizontal = Spacing.s16, vertical = Spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = AppTheme.colors.onSurfaceVariant,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(text = headline, style = AppTheme.typography.bodyLarge, color = AppTheme.colors.onSurface)
            if (supporting != null) {
                Text(
                    text = supporting,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
        }
        ListItemTrailingContent(trailing)
    }
}

/** Shows the [trailing] content of a [ListItem]. */
@Composable
private fun ListItemTrailingContent(trailing: ListItemTrailing) {
    when (trailing) {
        ListItemTrailing.None -> Unit
        ListItemTrailing.Chevron -> ChevronIcon()
        is ListItemTrailing.Value -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s4),
        ) {
            Text(text = trailing.text, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant)
            if (trailing.chevron) ChevronIcon()
        }
        is ListItemTrailing.Switch -> Switch(checked = trailing.checked, onCheckedChange = trailing.onCheckedChange)
        is ListItemTrailing.Check -> Checkbox(checked = trailing.checked, onCheckedChange = null)
        is ListItemTrailing.Radio -> RadioButton(selected = trailing.selected, onClick = null)
    }
}

/** Shows the 24 dp chevron at the end of a row. */
@Composable
private fun ChevronIcon() {
    Icon(
        imageVector = AppIcons.ChevronRight,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = AppTheme.colors.onSurfaceVariant,
    )
}

private val dummyListItemSwitch = ListItemTrailing.Switch(checked = true, onCheckedChange = {})
private val dummyListItemValue = ListItemTrailing.Value(text = "1 MB/s", chevron = true)
private val dummyListItemCheck = ListItemTrailing.Check(checked = true)
private val dummyListItemRadio = ListItemTrailing.Radio(selected = false)

@Composable
private fun ListItemSample() {
    Surface(color = AppTheme.colors.surface) {
        Column {
            ListItem(headline = "Home server", supporting = "192.168.1.10:9091", leadingIcon = AppIcons.Server, trailing = ListItemTrailing.Chevron, onClick = {})
            ListItem(headline = "Download limit", leadingIcon = AppIcons.Gauge, trailing = dummyListItemValue, onClick = {})
            ListItem(headline = "Alternative speeds", supporting = "Use lower limits at night", trailing = dummyListItemSwitch)
            ListItem(headline = "Start when added", trailing = dummyListItemCheck, onClick = {})
            ListItem(headline = "Sort by name", trailing = dummyListItemRadio, onClick = {})
        }
    }
}

@Preview
@Composable
private fun ListItemLightPreview() {
    AppTheme(darkTheme = false) { ListItemSample() }
}

@Preview
@Composable
private fun ListItemDarkPreview() {
    AppTheme(darkTheme = true) { ListItemSample() }
}
