# AGENTS.md

This file provides guidance to AI coding agents (Claude Code, Codex, Gemini CLI and others) when working with code in this repository.

## What this is

Kotlin Multiplatform + Compose Multiplatform client for a remote Transmission BitTorrent daemon,
targeting Android, iOS, Linux desktop (JVM) and the web (Wasm and JS). The package is
`eu.alsk.transmissionremote`.

## Commands

```shell
./gradlew :androidApp:assembleDebug            # Android APK
./gradlew :androidApp:installDebug             # install on a connected device/emulator
./gradlew :desktopApp:run                      # run desktop app
./gradlew :webApp:wasmJsBrowserDevelopmentRun  # web dev server (Wasm) on http://localhost:8080
./gradlew :webApp:jsBrowserDevelopmentRun      # web dev server (JS)
./gradlew :shared:compileKotlinJvm :shared:compileAndroidMain   # quick compile check of shared UI
./gradlew :shared:compileKotlinIosSimulatorArm64                # iOS klib compiles on Linux too
```

- iOS apps can only be built and run on macOS: open `iosApp/iosApp.xcodeproj`. Xcode runs
  `:shared:embedAndSignAppleFrameworkForXcode` and links the static `Shared` framework. Set
  `TEAM_ID` in `iosApp/Configuration/Config.xcconfig` to run on a device.
- Running the desktop app headless (Xvfb) needs `SKIKO_RENDER_API=SOFTWARE`. Xvfb has no GL.
- There are no tests or linters set up yet.

## Architecture

- **`shared`** holds all UI and logic in `commonMain`, and is the only module with real code.
  Its Android target uses the AGP 9 `com.android.kotlin.multiplatform.library` plugin, configured
  in a `kotlin { android { … } }` block (the old `androidLibrary { }` is deprecated). It also
  builds the iOS `Shared` framework.
- **`androidApp`**, **`desktopApp`** and **`webApp`** are thin entry points that only call `App()`.
  The iOS equivalent is `shared/src/iosMain/.../MainViewController.kt`, called from SwiftUI in
  `iosApp/iosApp/ContentView.swift`. With AGP 9, `com.android.application` can't be combined with
  the KMP plugin, which is why Android needs its own module.
- Versions live in `gradle/libs.versions.toml`. Material3 is versioned separately from Compose
  Multiplatform (`compose-material3`).

Inside `shared/src/commonMain/kotlin/eu/alsk/transmissionremote/`:

- `App.kt`: theme plus top-level navigation. A `rememberSaveable` enum switches
  Splash → Connection inside a `Crossfade`. There is no navigation library.
- `<feature>/` (`connection/`, `splash/`): one root package per feature (ADR-0006), holding its
  ViewModel and UI state, with its composables in `<feature>/views/`. A screen is a stateful
  `XxxScreen(viewModel = viewModel { … })` that collects a `StateFlow` and passes the state and
  callbacks to a stateless `XxxContent` in the same file.
  ViewModels come from `org.jetbrains.androidx.lifecycle`, and form state is an immutable data
  class with computed validation properties.
- `theme/Theme.kt`: Material 3 light/dark colour schemes (red seed). The Figma design file uses
  the same palette as colour variables.
- `data/`: `ConnectionRepository` is an in-memory singleton, so saved connections don't persist
  yet. There is no networking code yet.

## Conventions

The patterns this codebase follows are recorded as ADRs in `docs/adr/` (index: `docs/README.md`).
Follow them. Keep them current as decisions are made: when a new pattern is agreed, record it in
the same change as a new ADR from `docs/adr/template.md`, named
`ADR-<number>-<title-in-kebab-case>.md`. When a pattern changes, supersede or amend the affected
ADR, and fix example paths in other ADRs and in this file.
In short:

- **ADR-0001:** each non-trivial Composable gets its own file, with its `@Preview` functions in the
  same file. Preview sample data goes in a file-level `private val dummy<ElementName>`. A screen's
  container `XxxScreen` (takes the ViewModel, no previews) and its renderer `XxxContent` (takes UI
  state, `private`, has the previews) share one file, `XxxScreen.kt`.
- **ADR-0002:** everything not called from another Gradle module or from Swift is `internal`, or
  `private` if it's used in a single file. Today the only public declarations are `App()` and
  `MainViewController()`. This prepares for splitting `shared` into several modules later.
- **ADR-0003:** a Composable with more than two data parameters (not counting callbacks, `Modifier`
  or a ViewModel) takes a `<FunctionName>State` data class declared at the top of its file, e.g.
  `FormFieldState` in `FormField.kt`.
- **ADR-0005:** a component (ViewModel, view, use case, …) that fits in one file is one file. If it
  has parts used only by it, it becomes a folder named after it in camelCase, e.g.
  `connectionScreen/` — but only once its package holds a second component. Until then the parts
  sit directly in the package (today: `connection/views/ConnectionScreen.kt` + `FormField.kt` +
  `PasswordField.kt`). A part used by a second component moves up and becomes a component itself.
- **ADR-0006:** the root package holds `App.kt` and one package per feature, named after the feature
  (`connection/`, `splash/`). There is no `ui/`. A feature keeps its ViewModel at its root and its
  composables in `views/`. A ViewModel's UI state class gets its own file
  (`ConnectionFormState.kt`). Code shared by several features goes in role-named root packages
  (`theme/`, `data/`). ADR-0006 supersedes ADR-0004.
- **ADR-0007:** the Gradle daemon runs on Amazon Corretto 25, set in
  `gradle/gradle-daemon-jvm.properties` with direct, version-pinned `corretto.aws` download links.
  No foojay: don't re-add the resolver plugin or run `updateDaemonJvm`.
- **ADR-0008:** Composables take exactly the data they render plus their callbacks (interface
  segregation). Don't pass a ViewModel state or model a view only partly uses. Give it a
  display-ready `XxxState` instead (e.g. `ConnectionContentState`), mapped next to `XxxScreen`.

## Transmission RPC

`docs/reference/transmission-rpc-api.md` is the API reference to build the network layer from.
Key points:

- **Two wire formats:** Transmission 4.1+ speaks JSON-RPC 2.0 with `snake_case` names; ≤ 4.0
  speaks the legacy format with `kebab-case`/`camelCase` names. Support both, and pick the format
  based on whether the first HTTP 409 response carries an `X-Transmission-Rpc-Version` header.
- **Session id:** every request needs `X-Transmission-Session-Id`. On 409, store the new value and
  resend the request.
- **Web build:** browsers need CORS headers that Transmission doesn't send, so the web build only
  works behind a proxy or from the same origin.
