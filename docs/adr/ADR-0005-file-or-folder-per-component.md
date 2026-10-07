# ADR-0005: A component is one file, or a folder named after it when it has parts

- **Status:** Accepted
- **Date:** 2026-10-07

## Context

Code is organised around components: a ViewModel, a UI element, a use case, a repository. Some fit
in one file. Others need helpers, such as sub-views, mappers or utilities, that nothing else uses.
If those helpers sit next to unrelated files, it isn't clear what they belong to or whether they
are safe to change. Making them generic too early hides the opposite problem.

## Decision

- If a component fits in one file, it **is one file**, named after it, e.g.
  `ConnectionViewModel.kt`.
- If a component is split into parts, it becomes a **folder named after the component**:
  - The folder holds the component's main file plus every file used only by that component:
    sub-composables, `<FunctionName>State` classes, mappers, utilities.
  - Kotlin package names are lower-case first, so the folder uses the component name in
    camelCase. Kotlin's naming conventions allow camelCase package names.
    `ConnectionScreen` → `connectionScreen/ConnectionScreen.kt`, package
    `…views.connectionScreen`.
- A part that becomes used by a **second** component moves up, next to the components that use
  it, and becomes a component of its own (a file, or a folder if it has parts).
- This applies to every kind of component and nests: a part that itself grows parts becomes a
  folder inside its owner's folder.
- [ADR-0001](ADR-0001-one-composable-per-file.md) still applies inside a folder: one non-trivial
  Composable per file.

Example: `FormField` and `PasswordField` are used only by `ConnectionScreen`, and `AppLogo` only
by `SplashScreen`:

```
ui/connection/
├── ConnectionViewModel.kt              fits in one file
└── views/
    └── connectionScreen/               ConnectionScreen has parts
        ├── ConnectionScreen.kt
        ├── FormField.kt
        └── PasswordField.kt
ui/splash/views/
└── splashScreen/
    ├── SplashScreen.kt
    └── AppLogo.kt
```

## Consequences

- The tree shows ownership. Everything in a component's folder can change without affecting
  other components.
- Reuse is a deliberate step: moving a part out of a folder signals that it's now shared.
- Package names follow the folders, so moving a part changes its imports. That's cheap inside one
  module.
- Deeper paths for components with parts.
