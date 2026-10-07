# Transmission Remote

A Kotlin Multiplatform + Compose Multiplatform client for a remote
[Transmission](https://transmissionbt.com/) daemon. It runs on Android, iOS, Linux desktop and the web.

## Modules

| Module       | What it is                                                         |
|--------------|--------------------------------------------------------------------|
| `shared`     | All UI and logic (Compose Multiplatform), plus the iOS framework   |
| `androidApp` | Android application entry point                                    |
| `desktopApp` | JVM desktop entry point                                            |
| `webApp`     | Browser entry point (Wasm and JS)                                  |
| `iosApp`     | Xcode project that embeds the `Shared` framework                   |

## Running

```shell
./gradlew :androidApp:installDebug          # Android device/emulator
./gradlew :desktopApp:run                   # Desktop
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # Web (Wasm); jsBrowserDevelopmentRun for JS
```

iOS needs macOS: open `iosApp/iosApp.xcodeproj` in Xcode and run. Set `TEAM_ID` in
`iosApp/Configuration/Config.xcconfig` to run on a device.
