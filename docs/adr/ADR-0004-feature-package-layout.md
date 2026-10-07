# ADR-0004: Feature packages keep the ViewModel at the root and Composables in `views/`

- **Status:** Superseded by [ADR-0006](ADR-0006-features-at-the-package-root.md) (ADR-0006 moved
  the features from `ui/` to the package root). Earlier,
  [ADR-0005](ADR-0005-file-or-folder-per-component.md) amended this ADR.
- **Date:** 2026-10-07

## Context

With one file per Composable ([ADR-0001](ADR-0001-one-composable-per-file.md)), a feature package
fills with small Composable files. It is hard to find the ViewModel and its UI state among these
files. But the ViewModel and its UI state are the base of the logic of the feature. Each feature is
also a likely future Gradle module ([ADR-0002](ADR-0002-internal-by-default.md)). Thus its
boundaries should be visible in the tree.

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

- `ui/<feature>/` holds the ViewModel of the feature and the UI state that the ViewModel exposes,
  for example `ConnectionViewModel.kt`. Non-UI logic that only this feature uses also goes here.
- `ui/<feature>/views/` holds every Composable of the feature, one per file as ADR-0001 says. It
  also holds the `<FunctionName>State` classes that these Composables declare
  ([ADR-0003](ADR-0003-state-class-for-composables-with-many-parameters.md)). A Composable with
  parts is a folder of its own (ADR-0005). The packages are
  `eu.alsk.transmissionremote.ui.<feature>` and `eu.alsk.transmissionremote.ui.<feature>.views`.
- A feature without a ViewModel, like `splash`, has only `views/`.
- App-wide UI that is not a feature stays in its own package under `ui/`, for example `ui/theme/`.
- Data and network code that features share does not live under `ui/`. It lives in `data/` (later
  in `core:*` modules).

## Consequences

- When you open a feature, you see its logic first. Its Composables are together one level lower.
- A feature package maps directly to a future `feature:<name>` module.
- Code in `views` imports from the parent package (`ui.<feature>.ConnectionViewModel`). Both
  packages are in the same Gradle module. Thus `internal` declarations stay visible.
