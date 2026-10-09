package eu.alsk.transmissionremote.splash.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.component.CircularProgress
import eu.alsk.transmissionremote.designsystem.component.Logo
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1500L

@Composable
internal fun SplashScreen(onFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        currentOnFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Logo()
        Spacer(Modifier.height(Spacing.s24))
        Text(
            "Transmission Remote",
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.onSurface,
        )
        Spacer(Modifier.height(Spacing.s32))
        CircularProgress()
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
