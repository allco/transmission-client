package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows a status message in a rounded box in the container colour of [tone]. The action shows
 * only when [actionText] and [onAction] are both set.
 */
@Composable
public fun Banner(
    message: String,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.Info,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(tone.containerColor, RoundedCornerShape(Radius.lg))
            .padding(start = Spacing.s16, end = Spacing.s8, top = Spacing.s12, bottom = Spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s12),
    ) {
        Icon(
            imageVector = tone.bannerIcon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = tone.color,
        )
        Text(
            text = message,
            modifier = Modifier.weight(1f).padding(end = Spacing.s8),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurface,
        )
        if (actionText != null && onAction != null) {
            TextButton(
                onClick = onAction,
                colors = ButtonDefaults.textButtonColors(contentColor = tone.color),
            ) {
                Text(actionText)
            }
        }
    }
}

private val Tone.bannerIcon: ImageVector
    get() = when (this) {
        Tone.Info, Tone.Neutral -> AppIcons.Info
        Tone.Warning -> AppIcons.AlertTriangle
        Tone.Error -> AppIcons.AlertCircle
        Tone.Success -> AppIcons.CircleCheck
    }

private const val dummyBannerMessage = "The connection to the server is lost. The app tries again in 10 s."

@Composable
private fun BannerSample() {
    Surface(color = AppTheme.colors.surface) {
        Column(Modifier.padding(Spacing.s16), verticalArrangement = Arrangement.spacedBy(Spacing.s12)) {
            Tone.entries.forEach { tone ->
                Banner(message = dummyBannerMessage, tone = tone, actionText = "Retry", onAction = {})
            }
            Banner(message = "Speed limits are on.")
        }
    }
}

@Preview
@Composable
private fun BannerLightPreview() {
    AppTheme(darkTheme = false) { BannerSample() }
}

@Preview
@Composable
private fun BannerDarkPreview() {
    AppTheme(darkTheme = true) { BannerSample() }
}
