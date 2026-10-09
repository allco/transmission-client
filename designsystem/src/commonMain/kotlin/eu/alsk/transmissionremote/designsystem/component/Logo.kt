package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.theme.AppTheme

/** Draws the app logo: a primary circle with a "download" arrow in onPrimary. */
@Composable
public fun Logo(modifier: Modifier = Modifier, size: Dp = 96.dp) {
    val background = AppTheme.colors.primary
    val foreground = AppTheme.colors.onPrimary
    Canvas(modifier.size(size)) {
        drawCircle(background)
        val stroke = Stroke(width = this.size.minDimension * 0.08f, cap = StrokeCap.Round)
        val cx = this.size.width / 2
        val top = this.size.height * 0.25f
        val tip = this.size.height * 0.65f
        val wing = this.size.width * 0.17f
        val arrow = Path().apply {
            moveTo(cx, top)
            lineTo(cx, tip)
            moveTo(cx - wing, tip - wing)
            lineTo(cx, tip)
            lineTo(cx + wing, tip - wing)
        }
        drawPath(arrow, foreground, style = stroke)
        val baseY = this.size.height * 0.76f
        drawLine(
            foreground,
            Offset(cx - wing * 1.3f, baseY),
            Offset(cx + wing * 1.3f, baseY),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
    }
}

@Preview
@Composable
private fun LogoLightPreview() {
    AppTheme(darkTheme = false) { Logo() }
}

@Preview
@Composable
private fun LogoDarkPreview() {
    AppTheme(darkTheme = true) { Logo(size = 48.dp) }
}
