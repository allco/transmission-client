# ADR-0005: A component is one file, or a folder named after it when it has parts

- **Status:** Accepted. Amended 2026-10-07: no component folder while a package has only one
  component.
- **Date:** 2026-10-07

## Context

We organise code around components: a ViewModel, a UI element, a use case, a repository. Some
components fit in one file. Other components need helpers that nothing else uses, such as
sub-Composables, mappers or utilities. If these helpers sit next to unrelated files, it is not clear
what they belong to. It is also not clear whether they are safe to change. Making them generic too
early hides the opposite problem.

## Decision

- If a component fits in one file, it **is one file**. The file has the name of the component, for
  example `ConnectionViewModel.kt`.
- If you split a component into parts, the component becomes a **folder named after the
  component**:
  - The folder holds the main file of the component. It also holds every file that only that
    component uses: sub-Composables, `<FunctionName>State` classes, mappers, utilities.
  - Kotlin package names start with a lower-case letter. Thus the folder uses the component name in
    camelCase. The Kotlin naming conventions allow camelCase package names. Example:
    `ConnectionScreen` → `connectionScreen/ConnectionScreen.kt`, package `…views.connectionScreen`.
- **Exception: only one component in the package.** While a package such as `views/` holds a
  single component, the parts of that component sit directly in the package. They have no
  component folder. When a second component arrives, move the parts of the first component into a
  folder named after it. The second component also gets a folder if it has parts.
- When a **second** component starts to use a part, the part moves next to the components that use
  it. The part becomes a component of its own: a file, or a folder if it has parts.
- This rule applies to every kind of component, and it nests. A part that gets parts of its own
  becomes a folder inside the folder of its owner.
- [ADR-0010](ADR-0010-composable-conventions.md) still applies inside a folder: one non-trivial
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

When we add a second screen to `connection`, for example a server list:

```
connection/views/
├── connectionScreen/
│   ├── ConnectionScreen.kt
│   ├── FormField.kt
│   └── PasswordField.kt
└── ServerListScreen.kt                 one file; becomes serverListScreen/ once it has parts
```

## Consequences

- The tree shows ownership. Everything in the folder of a component can change with no effect on
  other components.
- The tree has no nesting until the nesting separates something. A package with one component
  does not need a folder to show what belongs to that component.
- When you add a second component to a package, you also move the parts of the first component
  into its folder. This move is a rename plus import updates. Inside one module, this work is
  cheap.
- Reuse is a deliberate step. When you move a part out of a folder, this signals that the part is
  now shared.
