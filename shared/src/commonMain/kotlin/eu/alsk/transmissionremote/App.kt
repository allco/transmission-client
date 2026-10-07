package eu.alsk.transmissionremote

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.connection.views.ConnectionScreen
import eu.alsk.transmissionremote.splash.views.SplashScreen
import eu.alsk.transmissionremote.theme.AppTheme

private enum class Screen { Splash, Connection }

@Composable
fun App() {
    AppTheme {
        var screen by rememberSaveable { mutableStateOf(Screen.Splash) }
        Crossfade(targetState = screen) { current ->
            when (current) {
                Screen.Splash -> SplashScreen(onFinished = { screen = Screen.Connection })
                Screen.Connection -> ConnectionScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    App()
}
