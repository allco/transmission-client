# ADR-0006: Features are top-level packages, with the ViewModel at the root and Composables in `views/`

- **Status:** Accepted. Amended 2026-10-08: the UI state class and the event class of a
  ViewModel get their own files.
- **Date:** 2026-10-07
- **Supersedes:** [ADR-0004](ADR-0004-feature-package-layout.md)

## Context

ADR-0004 put every feature under a `ui/` package. But a feature package holds more than UI: its
ViewModel and UI state now, and use cases and feature-only logic later. Thus a `ui/` level is
misleading. It also adds a level of nesting that gives no benefit. Each feature is also a likely
future Gradle module ([ADR-0002](ADR-0002-internal-by-default.md)). Thus the top level of the
source tree should read as the list of features.

## Decision

The root package `eu.alsk.transmissionremote` contains `App.kt` and one package for each
**feature**. Each feature package has the name of the feature. Shared code that is not a feature
sits next to the features, in packages named by role:

```
eu/alsk/transmissionremote/
├── App.kt                          entry point and top-level navigation
├── connection/                     feature            → later feature:connection
│   ├── ConnectionViewModel.kt      ViewModel
│   ├── ConnectionContentState.kt   the UI state that the ViewModel exposes
│   ├── ConnectionEvent.kt          the one-time events that the ViewModel sends
│   └── views/                      one screen, so its parts sit here directly (ADR-0005)
│       ├── ConnectionScreen.kt     ConnectionScreen (container) + ConnectionContent (renderer)
│       ├── FormField.kt
│       └── PasswordField.kt
├── splash/                         feature            → later feature:splash
│   └── views/
│       ├── SplashScreen.kt
│       └── AppLogo.kt
├── theme/                          shared UI          → later core:designsystem
│   └── Theme.kt
└── data/                           shared data        → later core:data / core:network
```

- A feature package `<feature>/` holds:
  - the ViewModel of the feature, for example `ConnectionViewModel.kt`.
  - the UI state that the ViewModel exposes, in a separate file, for example
    `ConnectionContentState.kt`.
  - the events that the ViewModel sends, in a separate file, for example `ConnectionEvent.kt`.
  - use cases and other logic that only this feature uses.
  - `views/`, with every Composable of the feature, one per file
    ([ADR-0001](ADR-0001-one-composable-per-file.md)), and with their `<FunctionName>State` classes
    ([ADR-0003](ADR-0003-state-class-for-composables-with-many-parameters.md)). When `views/` holds
    more than one component, a component with parts is a folder of its own
    ([ADR-0005](ADR-0005-file-or-folder-per-component.md)).
- A feature without a ViewModel, like `splash`, has only `views/`.
- The package names are `eu.alsk.transmissionremote.<feature>` and
  `eu.alsk.transmissionremote.<feature>.views[.<component>]`.
- Code that more than one feature uses is not a feature. It goes in a role-named package at the
  root: `theme/` for shared UI, `data/` for data and networking. Add more role-named packages only
  when a second feature needs the code.
- There is no `ui/` package.

## Consequences

- The root of the source tree lists the features. Each feature maps to a future `feature:<name>`
  module. The role-named packages map to `core:*` modules.
- A new feature gets a new root package. You should not add it as a sub-package of an existing
  feature.
- A feature name and a role name must not clash. Pick feature names that describe what the user
  does (`connection`, `torrents`, `settings`).
