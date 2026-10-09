package eu.alsk.transmissionremote.rpc

/**
 * Holds the address of the RPC endpoint of one Transmission server, for example
 * `http://192.168.1.10:9091/transmission/rpc`, and the credentials for it.
 *
 * Set [credentials] to null when the server does not ask for a password.
 */
public data class ServerEndpoint(
    val url: String,
    val credentials: Credentials? = null,
)
