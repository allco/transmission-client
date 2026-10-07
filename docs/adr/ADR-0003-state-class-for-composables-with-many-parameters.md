# ADR-0003: Composables with more than two data parameters take a `<FunctionName>State`

- **Status:** Accepted
- **Date:** 2026-10-07

## Context

Composables collect parameters as they grow: a label, a value, a placeholder, an error, a keyboard
type. Long parameter lists are hard to read at the call site, and they make previews repeat the
same arguments. They also hide which values belong together.

## Decision

- A **data parameter** is any parameter that isn't a callback (a function type), a `Modifier` or
  a ViewModel.
- A Composable with **more than two** data parameters gets a `<FunctionName>State` class, e.g.
  `FormField` → `FormFieldState`. The Composable takes `state: <FunctionName>State` in their place.
  Callbacks and `modifier` stay as separate parameters.
- The state class is declared **at the top of the Composable's file**, before the Composable. It
  is:
  - an immutable `data class` with `val` properties, with defaults for optional values;
  - as visible as the Composable, `internal` or `private` (see
    [ADR-0002](ADR-0002-internal-by-default.md)).
- Preview data for such a Composable is a set of `dummy<FunctionName>State…` values (see
  [ADR-0001](ADR-0001-one-composable-per-file.md)), e.g. `dummyFormFieldState` and
  `dummyFormFieldStateWithError`.
- An existing state object counts as one data parameter. For example, `ConnectionContent` takes
  the ViewModel's `ConnectionFormState` and a `SnackbarHostState`, which makes two, so it doesn't
  need its own class.

Example, in `ui/connection/FormField.kt`:

```kotlin
internal data class FormFieldState(
    val label: String,
    val value: String,
    val placeholder: String? = null,
    val error: String? = null,
    val keyboardType: KeyboardType = KeyboardType.Text,
)

@Composable
internal fun FormField(state: FormFieldState, onValueChange: (String) -> Unit, modifier: Modifier = Modifier)
```

## Consequences

- Call sites and previews read as "what to show" plus "what to do". Preview states are named
  values that can be reused.
- One more small class per larger Composable.
- Adding a third data parameter to a Composable means introducing its state class in the same
  change.
- State classes are plain data, so they can later be built in a ViewModel or a mapper and tested
  without Compose.
