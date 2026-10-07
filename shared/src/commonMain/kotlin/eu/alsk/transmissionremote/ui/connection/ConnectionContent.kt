package eu.alsk.transmissionremote.ui.connection

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConnectionContent(
    state: ConnectionFormState,
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

                FormField("Name (optional)", state.name, onNameChange, placeholder = "Home server")
                FormField(
                    label = "Host",
                    value = state.host,
                    onValueChange = onHostChange,
                    placeholder = "192.168.1.10 or nas.local",
                    error = state.hostError.takeIf { state.showErrors },
                    keyboardType = KeyboardType.Uri,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField(
                        label = "Port",
                        value = state.port,
                        onValueChange = onPortChange,
                        error = state.portError.takeIf { state.showErrors },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                    FormField(
                        label = "RPC path",
                        value = state.rpcPath,
                        onValueChange = onRpcPathChange,
                        error = state.rpcPathError.takeIf { state.showErrors },
                        keyboardType = KeyboardType.Uri,
                        modifier = Modifier.weight(2f),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Use HTTPS", modifier = Modifier.weight(1f))
                    Switch(checked = state.useHttps, onCheckedChange = onUseHttpsChange)
                }

                HorizontalDivider()
                Text("Authentication", style = MaterialTheme.typography.titleSmall)
                FormField("Username (optional)", state.username, onUsernameChange)
                PasswordField(state.password, onPasswordChange, onDone = onSave)

                if (state.isValid) {
                    Text(
                        state.toConnection().rpcUrl,
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

private val dummyConnectionFormState = ConnectionFormState(
    name = "Home server",
    host = "192.168.1.10",
    useHttps = true,
    username = "admin",
    password = "secret",
)

private val dummyConnectionFormStateWithErrors = ConnectionFormState(
    host = "http://nas.local",
    port = "99999",
    rpcPath = "rpc",
    showErrors = true,
)

@Preview(name = "Connection content – filled in", showBackground = true)
@Composable
private fun ConnectionContentFilledPreview() {
    ConnectionContentPreview(dummyConnectionFormState)
}

@Preview(name = "Connection content – filled in, dark", showBackground = true)
@Composable
private fun ConnectionContentFilledDarkPreview() {
    ConnectionContentPreview(dummyConnectionFormState, darkTheme = true)
}

@Preview(name = "Connection content – validation errors", showBackground = true)
@Composable
private fun ConnectionContentErrorsPreview() {
    ConnectionContentPreview(dummyConnectionFormStateWithErrors)
}

@Composable
private fun ConnectionContentPreview(state: ConnectionFormState, darkTheme: Boolean = false) {
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
