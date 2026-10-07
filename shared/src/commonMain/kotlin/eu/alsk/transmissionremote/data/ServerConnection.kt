package eu.alsk.transmissionremote.data

/** Holds the connection details for the RPC endpoint of a remote Transmission daemon. */
internal data class ServerConnection(
    val name: String,
    val host: String,
    val port: Int,
    val rpcPath: String,
    val useHttps: Boolean,
    val username: String,
    val password: String,
) {
    val rpcUrl: String
        get() = "${if (useHttps) "https" else "http"}://$host:$port$rpcPath"

    companion object {
        const val DEFAULT_PORT = 9091
        const val DEFAULT_RPC_PATH = "/transmission/rpc"
    }
}
