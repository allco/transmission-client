package eu.alsk.transmissionremote.rpc

import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode
import kotlin.concurrent.Volatile

internal const val SESSION_ID_HEADER = "X-Transmission-Session-Id"
internal const val RPC_VERSION_HEADER = "X-Transmission-Rpc-Version"

/** Holds the session id and the RPC version that the server sent in its last HTTP 409 response. */
internal class SessionState {
    @Volatile
    var sessionId: String? = null

    @Volatile
    var rpcVersion: String? = null
}

/**
 * Adds the session id to each request. When the server replies with HTTP 409, the plugin stores the
 * new session id and sends the request again, one time only. A second 409 goes to the caller.
 */
internal fun sessionPlugin(state: SessionState) = createClientPlugin("TransmissionSession") {
    on(Send) { request ->
        state.sessionId?.let { request.headers[SESSION_ID_HEADER] = it }
        val call = proceed(request)
        if (call.response.status != HttpStatusCode.Conflict) return@on call

        val sessionId = call.response.headers[SESSION_ID_HEADER] ?: return@on call
        state.sessionId = sessionId
        // Only Transmission 4.1 and later send this header. See section 1 of the RPC reference.
        call.response.headers[RPC_VERSION_HEADER]?.let { state.rpcVersion = it }
        request.headers[SESSION_ID_HEADER] = sessionId
        proceed(request)
    }
}
