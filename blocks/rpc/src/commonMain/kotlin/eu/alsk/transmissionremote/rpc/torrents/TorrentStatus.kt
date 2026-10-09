package eu.alsk.transmissionremote.rpc.torrents

/** Holds the state of a torrent. The [code] is the `status` value of the RPC API. */
public enum class TorrentStatus(public val code: Int) {
    Stopped(0),
    QueuedToCheck(1),
    Checking(2),
    QueuedToDownload(3),
    Downloading(4),
    QueuedToSeed(5),
    Seeding(6),
    ;

    internal companion object {
        /**
         * Returns the status for [code]. A code that this client does not know gives [Stopped], so
         * that a newer server does not break the torrent list.
         */
        fun fromCode(code: Int): TorrentStatus = entries.firstOrNull { it.code == code } ?: Stopped
    }
}
