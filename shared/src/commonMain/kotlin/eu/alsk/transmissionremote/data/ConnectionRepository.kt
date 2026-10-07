package eu.alsk.transmissionremote.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Holds the saved server connection. In-memory only for now: it is lost when the app exits. */
object ConnectionRepository {
    private val _connection = MutableStateFlow<ServerConnection?>(null)
    val connection: StateFlow<ServerConnection?> = _connection.asStateFlow()

    fun save(connection: ServerConnection) {
        _connection.value = connection
    }
}
