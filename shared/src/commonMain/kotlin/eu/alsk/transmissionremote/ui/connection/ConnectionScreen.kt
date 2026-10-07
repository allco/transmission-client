package eu.alsk.transmissionremote.ui.connection

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.alsk.transmissionremote.ui.theme.AppTheme

@Composable
internal fun ConnectionScreen(viewModel: ConnectionViewModel = viewModel { ConnectionViewModel() }) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar("Connection saved")
            viewModel.onSavedMessageShown()
        }
    }

    ConnectionContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNameChange = viewModel::onNameChange,
        onHostChange = viewModel::onHostChange,
        onPortChange = viewModel::onPortChange,
        onRpcPathChange = viewModel::onRpcPathChange,
        onUseHttpsChange = viewModel::onUseHttpsChange,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSave = viewModel::save,
    )
}

@Preview(name = "Connection – light", showBackground = true)
@Composable
private fun ConnectionScreenPreview() {
    AppTheme(darkTheme = false) { ConnectionScreen(viewModel = remember { ConnectionViewModel() }) }
}

@Preview(name = "Connection – dark", showBackground = true)
@Composable
private fun ConnectionScreenDarkPreview() {
    AppTheme(darkTheme = true) { ConnectionScreen(viewModel = remember { ConnectionViewModel() }) }
}
