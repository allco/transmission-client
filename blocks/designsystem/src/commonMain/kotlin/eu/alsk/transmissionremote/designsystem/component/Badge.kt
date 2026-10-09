package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows a small label with a coloured background, for example the status of a torrent.
 *
 * The [tone] sets the text colour and the background colour.
 */
@Composable
public fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.Info,
) {
    Text(
        text = text,
        modifier = modifier
            .background(color = tone.containerColor, shape = RoundedCornerShape(BadgeRadius))
            .padding(horizontal = Spacing.s8, vertical = Spacing.s2),
        color = tone.color,
        style = AppTheme.typography.labelSmall,
        maxLines = 1,
    )
}

private val BadgeRadius = 6.dp

private val dummyBadgeText = "Downloading"

@Composable
private fun BadgePreviewContent() {
    Surface {
        Row(
            modifier = Modifier.padding(Spacing.s16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            Tone.entries.forEach { tone ->
                Badge(text = dummyBadgeText, tone = tone)
            }
        }
    }
}

@Preview
@Composable
private fun BadgePreview() {
    AppTheme(darkTheme = false) { BadgePreviewContent() }
}

@Preview
@Composable
private fun BadgeDarkPreview() {
    AppTheme(darkTheme = true) { BadgePreviewContent() }
}
