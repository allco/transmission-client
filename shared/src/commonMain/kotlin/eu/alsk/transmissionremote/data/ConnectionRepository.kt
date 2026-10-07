package eu.alsk.transmissionremote.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the saved server connection. For now, it keeps the connection in memory only.
 * When the app exits, the app loses the connection.
 */
internal object ConnectionRepository {
    private val _connection = MutableStateFlow<ServerConnection?>(null)
    val connection: StateFlow<ServerConnection?> = _connection.asStateFlow()

    fun save(connection: ServerConnection) {
        _connection.value = connection
    }
}
