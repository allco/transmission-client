package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows an icon, a title, an optional body and an optional action when a screen has no content.
 * The action shows only when [actionText] and [onAction] are both set.
 */
@Composable
public fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(Spacing.s24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(64.dp).background(AppTheme.colors.surfaceContainerHigh, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = AppTheme.colors.onSurfaceVariant,
            )
        }
        Column(
            modifier = Modifier.padding(top = Spacing.s16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
            if (body != null) {
                Text(
                    text = body,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (actionText != null && onAction != null) {
            FilledTonalButton(onClick = onAction, modifier = Modifier.padding(top = Spacing.s24)) {
                Text(actionText)
            }
        }
    }
}

private const val dummyEmptyStateTitle = "No torrents"
private const val dummyEmptyStateBody = "Add a torrent file or a magnet link to start a download."

@Preview
@Composable
private fun EmptyStateLightPreview() {
    AppTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.surface) {
            EmptyState(
                icon = AppIcons.List,
                title = dummyEmptyStateTitle,
                body = dummyEmptyStateBody,
                actionText = "Add torrent",
                onAction = {},
            )
        }
    }
}

@Preview
@Composable
private fun EmptyStateDarkPreview() {
    AppTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.surface) {
            EmptyState(icon = AppIcons.Search, title = dummyEmptyStateTitle)
        }
    }
}
