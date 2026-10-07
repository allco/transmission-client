package eu.alsk.transmissionremote.connection.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.alsk.transmissionremote.connection.ConnectionFormState
import eu.alsk.transmissionremote.connection.ConnectionViewModel
import eu.alsk.transmissionremote.theme.AppTheme

/** What [ConnectionContent] shows: field values, the errors to display, and the URL preview. */
private data class ConnectionContentState(
    val name: String,
    val host: String,
    val hostError: String?,
    val port: String,
    val portError: String?,
    val rpcPath: String,
    val rpcPathError: String?,
    val useHttps: Boolean,
    val username: String,
    val password: String,
    /** Full RPC URL, or null while the form is invalid. */
    val rpcUrl: String?,
)

private fun ConnectionFormState.toContentState() = ConnectionContentState(
    name = name,
    host = host,
    hostError = hostError.takeIf { showErrors },
    port = port,
    portError = portError.takeIf { showErrors },
    rpcPath = rpcPath,
    rpcPathError = rpcPathError.takeIf { showErrors },
    useHttps = useHttps,
    username = username,
    password = password,
    rpcUrl = if (isValid) toConnection().rpcUrl else null,
)

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
        state = state.toContentState(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectionContent(
    state: ConnectionContentState,
    snackbarHostState: SnackbarHostState,
    onNameChange: (String) -> Unit,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onRpcPathChange: (String) -> Unit,
    onUseHttpsChange: (Boolean) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Server connection") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "Enter the details of the Transmission daemon you want to control.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                FormField(
                    state = FormFieldState(
                        label = "Name (optional)",
                        value = state.name,
                        placeholder = "Home server",
                    ),
                    onValueChange = onNameChange,
                )
                FormField(
                    state = FormFieldState(
                        label = "Host",
                        value = state.host,
                        placeholder = "192.168.1.10 or nas.local",
                        error = state.hostError,
                        keyboardType = KeyboardType.Uri,
                    ),
                    onValueChange = onHostChange,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField(
                        state = FormFieldState(
                            label = "Port",
                            value = state.port,
                            error = state.portError,
                            keyboardType = KeyboardType.Number,
                        ),
                        onValueChange = onPortChange,
                        modifier = Modifier.weight(1f),
                    )
                    FormField(
                        state = FormFieldState(
                            label = "RPC path",
                            value = state.rpcPath,
                            error = state.rpcPathError,
                            keyboardType = KeyboardType.Uri,
                        ),
                        onValueChange = onRpcPathChange,
                        modifier = Modifier.weight(2f),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Use HTTPS", modifier = Modifier.weight(1f))
                    Switch(checked = state.useHttps, onCheckedChange = onUseHttpsChange)
                }

                HorizontalDivider()
                Text("Authentication", style = MaterialTheme.typography.titleSmall)
                FormField(
                    state = FormFieldState(label = "Username (optional)", value = state.username),
                    onValueChange = onUsernameChange,
                )
                PasswordField(state.password, onPasswordChange, onDone = onSave)

                if (state.rpcUrl != null) {
                    Text(
                        state.rpcUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.padding(4.dp))
                Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                    Text("Save connection")
                }
            }
        }
    }
}

private val dummyConnectionContentState = ConnectionContentState(
    name = "Home server",
    host = "192.168.1.10",
    hostError = null,
    port = "9091",
    portError = null,
    rpcPath = "/transmission/rpc",
    rpcPathError = null,
    useHttps = true,
    username = "admin",
    password = "secret",
    rpcUrl = "https://192.168.1.10:9091/transmission/rpc",
)

private val dummyConnectionContentStateWithErrors = ConnectionContentState(
    name = "",
    host = "http://nas.local",
    hostError = "Enter the host without http:// or https://",
    port = "99999",
    portError = "Port must be between 1 and 65535",
    rpcPath = "rpc",
    rpcPathError = "Path must start with /",
    useHttps = false,
    username = "",
    password = "",
    rpcUrl = null,
)

@Preview(name = "Connection content – filled in", showBackground = true)
@Composable
private fun ConnectionContentFilledPreview() {
    ConnectionContentPreview(dummyConnectionContentState)
}

@Preview(name = "Connection content – filled in, dark", showBackground = true)
@Composable
private fun ConnectionContentFilledDarkPreview() {
    ConnectionContentPreview(dummyConnectionContentState, darkTheme = true)
}

@Preview(name = "Connection content – validation errors", showBackground = true)
@Composable
private fun ConnectionContentErrorsPreview() {
    ConnectionContentPreview(dummyConnectionContentStateWithErrors)
}

@Composable
private fun ConnectionContentPreview(state: ConnectionContentState, darkTheme: Boolean = false) {
    AppTheme(darkTheme = darkTheme) {
        ConnectionContent(
            state = state,
            snackbarHostState = remember { SnackbarHostState() },
            onNameChange = {},
            onHostChange = {},
            onPortChange = {},
            onRpcPathChange = {},
            onUseHttpsChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onSave = {},
        )
    }
}
