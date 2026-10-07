package eu.alsk.transmissionremote.connection

import eu.alsk.transmissionremote.data.ServerConnection

/** The connection form as the user is editing it, with its validation rules. */
internal data class ConnectionFormState(
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
