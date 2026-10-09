package eu.alsk.transmissionremote.connection.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.alsk.transmissionremote.connection.ConnectionContentState
import eu.alsk.transmissionremote.connection.ConnectionEvent
import eu.alsk.transmissionremote.connection.ConnectionViewModel
import eu.alsk.transmissionremote.designsystem.component.Button
import eu.alsk.transmissionremote.designsystem.component.ListItem
import eu.alsk.transmissionremote.designsystem.component.ListItemTrailing
import eu.alsk.transmissionremote.designsystem.component.PasswordField
import eu.alsk.transmissionremote.designsystem.component.SectionHeader
import eu.alsk.transmissionremote.designsystem.component.SnackbarHost
import eu.alsk.transmissionremote.designsystem.component.TextField
import eu.alsk.transmissionremote.designsystem.component.TopAppBar
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

@Composable
internal fun ConnectionScreen(viewModel: ConnectionViewModel = viewModel { ConnectionViewModel() }) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ConnectionEvent.Saved -> snackbarHostState.showSnackbar("Connection saved")
            }
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
    val next = KeyboardOptions(imeAction = ImeAction.Next)
    Scaffold(
        topBar = { TopAppBar(title = "Add server") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { SaveBar(onSave = onSave) },
        containerColor = AppTheme.colors.surface,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .padding(Spacing.s16),
                verticalArrangement = Arrangement.spacedBy(Spacing.s12),
            ) {
                TextField(
                    label = "Name (optional)",
                    value = state.name,
                    onValueChange = onNameChange,
                    placeholder = "Home server",
                    keyboardOptions = next,
                )
                TextField(
                    label = "Host",
                    value = state.host,
                    onValueChange = onHostChange,
                    placeholder = "192.168.1.10 or nas.local",
                    error = state.hostError,
                    keyboardOptions = next.copy(keyboardType = KeyboardType.Uri),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s12)) {
                    TextField(
                        label = "Port",
                        value = state.port,
                        onValueChange = onPortChange,
                        modifier = Modifier.weight(1f),
                        error = state.portError,
                        keyboardOptions = next.copy(keyboardType = KeyboardType.Number),
                    )
                    TextField(
                        label = "RPC path",
                        value = state.rpcPath,
                        onValueChange = onRpcPathChange,
                        modifier = Modifier.weight(2f),
                        error = state.rpcPathError,
                        keyboardOptions = next.copy(keyboardType = KeyboardType.Uri),
                    )
                }
            }
            ListItem(
                headline = "Use HTTPS",
                modifier = Modifier.widthIn(max = 560.dp),
                leadingIcon = AppIcons.Lock,
                trailing = ListItemTrailing.Switch(checked = state.useHttps, onCheckedChange = onUseHttpsChange),
            )
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
            ) {
                SectionHeader("Authentication")
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.s16),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s12),
                ) {
                    TextField(
                        label = "Username (optional)",
                        value = state.username,
                        onValueChange = onUsernameChange,
                        keyboardOptions = next,
                    )
                    PasswordField(
                        label = "Password (optional)",
                        value = state.password,
                        onValueChange = onPasswordChange,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = { onSave() }),
                    )
                    if (state.rpcUrl != null) {
                        Text(
                            state.rpcUrl,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// The save action stays visible above the keyboard, so the user does not scroll to find it.
@Composable
private fun SaveBar(onSave: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface)
            .navigationBarsPadding()
            .imePadding()
            .padding(Spacing.s16),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            text = "Save",
            onClick = onSave,
            modifier = Modifier
                .widthIn(max = 528.dp)
                .fillMaxWidth(),
        )
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
