# ADR-1: Feature packages, file layout and blocks

- **Status:** Accepted
- **Date:** 2026-10-07

## Context

The source tree must answer two questions:

- **Where is a feature?** A feature package holds more than UI: its ViewModel and UI state now,
  and use cases and feature-only logic later. Thus a `ui/` package level would mislead. Each
  feature is also a likely future Gradle module ([ADR-2](ADR-2-internal-by-default.md)).
  The top level of the source tree must read as the list of features.
- **What belongs to what?** Some components fit in one file. Other components need helpers that
  nothing else uses: sub-Composables, mappers, utilities. When these helpers are next to unrelated
  files, a reader cannot tell what they belong to, or if a change to them is safe. A helper that a
  writer makes generic too early hides the opposite problem.

## Decision

### 1. The root package lists the features

The root package `eu.alsk.transmissionremote` holds `App.kt` and one package for each
**feature**. A feature is a part of the app that the user sees as a unit
(see [Features](../reference/features.md)). Each feature package has the name of the feature.

```
eu/alsk/transmissionremote/
├── App.kt                          entry point and top-level navigation
├── connection/                     feature            → later feature:connection
│   ├── ConnectionViewModel.kt      ViewModel
│   ├── ConnectionContentState.kt   the UI state that the ViewModel exposes
│   ├── ConnectionEvent.kt          the one-time events that the ViewModel sends
│   ├── data/                       the data of the feature
│   │   ├── ServerConnection.kt     the server configuration
│   │   └── ConnectionRepository.kt keeps the server configuration
│   └── views/                      one screen and no parts, so no component folder (section 3)
│       └── ConnectionScreen.kt     ConnectionScreen (container) + ConnectionContent (renderer)
└── splash/                         feature            → later feature:splash
    └── views/
        └── SplashScreen.kt
```

The shared UI is not in this tree. It is in the `:blocks:designsystem` module
([ADR-6](ADR-6-design-system-module.md)).

- There is no `ui/` package.
- Code that more than one feature uses is not a feature. Put it in a package at the root that has
  the name of its role, for example `rpc/` for the RPC client. Add a new role package only when a
  second feature needs the code. Today there is no role package.
- Shared UI is in the `:blocks:designsystem` module, not in a role package
  ([ADR-6](ADR-6-design-system-module.md)).
- Give a feature a name that tells what the user does, for example `connection` or `torrents`. A
  feature name must not be the same as a role name.

The diagram shows the allowed dependencies between the packages.

```mermaid
flowchart TD
    App[App.kt] --> connection
    App --> splash
    connection --> designsystem[":blocks:designsystem"]
    splash --> designsystem
```

`App.kt` connects the features.

### 2. The content of a feature package

A feature package `<feature>/` holds:

- the ViewModel of the feature, for example `ConnectionViewModel.kt`.
- the UI state that the ViewModel exposes, in its own file, for example
  `ConnectionContentState.kt`.
- the events that the ViewModel sends, in their own file, for example `ConnectionEvent.kt`.
- the use cases and other logic that only this feature uses.
- `data/`, with the data classes and the repositories that only this feature uses, for example
  `connection/data/ConnectionRepository.kt`. When a second feature needs a class of `data/`, move
  the class to a role package (section 1).
- `views/`, with all Composables of the feature and their `<FunctionName>State` classes
  ([ADR-3](ADR-3-composable-conventions.md)).

A feature without a ViewModel, like `splash`, has only `views/`.

The package names are `eu.alsk.transmissionremote.<feature>`,
`eu.alsk.transmissionremote.<feature>.data` and
`eu.alsk.transmissionremote.<feature>.views[.<component>]`.

### 3. A component is one file, or a folder

A component is a ViewModel, a UI element, a use case or a repository.

- If a component fits in one file, the component **is one file**. The file has the name of the
  component, for example `ConnectionViewModel.kt`.
- If you divide a component into parts, the component becomes a **folder that has the name of the
  component**:
  - The folder holds the main file of the component. It also holds each file that only this
    component uses: sub-Composables, `<FunctionName>State` classes, mappers, utilities.
  - A Kotlin package name starts with a lower-case letter. Thus the folder uses the component name
    in camelCase. Example: `ConnectionScreen` → `connectionScreen/ConnectionScreen.kt`, package
    `…views.connectionScreen`.
- **Exception: one component in the package.** While a package such as `views/` holds only one
  component, the parts of that component are directly in the package. They have no component
  folder. When a second component comes, move the parts of the first component into a folder with
  its name. The second component also gets a folder if it has parts.
- When a **second** component starts to use a part, move the part next to the components that use
  it. The part becomes a component: a file, or a folder if it has parts.
- This rule applies to all kinds of components, and it nests. A part that gets parts becomes a
  folder in the folder of its owner.
- [ADR-3](ADR-3-composable-conventions.md) applies in a folder too: one non-trivial
  Composable in each file.

Today each feature has one screen. Thus `views/` holds the screen and its parts directly (see the
tree in section 1). `ConnectionScreen` has no parts today, because it uses the components of the
`:blocks:designsystem` module. This example shows `connection/views/` after we add a second
screen, and after `ConnectionScreen` gets two parts:

```
connection/views/
├── connectionScreen/
│   ├── ConnectionScreen.kt
│   ├── UrlPreview.kt                   example: a part that only ConnectionScreen uses
│   └── TestResultBanner.kt             example: a part that only ConnectionScreen uses
└── ServerListScreen.kt                 one file. It becomes serverListScreen/ when it has parts.
```

### 4. Blocks: the modules in `blocks/`

The folder `blocks/` holds the blocks of the app. A block is a Gradle module of the app: a feature
or a shared part. The Gradle path of a block is `:blocks:<name>`.

```
blocks/
└── designsystem/        :blocks:designsystem (ADR-6)
shared/                  the features as packages; connects the blocks; builds the iOS framework
androidApp/ desktopApp/ webApp/ iosApp/
```

- Today `blocks/` holds one block: `designsystem`
  ([ADR-6](ADR-6-design-system-module.md)).
- We plan to move each feature and each shared part from `shared` to a block of its own, for
  example `:blocks:connection` or `:blocks:data`. Until then, the features are packages in
  `shared` (sections 1 to 3).
- A block keeps the package layout of sections 2 and 3. Its root package is the package of the
  feature or the role, for example `eu.alsk.transmissionremote.connection`.
- Give a block the name of its feature or its role, in camelCase.
- The entry-point modules (`androidApp`, `desktopApp`, `webApp`) and `iosApp` stay at the root.
  `shared` stays at the root, too.

## Consequences

- The root of the source tree lists the features. Each feature maps to a future
  `feature:<name>` module. The role packages map to `core:*` modules.
- A new feature gets a new root package. Do not add it as a sub-package of a feature.
- When a feature moves to a block, its packages do not change. Only the Gradle module changes.
- The tree shows ownership. A change to a file in the folder of a component has no effect on
  other components.
- The tree has no nesting until the nesting separates something.
- When you add a second component to a package, you also move the parts of the first component
  into its folder. This move is a rename plus import changes. In one module, this work is small.
- Reuse is a step that you do on purpose. When you move a part out of a folder, the move shows
  that other components now use the part.
