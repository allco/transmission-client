package eu.alsk.transmissionremote.rpc.torrents

import eu.alsk.transmissionremote.rpc.RpcFormat

/**
 * Holds the names of the torrent fields that [TorrentsClient] requests, in both wire formats.
 * Section 4.3 of the RPC reference gives the names.
 */
internal enum class TorrentField(val jsonRpc2: String, val legacy: String) {
    Id("id", "id"),
    Hash("hash_string", "hashString"),
    Name("name", "name"),
    Status("status", "status"),
    Error("error", "error"),
    ErrorString("error_string", "errorString"),
    PercentDone("percent_done", "percentDone"),
    SizeWhenDone("size_when_done", "sizeWhenDone"),
    LeftUntilDone("left_until_done", "leftUntilDone"),
    RateDownload("rate_download", "rateDownload"),
    RateUpload("rate_upload", "rateUpload"),
    Eta("eta", "eta"),
    UploadRatio("upload_ratio", "uploadRatio"),
    QueuePosition("queue_position", "queuePosition"),
    AddedDate("added_date", "addedDate"),
    Labels("labels", "labels"),
    MetadataPercentComplete("metadata_percent_complete", "metadataPercentComplete"),
    RecheckProgress("recheck_progress", "recheckProgress"),
    ;

    fun name(format: RpcFormat): String = when (format) {
        RpcFormat.JsonRpc2 -> jsonRpc2
        RpcFormat.Legacy -> legacy
    }
}
