package eu.alsk.transmissionremote.rpc

/** Holds a failure of a request to the Transmission server. */
public sealed class RpcException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /** The server refused the user name or the password (HTTP 401). */
    public class Unauthorized : RpcException("The server refused the user name or the password (HTTP 401)")

    /** The server does not accept requests from this IP address, see `rpc-whitelist` (HTTP 403). */
    public class Forbidden : RpcException("The server does not accept requests from this address (HTTP 403)")

    /**
     * The server refused the session id two times (HTTP 409). A proxy can cause this when it removes
     * the `X-Transmission-Session-Id` header.
     */
    public class SessionRejected : RpcException("The server refused the session id (HTTP 409)")

    /** The server sent an HTTP status that this client does not expect. [body] holds the start of the reply. */
    public class Http(public val status: Int, public val body: String) :
        RpcException("The server sent HTTP $status")

    /**
     * The server processed the request and sent an RPC error. [code] is the JSON-RPC error code. It is
     * null for the legacy format. [details] holds the `error_string` of the server, if there is one.
     */
    public class ServerError(public val code: Int?, message: String, public val details: String? = null) :
        RpcException(message)

    /** The reply of the server is not a valid RPC response. */
    public class InvalidResponse(message: String, cause: Throwable? = null) : RpcException(message, cause)

    /** The client could not reach the server, for example because of a timeout or no route to the host. */
    public class Network(cause: Throwable) : RpcException(cause.message ?: "The client could not reach the server", cause)
}
