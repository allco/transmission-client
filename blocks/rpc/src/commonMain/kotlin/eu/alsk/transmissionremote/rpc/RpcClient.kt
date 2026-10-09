package eu.alsk.transmissionremote.rpc

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonArray

/**
 * Sends RPC requests to one Transmission server.
 *
 * The client handles the transport rules of section 2 of `docs/reference/transmission-rpc-api.md`:
 * - It sends the credentials of [endpoint] with HTTP Basic authentication on each request.
 * - It adds the session id to each request. On HTTP 409 it stores the new session id and sends the
 *   request again, one time.
 * - It finds the [RpcFormat] of the server on the first request, and uses it for all requests.
 *
 * The client does not know the RPC methods. The caller gives the method name and the params in
 * the format that [format] returns. Call [close] when the client is no longer necessary.
 */
public class RpcClient internal constructor(
    public val endpoint: ServerEndpoint,
    engine: HttpClientEngine?,
) : AutoCloseable {

    /** Makes a client for [endpoint] with the HTTP engine of the platform. */
    public constructor(endpoint: ServerEndpoint) : this(endpoint, engine = null)

    private val session = SessionState()
    private val formatLock = Mutex()
    private var detectedFormat: RpcFormat? = null

    // The id only connects a request to its response in logs. Duplicate ids do no harm, because
    // each HTTP request gets its own response.
    private var nextId = 1L

    private val http: HttpClient = if (engine == null) HttpClient { configure() } else HttpClient(engine) { configure() }

    private fun HttpClientConfig<*>.configure() {
        expectSuccess = false
        install(sessionPlugin(session))
        endpoint.credentials?.let { credentials ->
            install(Auth) {
                basic {
                    credentials { BasicAuthCredentials(credentials.username, credentials.password) }
                    // Send the credentials on the first request. Do not wait for HTTP 401.
                    sendWithoutRequest { true }
                }
            }
        }
    }

    /**
     * Returns the wire format of the server. The first call sends a `session-get` request to find
     * the format. The next calls return the stored value.
     */
    public suspend fun format(): RpcFormat = formatLock.withLock {
        detectedFormat ?: detectFormat().also { detectedFormat = it }
    }

    /**
     * Sends the RPC [method] with [params] and returns the `result` (JSON-RPC 2.0) or the
     * `arguments` (legacy) of the response. Use the method name and the param names of [format].
     *
     * @throws RpcException when the request fails or the server sends an RPC error.
     */
    public suspend fun call(method: String, params: JsonObject = JsonObject(emptyMap())): JsonObject {
        val format = format()
        return RpcEnvelope.decode(format, send(format, method, params))
    }

    /** Releases the HTTP client and its connections. */
    override fun close() {
        http.close()
    }

    private suspend fun detectFormat(): RpcFormat {
        // Each server version accepts the legacy format, so the first request uses it. The HTTP 409
        // of the first request tells if the server also speaks JSON-RPC 2.0.
        val params = buildJsonObject { putJsonArray("fields") { add("rpc-version-semver") } }
        val text = send(RpcFormat.Legacy, "session-get", params)
        if (session.rpcVersion != null) return RpcFormat.JsonRpc2

        // A server without the session id check sends no HTTP 409. Then read the version from the body.
        val version = RpcEnvelope.decode(RpcFormat.Legacy, text)["rpc-version-semver"]?.jsonPrimitive?.contentOrNull
        val major = version?.substringBefore('.')?.toIntOrNull() ?: 0
        return if (major >= JSON_RPC_2_MAJOR_VERSION) RpcFormat.JsonRpc2 else RpcFormat.Legacy
    }

    private suspend fun send(format: RpcFormat, method: String, params: JsonObject): String {
        val body = RpcEnvelope.encode(format, method, params, nextId++)
        val response: HttpResponse = try {
            http.post(endpoint.url) { setBody(TextContent(body, ContentType.Application.Json)) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: RpcException) {
            throw e
        } catch (e: Exception) {
            throw RpcException.Network(e)
        }
        val text = response.bodyAsText()
        when (response.status) {
            HttpStatusCode.OK -> return text
            HttpStatusCode.Unauthorized -> throw RpcException.Unauthorized()
            HttpStatusCode.Forbidden -> throw RpcException.Forbidden()
            HttpStatusCode.Conflict -> throw RpcException.SessionRejected()
            else -> throw RpcException.Http(response.status.value, text.take(ERROR_BODY_LENGTH))
        }
    }

    private companion object {
        /** Transmission 4.1 has the RPC version 6.0.0, the first version with JSON-RPC 2.0. */
        const val JSON_RPC_2_MAJOR_VERSION = 6
        const val ERROR_BODY_LENGTH = 200
    }
}
