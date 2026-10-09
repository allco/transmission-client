# PROPOSAL-1: Store the server configurations in multiplatform-settings, and passwords in secure storage

- **Status:** Open
- **Date:** 2026-10-08

## Problem

`ConnectionRepository` keeps the saved server connection in memory only. When the app stops, the
user loses the connection. The app must keep a list of servers between runs on all four targets:
Android, iOS, desktop (JVM) and the web (Wasm and JS).

The data is small. A user has between 1 and 10 servers. The app always reads the full list, and the
app never runs a query on the list. Each server has a password for the RPC endpoint.

## Options

| Option | Targets | Result |
|---|---|---|
| Android `SharedPreferences` | Android only | Rejected. The shared code cannot use it. |
| Room 2.8.5 | Android, iOS, desktop. No web build. | Rejected. Room needs schemas, migrations and generated code. This data does not need a database. |
| Jetpack DataStore | Android, iOS, desktop are stable. The web build is `1.3.0-alpha11` only. | Possible later. The web build is not stable. |
| `multiplatform-settings` 1.3.0 (russhwolf) | Android, iOS, desktop, web. All are stable. | Proposed. |

The table shows the versions that were on Maven Central and Google Maven on 2026-10-08.

## Proposal

1. Make `ServerConnection` serializable with `kotlinx.serialization`.
2. Store the server list as one JSON value under one key, for example `servers`. Use
   `multiplatform-settings` for the storage. The library uses these stores:
   - Android: `SharedPreferences`.
   - iOS: `NSUserDefaults`.
   - Desktop: Java `Preferences`.
   - Web: `localStorage`.
3. Do not put passwords in this JSON value. All four stores keep their data as plain text.
4. Put each password in the secure storage of the platform. Use an `expect`/`actual` secret store:

   | Target | Secure storage |
   |---|---|
   | Android | Android Keystore. Store the values that the Keystore encrypts. |
   | iOS | Keychain |
   | Desktop | The keyring of the OS: libsecret on Linux, Keychain on macOS, Credential Manager on Windows |
   | Web | None. The web has no secure storage that is reliable. |

5. In the JSON value, keep a reference from each server to its password in the secret store.
6. On the web, do not store the password. Ask for the password in each session. As an option, let
   the user select "remember me". Show a clear warning that the browser keeps the password as plain
   text.
7. Keep all storage code behind `ConnectionRepository`. The ViewModels must not know where the data
   is. A later change to DataStore then changes only the repository.

## Consequences

- The app keeps the servers between runs on all four targets.
- Passwords stay out of plain-text storage on Android, iOS and desktop.
- The web build is less convenient, because it asks for the password again.
- The project gets two new dependencies: `multiplatform-settings` and `kotlinx-serialization-json`.
  It also gets the Gradle serialization plugin.
- The project needs one `expect`/`actual` implementation of the secret store for each platform.

## Open questions

- Desktop on Linux: is libsecret available on all target distributions? If it is not, what does
  the app do?
- Web: is "remember me" in `localStorage` acceptable, or must the web build never store the
  password?
- Do we need Room later for a cache, for example torrent history? If so, Room covers only Android,
  iOS and desktop.

## After the team accepts this proposal

Write an ADR that records the decision. Set the status of this proposal to "Accepted as ADR-N".
