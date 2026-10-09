package eu.alsk.transmissionremote.rpc.torrents

import eu.alsk.transmissionremote.rpc.RPC_VERSION_HEADER
import eu.alsk.transmissionremote.rpc.RpcClient
import eu.alsk.transmissionremote.rpc.RpcException
import eu.alsk.transmissionremote.rpc.SESSION_ID_HEADER
import eu.alsk.transmissionremote.rpc.ServerEndpoint
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TorrentsClientTest {

    private val endpoint = ServerEndpoint(url = "http://server:9091/transmission/rpc")

    /**
     * Makes a client with a mock server. The server sends HTTP 409 for a request without the session
     * id. A JSON-RPC 2.0 server ([jsonRpc2] true) also sends the RPC version header. For each
     * `torrent_get` or `torrent-get` request, the server stores the body in [requests] and replies
     * with [torrentGetResult].
     */
    private fun client(jsonRpc2: Boolean, requests: MutableList<JsonObject>, torrentGetResult: String) =
        RpcClient(
            endpoint,
            MockEngine { request ->
                val body = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
                val method = body["method"]?.jsonPrimitive?.content
                when {
                    request.headers[SESSION_ID_HEADER] != "s1" -> {
                        val headers = if (jsonRpc2) {
                            headersOf(SESSION_ID_HEADER to listOf("s1"), RPC_VERSION_HEADER to listOf("6.0.0"))
                        } else {
                            headersOf(SESSION_ID_HEADER, "s1")
                        }
                        respond("", HttpStatusCode.Conflict, headers)
                    }
                    method == "session-get" -> json("""{"result":"success","arguments":{},"tag":1}""")
                    else -> {
                        requests += body
                        json(
                            if (jsonRpc2) """{"jsonrpc":"2.0","result":$torrentGetResult,"id":1}"""
                            else """{"result":"success","arguments":$torrentGetResult,"tag":1}""",
                        )
                    }
                }
            },
        )

    private fun io.ktor.client.engine.mock.MockRequestHandleScope.json(text: String) =
        respond(text, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))

    private val jsonRpc2Torrents = """
        {"torrents":[
          {"id":1,"hash_string":"3b2455","name":"ubuntu-24.04.1-desktop-amd64.iso","status":4,"error":0,
           "error_string":"","percent_done":0.45,"size_when_done":5800000000,"left_until_done":3190000000,
           "rate_download":3200000,"rate_upload":220000,"eta":480,"upload_ratio":0.08,"queue_position":0,
           "added_date":1791400000,"labels":["iso","linux"],"metadata_percent_complete":1,"recheck_progress":0},
          {"id":2,"hash_string":"9f1c00","name":"debian-13.1.0-amd64-netinst.iso","status":6,"error":0,
           "error_string":"","percent_done":1,"size_when_done":754000000,"left_until_done":0,
           "rate_download":0,"rate_upload":640000,"eta":-1,"upload_ratio":-2,"queue_position":1,
           "added_date":1791300000,"labels":[],"metadata_percent_complete":1,"recheck_progress":0}
        ],"removed":[5,7]}
    """.trimIndent()

    private val legacyTorrents = """
        {"torrents":[
          {"id":3,"hashString":"aa11bb","name":"fedora-workstation-44-live.iso","status":0,"error":2,
           "errorString":"Tracker: connection refused","percentDone":0.3,"sizeWhenDone":2200000000,
           "leftUntilDone":1540000000,"rateDownload":0,"rateUpload":0,"eta":-2,"uploadRatio":-1,
           "queuePosition":2,"addedDate":1791200000,"metadataPercentComplete":1,"recheckProgress":0}
        ]}
    """.trimIndent()

    @Test
    fun requestsTheFieldsWithTheJsonRpc2Names() = runTest {
        val requests = mutableListOf<JsonObject>()
        TorrentsClient(client(jsonRpc2 = true, requests, jsonRpc2Torrents)).getTorrents()

        val request = requests.single()
        assertEquals("torrent_get", request["method"]?.jsonPrimitive?.content)
        val fields = (request["params"]?.jsonObject?.get("fields") as JsonArray).map { it.jsonPrimitive.content }
        assertTrue("hash_string" in fields && "percent_done" in fields && "rate_download" in fields)
        assertNull(request["params"]?.jsonObject?.get("ids"))
    }

    @Test
    fun requestsTheFieldsWithTheLegacyNames() = runTest {
        val requests = mutableListOf<JsonObject>()
        TorrentsClient(client(jsonRpc2 = false, requests, legacyTorrents)).getTorrents()

        val request = requests.single()
        assertEquals("torrent-get", request["method"]?.jsonPrimitive?.content)
        val fields = (request["arguments"]?.jsonObject?.get("fields") as JsonArray).map { it.jsonPrimitive.content }
        assertTrue("hashString" in fields && "percentDone" in fields && "rateDownload" in fields)
    }

    @Test
    fun readsTheTorrentsOfAJsonRpc2Server() = runTest {
        val torrents = TorrentsClient(client(jsonRpc2 = true, mutableListOf(), jsonRpc2Torrents)).getTorrents()

        assertEquals(2, torrents.size)
        val ubuntu = torrents[0]
        assertEquals(1, ubuntu.id)
        assertEquals("3b2455", ubuntu.hash)
        assertEquals("ubuntu-24.04.1-desktop-amd64.iso", ubuntu.name)
        assertEquals(TorrentStatus.Downloading, ubuntu.status)
        assertEquals(TorrentError.None, ubuntu.error)
        assertEquals(0.45, ubuntu.percentDone)
        assertEquals(5_800_000_000, ubuntu.sizeWhenDone)
        assertEquals(3_200_000, ubuntu.rateDownload)
        assertEquals(480, ubuntu.etaSeconds)
        assertEquals(0.08, ubuntu.uploadRatio)
        assertEquals(listOf("iso", "linux"), ubuntu.labels)

        val debian = torrents[1]
        assertEquals(TorrentStatus.Seeding, debian.status)
        assertNull(debian.etaSeconds, "eta -1 means: not available")
        assertEquals(Double.POSITIVE_INFINITY, debian.uploadRatio, "ratio -2 means: infinite")
    }

    @Test
    fun readsTheTorrentsOfALegacyServer() = runTest {
        val torrents = TorrentsClient(client(jsonRpc2 = false, mutableListOf(), legacyTorrents)).getTorrents()

        val fedora = torrents.single()
        assertEquals(3, fedora.id)
        assertEquals("aa11bb", fedora.hash)
        assertEquals(TorrentStatus.Stopped, fedora.status)
        assertEquals(TorrentError.TrackerError, fedora.error)
        assertEquals("Tracker: connection refused", fedora.errorString)
        assertEquals(0.3, fedora.percentDone)
        assertNull(fedora.etaSeconds, "eta -2 means: unknown")
        assertNull(fedora.uploadRatio, "ratio -1 means: not available")
        assertEquals(emptyList(), fedora.labels, "servers before 3.0 send no labels")
    }

    @Test
    fun requestsRecentlyActiveTorrentsAndReadsTheRemovedIds() = runTest {
        val requests = mutableListOf<JsonObject>()
        val changes = TorrentsClient(client(jsonRpc2 = true, requests, jsonRpc2Torrents)).getRecentlyActive()

        assertEquals("recently_active", requests.single()["params"]?.jsonObject?.get("ids")?.jsonPrimitive?.content)
        assertEquals(2, changes.changed.size)
        assertEquals(listOf(5, 7), changes.removedIds)
    }

    @Test
    fun usesTheLegacyNameForRecentlyActive() = runTest {
        val requests = mutableListOf<JsonObject>()
        val changes = TorrentsClient(client(jsonRpc2 = false, requests, legacyTorrents)).getRecentlyActive()

        assertEquals("recently-active", requests.single()["arguments"]?.jsonObject?.get("ids")?.jsonPrimitive?.content)
        assertEquals(emptyList(), changes.removedIds)
    }

    @Test
    fun mapsAnUnknownStatusToStopped() {
        assertEquals(TorrentStatus.Stopped, TorrentStatus.fromCode(42))
    }

    @Test
    fun throwsWhenTheResultHasNoTorrents() = runTest {
        val client = TorrentsClient(client(jsonRpc2 = true, mutableListOf(), "{}"))

        assertFailsWith<RpcException.InvalidResponse> { client.getTorrents() }
    }
}
