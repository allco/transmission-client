# ADR-0004: Feature packages keep the ViewModel at the root and composables in `views/`

- **Status:** Accepted, amended by [ADR-0005](ADR-0005-file-or-folder-per-component.md)
  (components with parts are folders inside `views/`)
- **Date:** 2026-10-07

## Context

With one file per Composable ([ADR-0001](ADR-0001-one-composable-per-file.md)), a feature package
fills up with small view files. The ViewModel and its UI state get lost among them, even though
they are what the feature's logic is built on. Each feature is also a likely future Gradle module
([ADR-0002](ADR-0002-internal-by-default.md)), so its boundaries should be visible in the tree.

## Decision

Each feature has its own package under `ui/`:

```
ui/
├── theme/Theme.kt
├── connection/
│   ├── ConnectionViewModel.kt      ViewModel + its UI state (ConnectionFormState)
│   └── views/
│       └── connectionScreen/       ConnectionScreen and its parts (ADR-0005)
│           ├── ConnectionScreen.kt ConnectionScreen (container) + ConnectionContent (renderer)
│           ├── FormField.kt
│           └── PasswordField.kt
└── splash/
    └── views/
        └── splashScreen/
            ├── SplashScreen.kt
            └── AppLogo.kt
```

- `ui/<feature>/` holds the feature's ViewModel and the UI state it exposes, e.g.
  `ConnectionViewModel.kt`. Non-UI logic used only by this feature also goes here.
- `ui/<feature>/views/` holds every Composable of the feature, one per file under ADR-0001, plus
  any `<FunctionName>State` classes they declare
  ([ADR-0003](ADR-0003-state-class-for-composables-with-many-parameters.md)). A view with parts
  is a folder of its own (ADR-0005). Packages are
  `eu.alsk.transmissionremote.ui.<feature>` and `eu.alsk.transmissionremote.ui.<feature>.views`.
- A feature without a ViewModel, like `splash`, has only `views/`.
- App-wide UI that isn't a feature stays in its own package under `ui/`, e.g. `ui/theme/`.
- Data and network code shared by features doesn't go under `ui/`. It lives in `data/` (later
  `core:*` modules).

## Consequences

- Opening a feature shows its logic first, and the views are grouped one level down.
- A feature package maps directly to a future `feature:<name>` module.
- `views` code imports from the parent package (`ui.<feature>.ConnectionViewModel`). Both are in
  the same Gradle module, so `internal` declarations stay visible.
