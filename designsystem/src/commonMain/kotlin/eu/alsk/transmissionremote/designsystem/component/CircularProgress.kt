package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows a turning circle. Use it when the app waits for a result and does not know the progress.
 *
 * The [size] sets the diameter of the circle.
 */
@Composable
public fun CircularProgress(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = AppTheme.colors.primary,
        strokeWidth = size * StrokeRatio,
        trackColor = AppTheme.colors.primaryContainer,
        strokeCap = StrokeCap.Round,
    )
}

// The Material 3 indicator is 40 dp with a 4 dp stroke. The stroke keeps this ratio at all sizes.
private const val StrokeRatio = 0.1f

@Composable
private fun CircularProgressPreviewContent() {
    Surface {
        Row(
            modifier = Modifier.padding(Spacing.s16),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
        ) {
            CircularProgress(size = 24.dp)
            CircularProgress()
            CircularProgress(size = 48.dp)
        }
    }
}

@Preview
@Composable
private fun CircularProgressPreview() {
    AppTheme(darkTheme = false) { CircularProgressPreviewContent() }
}

@Preview
@Composable
private fun CircularProgressDarkPreview() {
    AppTheme(darkTheme = true) { CircularProgressPreviewContent() }
}
