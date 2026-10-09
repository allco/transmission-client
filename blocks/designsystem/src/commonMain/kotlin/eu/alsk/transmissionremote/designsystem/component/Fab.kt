package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Elevation
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows an extended floating action button with an icon and a text label.
 *
 * Use it for the main action of a screen, for example "Add torrent". The button is 56 dp high and
 * floats above the content.
 */
@Composable
public fun Fab(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        text = { Text(text = text, style = AppTheme.typography.labelLarge) },
        icon = { Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(IconSize)) },
        onClick = onClick,
        modifier = modifier.heightIn(min = FabHeight),
        shape = RoundedCornerShape(Radius.lg),
        containerColor = AppTheme.colors.primaryContainer,
        contentColor = AppTheme.colors.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Elevation.level3),
    )
}

private val FabHeight = 56.dp
private val IconSize = 24.dp

private val dummyFabText = "Add torrent"

@Composable
private fun FabPreviewContent() {
    Surface {
        Box(Modifier.padding(Spacing.s16)) {
            Fab(text = dummyFabText, icon = AppIcons.Plus, onClick = {})
        }
    }
}

@Preview
@Composable
private fun FabPreview() {
    AppTheme(darkTheme = false) { FabPreviewContent() }
}

@Preview
@Composable
private fun FabDarkPreview() {
    AppTheme(darkTheme = true) { FabPreviewContent() }
}
