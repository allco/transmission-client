package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.SnackbarHost as M3SnackbarHost

/**
 * Shows the snackbars of [hostState] in the inverse colours of the theme. Call
 * `hostState.showSnackbar(…)` to show a message.
 */
@Composable
public fun SnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    M3SnackbarHost(hostState = hostState, modifier = modifier) { data -> AppSnackbar(data) }
}

/** Shows one snackbar with an optional action in the inversePrimary colour. */
@Composable
private fun AppSnackbar(data: SnackbarData) {
    Snackbar(
        snackbarData = data,
        modifier = Modifier.padding(Spacing.s12),
        shape = RoundedCornerShape(Radius.xs),
        containerColor = AppTheme.colors.inverseSurface,
        contentColor = AppTheme.colors.inverseOnSurface,
        actionColor = AppTheme.colors.inversePrimary,
        actionContentColor = AppTheme.colors.inversePrimary,
        dismissActionContentColor = AppTheme.colors.inverseOnSurface,
    )
}

private val dummySnackbarData: SnackbarData = object : SnackbarData {
    override val visuals: SnackbarVisuals = object : SnackbarVisuals {
        override val message: String = "Torrent removed"
        override val actionLabel: String = "Undo"
        override val withDismissAction: Boolean = false
        override val duration: SnackbarDuration = SnackbarDuration.Short
    }

    override fun dismiss() = Unit

    override fun performAction() = Unit
}

@Preview
@Composable
private fun SnackbarLightPreview() {
    AppTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.surface) { AppSnackbar(dummySnackbarData) }
    }
}

@Preview
@Composable
private fun SnackbarDarkPreview() {
    AppTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.surface) { AppSnackbar(dummySnackbarData) }
    }
}
