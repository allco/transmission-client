# ADR-0001: One non-trivial Composable per file, with its previews

- **Status:** Accepted
- **Date:** 2026-10-07

## Context

Screens grow by accumulating private helper composables in one file. Such a file becomes long,
its previews end up far from the code they show, and helpers are hard to find or reuse. Previews
are also the cheapest way to check UI on every platform, but only if each piece has its own.

## Decision

- Each non-trivial Composable lives in its own file, named after it (`FormField` →
  `FormField.kt`).
- A Composable counts as trivial, and may stay in the file of the one Composable that uses it, if
  it is a few lines of layout with no state and no logic, e.g. a styled `Text` wrapper. When in
  doubt, give it its own file.
- The file also contains that Composable's `@Preview` functions:
  - Use the common annotation `androidx.compose.ui.tooling.preview.Preview`, so previews work for
    Android and desktop.
  - Wrap every preview in `AppTheme`. For a light/dark pair, pass `darkTheme = false/true`
    explicitly; `uiMode` only affects Android previews.
  - Preview functions are `private`.
- Preview sample data is a file-level `private val dummy<ElementName>` next to the previews (e.g.
  `dummyConnectionFormState`, `dummyConnectionFormStateWithErrors`, `dummyPassword`). Previews use
  these values instead of inline literals.
- A screen is one element made of two Composables, and both live in `XxxScreen.kt`:
  - `XxxScreen` is the container. It takes the ViewModel, collects its state and wires callbacks.
    It has **no previews**: a preview would need a real ViewModel and its dependencies, and would
    show only the ViewModel's initial state. The same holds for any Composable that takes a
    ViewModel.
  - `XxxContent` renders a UI state. It takes state and callbacks, and is `private` because only
    `XxxScreen` calls it. Its previews cover the interesting states, including light and dark.

Example: `ui/connection/` has `ConnectionScreen.kt` (`ConnectionScreen` + `ConnectionContent`),
`FormField.kt` and `PasswordField.kt`.

## Consequences

- Each Composable can be found by file name, and its previews sit right below it.
- Helpers used by several files must be `internal` instead of `private`. See
  [ADR-0002](ADR-0002-internal-by-default.md).
- More, smaller files. Feature packages (`ui/<feature>/`) keep them grouped.
