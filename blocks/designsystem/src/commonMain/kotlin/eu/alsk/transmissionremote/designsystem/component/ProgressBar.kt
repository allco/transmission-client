package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/** Selects the height of a [ProgressBar]. */
public enum class ProgressBarSize(internal val height: Dp) {
    /** Makes the bar 4 dp high. Use it in list rows. */
    Small(4.dp),

    /** Makes the bar 8 dp high. Use it in a detail view. */
    Large(8.dp),
}

/**
 * Shows a horizontal bar that fills from the start to [progress].
 *
 * The value of [progress] is from 0 to 1. The bar clamps other values into this range. The [tone]
 * sets the colour of the filled part.
 */
@Composable
public fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.Info,
    size: ProgressBarSize = ProgressBarSize.Small,
) {
    val clamped = progress.coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { clamped },
        modifier = modifier.height(size.height),
        color = tone.color,
        trackColor = AppTheme.colors.surfaceContainerHigh,
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {},
    )
}

private val dummyProgress = 0.62f
private val dummyProgressComplete = 1f

@Composable
private fun ProgressBarPreviewContent() {
    Surface {
        Column(
            modifier = Modifier.padding(Spacing.s16),
            verticalArrangement = Arrangement.spacedBy(Spacing.s12),
        ) {
            Tone.entries.forEach { tone ->
                ProgressBar(progress = dummyProgress, modifier = Modifier.fillMaxWidth(), tone = tone)
            }
            ProgressBar(
                progress = dummyProgress,
                modifier = Modifier.fillMaxWidth(),
                size = ProgressBarSize.Large,
            )
            ProgressBar(
                progress = dummyProgressComplete,
                modifier = Modifier.fillMaxWidth(),
                tone = Tone.Success,
                size = ProgressBarSize.Large,
            )
        }
    }
}

@Preview
@Composable
private fun ProgressBarPreview() {
    AppTheme(darkTheme = false) { ProgressBarPreviewContent() }
}

@Preview
@Composable
private fun ProgressBarDarkPreview() {
    AppTheme(darkTheme = true) { ProgressBarPreviewContent() }
}
