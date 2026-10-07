# ADR-0005: A component is one file, or a folder named after it when it has parts

- **Status:** Accepted. Amended 2026-10-07: no component folder while a package has only one
  component.
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
    camelCase. Kotlin's naming conventions allow camelCase package names. Example:
    `ConnectionScreen` → `connectionScreen/ConnectionScreen.kt`, package `…views.connectionScreen`.
- **Exception: only one component in the package.** While a package such as `views/` holds a
  single component, that component's parts sit directly in the package, without a component
  folder. When a second component arrives, move the first one's parts into a folder named after
  it. The second component also gets a folder if it has parts.
- A part that becomes used by a **second** component moves up, next to the components that use
  it, and becomes a component of its own (a file, or a folder if it has parts).
- This applies to every kind of component and nests: a part that itself grows parts becomes a
  folder inside its owner's folder.
- [ADR-0001](ADR-0001-one-composable-per-file.md) still applies inside a folder: one non-trivial
  Composable per file.

Example: today each feature has one screen, so `views/` holds the screen and its parts directly:

```
connection/
├── ConnectionViewModel.kt              fits in one file
└── views/                              only one screen → no component folder yet
    ├── ConnectionScreen.kt
    ├── FormField.kt                    used only by ConnectionScreen
    └── PasswordField.kt                used only by ConnectionScreen
splash/views/
├── SplashScreen.kt
└── AppLogo.kt                          used only by SplashScreen
```

When a second screen is added to `connection`, for example a server list:

```
connection/views/
├── connectionScreen/
│   ├── ConnectionScreen.kt
│   ├── FormField.kt
│   └── PasswordField.kt
└── ServerListScreen.kt                 one file; becomes serverListScreen/ once it has parts
```

## Consequences

- The tree shows ownership. Everything in a component's folder can change without affecting
  other components.
- No nesting until it separates something. A package with one component doesn't need a folder
  to say what belongs to it.
- Adding a second component to a package includes moving the first one's parts into its folder.
  That's a rename plus import updates, which is cheap inside one module.
- Reuse is a deliberate step: moving a part out of a folder signals that it's now shared.
