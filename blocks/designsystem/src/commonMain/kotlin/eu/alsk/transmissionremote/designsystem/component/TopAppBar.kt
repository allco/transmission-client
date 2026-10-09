package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

private val TopAppBarHeight = 64.dp

/** Holds the default window insets of the top app bars: the top and the sides of the safe area. */
private val topAppBarInsets: WindowInsets
    @Composable get() = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)

/**
 * Shows the top bar of a screen: an optional back button, a title, an optional subtitle and the
 * actions. When [statusTone] is set, a dot in the tone colour shows before the subtitle.
 */
@Composable
public fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    statusTone: Tone? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = topAppBarInsets,
) {
    TopAppBarFrame(color = AppTheme.colors.surface, windowInsets = windowInsets, modifier = modifier) {
        if (onBack != null) {
            BarIconButton(icon = AppIcons.ArrowLeft, contentDescription = "Back", onClick = onBack)
        } else {
            Spacer(Modifier.width(Spacing.s12))
        }
        Column(Modifier.weight(1f).padding(horizontal = Spacing.s4)) {
            Text(
                text = title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s4),
                ) {
                    if (statusTone != null) {
                        Box(Modifier.size(8.dp).background(statusTone.color, CircleShape))
                    }
                    Text(
                        text = subtitle,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        actions()
    }
}

/**
 * Shows a top bar with a search field in a pill. The close button clears the query. It shows only
 * when the query is not empty.
 */
@Composable
public fun SearchTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    windowInsets: WindowInsets = topAppBarInsets,
) {
    TopAppBarFrame(color = AppTheme.colors.surface, windowInsets = windowInsets, modifier = modifier) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.s4)
                .height(48.dp)
                .background(AppTheme.colors.surfaceContainerHigh, CircleShape),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BarIconButton(icon = AppIcons.ArrowLeft, contentDescription = "Back", onClick = onBack)
            val textStyle = AppTheme.typography.bodyLarge.copy(color = AppTheme.colors.onSurface)
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = textStyle,
                singleLine = true,
                cursorBrush = SolidColor(AppTheme.colors.primary),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle,
                                color = AppTheme.colors.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            if (query.isNotEmpty()) {
                BarIconButton(icon = AppIcons.Close, contentDescription = "Clear", onClick = { onQueryChange("") })
            } else {
                Spacer(Modifier.width(Spacing.s16))
            }
        }
    }
}

/**
 * Shows the top bar of the selection mode: a close button, the title (for example the number of
 * selected items) and the actions on the selection.
 */
@Composable
public fun SelectionTopAppBar(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = topAppBarInsets,
) {
    TopAppBarFrame(color = AppTheme.colors.surfaceContainer, windowInsets = windowInsets, modifier = modifier) {
        BarIconButton(icon = AppIcons.Close, contentDescription = "Close", onClick = onClose)
        Text(
            text = title,
            modifier = Modifier.weight(1f).padding(horizontal = Spacing.s4),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        actions()
    }
}

/** Gives the three top app bars the same surface, insets and height. */
@Composable
private fun TopAppBarFrame(
    color: Color,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(modifier = modifier.fillMaxWidth(), color = color) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(windowInsets)
                .height(TopAppBarHeight)
                .padding(horizontal = Spacing.s4),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/** Shows a 24 dp icon in an icon button, in the onSurface colour. */
@Composable
private fun BarIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = AppTheme.colors.onSurface,
        )
    }
}

private const val dummyTopAppBarTitle = "Home server"
private const val dummyTopAppBarSubtitle = "Connected, 12 torrents"
private const val dummySearchTopAppBarQuery = "ubuntu"
private const val dummySelectionTopAppBarTitle = "3 selected"

@Composable
private fun TopAppBarSample() {
    Column {
        TopAppBar(
            title = dummyTopAppBarTitle,
            subtitle = dummyTopAppBarSubtitle,
            statusTone = Tone.Success,
            actions = {
                BarIconButton(icon = AppIcons.Search, contentDescription = "Search", onClick = {})
                BarIconButton(icon = AppIcons.MoreVertical, contentDescription = "More", onClick = {})
            },
        )
        TopAppBar(title = "Settings", onBack = {})
        SearchTopAppBar(query = "", onQueryChange = {}, onBack = {})
        SearchTopAppBar(query = dummySearchTopAppBarQuery, onQueryChange = {}, onBack = {})
        SelectionTopAppBar(
            title = dummySelectionTopAppBarTitle,
            onClose = {},
            actions = { BarIconButton(icon = AppIcons.Trash, contentDescription = "Delete", onClick = {}) },
        )
    }
}

@Preview
@Composable
private fun TopAppBarLightPreview() {
    AppTheme(darkTheme = false) { TopAppBarSample() }
}

@Preview
@Composable
private fun TopAppBarDarkPreview() {
    AppTheme(darkTheme = true) { TopAppBarSample() }
}
