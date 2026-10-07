# Transmission RPC API

How to talk to a remote Transmission daemon (`transmission-daemon`, or the GUI clients with remote
access turned on). This is the API this app is built on.

Sources:
- current spec (Transmission 4.1+, JSON-RPC 2.0):
  [`docs/rpc-spec.md` on `main`](https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md)
- legacy spec (Transmission 4.0.x and older):
  [`docs/rpc-spec.md` at 4.0.6](https://github.com/transmission/transmission/blob/4.0.6/docs/rpc-spec.md)
- enum values: [`libtransmission/transmission.h`](https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h)

## Contents

1. [Protocol versions](#1-protocol-versions)
2. [Transport](#2-transport)
3. [Message format](#3-message-format)
4. [Torrent methods](#4-torrent-methods)
5. [Session methods](#5-session-methods)
6. [Enums and units](#6-enums-and-units)
7. [Notes for this client](#7-notes-for-this-client)
8. [Version history](#8-version-history)

---

## 1. Protocol versions

There are two wire formats. A client that has to work with servers people actually run (NAS
packages often lag behind) needs to support both.

| | JSON-RPC 2.0 | Legacy |
|---|---|---|
| Server | Transmission **4.1.0+** (`rpc_version_semver` ≥ 6.0.0) | Transmission ≤ 4.0.x (`rpc-version-semver` ≤ 5.3.0) |
| Envelope | `jsonrpc`, `method`, `params`, `id` | `method`, `arguments`, `tag` |
| Result | `result` object, or `error` object | `result: "success"` or an error string, plus `arguments` |
| Naming | `snake_case` everywhere | a mix of `kebab-case` and `camelCase` |
| Status | current | deprecated: still accepted by 4.x, to be removed in a later major version |

**How to detect which one to use, without an extra request:** every client's first request gets
an HTTP 409 (see [CSRF](#22-csrf-protection-x-transmission-session-id)). From 4.1.0 on, that 409
response carries:

```
X-Transmission-Rpc-Version: 6.0.0
```

- Header present → use JSON-RPC 2.0.
- Header missing → legacy server. Use the legacy format.

After connecting, `session_get` with `fields: ["rpc_version_semver", "version"]` gives the exact
versions (`rpc-version-semver` on legacy servers; it exists since 4.0.0. Older servers only have
the integer `rpc-version`).

Throughout this document, names are the JSON-RPC 2.0 ones, and a **Legacy** column gives the old
name wherever it differs.

---

## 2. Transport

### 2.1 Endpoint

```
POST http(s)://<host>:<port><rpc-path>
Content-Type: application/json
```

- Default URL: `http://<host>:9091/transmission/rpc`. Port and path are configurable on the
  server (`rpc-port`, `rpc-url`), so clients must let users change them.
- HTTPS is not built into Transmission. It is only available when the daemon is behind a reverse
  proxy that terminates TLS.

### 2.2 CSRF protection (`X-Transmission-Session-Id`)

Every request must carry the current session id header:

```
X-Transmission-Session-Id: <token>
```

If the token is missing or stale (the first request, or after the server rotates it), the server
answers **HTTP 409 Conflict** and puts the correct token in its own `X-Transmission-Session-Id`
response header. The client must store it and **resend the same request**.

```
Client                                  Server
  | POST /transmission/rpc  (no token)    |
  |-------------------------------------->|
  |   409  X-Transmission-Session-Id: abc |
  |   (4.1+: X-Transmission-Rpc-Version)  |
  |<--------------------------------------|
  | POST  X-Transmission-Session-Id: abc  |
  |-------------------------------------->|
  |   200  { ...response... }             |
  |<--------------------------------------|
```

Handle the 409 at the HTTP-client level (an interceptor or plugin) so every call gets it for free.
Retry once per request to avoid loops.

### 2.3 Authentication

Optional, enabled on the server with `rpc-authentication-required`. It uses HTTP Basic auth:

```
Authorization: Basic base64("<username>:<password>")
```

A wrong or missing password gives **HTTP 401**. Since credentials go in every request, only use
plain `http://` on a trusted network.

The server can also block brute-force attempts (`anti_brute_force_enabled`). After too many
failed logins it rejects requests even when the password is right.

### 2.4 Host whitelist (DNS-rebinding protection)

When `rpc-host-whitelist-enabled` is on (the default), the server checks the request's `Host:`
header against `rpc-host-whitelist`. IP addresses, `localhost` and `localhost.` are always allowed.
Connecting by a hostname such as `nas.local` that is not on the list is rejected with an HTML
error page explaining the whitelist. Show that to the user as "add this hostname to
`rpc-host-whitelist`, or connect by IP".

There is also an IP whitelist (`rpc-whitelist`, default `127.0.0.1,::1`). Connecting from
another machine is refused with **HTTP 403** until the user adds the client's address or turns
`rpc-whitelist-enabled` off.

### 2.5 HTTP status codes

| Status | Meaning | Client action |
|---|---|---|
| 200 | Request processed. **Check the body**: method-level failures still return 200 | Parse `result` / `error` |
| 204 | JSON-RPC notification (request without `id`) accepted | Nothing to parse |
| 401 | Authentication required or wrong credentials | Ask for credentials |
| 403 | Client IP not in `rpc-whitelist` | Explain the whitelist |
| 409 | Missing or stale session id | Store new token, resend |
| Other 4xx/5xx, HTML body | Host whitelist rejection, reverse-proxy error, wrong path | Show the error, check URL |

---

## 3. Message format

### 3.1 JSON-RPC 2.0 (4.1+)

Follows [JSON-RPC 2.0](https://www.jsonrpc.org/specification) fully, except that **parameters must
be an object**: positional (array) params are not supported. Batch requests are allowed.

Request:

```json
{
  "jsonrpc": "2.0",
  "method": "session_get",
  "params": { "fields": ["version"] },
  "id": 912313
}
```

Success:

```json
{
  "jsonrpc": "2.0",
  "result": { "version": "4.1.0 (ae226418eb)" },
  "id": 912313
}
```

Error: the `data` object may carry an `error_string` with more detail, and a method-specific
`result` object:

```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": 7,
    "message": "HTTP error from backend service",
    "data": {
      "error_string": "Couldn't test port: No Response (0)",
      "result": { "ip_protocol": "ipv6" }
    }
  },
  "id": 912313
}
```

Standard JSON-RPC codes apply (`-32700` parse error, `-32600` invalid request, `-32601` method
not found, `-32602` invalid params). The spec does not list Transmission's own codes, so show
`message` + `data.error_string` to the user rather than switching on `code`.

### 3.2 Legacy (≤ 4.0)

Request:

```json
{
  "method": "session-get",
  "arguments": { "fields": ["version"] },
  "tag": 912313
}
```

Response: `result` is the string `"success"` or an error message:

```json
{
  "result": "success",
  "arguments": { "version": "4.0.6 (38c164933e)" },
  "tag": 912313
}
```

`tag` is optional and echoed back.

### 3.3 Method names

| JSON-RPC 2.0 | Legacy |
|---|---|
| `torrent_start` · `torrent_start_now` · `torrent_stop` · `torrent_verify` · `torrent_reannounce` | `torrent-start` · `torrent-start-now` · `torrent-stop` · `torrent-verify` · `torrent-reannounce` |
| `torrent_get` · `torrent_set` | `torrent-get` · `torrent-set` |
| `torrent_add` · `torrent_remove` | `torrent-add` · `torrent-remove` |
| `torrent_set_location` · `torrent_rename_path` | `torrent-set-location` · `torrent-rename-path` |
| `session_get` · `session_set` · `session_stats` · `session_close` | `session-get` · `session-set` · `session-stats` · `session-close` |
| `blocklist_update` · `port_test` · `free_space` | `blocklist-update` · `port-test` · `free-space` |
| `queue_move_top` · `queue_move_up` · `queue_move_down` · `queue_move_bottom` | `queue-move-top` · `queue-move-up` · `queue-move-down` · `queue-move-bottom` |
| `group_get` · `group_set` | `group-get` · `group-set` |

In short: legacy method names are the same with `-` instead of `_`.

---

## 4. Torrent methods

### 4.1 Selecting torrents: `ids`

Most torrent methods take an `ids` parameter:

| Value | Meaning |
|---|---|
| omitted | **all** torrents |
| `42` | one torrent by numeric id |
| `[1, 2, "3b2455…"]` | a list of numeric ids and/or SHA-1 info-hash strings |
| `"recently_active"` (legacy: `"recently-active"`) | torrents that changed recently. `torrent_get` also returns `removed` |

Numeric ids are **not stable across daemon restarts**. Store `hash_string` if you need to keep a
reference (for example in a notification or a deep link).

### 4.2 Actions: start / stop / verify / reannounce

| Method | Effect |
|---|---|
| `torrent_start` | Start (respects the download/seed queue) |
| `torrent_start_now` | Start immediately, bypassing the queue |
| `torrent_stop` | Stop (pause) |
| `torrent_verify` | Re-check local data |
| `torrent_reannounce` | Ask trackers for more peers now |

Params: `ids`. Result: empty.

```json
{ "jsonrpc": "2.0", "method": "torrent_stop", "params": { "ids": [7, 10] }, "id": 1 }
```

### 4.3 `torrent_get`

Params:

| Key | Type | Description |
|---|---|---|
| `fields` | string[] | **Required.** Which torrent fields to return (table below) |
| `ids` | see 4.1 | Optional. Default: all |
| `format` | string | `"objects"` (default) or `"table"` |

Result:

- `torrents`: with `format: "objects"`, an array of objects holding the requested fields. With
  `format: "table"`, an array of arrays: the first row is the field names, each other row is one
  torrent's values. The table form is smaller and faster to parse; use it for big lists.
- `removed`: only when `ids` is `recently_active`, the ids of torrents removed recently.

Example:

```json
{
  "jsonrpc": "2.0",
  "method": "torrent_get",
  "params": { "fields": ["id", "name", "total_size"], "ids": [7, 10] },
  "id": 39693
}
```

```json
{
  "jsonrpc": "2.0",
  "result": {
    "torrents": [
      { "id": 10, "name": "Fedora x86_64 DVD", "total_size": 34983493932 },
      { "id": 7,  "name": "Ubuntu x86_64 DVD", "total_size": 9923890123 }
    ]
  },
  "id": 39693
}
```

#### Torrent fields

Times are Unix timestamps in seconds (`0` = never). Sizes are bytes. Rates are **bytes per
second**; limits are **kB/s** (see [units](#63-units)).

| Field | Legacy | Type | Description |
|---|---|---|---|
| `activity_date` | `activityDate` | number | Last time data was sent or received |
| `added_date` | `addedDate` | number | When the torrent was added |
| `availability` | — | number[] | Per piece: number of connected peers that have it, or `-1` if we have it |
| `bandwidth_priority` | `bandwidthPriority` | number | [Priority](#61-priority) |
| `bytes_completed` | — | number[] | Completed bytes per file (4.1+) |
| `comment` | — | string | Comment from the .torrent |
| `corrupt_ever` | `corruptEver` | number | Bytes discarded as corrupt |
| `creator` | — | string | Creator from the .torrent |
| `date_created` | `dateCreated` | number | Creation date from the .torrent |
| `desired_available` | `desiredAvailable` | number | Bytes we want that connected peers can provide |
| `done_date` | `doneDate` | number | When the download completed |
| `download_dir` | `downloadDir` | string | Where the data is stored |
| `downloaded_ever` | `downloadedEver` | number | Total bytes downloaded |
| `download_limit` | `downloadLimit` | number | Download limit, kB/s |
| `download_limited` | `downloadLimited` | boolean | Whether `download_limit` is applied |
| `edit_date` | `editDate` | number | Last time the torrent's settings changed |
| `error` | — | number | [Error type](#64-torrent-error-type), `0` = none |
| `error_string` | `errorString` | string | Human-readable error |
| `eta` | — | number | Seconds until done. [Special values](#65-eta) |
| `eta_idle` | `etaIdle` | number | Seconds until the idle-seeding limit stops it |
| `file_count` | `file-count` | number | Number of files |
| `files` | — | object[] | [Files](#files), in torrent order |
| `file_stats` | `fileStats` | object[] | [Per-file state](#file_stats), same order as `files` |
| `group` | — | string | Bandwidth group name |
| `hash_string` | `hashString` | string | Info-hash (hex). **Stable id** |
| `have_unchecked` | `haveUnchecked` | number | Bytes downloaded but not yet verified |
| `have_valid` | `haveValid` | number | Bytes downloaded and verified |
| `honors_session_limits` | `honorsSessionLimits` | boolean | Whether session speed limits apply |
| `id` | — | number | Numeric id (not stable across restarts) |
| `is_finished` | `isFinished` | boolean | Reached its seed ratio / idle limit |
| `is_private` | `isPrivate` | boolean | Private torrent (no DHT/PEX/LPD) |
| `is_stalled` | `isStalled` | boolean | No transfer for `queue_stalled_minutes` |
| `labels` | — | string[] | User labels |
| `left_until_done` | `leftUntilDone` | number | Bytes still needed for the wanted files |
| `magnet_link` | `magnetLink` | string | Magnet URI |
| `max_connected_peers` | `maxConnectedPeers` | number | Peer connection limit |
| `metadata_percent_complete` | `metadataPercentComplete` | double | 0–1; below 1 while a magnet is fetching metadata |
| `name` | — | string | Display name |
| `peer_limit` | `peer-limit` | number | Max peers |
| `peers` | — | object[] | [Connected peers](#peers) |
| `peers_connected` | `peersConnected` | number | Connected peer count |
| `peers_from` | `peersFrom` | object | [Where peers came from](#peers_from) |
| `peers_getting_from_us` | `peersGettingFromUs` | number | Peers we upload to |
| `peers_sending_to_us` | `peersSendingToUs` | number | Peers we download from |
| `percent_complete` | `percentComplete` | double | 0–1 of the **whole** torrent |
| `percent_done` | `percentDone` | double | 0–1 of the **wanted** files. **Use this for progress bars** |
| `pieces` | — | string | Base64 bitfield of pieces we have |
| `piece_count` | `pieceCount` | number | Number of pieces |
| `piece_size` | `pieceSize` | number | Piece size, bytes |
| `priorities` | — | number[] | [Priority](#61-priority) per file |
| `primary_mime_type` | `primary-mime-type` | string | Dominant MIME type, e.g. `video/mp4` |
| `queue_position` | `queuePosition` | number | Position in its queue, from 0 |
| `rate_download` | `rateDownload` | number | Download speed, **B/s** |
| `rate_upload` | `rateUpload` | number | Upload speed, **B/s** |
| `recheck_progress` | `recheckProgress` | double | 0–1 while verifying |
| `seconds_downloading` | `secondsDownloading` | number | Total time spent downloading |
| `seconds_seeding` | `secondsSeeding` | number | Total time spent seeding |
| `seed_idle_limit` | `seedIdleLimit` | number | Idle-seeding limit, minutes |
| `seed_idle_mode` | `seedIdleMode` | number | [Limit mode](#62-ratio-and-idle-limit-mode) |
| `seed_ratio_limit` | `seedRatioLimit` | double | Seed ratio limit |
| `seed_ratio_mode` | `seedRatioMode` | number | [Limit mode](#62-ratio-and-idle-limit-mode) |
| `sequential_download` | — | boolean | Download pieces in order (4.1+) |
| `sequential_download_from_piece` | — | number | Start piece for sequential download (4.1+) |
| `size_when_done` | `sizeWhenDone` | number | Bytes of the wanted files |
| `start_date` | `startDate` | number | Last time it was started |
| `status` | — | number | [Status](#66-torrent-status) |
| `torrent_file` | `torrentFile` | string | Path of the .torrent on the server |
| `total_size` | `totalSize` | number | Bytes of all files |
| `trackers` | — | object[] | [Trackers](#trackers) |
| `tracker_list` | `trackerList` | string | Announce URLs, one per line, blank line between tiers |
| `tracker_stats` | `trackerStats` | object[] | [Tracker state](#tracker_stats) |
| `uploaded_ever` | `uploadedEver` | number | Total bytes uploaded |
| `upload_limit` | `uploadLimit` | number | Upload limit, kB/s |
| `upload_limited` | `uploadLimited` | boolean | Whether `upload_limit` is applied |
| `upload_ratio` | `uploadRatio` | double | Upload ratio. `-1` = not available, `-2` = infinite |
| `wanted` | — | boolean[] | Per file: whether it's downloaded. **Legacy servers return `0`/`1`** |
| `webseeds_ex` | — | object[] | [Web seeds](#webseeds_ex) (4.2+) |
| `webseeds` | — | string[] | Web seed URLs. Deprecated in 4.2 in favour of `webseeds_ex` |
| `webseeds_sending_to_us` | `webseedsSendingToUs` | number | Web seeds we download from |

Deprecated: `manual_announce_time` (`manualAnnounceTime`): never worked, don't use.

#### Nested objects

##### `files`

| Field | Legacy | Type | Description |
|---|---|---|---|
| `name` | — | string | Path inside the torrent |
| `length` | — | number | Size, bytes |
| `bytes_completed` | `bytesCompleted` | number | Bytes downloaded |
| `begin_piece` | — | number | First piece (4.1+) |
| `end_piece` | — | number | Last piece (4.1+) |

"File index" everywhere in this API means the position in this array, starting at 0.

##### `file_stats`

| Field | Legacy | Type | Description |
|---|---|---|---|
| `bytes_completed` | `bytesCompleted` | number | Bytes downloaded |
| `wanted` | — | boolean | Whether the file is downloaded |
| `priority` | — | number | [Priority](#61-priority) |

##### `peers`

| Field | Legacy | Type | Description |
|---|---|---|---|
| `address` | — | string | IP address |
| `port` | — | number | Port |
| `client_name` | `clientName` | string | Peer's client, e.g. `qBittorrent 5.0.1` |
| `flag_str` | `flagStr` | string | Status flags, as in the GTK/Qt peer list |
| `progress` | — | double | 0–1, how much of the torrent the peer has |
| `rate_to_client` | `rateToClient` | number | Download from this peer, B/s |
| `rate_to_peer` | `rateToPeer` | number | Upload to this peer, B/s |
| `is_downloading_from` | `isDownloadingFrom` | boolean | |
| `is_uploading_to` | `isUploadingTo` | boolean | |
| `is_encrypted` | `isEncrypted` | boolean | |
| `is_incoming` | `isIncoming` | boolean | |
| `is_utp` | `isUTP` | boolean | |
| `client_is_choked` · `client_is_interested` | `clientIsChoked` · `clientIsInterested` | boolean | |
| `peer_is_choked` · `peer_is_interested` | `peerIsChoked` · `peerIsInterested` | boolean | |
| `bytes_to_client` · `bytes_to_peer` | — | number | 4.1+ |
| `peer_id` | — | string | 4.1+ |
| `supports_holepunch` | — | boolean | 4.2+ |

##### `peers_from`

Counts of peers by source: `from_cache`, `from_dht`, `from_incoming`, `from_lpd`, `from_ltep`,
`from_pex`, `from_tracker`, and `from_holepunch` (4.2+). Legacy: `fromCache`, `fromDht`, `fromIncoming`,
`fromLpd`, `fromLtep`, `fromPex`, `fromTracker`.

##### `trackers`

`announce` (URL), `scrape` (URL), `sitename`, `tier`, `id`.

##### `tracker_stats`

| Field | Legacy | Type |
|---|---|---|
| `id` · `tier` · `host` · `sitename` · `announce` · `scrape` · `is_backup` | `isBackup` | |
| `announce_state` | `announceState` | number (see `tr_tracker_state`) |
| `has_announced` · `last_announce_time` · `last_announce_start_time` | `hasAnnounced` · `lastAnnounceTime` · `lastAnnounceStartTime` | |
| `last_announce_succeeded` · `last_announce_timed_out` · `last_announce_result` | `lastAnnounceSucceeded` · `lastAnnounceTimedOut` · `lastAnnounceResult` | `last_announce_result` is the tracker's message |
| `last_announce_peer_count` · `next_announce_time` | `lastAnnouncePeerCount` · `nextAnnounceTime` | |
| `scrape_state` · `has_scraped` · `last_scrape_time` · `last_scrape_start_time` | `scrapeState` · `hasScraped` · `lastScrapeTime` · `lastScrapeStartTime` | |
| `last_scrape_succeeded` · `last_scrape_timed_out` · `last_scrape_result` · `next_scrape_time` | `lastScrapeSucceeded` · `lastScrapeTimedOut` · `lastScrapeResult` · `nextScrapeTime` | |
| `seeder_count` · `leecher_count` · `download_count` | `seederCount` · `leecherCount` · `downloadCount` | from the last scrape, `-1` if unknown |
| `downloader_count` | — | 4.1+ |

##### `webseeds_ex`

`url` (string), `is_downloading` (boolean), `download_bytes_per_second` (number). 4.2+.

### 4.4 `torrent_set`

Changes torrent settings. Params: `ids` plus any of:

| Key | Legacy | Type | Description |
|---|---|---|---|
| `bandwidth_priority` | `bandwidthPriority` | number | [Priority](#61-priority) |
| `download_limit` · `download_limited` | `downloadLimit` · `downloadLimited` | number · boolean | kB/s |
| `upload_limit` · `upload_limited` | `uploadLimit` · `uploadLimited` | number · boolean | kB/s |
| `honors_session_limits` | `honorsSessionLimits` | boolean | |
| `files_wanted` · `files_unwanted` | `files-wanted` · `files-unwanted` | number[] | File indices; `[]` = all files |
| `priority_high` · `priority_normal` · `priority_low` | `priority-high` · `priority-normal` · `priority-low` | number[] | File indices; `[]` = all files |
| `group` | — | string | Bandwidth group |
| `labels` | — | string[] | Replaces all labels |
| `location` | — | string | New data location (prefer `torrent_set_location`) |
| `peer_limit` | `peer-limit` | number | |
| `queue_position` | `queuePosition` | number | 0…n-1 |
| `seed_idle_limit` · `seed_idle_mode` | `seedIdleLimit` · `seedIdleMode` | number | minutes · [mode](#62-ratio-and-idle-limit-mode) |
| `seed_ratio_limit` · `seed_ratio_mode` | `seedRatioLimit` · `seedRatioMode` | double · number | |
| `sequential_download` · `sequential_download_from_piece` | — | boolean · number | 4.1+ |
| `tracker_list` | `trackerList` | string | Replaces all trackers. One URL per line, blank line between tiers |

Deprecated: `tracker_add`, `tracker_remove`, `tracker_replace` (use `tracker_list`).

Result: empty.

### 4.5 `torrent_add`

Params (**either** `filename` **or** `metainfo` is required):

| Key | Legacy | Type | Description |
|---|---|---|---|
| `filename` | — | string | Magnet URI, URL of a .torrent, or a path on the **server** |
| `metainfo` | — | string | Base64 of a .torrent file's contents. Use this to upload a file from the client device |
| `download_dir` | `download-dir` | string | Destination. Default: session `download_dir` |
| `paused` | — | boolean | Add without starting |
| `labels` | — | string[] | |
| `peer_limit` | `peer-limit` | number | |
| `bandwidth_priority` | `bandwidthPriority` | number | |
| `files_wanted` · `files_unwanted` | `files-wanted` · `files-unwanted` | number[] | File indices |
| `priority_high` · `priority_normal` · `priority_low` | `priority-high` · … | number[] | File indices |
| `sequential_download` · `sequential_download_from_piece` | — | boolean · number | 4.1+ |
| `cookies` | — | string | Cookies for fetching `filename` from a URL: `name1=v1; name2=v2;` |

Result: one of

- `torrent_added` (legacy `torrent-added`): `{ id, name, hash_string }`
- `torrent_duplicate` (legacy `torrent-duplicate`): same shape. The torrent already existed. This
  is **not** an error.

```json
{
  "jsonrpc": "2.0",
  "method": "torrent_add",
  "params": { "filename": "magnet:?xt=urn:btih:…", "download_dir": "/downloads", "paused": false },
  "id": 5
}
```

```json
{
  "jsonrpc": "2.0",
  "result": { "torrent_added": { "id": 13, "name": "ubuntu-24.04.1-desktop-amd64.iso", "hash_string": "3b2455…" } },
  "id": 5
}
```

### 4.6 `torrent_remove`

| Key | Legacy | Type | Description |
|---|---|---|---|
| `ids` | — | see 4.1 | |
| `delete_local_data` | `delete-local-data` | boolean | Also delete downloaded files. Default `false` |

Result: empty. **Omitting `ids` removes every torrent.** Never send this without `ids`.

### 4.7 `torrent_set_location`

| Key | Type | Description |
|---|---|---|
| `ids` | see 4.1 | |
| `location` | string | New directory |
| `move` | boolean | `true`: move the data there. `false` (default): look for the data in `location` |

Result: empty.

### 4.8 `torrent_rename_path`

Renames a file or folder inside **one** torrent.

| Key | Type | Description |
|---|---|---|
| `ids` | see 4.1 | Exactly one torrent |
| `path` | string | Current path of the file or folder, inside the torrent |
| `name` | string | New name (not a path) |

Result: `path`, `name`, `id`. Re-fetch `files` and `name` afterwards.

---

## 5. Session methods

### 5.1 `session_get` / `session_set`

`session_get` params: optional `fields` (string[]). Without it, all fields come back.
Result: the requested fields.

`session_set` params: any writable field below. Result: empty.

Read-only: `blocklist_size`, `config_dir`, `rpc_version`, `rpc_version_minimum`,
`rpc_version_semver`, `session_id`, `units`, `version`.

The legacy name is the same with `-` instead of `_` (`alt-speed-down`, `download-dir`, …), except
`seedRatioLimit` and `seedRatioLimited`, which are camelCase.

| Field | Type | Description |
|---|---|---|
| **Speed** | | |
| `speed_limit_down` · `speed_limit_down_enabled` | number · boolean | Global download limit, kB/s |
| `speed_limit_up` · `speed_limit_up_enabled` | number · boolean | Global upload limit, kB/s |
| `alt_speed_enabled` | boolean | "Turtle mode" on |
| `alt_speed_down` · `alt_speed_up` | number | Turtle-mode limits, kB/s |
| `alt_speed_time_enabled` | boolean | Turn turtle mode on/off on a schedule |
| `alt_speed_time_begin` · `alt_speed_time_end` | number | Minutes after midnight |
| `alt_speed_time_day` | number | Day bitmask, see `tr_sched_day` (Sun = 1, Mon = 2, … Sat = 64; weekdays = 62, weekend = 65, all = 127) |
| **Downloads** | | |
| `download_dir` | string | Default destination |
| `incomplete_dir` · `incomplete_dir_enabled` | string · boolean | Keep partial downloads elsewhere |
| `rename_partial_files` | boolean | Append `.part` to incomplete files |
| `start_added_torrents` | boolean | Start torrents when added |
| `trash_original_torrent_files` | boolean | Delete the .torrent file after adding |
| `sequential_download` | boolean | Default for new torrents (4.1+) |
| `default_trackers` | string | Added to every public torrent. One per line, blank line between tiers |
| **Queue** | | |
| `download_queue_enabled` · `download_queue_size` | boolean · number | Max active downloads |
| `seed_queue_enabled` · `seed_queue_size` | boolean · number | Max active seeds |
| `queue_stalled_enabled` · `queue_stalled_minutes` | boolean · number | Idle torrents don't count toward the queue |
| **Seeding** | | |
| `seed_ratio_limit` · `seed_ratio_limited` | double · boolean | Default seed ratio. Legacy: `seedRatioLimit` · `seedRatioLimited` |
| `idle_seeding_limit` · `idle_seeding_limit_enabled` | number · boolean | Stop seeding after N idle minutes |
| **Network** | | |
| `peer_port` · `peer_port_random_on_start` | number · boolean | Incoming peer port |
| `port_forwarding_enabled` | boolean | UPnP / NAT-PMP |
| `peer_limit_global` · `peer_limit_per_torrent` | number | |
| `encryption` | string | `required`, `preferred`, `allowed` (legacy: `tolerated` instead of `allowed`) |
| `dht_enabled` · `pex_enabled` · `lpd_enabled` | boolean | Peer discovery in public torrents |
| `preferred_transports` | string[] | Transport preference order (4.1+). Replaces `utp_enabled` / `tcp_enabled` |
| `utp_enabled` | boolean | Deprecated in 4.1 |
| `reqq` | number | Max queued block requests per peer |
| **Blocklist** | | |
| `blocklist_enabled` · `blocklist_url` | boolean · string | |
| `blocklist_size` | number | Rules loaded (read-only) |
| **Scripts** | | |
| `script_torrent_added_enabled` · `script_torrent_added_filename` | boolean · string | |
| `script_torrent_done_enabled` · `script_torrent_done_filename` | boolean · string | |
| `script_torrent_done_seeding_enabled` · `script_torrent_done_seeding_filename` | boolean · string | |
| **RPC / info** | | |
| `anti_brute_force_enabled` | boolean | |
| `version` | string | `"4.1.3 (abcdef0123)"` |
| `rpc_version_semver` | string | e.g. `"6.0.1"` |
| `rpc_version` · `rpc_version_minimum` | number | Deprecated integer versions |
| `session_id` | string | Current CSRF token |
| `config_dir` | string | Server's config directory |
| `units` | object | Server's unit names, see below |
| `cache_size_mib` | number | Deprecated (4.2), legacy `cache-size-mb` |
| `download_dir_free_space` | number | Deprecated, use `free_space` |

`units` object: `speed_units`, `size_units`, `memory_units` (5 strings each, e.g.
`["B/s","kB/s","MB/s","GB/s","TB/s"]`) and `speed_bytes`, `size_bytes`, `memory_bytes` (bytes in
one "k": 1000 or 1024). Legacy keys use `-`.

### 5.2 `session_stats`

No params. Result:

| Field | Legacy | Type | Description |
|---|---|---|---|
| `torrent_count` | `torrentCount` | number | |
| `active_torrent_count` | `activeTorrentCount` | number | |
| `paused_torrent_count` | `pausedTorrentCount` | number | |
| `download_speed` | `downloadSpeed` | number | Total download, B/s |
| `upload_speed` | `uploadSpeed` | number | Total upload, B/s |
| `current_stats` | `current-stats` | stats | Since the daemon started |
| `cumulative_stats` | `cumulative-stats` | stats | All time |

Stats object: `uploaded_bytes`, `downloaded_bytes`, `files_added`, `seconds_active`,
`session_count` (legacy: `uploadedBytes`, `downloadedBytes`, `filesAdded`, `secondsActive`,
`sessionCount`).

### 5.3 `free_space`

| | Key | Type | Description |
|---|---|---|---|
| Param | `path` | string | Directory on the server |
| Result | `path` | string | Echoed |
| Result | `size_bytes` | number | Free bytes (legacy `size-bytes`) |
| Result | `total_size` | number | Capacity, bytes (4.0+) |

### 5.4 `port_test`

Checks whether the incoming peer port is reachable from the internet.

- Param (4.1+): optional `ip_protocol`, `"ipv4"` or `"ipv6"`.
- Result: `port_is_open` (boolean; legacy `port-is-open`), and on 4.1+ `ip_protocol`.

### 5.5 `blocklist_update`

Downloads the blocklist from `blocklist_url`. Result: `blocklist_size`.

### 5.6 Queue: `queue_move_top` / `queue_move_up` / `queue_move_down` / `queue_move_bottom`

Params: `ids`. Result: empty.

### 5.7 Bandwidth groups: `group_get` / `group_set`

`group_get` params: optional `name` (string or string[]); default all. Result: `group`, an array
of group objects.

`group_set` params: one group object.

| Field | Legacy | Type |
|---|---|---|
| `name` | — | string |
| `honors_session_limits` | `honorsSessionLimits` | boolean |
| `speed_limit_down` · `speed_limit_down_enabled` | `speed-limit-down` · `speed-limit-down-enabled` | number (kB/s) · boolean |
| `speed_limit_up` · `speed_limit_up_enabled` | `speed-limit-up` · `speed-limit-up-enabled` | number (kB/s) · boolean |

### 5.8 `session_close`

Shuts the daemon down. No params, empty result. Put this behind a confirmation in the UI.

---

## 6. Enums and units

Values from `libtransmission/transmission.h`.

### 6.1 Priority

`bandwidth_priority`, `priorities[]`, `file_stats[].priority`:

| Value | Meaning |
|---|---|
| `-1` | Low |
| `0` | Normal |
| `1` | High |

### 6.2 Ratio and idle limit mode

`seed_ratio_mode`, `seed_idle_mode`:

| Value | Meaning |
|---|---|
| `0` | Use the session (global) setting |
| `1` | Use this torrent's own limit |
| `2` | Unlimited: seed forever |

### 6.3 Units

| What | Unit |
|---|---|
| Speeds reported (`rate_download`, `download_speed`, peer rates) | **bytes per second** |
| Speed limits (`*_limit`, `alt_speed_*`, `speed_limit_*`) | **kB/s** (1 kB = 1000 bytes, or as `units.speed_bytes` says) |
| Sizes | bytes |
| Times / dates | Unix seconds; `0` = never |
| Progress (`percent_done`, …) | 0.0–1.0 |

### 6.4 Torrent error type

`error`:

| Value | Meaning |
|---|---|
| `0` | OK |
| `1` | Tracker warning (still working) |
| `2` | Tracker error |
| `3` | Local error, e.g. disk full or missing files. The torrent is stopped |

`error_string` has the text.

### 6.5 ETA

`eta`, `eta_idle`: seconds, or

| Value | Meaning |
|---|---|
| `-1` | Not available (e.g. stopped or complete) |
| `-2` | Unknown (e.g. no peers) |

### 6.6 Torrent status

`status`:

| Value | Meaning | App filter |
|---|---|---|
| `0` | Stopped | Paused (or Error if `error != 0`) |
| `1` | Queued to verify | Checking |
| `2` | Verifying | Checking |
| `3` | Queued to download | Downloading |
| `4` | Downloading | Downloading |
| `5` | Queued to seed | Seeding |
| `6` | Seeding | Seeding |

These values changed in Transmission 2.40 (`rpc-version` 14). Servers that old aren't worth
supporting.

---

## 7. Notes for this client

### 7.1 Connecting

1. Build the URL from the connection screen: `http(s)://host:port/rpc-path`.
2. Send `session_get` with `fields: ["version", "rpc_version_semver", "download_dir"]`, encoded as
   JSON-RPC 2.0, with no session id.
3. On 409: store `X-Transmission-Session-Id`. If `X-Transmission-Rpc-Version` is present, use
   JSON-RPC 2.0; otherwise switch to the legacy encoding. Resend.
4. Map failures to messages: 401 → wrong username/password; 403 → client IP not allowed
   (`rpc-whitelist`); HTML error mentioning the whitelist → hostname not allowed
   (`rpc-host-whitelist`); connection refused / timeout → wrong host or port, or daemon not running.

The connection screen's "Test" button can run exactly this.

### 7.2 Keeping the list up to date

- First load: `torrent_get` with no `ids`, `format: "table"`, and only the fields the list
  shows:

  ```json
  ["id", "hash_string", "name", "status", "error", "error_string", "percent_done",
   "size_when_done", "left_until_done", "rate_download", "rate_upload", "eta",
   "upload_ratio", "queue_position", "added_date", "labels", "metadata_percent_complete",
   "recheck_progress"]
  ```

- Then poll every 2–5 s with `ids: "recently_active"`: merge the returned torrents and drop the
  ids listed in `removed`. Poll less often, or stop, when the app is in the background.
- Fetch heavy fields (`files`, `file_stats`, `peers`, `tracker_stats`, `pieces`) only on the
  details screen, for the one torrent shown.
- Speeds for the summary card: `session_stats` (`download_speed`, `upload_speed`). Free space:
  `free_space` with the session `download_dir`.

### 7.3 Adding torrents

- Magnet link or URL → `torrent_add` with `filename`.
- File picked on the device → read it, base64-encode it, `torrent_add` with `metainfo`. A
  `filename` path would refer to the server's disk.
- Treat `torrent_duplicate` as "already added", not as a failure.

### 7.4 Platform notes

- **Web (Wasm/JS):** the browser enforces CORS, and Transmission sends no CORS headers. Calling a
  daemon on another origin from the web build fails unless the daemon is reached through a reverse
  proxy that adds `Access-Control-Allow-Origin`, `Access-Control-Allow-Headers: Authorization,
  Content-Type, X-Transmission-Session-Id`, and `Access-Control-Expose-Headers:
  X-Transmission-Session-Id, X-Transmission-Rpc-Version`. Without the expose header the browser
  hides the session id, and every request loops on 409. Alternatively, serve the web app from the
  same origin as the RPC endpoint.
- **Android:** plain `http://` to LAN addresses needs a network security config that permits
  cleartext traffic (blocked by default since API 28).
- **iOS:** plain `http://` needs an App Transport Security exception (`NSAllowsLocalNetworking`
  covers local addresses), and LAN access shows the local-network permission prompt
  (`NSLocalNetworkUsageDescription`).

---

## 8. Version history

Main RPC changes relevant to a client. `rpc_version_semver` in brackets.

| Transmission | RPC | Changes |
|---|---|---|
| 2.40 | 5.0.0 | **Breaking:** new `status` values. Queue methods, `torrent-start-now`, `isStalled`, `queuePosition` |
| 2.80 | 5.1.0 | `torrent-rename-path`, `free-space`, `etaIdle`, `torrent-duplicate` |
| 3.00 | 5.2.0 | `session-get` `fields`, `labels`, `editDate`, `torrent-get` `format: "table"` |
| 4.0.0 | 5.3.0 | `group-get`/`group-set`, `trackerList`, `file-count`, `percentComplete`, `primary-mime-type`, `availability`, `rpc-version-semver`; `free-space` returns `total_size`. Deprecated `trackerAdd/Remove/Replace`, `download-dir-free-space` |
| 4.1.0 | 6.0.0 | **Breaking:** JSON-RPC 2.0 and `snake_case` (legacy still accepted). `X-Transmission-Rpc-Version` header on 409. `sequential_download`, `files[].begin_piece/end_piece`, `port_test` `ip_protocol`, `preferred_transports`. `wanted` becomes booleans; `cache_size_mb` → `cache_size_mib`; encryption `tolerated` → `allowed` |
| 4.1.1 | 6.0.1 | `speed_limit_down/up` returned as integers again |
| 4.2.0 | 6.1.0 | `peers[].supports_holepunch`, `peers_from.from_holepunch`, `webseeds_ex`. Deprecated `webseeds`, `cache_size_mib` |
