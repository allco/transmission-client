# AGENTS.md

This file guides AI coding agents (Claude Code, Codex, Gemini CLI and others) when they work with
code in this repository.

## What this is

This project is a Kotlin Multiplatform + Compose Multiplatform client for a remote Transmission
BitTorrent daemon. It targets Android, iOS, Linux desktop (JVM) and the web (Wasm and JS). The
package is `eu.alsk.transmissionremote`.

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

- You can build and run the iOS app only on macOS. Open `iosApp/iosApp.xcodeproj`. Xcode runs
  `:shared:embedAndSignAppleFrameworkForXcode` and links the static `Shared` framework. To run the
  app on a device, set `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`.
- To run the desktop app headless (Xvfb), set `SKIKO_RENDER_API=SOFTWARE`. Xvfb has no GL.
- The project has no tests and no code linters yet.

## Architecture

- **`shared`** holds all UI and logic in `commonMain`. It is the only module with real code.
  Its Android target uses the AGP 9 `com.android.kotlin.multiplatform.library` plugin. A
  `kotlin { android { … } }` block configures this plugin. The old `androidLibrary { }` block is
  deprecated. The `shared` module also builds the iOS `Shared` framework.
- **`androidApp`**, **`desktopApp`** and **`webApp`** are thin entry points that only call `App()`.
  The iOS equivalent is `shared/src/iosMain/.../MainViewController.kt`. SwiftUI calls it from
  `iosApp/iosApp/ContentView.swift`. With AGP 9, you cannot combine `com.android.application` with
  the KMP plugin. For this reason, Android needs its own module.
- `gradle/libs.versions.toml` holds the versions. Material3 has its own version
  (`compose-material3`), separate from the Compose Multiplatform version.

The directory `shared/src/commonMain/kotlin/eu/alsk/transmissionremote/` holds these items:

- `App.kt` holds the theme and the top-level navigation. A `rememberSaveable` enum switches from
  Splash to Connection inside a `Crossfade`. The app uses no navigation library.
- `<feature>/` (`connection/`, `splash/`): each feature has one root package (ADR-0006). This
  package holds the ViewModel and the UI state of the feature. `<feature>/views/` holds the
  composables of the feature.
  A screen is a stateful `XxxScreen(viewModel = viewModel { … })`. It collects a `StateFlow` and
  passes the state and the callbacks to a stateless `XxxContent` in the same file.
  The ViewModels come from `org.jetbrains.androidx.lifecycle`. The form state is an immutable data
  class with computed validation properties.
- `theme/Theme.kt` holds the Material 3 light and dark colour schemes (red seed). The Figma design
  file uses the same palette as colour variables.
- In `data/`, `ConnectionRepository` is an in-memory singleton. Thus the app does not persist the
  saved connections yet. The project has no networking code yet.

## Conventions

The ADRs in `docs/adr/` record the patterns that this codebase follows. `docs/README.md` holds the
index of the ADRs. Obey the ADRs. Keep the ADRs current when we make decisions:

1. When we agree on a new pattern, record it as a new ADR in the same change.
2. Use `docs/adr/template.md` for the new ADR.
3. Name the new ADR `ADR-<number>-<title-in-kebab-case>.md`.
4. When a pattern changes, supersede or amend the affected ADR.
5. Then fix the example paths in the other ADRs and in this file.

This list gives a short summary of each ADR:

- **ADR-0001:** Put each non-trivial Composable in its own file. Put its `@Preview` functions in
  the same file. Put the preview sample data in a file-level `private val dummy<ElementName>`.
  Put the container `XxxScreen` of a screen and its renderer `XxxContent` in one file,
  `XxxScreen.kt`. `XxxScreen` takes the ViewModel and has no previews. `XxxContent` takes the UI
  state, is `private` and has the previews.
- **ADR-0002:** Make a declaration `internal` if no other Gradle module and no Swift code calls it.
  Make it `private` if only one file uses it. Today the only public declarations are `App()` and
  `MainViewController()`. This rule prepares `shared` so that we can split it into several modules
  later.
- **ADR-0003:** If a Composable has more than two data parameters, make it take a
  `<FunctionName>State` data class. Do not count callbacks, `Modifier` or a ViewModel as data
  parameters. Declare this class at the top of the file of the Composable, for example
  `FormFieldState` in `FormField.kt`.
- **ADR-0005:**
  - If a component (ViewModel, view, use case, …) fits in one file, keep it in one file.
  - If a component has parts that only this component uses, put the component and its parts in a
    folder. Name the folder after the component in camelCase, for example `connectionScreen/`.
  - Make this folder only when the package of the component holds a second component. Until then,
    put the parts directly in the package (today: `connection/views/ConnectionScreen.kt`,
    `FormField.kt` and `PasswordField.kt`).
  - If a second component uses a part, move the part higher in the folder tree. The part
    then becomes a component itself.
- **ADR-0006:**
  - The root package holds `App.kt` and one package for each feature. Name each feature package
    after the feature (`connection/`, `splash/`). The project has no `ui/` package.
  - Put the ViewModel of a feature at the root of the feature package. Put the composables of the
    feature in `views/`.
  - Put the UI state class and the event class of a ViewModel in their own files
    (`ConnectionContentState.kt`, `ConnectionEvent.kt`).
  - Put code that several features use in root packages that have the name of their role
    (`theme/`, `data/`).
  - ADR-0006 supersedes ADR-0004.
- **ADR-0007:** The Gradle daemon runs on Amazon Corretto 25. `gradle/gradle-daemon-jvm.properties`
  sets this JVM with direct `corretto.aws` download links that pin the version. Do not use foojay.
  Do not add the resolver plugin again. Do not run `updateDaemonJvm`.
- **ADR-0008:** A Composable takes exactly the data that it renders, plus its callbacks (interface
  segregation). Do not pass a model that the Composable uses only in part. The ViewModel makes the
  state of `XxxContent` directly (`ConnectionViewModel` → `ConnectionContentState`). Keep the
  working data of the ViewModel private. Send a one-time signal as an event (`ConnectionEvent`),
  not as a state field.
- **ADR-0009:** Use the `asd-ste100` skill to write all Markdown files, KDoc, code comments and
  commit messages in Simplified Technical English. Use the Strict mode for `AGENTS.md`, code
  comments and KDoc, and the STE-flavored mode for `README.md`, ADRs and `docs/reference/`. Before
  you commit, run `python3 -I .claude/skills/asd-ste100/scripts/ste-lint.py <file.md>` on each
  changed Markdown file.

## Transmission RPC

`docs/reference/transmission-rpc-api.md` is the API reference. Build the network layer from this
reference. The main points are:

- **Two wire formats:** Transmission 4.1+ uses JSON-RPC 2.0 with `snake_case` names. Transmission
  ≤ 4.0 uses the legacy format with `kebab-case`/`camelCase` names. Support both formats. Select
  the format according to one condition: whether the first HTTP 409 response has an
  `X-Transmission-Rpc-Version` header.
- **Session id:** Every request needs the `X-Transmission-Session-Id` header. If a response has
  the status 409, store the new value. Then send the request again.
- **Web build:** Browsers need CORS headers, but Transmission does not send these headers. Thus
  the web build works only behind a proxy or from the same origin.
