package eu.alsk.transmissionremote.connection

/**
 * Holds the data that the connection screen shows: the field values, the errors to show and the
 * URL preview. [ConnectionViewModel] makes this state.
 */
internal data class ConnectionContentState(
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
    /** The full RPC URL. The value is null while the form is invalid. */
    val rpcUrl: String?,
)
