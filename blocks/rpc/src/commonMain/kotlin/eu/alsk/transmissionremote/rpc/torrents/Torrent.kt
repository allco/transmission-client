package eu.alsk.transmissionremote.rpc.torrents

/**
 * Holds the data of one torrent that the torrent list shows. The values do not depend on the wire
 * format. See the field table in section 4.3 of `docs/reference/transmission-rpc-api.md`.
 */
public data class Torrent(
    /** The numeric id. It changes when the server restarts. Use [hash] to keep a reference. */
    val id: Int,
    /** The info hash in hexadecimal. It does not change. */
    val hash: String,
    val name: String,
    val status: TorrentStatus,
    val error: TorrentError,
    /** The error text of the server. It is empty when [error] is [TorrentError.None]. */
    val errorString: String,
    /** The progress of the wanted files, from 0.0 to 1.0. Use it for progress bars. */
    val percentDone: Double,
    /** The size of the wanted files, in bytes. */
    val sizeWhenDone: Long,
    /** The bytes of the wanted files that the server does not have yet. */
    val leftUntilDone: Long,
    /** The download speed, in bytes per second. */
    val rateDownload: Long,
    /** The upload speed, in bytes per second. */
    val rateUpload: Long,
    /** The time until the download is complete, in seconds. Null when the server does not know it. */
    val etaSeconds: Long?,
    /**
     * The upload ratio. Null when the server has no ratio yet. [Double.POSITIVE_INFINITY] when the
     * ratio is infinite.
     */
    val uploadRatio: Double?,
    /** The position in the download queue or in the seed queue, from 0. */
    val queuePosition: Int,
    /** The time when the server added the torrent, in seconds since 1970-01-01 UTC. */
    val addedDate: Long,
    /** The labels of the torrent. Empty on servers before Transmission 3.0. */
    val labels: List<String>,
    /** The progress of the metadata download of a magnet link, from 0.0 to 1.0. */
    val metadataPercentComplete: Double,
    /** The progress of a data check, from 0.0 to 1.0. */
    val recheckProgress: Double,
)
