# ADR-0006: Features are top-level packages, with the ViewModel at the root and composables in `views/`

- **Status:** Accepted
- **Date:** 2026-10-07
- **Supersedes:** [ADR-0004](ADR-0004-feature-package-layout.md)

## Context

ADR-0004 put every feature under a `ui/` package. A feature package holds more than UI, though:
its ViewModel and UI state now, and use cases and feature-only logic later. So a `ui/` level is
misleading, and it adds a level of nesting that buys nothing. Each feature is also a likely
future Gradle module ([ADR-0002](ADR-0002-internal-by-default.md)), so the top level of the source
tree should read as the list of features.

## Decision

The root package `eu.alsk.transmissionremote` contains `App.kt` and one package per **feature**,
named after the feature. Shared, non-feature code sits next to the features in packages named by
role:

```
eu/alsk/transmissionremote/
├── App.kt                          entry point and top-level navigation
├── connection/                     feature            → later feature:connection
│   ├── ConnectionViewModel.kt      ViewModel + its UI state (ConnectionFormState)
│   └── views/
│       └── connectionScreen/       a view with parts (ADR-0005)
│           ├── ConnectionScreen.kt ConnectionScreen (container) + ConnectionContent (renderer)
│           ├── FormField.kt
│           └── PasswordField.kt
├── splash/                         feature            → later feature:splash
│   └── views/
│       └── splashScreen/
│           ├── SplashScreen.kt
│           └── AppLogo.kt
├── theme/                          shared UI          → later core:designsystem
│   └── Theme.kt
└── data/                           shared data        → later core:data / core:network
```

- A feature package `<feature>/` holds:
  - the feature's ViewModel and the UI state it exposes, e.g. `ConnectionViewModel.kt`;
  - use cases and other logic used only by this feature;
  - `views/`, with every Composable of the feature: one per file
    ([ADR-0001](ADR-0001-one-composable-per-file.md)), plus their `<FunctionName>State` classes
    ([ADR-0003](ADR-0003-state-class-for-composables-with-many-parameters.md)). A view with parts
    is a folder of its own ([ADR-0005](ADR-0005-file-or-folder-per-component.md)).
- A feature without a ViewModel, like `splash`, has only `views/`.
- Packages: `eu.alsk.transmissionremote.<feature>` and
  `eu.alsk.transmissionremote.<feature>.views[.<component>]`.
- Code used by more than one feature isn't a feature. It goes in a role-named package at the root:
  `theme/` for shared UI, `data/` for data and networking. Add more of these only when a second
  feature needs the code.
- There is no `ui/` package.

## Consequences

- The root of the source tree lists the features, and each one maps to a future
  `feature:<name>` module. The role-named packages map to `core:*` modules.
- A new feature gets a new root package. It should not be added as a sub-package of an existing
  one.
- A feature name and a role name must not clash. Pick feature names that describe what the user
  does (`connection`, `torrents`, `settings`).
