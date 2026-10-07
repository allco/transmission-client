package eu.alsk.transmissionremote.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.ui.theme.AppTheme

/** A filled circle with a "download" arrow. */
@Composable
internal fun AppLogo(background: Color, foreground: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawCircle(background)
        val stroke = Stroke(width = size.minDimension * 0.08f, cap = StrokeCap.Round)
        val cx = size.width / 2
        val top = size.height * 0.25f
        val tip = size.height * 0.65f
        val wing = size.width * 0.17f
        val arrow = Path().apply {
            moveTo(cx, top)
            lineTo(cx, tip)
            moveTo(cx - wing, tip - wing)
            lineTo(cx, tip)
            lineTo(cx + wing, tip - wing)
        }
        drawPath(arrow, foreground, style = stroke)
        val baseY = size.height * 0.76f
        drawLine(
            foreground,
            Offset(cx - wing * 1.3f, baseY),
            Offset(cx + wing * 1.3f, baseY),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
    }
}

private val dummyAppLogoSize = 96.dp

@Preview(showBackground = true)
@Composable
private fun AppLogoPreview() {
    AppTheme {
        AppLogo(
            background = MaterialTheme.colorScheme.primary,
            foreground = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(dummyAppLogoSize),
        )
    }
}
