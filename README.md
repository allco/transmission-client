# Transmission Remote

Transmission Remote is a Kotlin Multiplatform + Compose Multiplatform client for a remote
[Transmission](https://transmissionbt.com/) daemon. It runs on Android, iOS, Linux desktop and the
web.

## Modules

| Module       | What it is                                                         |
|--------------|--------------------------------------------------------------------|
| `shared`     | All UI and logic (Compose Multiplatform), plus the iOS framework   |
| `androidApp` | Entry point for the Android app                                    |
| `desktopApp` | Entry point for the JVM desktop app                                |
| `webApp`     | Entry point for the browser (Wasm and JS)                          |
| `iosApp`     | Xcode project that embeds the `Shared` framework                   |

## Running

```shell
./gradlew :androidApp:installDebug          # Android device/emulator
./gradlew :desktopApp:run                   # Desktop
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # Web (Wasm); jsBrowserDevelopmentRun for JS
```

The iOS app needs macOS. Open `iosApp/iosApp.xcodeproj` in Xcode, then run the app. To run the app
on a device, set `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`.
