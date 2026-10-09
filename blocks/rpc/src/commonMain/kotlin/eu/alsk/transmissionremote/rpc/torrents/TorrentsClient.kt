package eu.alsk.transmissionremote.rpc.torrents

import eu.alsk.transmissionremote.rpc.RpcClient
import eu.alsk.transmissionremote.rpc.RpcException
import eu.alsk.transmissionremote.rpc.RpcFormat
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

/**
 * Gets the torrent list from the server with the `torrent_get` method (`torrent-get` in the legacy
 * format). See section 4.3 and section 7.2 of `docs/reference/transmission-rpc-api.md`.
 *
 * The client requests only the fields of [Torrent]. It uses the field names of the wire format of
 * the server, so the caller does not see the format.
 */
public class TorrentsClient(private val rpc: RpcClient) {

    /** Returns all torrents of the server. Use it for the first load of the torrent list. */
    public suspend fun getTorrents(): List<Torrent> {
        val format = rpc.format()
        val result = rpc.call(method(format), params(format, recentlyActive = false))
        return torrents(result, format)
    }

    /**
     * Returns the torrents that changed recently and the ids of the torrents that the server
     * removed. Use it to update the torrent list after the first load.
     */
    public suspend fun getRecentlyActive(): TorrentChanges {
        val format = rpc.format()
        val result = rpc.call(method(format), params(format, recentlyActive = true))
        val removed = (result["removed"] as? JsonArray).orEmpty().mapNotNull { (it as? JsonPrimitive)?.intOrNull }
        return TorrentChanges(changed = torrents(result, format), removedIds = removed)
    }

    private fun method(format: RpcFormat): String = when (format) {
        RpcFormat.JsonRpc2 -> "torrent_get"
        RpcFormat.Legacy -> "torrent-get"
    }

    private fun params(format: RpcFormat, recentlyActive: Boolean): JsonObject = buildJsonObject {
        putJsonArray("fields") { TorrentField.entries.forEach { add(it.name(format)) } }
        if (recentlyActive) {
            put(
                "ids",
                when (format) {
                    RpcFormat.JsonRpc2 -> "recently_active"
                    RpcFormat.Legacy -> "recently-active"
                },
            )
        }
    }

    private fun torrents(result: JsonObject, format: RpcFormat): List<Torrent> {
        val list = result["torrents"] as? JsonArray
            ?: throw RpcException.InvalidResponse("The torrent_get result has no \"torrents\" array")
        return list.map { element ->
            val json = element as? JsonObject
                ?: throw RpcException.InvalidResponse("An item of \"torrents\" is not a JSON object")
            torrent(TorrentJson(json, format))
        }
    }

    private fun torrent(json: TorrentJson): Torrent = Torrent(
        id = json.int(TorrentField.Id)
            ?: throw RpcException.InvalidResponse("A torrent has no \"id\""),
        hash = json.string(TorrentField.Hash),
        name = json.string(TorrentField.Name),
        status = TorrentStatus.fromCode(json.int(TorrentField.Status) ?: TorrentStatus.Stopped.code),
        error = TorrentError.fromCode(json.int(TorrentField.Error) ?: TorrentError.None.code),
        errorString = json.string(TorrentField.ErrorString),
        percentDone = json.double(TorrentField.PercentDone) ?: 0.0,
        sizeWhenDone = json.long(TorrentField.SizeWhenDone) ?: 0,
        leftUntilDone = json.long(TorrentField.LeftUntilDone) ?: 0,
        rateDownload = json.long(TorrentField.RateDownload) ?: 0,
        rateUpload = json.long(TorrentField.RateUpload) ?: 0,
        etaSeconds = json.long(TorrentField.Eta)?.takeIf { it >= 0 },
        uploadRatio = uploadRatio(json.double(TorrentField.UploadRatio)),
        queuePosition = json.int(TorrentField.QueuePosition) ?: 0,
        addedDate = json.long(TorrentField.AddedDate) ?: 0,
        labels = json.strings(TorrentField.Labels),
        metadataPercentComplete = json.double(TorrentField.MetadataPercentComplete) ?: 1.0,
        recheckProgress = json.double(TorrentField.RecheckProgress) ?: 0.0,
    )

    // The server sends -1 when it has no ratio, and -2 when the ratio is infinite (section 4.3).
    private fun uploadRatio(value: Double?): Double? = when {
        value == null || value == RATIO_NOT_AVAILABLE -> null
        value == RATIO_INFINITE -> Double.POSITIVE_INFINITY
        else -> value
    }

    /** Reads the fields of one torrent with the names of [format]. */
    private class TorrentJson(private val json: JsonObject, private val format: RpcFormat) {
        private fun primitive(field: TorrentField): JsonPrimitive? = json[field.name(format)] as? JsonPrimitive

        fun string(field: TorrentField): String = primitive(field)?.contentOrNull.orEmpty()
        fun int(field: TorrentField): Int? = primitive(field)?.intOrNull
        fun long(field: TorrentField): Long? = primitive(field)?.longOrNull
        fun double(field: TorrentField): Double? = primitive(field)?.doubleOrNull

        fun strings(field: TorrentField): List<String> =
            (json[field.name(format)] as? JsonArray).orEmpty().mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
    }

    private companion object {
        const val RATIO_NOT_AVAILABLE = -1.0
        const val RATIO_INFINITE = -2.0
    }
}

private fun JsonArray?.orEmpty(): List<JsonElement> = this ?: emptyList()
