package eu.alsk.transmissionremote.rpc

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RpcClientTest {

    private val endpoint = ServerEndpoint(url = "http://server:9091/transmission/rpc")

    /** Holds the requests that the mock server received, with their bodies. */
    private class Received(val request: HttpRequestData, val body: JsonObject)

    /**
     * Makes a mock server. It replies with HTTP 409 when a request has no valid session id, as
     * Transmission does. [rpcVersion] is the value of the `X-Transmission-Rpc-Version` header, or null
     * for a legacy server. [reply] makes the HTTP 200 reply.
     */
    private fun server(
        rpcVersion: String? = "6.0.0",
        validSessionId: String = "session-1",
        received: MutableList<Received>,
        reply: suspend MockRequestHandleScope.(Received) -> HttpResponseData,
    ) = MockEngine { request ->
        val body = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
        val item = Received(request, body)
        received += item
        if (request.headers[SESSION_ID_HEADER] != validSessionId) {
            val headers = buildList {
                add(SESSION_ID_HEADER to listOf(validSessionId))
                if (rpcVersion != null) add(RPC_VERSION_HEADER to listOf(rpcVersion))
            }
            respond("", HttpStatusCode.Conflict, headersOf(*headers.toTypedArray()))
        } else {
            reply(item)
        }
    }

    private fun MockRequestHandleScope.json(text: String, status: HttpStatusCode = HttpStatusCode.OK) =
        respond(text, status, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun MockRequestHandleScope.reply(item: Received): HttpResponseData = if (item.body.containsKey("jsonrpc")) {
        json("""{"jsonrpc":"2.0","result":{"version":"4.1.3"},"id":${item.body["id"]}}""")
    } else {
        json("""{"result":"success","arguments":{"rpc-version-semver":"5.3.0"},"tag":${item.body["tag"]}}""")
    }

    @Test
    fun retriesOnceAfter409AndKeepsTheSessionId() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(received = received) { reply(it) })

        client.call("session_get")

        // Request 1: the format probe without a session id (409). Request 2: the probe again.
        // Request 3: session_get with the stored session id, no 409.
        assertEquals(3, received.size)
        assertNull(received[0].request.headers[SESSION_ID_HEADER])
        assertEquals("session-1", received[1].request.headers[SESSION_ID_HEADER])
        assertEquals("session-1", received[2].request.headers[SESSION_ID_HEADER])
    }

    @Test
    fun throwsWhenTheServerRefusesTheNewSessionIdToo() = runTest {
        val received = mutableListOf<Received>()
        // The server changes the session id on each request, so the retry also gets 409.
        var counter = 0
        val engine = MockEngine { request ->
            received += Received(request, JsonObject(emptyMap()))
            counter++
            respond("", HttpStatusCode.Conflict, headersOf(SESSION_ID_HEADER, "session-$counter"))
        }
        val client = RpcClient(endpoint, engine)

        assertFailsWith<RpcException.SessionRejected> { client.call("session_get") }
        assertEquals(2, received.size)
    }

    @Test
    fun selectsJsonRpc2WhenThe409HasTheRpcVersionHeader() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(rpcVersion = "6.0.1", received = received) { reply(it) })

        assertEquals(RpcFormat.JsonRpc2, client.format())
    }

    @Test
    fun selectsLegacyWhenThe409HasNoRpcVersionHeader() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(rpcVersion = null, received = received) { reply(it) })

        assertEquals(RpcFormat.Legacy, client.format())
    }

    @Test
    fun detectsTheFormatOnlyOnce() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(received = received) { reply(it) })

        client.format()
        client.format()

        assertEquals(2, received.size)
    }

    @Test
    fun buildsAJsonRpc2Request() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(received = received) { reply(it) })
        val params = buildJsonObject { put("ids", 7) }

        val result = client.call("torrent_stop", params)

        val body = received.last().body
        assertEquals("2.0", body["jsonrpc"]?.jsonPrimitive?.content)
        assertEquals("torrent_stop", body["method"]?.jsonPrimitive?.content)
        assertEquals(7, body["params"]?.jsonObject?.get("ids")?.jsonPrimitive?.int)
        assertTrue(body.containsKey("id"))
        assertEquals("4.1.3", result["version"]?.jsonPrimitive?.content)
    }

    @Test
    fun buildsALegacyRequest() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(rpcVersion = null, received = received) { reply(it) })
        val params = buildJsonObject { put("ids", 7) }

        val result = client.call("torrent-stop", params)

        val body = received.last().body
        assertEquals("torrent-stop", body["method"]?.jsonPrimitive?.content)
        assertEquals(7, body["arguments"]?.jsonObject?.get("ids")?.jsonPrimitive?.int)
        assertTrue(body.containsKey("tag"))
        assertEquals("5.3.0", result["rpc-version-semver"]?.jsonPrimitive?.content)
    }

    @Test
    fun sendsTheCredentialsWithBasicAuthentication() = runTest {
        val received = mutableListOf<Received>()
        val withCredentials = endpoint.copy(credentials = Credentials("admin", "secret"))
        val client = RpcClient(withCredentials, server(received = received) { reply(it) })

        client.call("session_get")

        // "admin:secret" in base64.
        assertTrue(received.all { it.request.headers[HttpHeaders.Authorization] == "Basic YWRtaW46c2VjcmV0" })
    }

    @Test
    fun throwsUnauthorizedOn401() = runTest {
        val engine = MockEngine { respond("", HttpStatusCode.Unauthorized) }
        val client = RpcClient(endpoint, engine)

        assertFailsWith<RpcException.Unauthorized> { client.call("session_get") }
    }

    @Test
    fun throwsForbiddenOn403() = runTest {
        val engine = MockEngine { respond("", HttpStatusCode.Forbidden) }
        val client = RpcClient(endpoint, engine)

        assertFailsWith<RpcException.Forbidden> { client.call("session_get") }
    }

    @Test
    fun throwsTheJsonRpc2ErrorOfTheServer() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(received = received) { item ->
            if (item.body["method"]?.jsonPrimitive?.content == "port_test") {
                json(
                    """{"jsonrpc":"2.0","error":{"code":7,"message":"HTTP error from backend service",""" +
                        """"data":{"error_string":"Couldn't test port"}},"id":1}""",
                )
            } else {
                reply(item)
            }
        })

        val error = assertFailsWith<RpcException.ServerError> { client.call("port_test") }
        assertEquals(7, error.code)
        assertEquals("HTTP error from backend service", error.message)
        assertEquals("Couldn't test port", error.details)
    }

    @Test
    fun throwsTheLegacyErrorOfTheServer() = runTest {
        val received = mutableListOf<Received>()
        val client = RpcClient(endpoint, server(rpcVersion = null, received = received) { item ->
            if (item.body["method"]?.jsonPrimitive?.content == "torrent-add") {
                json("""{"result":"invalid or corrupt torrent file","tag":1}""")
            } else {
                reply(item)
            }
        })

        val error = assertFailsWith<RpcException.ServerError> { client.call("torrent-add") }
        assertNull(error.code)
        assertEquals("invalid or corrupt torrent file", error.message)
    }

    @Test
    fun hidesThePasswordInToString() {
        assertEquals("Credentials(username=admin, password=***)", Credentials("admin", "secret").toString())
    }
}
