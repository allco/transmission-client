# ADR-0001: One non-trivial Composable per file, with its previews

- **Status:** Accepted
- **Date:** 2026-10-07

## Context

When a screen grows, its file collects private helper Composables. Such a file becomes long. Its
previews are then far from the code that they show, and the helpers are hard to find or reuse.
Previews are also the cheapest way to check the UI on every platform. But this is true only if each
Composable has its own previews.

## Decision

- Each non-trivial Composable lives in its own file. The file has the name of the Composable
  (`FormField` → `FormField.kt`).
- A Composable is trivial if it is a few lines of layout with no state and no logic. An example is
  a styled `Text` wrapper. A trivial Composable may stay in the file of the one Composable that
  uses it. If you are not sure, give the Composable its own file.
- The file also contains the `@Preview` functions of that Composable:
  - Use the common annotation `androidx.compose.ui.tooling.preview.Preview`, so that previews work
    for Android and desktop.
  - Wrap every preview in `AppTheme`. For a light and dark pair, pass `darkTheme = false/true`
    explicitly. `uiMode` has an effect only on Android previews.
  - Preview functions are `private`.
- Preview sample data is a file-level `private val dummy<ElementName>` next to the previews.
  Examples are `dummyConnectionContentState`, `dummyConnectionContentStateWithErrors` and
  `dummyPassword`. Previews use these values instead of inline literals.
- A screen is one element that has two Composables. Both Composables live in `XxxScreen.kt`:
  - `XxxScreen` is the container. It takes the ViewModel, collects the state of the ViewModel and
    connects the callbacks. It has **no previews**. A preview would need a real ViewModel and its
    dependencies. The preview would also show only the initial state of the ViewModel. The same is
    true for any Composable that takes a ViewModel.
  - `XxxContent` shows a UI state. It takes state and callbacks. It is `private`, because only
    `XxxScreen` calls it. Its previews cover the interesting states, including light and dark.

Example: `connection/views/` contains `ConnectionScreen.kt` (`ConnectionScreen` +
`ConnectionContent`), `FormField.kt` and `PasswordField.kt`.

## Consequences

- You can find each Composable by its file name. Its previews are directly below it.
- Helpers that more than one file uses must be `internal` instead of `private`. See
  [ADR-0002](ADR-0002-internal-by-default.md).
- The code has more files, and the files are smaller. The `views/` package of each feature keeps
  these files together (see [ADR-0006](ADR-0006-features-at-the-package-root.md)).
