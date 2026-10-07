package eu.alsk.transmissionremote

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Transmission Remote",
    ) {
        App()
    }
}
