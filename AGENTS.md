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
./gradlew :blocks:designsystem:compileKotlinJvm                 # quick compile check of the design system
./gradlew :blocks:networkclient:jvmTest                         # tests of the network client
./gradlew :shared:compileKotlinIosSimulatorArm64                # iOS klib compiles on Linux too
```

- You can build and run the iOS app only on macOS. Open `iosApp/iosApp.xcodeproj`. Xcode runs
  `:shared:embedAndSignAppleFrameworkForXcode` and links the static `Shared` framework. To run the
  app on a device, set `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`.
- To run the desktop app headless (Xvfb), set `SKIKO_RENDER_API=SOFTWARE`. Xvfb has no GL.
- Only `:blocks:networkclient` has tests. The project has no code linters yet.

## Architecture

- **`shared`** holds the features and the logic in `commonMain`. Its Android target uses the
  AGP 9 `com.android.kotlin.multiplatform.library` plugin. A `kotlin { android { … } }` block
  configures this plugin. The old `androidLibrary { }` block is deprecated. The `shared` module
  also builds the iOS `Shared` framework.
- **`blocks/`** holds the blocks: the Gradle modules of the app, for features and shared parts.
  The Gradle path of a block is `:blocks:<name>` (ADR-1).
  - **`blocks/designsystem`** (`:blocks:designsystem`) holds the design system: the theme, the
    tokens, the icons and the generic components (ADR-6). `shared` uses it. The Figma file is the
    source of its tokens.
  - **`blocks/networkclient`** (`:blocks:networkclient`) sends the RPC requests to the server:
    Basic authentication, the session id (HTTP 409) and the wire format (ADR-7).
  - Later, each feature moves from `shared` to a block of its own.
- **`androidApp`**, **`desktopApp`** and **`webApp`** are thin entry points that only call `App()`.
  The iOS equivalent is `shared/src/iosMain/.../MainViewController.kt`. SwiftUI calls it from
  `iosApp/iosApp/ContentView.swift`. With AGP 9, you cannot combine `com.android.application` with
  the KMP plugin. For this reason, Android needs its own module.
- `gradle/libs.versions.toml` holds the versions. Material3 has its own version
  (`compose-material3`), separate from the Compose Multiplatform version.

The directory `shared/src/commonMain/kotlin/eu/alsk/transmissionremote/` holds these items:

- `App.kt` applies `AppTheme` from `:blocks:designsystem` and holds the top-level navigation. A
  `rememberSaveable` enum switches from Splash to Connection inside a `Crossfade`. The app uses no
  navigation library.
- `<feature>/` (`connection/`, `splash/`): each feature has one root package (ADR-1). This
  package holds the ViewModel and the UI state of the feature. `<feature>/views/` holds the
  composables of the feature.
  A screen is a stateful `XxxScreen(viewModel = viewModel { … })`. It collects a `StateFlow` and
  passes the state and the callbacks to a stateless `XxxContent` in the same file.
  The ViewModels come from `org.jetbrains.androidx.lifecycle`. A ViewModel keeps the text that
  the user typed and the validation rules private. It exposes the content state
  (`ConnectionContentState`) and sends events (`ConnectionEvent`).
- In `connection/data/`, `ConnectionRepository` is an in-memory singleton. Thus the app does
  not persist the saved connections yet. No feature uses `:blocks:networkclient` yet.

## Conventions

Update the docs as you write the code. A change that changes the behaviour, the structure or a
rule also changes the docs in the same commit:

- `AGENTS.md`, when a command, a module, a package or a rule changes.
- The ADR that covers the subject (see below).
- `docs/reference/features.md`, when the status or the package of a feature changes.
- `docs/reference/design-system.md`, when a token or a component changes.
- `docs/README.md`, when you add or remove a document.

Do not leave a doc update for a later change.

The ADRs in `docs/adr/` record the patterns that this codebase follows. `docs/README.md` holds the
index of the ADRs, in groups. Obey the ADRs. Keep the ADRs current when we make decisions:

1. When we agree on a new pattern, find the ADR that covers the subject. Add the pattern to that
   ADR.
2. If no ADR covers the subject, make a new ADR from `docs/adr/template.md`. Name it
   `ADR-<number>-<title-in-kebab-case>.md`. Add it to a group in `docs/README.md`.
3. When a pattern changes, edit the ADR in place. When a pattern no longer applies, delete it.
   Do not keep superseded ADRs. The git history keeps the old versions.
4. Then fix the links and the example paths in the other ADRs, in `docs/reference/` and in this
   file.

This list gives a short summary of each ADR, in groups:

**Code structure**

- **ADR-1:** Feature packages, file layout and blocks.
  - Put a new Gradle module of the app in `blocks/`. Give it the Gradle path `:blocks:<name>`.
    Write the name in lower case with no separators (`networkclient`).
  - The root package holds `App.kt` and one package for each feature. Name each feature package
    after the feature (`connection/`, `splash/`). The project has no `ui/` package.
  - Put the ViewModel of a feature at the root of the feature package. Put the UI state class and
    the event class of the ViewModel in their own files (`ConnectionContentState.kt`,
    `ConnectionEvent.kt`). Put the Composables of the feature in `views/`.
  - Put code that several features use in root packages that have the name of their role
    (for example `format/`). Put shared UI in the `:blocks:designsystem` module.
  - Put the data classes and the repositories that only one feature uses in `<feature>/data/`
    (`connection/data/`). Move a class to a role package when a second feature needs it.
  - If a component (ViewModel, view, use case, …) fits in one file, keep it in one file.
  - If a component has parts that only this component uses, put the component and its parts in a
    folder. Name the folder after the component in camelCase, for example `connectionScreen/`.
  - Make this folder only when the package of the component holds a second component. Until then,
    put the parts directly in the package.
  - If a second component uses a part, move the part higher in the folder tree. The part then
    becomes a component itself.
- **ADR-2:** Make a declaration `internal` if no other Gradle module and no Swift code calls it.
  Make it `private` if only one file uses it. Today the only public declarations are `App()` and
  `MainViewController()`. This rule prepares `shared` so that we can split it into several modules
  later.

**UI**

- **ADR-3:** Composable conventions.
  - Put each non-trivial Composable in its own file. Put its `@Preview` functions in the same
    file. Put the preview sample data in a file-level `private val dummy<ElementName>`.
  - For a dialog, a menu or a modal sheet, put the visible panel in a `private` Composable.
    Preview the panel, because the preview tools do not draw popup windows.
  - Put the container `XxxScreen` of a screen and its renderer `XxxContent` in one file,
    `XxxScreen.kt`. `XxxScreen` takes the ViewModel and has no previews. `XxxContent` takes the
    UI state, is `private` and has the previews.
  - A Composable takes exactly the data that it renders, plus its callbacks (interface
    segregation). Do not pass a model that the Composable uses only in part.
  - Make each config value (label, placeholder, keyboard type, icon) a plain parameter. Do not
    put config values in a state class.
  - If a Composable has more than two state values (values that change at runtime), make it take
    a `<FunctionName>State` data class. Declare this class at the top of the file of the
    Composable. The ViewModel or the parent makes it. `TextField` in `:blocks:designsystem` has
    two state values, `value` and `error`, so it has no state class.
  - The ViewModel makes the state of `XxxContent` directly (`ConnectionViewModel` →
    `ConnectionContentState`). Keep the working data of the ViewModel private. Send a one-time
    signal as an event (`ConnectionEvent`), not as a state field.

- **ADR-6:** The `:blocks:designsystem` module holds the design system.
  - Use its components (`Button`, `TextField`, `TopAppBar`, …) and its tokens (`AppTheme.colors`,
    `Spacing`, `Radius`, `AppIcons`). Do not use a Material 3 component when the module has one.
    Do not write colour values in feature code.
  - Keep the tokens equal to the page "Design System" of the Figma file. Change both in the same
    change.
  - Put a component that shows the data of one feature in the `views/` package of that feature,
    not in the module.
  - To add a component, add it to Figma, to `component/` with previews, and to
    `docs/reference/design-system.md`.

**Network**

- **ADR-7:** `:blocks:networkclient` sends all RPC requests. Do not send HTTP requests to the
  server from a feature.
  - It uses the Ktor client. The Ktor `Auth` plugin sends Basic authentication on the first
    request. A Ktor plugin handles HTTP 409: it stores the session id and sends the request again,
    one time.
  - `NetworkClient.format()` finds the wire format from the first HTTP 409.
    `call(method, params)` sends a request in that format.
  - The failures are the subclasses of `NetworkClientException`.
  - Test the block with the Ktor `MockEngine`.

**Build**

- **ADR-4:** The Gradle daemon runs on Amazon Corretto 25. `gradle/gradle-daemon-jvm.properties`
  sets this JVM with direct `corretto.aws` download links that pin the version. Do not use foojay.
  Do not add the resolver plugin again. Do not run `updateDaemonJvm`.

**Docs**

- **ADR-5:** Use the `asd-ste100` skill to write all Markdown files, KDoc, code comments and
  commit messages in Simplified Technical English. Use the Strict mode for `AGENTS.md`, code
  comments and KDoc, and the STE-flavored mode for `README.md`, ADRs and `docs/reference/`. Before
  you commit, run `python3 -I .claude/skills/asd-ste100/scripts/ste-lint.py <file.md>` on each
  changed Markdown file. Use a Mermaid diagram when the subject is a flow, a sequence, a set of
  states or a structure. `docs/reference/documentation.md` gives the procedure and the words of
  this project.

`docs/reference/features.md` lists the features of the app, with the status and the package of
each feature. Update this file when you add a feature or change the status of a feature.

`docs/proposals/` holds ideas that the team has not decided yet. Do not implement an open proposal.
When the team accepts a proposal, record the decision in an ADR.

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
