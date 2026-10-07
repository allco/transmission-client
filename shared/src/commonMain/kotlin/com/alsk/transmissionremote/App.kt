package com.alsk.transmissionremote

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.alsk.transmissionremote.ui.connection.ConnectionScreen
import com.alsk.transmissionremote.ui.splash.SplashScreen
import com.alsk.transmissionremote.ui.theme.AppTheme

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
