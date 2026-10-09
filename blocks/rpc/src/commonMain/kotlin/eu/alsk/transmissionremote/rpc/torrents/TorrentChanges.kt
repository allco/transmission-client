package eu.alsk.transmissionremote.rpc.torrents

/**
 * Holds the result of [TorrentsClient.getRecentlyActive]: the torrents that changed, and the ids of
 * the torrents that the server removed.
 */
public data class TorrentChanges(
    val changed: List<Torrent>,
    val removedIds: List<Int>,
)
