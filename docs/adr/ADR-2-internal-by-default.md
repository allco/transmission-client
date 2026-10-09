# ADR-0002: Everything that is not exposed is `internal`

- **Status:** Accepted
- **Date:** 2026-10-07

## Context

Today all app code lives in the single `shared` module. Later, we plan to split this module into
several Gradle modules, for example `core:network`, `core:data`, `feature:connection` and
`feature:torrents`. The default visibility in Kotlin is `public`. Thus everything that we write now
silently becomes API that other modules could depend on. Then the split would be a large job to
untangle the code, instead of a move.

`internal` means "visible inside this Gradle module only". In one module, `internal` costs nothing.
After a split, the compiler shows exactly which declarations must become public API.

## Decision

- All implementation details are `internal`. These details are:
  - screens and their content Composables
  - ViewModels
  - UI state classes
  - repositories
  - data models
  - the theme
  - helpers that more than one file uses.

  Use `private` for a declaration that only one file uses.
- Use `public` only for declarations that another Gradle module or a platform host actually calls.
  Write `public` as the default, with no modifier. Today, exactly two functions are `public`:

  | Declaration | Used by |
  |---|---|
  | `App()` in `shared/.../App.kt` | `androidApp`, `desktopApp`, `webApp` |
  | `MainViewController()` in `shared/src/iosMain/.../MainViewController.kt` | Swift, in `iosApp` |

- To make a declaration public, you need a reason: a caller in another module or in Swift. Add the
  declaration to the table above.
- When you extract a module, decide its public API on purpose. Only then promote the necessary
  declarations from `internal` to `public`.

## Consequences

- The public surface of `shared` is two functions. Thus the iOS framework header and the
  cross-module API stay small.
- To extract a module, you mostly move files. The compile errors list the declarations to promote.
- `internal` declarations are not visible from Swift. Anything that iOS code needs must be public
  by design.
- Tests in the test source sets of the same module can still see `internal` declarations.
- Optional next step: after a split, enable `explicitApi()` in the Kotlin block of each library
  module. The compiler then requires a visibility modifier on every public declaration. Thus
  nothing becomes public by accident.
