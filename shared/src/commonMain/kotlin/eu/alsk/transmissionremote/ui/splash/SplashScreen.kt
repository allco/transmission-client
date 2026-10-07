package eu.alsk.transmissionremote.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.ui.theme.AppTheme
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1500L

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        currentOnFinished()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppLogo(
                background = MaterialTheme.colorScheme.primary,
                foreground = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(96.dp),
            )
            Spacer(Modifier.height(24.dp))
            Text("Transmission Remote", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
        }
    }
}

/** A filled circle with a "download" arrow. */
@Composable
private fun AppLogo(background: Color, foreground: Color, modifier: Modifier = Modifier) {
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

@Preview(name = "Splash – light", showBackground = true)
@Composable
private fun SplashScreenPreview() {
    AppTheme(darkTheme = false) { SplashScreen(onFinished = {}) }
}

@Preview(name = "Splash – dark", showBackground = true)
@Composable
private fun SplashScreenDarkPreview() {
    AppTheme(darkTheme = true) { SplashScreen(onFinished = {}) }
}

@Preview(showBackground = true)
@Composable
private fun AppLogoPreview() {
    AppTheme {
        AppLogo(
            background = MaterialTheme.colorScheme.primary,
            foreground = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(96.dp),
        )
    }
}
