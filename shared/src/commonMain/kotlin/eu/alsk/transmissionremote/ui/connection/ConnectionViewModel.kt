package eu.alsk.transmissionremote.ui.connection

import androidx.lifecycle.ViewModel
import eu.alsk.transmissionremote.data.ConnectionRepository
import eu.alsk.transmissionremote.data.ServerConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConnectionFormState(
    val name: String = "",
    val host: String = "",
    val port: String = ServerConnection.DEFAULT_PORT.toString(),
    val rpcPath: String = ServerConnection.DEFAULT_RPC_PATH,
    val useHttps: Boolean = false,
    val username: String = "",
    val password: String = "",
    /** Validation errors are only shown after the first save attempt. */
    val showErrors: Boolean = false,
    val saved: Boolean = false,
) {
    val hostError: String?
        get() = when {
            host.isBlank() -> "Host is required"
            host.contains("://") -> "Enter the host without http:// or https://"
            host.any { it.isWhitespace() || it == '/' } -> "Host must not contain spaces or slashes"
            else -> null
        }

    val portError: String?
        get() = if (port.toIntOrNull() in 1..65535) null else "Port must be between 1 and 65535"

    val rpcPathError: String?
        get() = if (rpcPath.startsWith("/")) null else "Path must start with /"

    val isValid: Boolean
        get() = hostError == null && portError == null && rpcPathError == null

    fun toConnection() = ServerConnection(
        name = name.trim().ifEmpty { host.trim() },
        host = host.trim(),
        port = port.toInt(),
        rpcPath = rpcPath.trim(),
        useHttps = useHttps,
        username = username,
        password = password,
    )
}

class ConnectionViewModel(
    private val repository: ConnectionRepository = ConnectionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(
        repository.connection.value?.let { saved ->
            ConnectionFormState(
                name = saved.name,
                host = saved.host,
                port = saved.port.toString(),
                rpcPath = saved.rpcPath,
                useHttps = saved.useHttps,
                username = saved.username,
                password = saved.password,
            )
        } ?: ConnectionFormState()
    )
    val state: StateFlow<ConnectionFormState> = _state.asStateFlow()

    fun onNameChange(value: String) = edit { copy(name = value) }
    fun onHostChange(value: String) = edit { copy(host = value) }
    fun onPortChange(value: String) = edit { copy(port = value.filter(Char::isDigit).take(5)) }
    fun onRpcPathChange(value: String) = edit { copy(rpcPath = value) }
    fun onUseHttpsChange(value: Boolean) = edit { copy(useHttps = value) }
    fun onUsernameChange(value: String) = edit { copy(username = value) }
    fun onPasswordChange(value: String) = edit { copy(password = value) }

    fun save() {
        val current = _state.value
        if (!current.isValid) {
            _state.update { it.copy(showErrors = true) }
            return
        }
        repository.save(current.toConnection())
        _state.update { it.copy(showErrors = true, saved = true) }
    }

    fun onSavedMessageShown() = _state.update { it.copy(saved = false) }

    private fun edit(transform: ConnectionFormState.() -> ConnectionFormState) =
        _state.update { it.transform() }
}
