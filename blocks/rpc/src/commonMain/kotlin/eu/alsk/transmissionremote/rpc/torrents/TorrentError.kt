package eu.alsk.transmissionremote.rpc.torrents

/** Holds the error type of a torrent. The [code] is the `error` value of the RPC API. */
public enum class TorrentError(public val code: Int) {
    None(0),

    /** The tracker sent a warning. The torrent still works. */
    TrackerWarning(1),

    /** The tracker sent an error. */
    TrackerError(2),

    /** A local error, for example a full disk or missing files. The server stopped the torrent. */
    LocalError(3),
    ;

    internal companion object {
        /** Returns the error type for [code]. A code that this client does not know gives [LocalError]. */
        fun fromCode(code: Int): TorrentError = entries.firstOrNull { it.code == code } ?: LocalError
    }
}
