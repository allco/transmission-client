package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/** Shows one statistic: an icon, a small label and a value, for example a download speed. */
@Composable
public fun Stat(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color = AppTheme.colors.onSurfaceVariant,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = iconTint)
        Column {
            Text(text = label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.onSurfaceVariant)
            Text(text = value, style = AppTheme.typography.titleSmall, color = AppTheme.colors.onSurface)
        }
    }
}

private const val dummyStatValue = "4.2 MB/s"

@Composable
private fun StatSample() {
    Surface(color = AppTheme.colors.surface) {
        Row(Modifier.padding(Spacing.s16), horizontalArrangement = Arrangement.spacedBy(Spacing.s24)) {
            Stat(icon = AppIcons.ArrowDown, label = "Download", value = dummyStatValue, iconTint = AppTheme.statusColors.success)
            Stat(icon = AppIcons.Clock, label = "Remaining", value = "12 min")
        }
    }
}

@Preview
@Composable
private fun StatLightPreview() {
    AppTheme(darkTheme = false) { StatSample() }
}

@Preview
@Composable
private fun StatDarkPreview() {
    AppTheme(darkTheme = true) { StatSample() }
}
