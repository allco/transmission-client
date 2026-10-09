package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import eu.alsk.transmissionremote.designsystem.theme.Elevation
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.DropdownMenu as M3DropdownMenu

private val MenuMinWidth = 232.dp
private val MenuIconSize = 20.dp

/** Shows a popup menu of [MenuItem] rows when [expanded] is true. */
@Composable
public fun DropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    M3DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.widthIn(min = MenuMinWidth),
        shape = RoundedCornerShape(Radius.md),
        containerColor = AppTheme.colors.surfaceContainer,
        tonalElevation = 0.dp,
        shadowElevation = Elevation.level2,
        content = content,
    )
}

/** Holds the content at the end of a [MenuItem]. */
public sealed interface MenuItemTrailing {
    /** Shows no content at the end. */
    public data object None : MenuItemTrailing

    /** Shows the current value of the item. */
    public data class Value(val text: String) : MenuItemTrailing

    /** Shows a chevron, and the current [value] before it when it is set. Use it for a submenu. */
    public data class Chevron(val value: String? = null) : MenuItemTrailing

    /** Shows a switch. The item click changes the value. */
    public data class Switch(val checked: Boolean) : MenuItemTrailing
}

/** Shows one row of a [DropdownMenu]: an optional leading icon, the text and the [trailing] content. */
@Composable
public fun MenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailing: MenuItemTrailing = MenuItemTrailing.None,
) {
    DropdownMenuItem(
        text = { Text(text = text, style = AppTheme.typography.bodyLarge, color = AppTheme.colors.onSurface) },
        onClick = onClick,
        modifier = modifier,
        leadingIcon = if (leadingIcon != null) {
            { MenuIcon(leadingIcon) }
        } else {
            null
        },
        trailingIcon = if (trailing != MenuItemTrailing.None) {
            { MenuItemTrailingContent(trailing) }
        } else {
            null
        },
    )
}

/** Shows a line that separates two groups of items in a [DropdownMenu]. */
@Composable
public fun MenuDivider() {
    Divider(Modifier.padding(vertical = Spacing.s8))
}

/** Shows the [trailing] content of a [MenuItem]. */
@Composable
private fun MenuItemTrailingContent(trailing: MenuItemTrailing) {
    when (trailing) {
        MenuItemTrailing.None -> Unit
        is MenuItemTrailing.Value -> MenuValueText(trailing.text)
        is MenuItemTrailing.Chevron -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s4),
        ) {
            if (trailing.value != null) MenuValueText(trailing.value)
            MenuIcon(AppIcons.ChevronRight)
        }
        is MenuItemTrailing.Switch -> Switch(checked = trailing.checked, onCheckedChange = null)
    }
}

/** Shows the value text at the end of a [MenuItem]. */
@Composable
private fun MenuValueText(text: String) {
    Text(text = text, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant)
}

/** Shows a 20 dp menu icon in the onSurfaceVariant colour. */
@Composable
private fun MenuIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(MenuIconSize),
        tint = AppTheme.colors.onSurfaceVariant,
    )
}

private val dummyMenuItemSort = MenuItemTrailing.Chevron(value = "Name")
private val dummyMenuItemSpeed = MenuItemTrailing.Switch(checked = true)
private val dummyMenuItemValue = MenuItemTrailing.Value(text = "12")

// The preview shows the items on the menu surface. A popup does not show in all preview tools.
@Composable
private fun MenuSample() {
    Surface(
        modifier = Modifier.width(MenuMinWidth),
        shape = RoundedCornerShape(Radius.md),
        color = AppTheme.colors.surfaceContainer,
    ) {
        Column(Modifier.padding(vertical = Spacing.s8)) {
            MenuItem(text = "Sort", onClick = {}, leadingIcon = AppIcons.Sort, trailing = dummyMenuItemSort)
            MenuItem(text = "Alternative speeds", onClick = {}, leadingIcon = AppIcons.Gauge, trailing = dummyMenuItemSpeed)
            MenuItem(text = "Active torrents", onClick = {}, leadingIcon = AppIcons.List, trailing = dummyMenuItemValue)
            MenuDivider()
            MenuItem(text = "Settings", onClick = {}, leadingIcon = AppIcons.Sliders)
        }
    }
}

@Preview
@Composable
private fun MenuLightPreview() {
    AppTheme(darkTheme = false) { MenuSample() }
}

@Preview
@Composable
private fun MenuDarkPreview() {
    AppTheme(darkTheme = true) { MenuSample() }
}
