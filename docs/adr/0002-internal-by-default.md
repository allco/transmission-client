# 0002. Everything not exposed is `internal`

- **Status:** Accepted
- **Date:** 2026-10-07

## Context

Today all app code lives in the single `shared` module. We plan to split it into several Gradle
modules later, for example `core:network`, `core:data`, `feature:connection` and
`feature:torrents`. Kotlin's default visibility is `public`, so everything written now silently
becomes API that other modules could depend on. That would make the split a large untangling job
instead of a move.

`internal` means "visible inside this Gradle module only". In one module it costs nothing. After
a split, the compiler shows exactly which declarations have to become public API.

## Decision

- All implementation details are `internal`: screens and their content composables, ViewModels,
  UI state classes, repositories, data models, theme, and helpers shared across files. Use
  `private` where something is used in one file only.
- `public`, written as the default with no modifier, is reserved for what another Gradle module
  or a platform host actually calls. Today that is exactly two functions:

  | Declaration | Used by |
  |---|---|
  | `App()` in `shared/.../App.kt` | `androidApp`, `desktopApp`, `webApp` |
  | `MainViewController()` in `shared/src/iosMain/.../MainViewController.kt` | Swift, in `iosApp` |

- Making something public needs a reason: a caller in another module or in Swift. Add it to the
  table above.
- When a module is split out, decide its public API on purpose. Only then promote the needed
  declarations from `internal` to `public`.

## Consequences

- The public surface of `shared` is two functions, so the iOS framework header and the
  cross-module API stay small.
- Extracting a module is mostly moving files. Compile errors list what needs promoting.
- `internal` declarations are not visible from Swift. Anything iOS code needs must be public by
  design.
- Tests in the same module's test source sets can still see `internal` declarations.
- Optional follow-up: enable `explicitApi()` in the Kotlin block of each library module after a
  split. The compiler then requires a visibility modifier on every public declaration, so nothing
  becomes public by accident.
