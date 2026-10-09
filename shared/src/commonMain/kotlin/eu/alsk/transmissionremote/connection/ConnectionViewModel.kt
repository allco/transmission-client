package eu.alsk.transmissionremote.connection

import androidx.lifecycle.ViewModel
import eu.alsk.transmissionremote.connection.data.ConnectionRepository
import eu.alsk.transmissionremote.connection.data.ServerConnection
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

internal class ConnectionViewModel(
    private val repository: ConnectionRepository = ConnectionRepository,
) : ViewModel() {
    private var input: Input = repository.connection.value?.let(::Input) ?: Input()

    /** The screen shows validation errors only after the user first tries to save the form. */
    private var showErrors = false

    private val _state = MutableStateFlow(contentState())
    val state: StateFlow<ConnectionContentState> = _state.asStateFlow()

    private val _events = Channel<ConnectionEvent>(Channel.BUFFERED)
    val events: Flow<ConnectionEvent> = _events.receiveAsFlow()

    fun onNameChange(value: String) = edit { copy(name = value) }
    fun onHostChange(value: String) = edit { copy(host = value) }
    fun onPortChange(value: String) = edit { copy(port = value.filter(Char::isDigit).take(5)) }
    fun onRpcPathChange(value: String) = edit { copy(rpcPath = value) }
    fun onUseHttpsChange(value: Boolean) = edit { copy(useHttps = value) }
    fun onUsernameChange(value: String) = edit { copy(username = value) }
    fun onPasswordChange(value: String) = edit { copy(password = value) }

    fun save() {
        showErrors = true
        if (input.isValid) {
            repository.save(input.toConnection())
            _events.trySend(ConnectionEvent.Saved)
        }
        _state.value = contentState()
    }

    private fun edit(transform: Input.() -> Input) {
        input = input.transform()
        _state.value = contentState()
    }

    private fun contentState() = ConnectionContentState(
        name = input.name,
        host = input.host,
        hostError = input.hostError.takeIf { showErrors },
        port = input.port,
        portError = input.portError.takeIf { showErrors },
        rpcPath = input.rpcPath,
        rpcPathError = input.rpcPathError.takeIf { showErrors },
        useHttps = input.useHttps,
        username = input.username,
        password = input.password,
        rpcUrl = if (input.isValid) input.toConnection().rpcUrl else null,
    )

    /** Holds the text that the user typed into the form, and the validation rules for it. */
    private data class Input(
        val name: String = "",
        val host: String = "",
        val port: String = ServerConnection.DEFAULT_PORT.toString(),
        val rpcPath: String = ServerConnection.DEFAULT_RPC_PATH,
        val useHttps: Boolean = false,
        val username: String = "",
        val password: String = "",
    ) {
        constructor(saved: ServerConnection) : this(
            name = saved.name,
            host = saved.host,
            port = saved.port.toString(),
            rpcPath = saved.rpcPath,
            useHttps = saved.useHttps,
            username = saved.username,
            password = saved.password,
        )

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
}
