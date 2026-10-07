package eu.alsk.transmissionremote.connection

import androidx.lifecycle.ViewModel
import eu.alsk.transmissionremote.data.ConnectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class ConnectionViewModel(
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
