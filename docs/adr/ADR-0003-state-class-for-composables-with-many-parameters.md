# ADR-0003: Composables with more than two data parameters take a `<FunctionName>State`

- **Status:** Superseded by [ADR-0010](ADR-0010-composable-conventions.md)
- **Date:** 2026-10-07

## Context

When Composables grow, they collect parameters: a label, a value, a placeholder, an error, a
keyboard type. Long parameter lists are hard to read at the call site. They make previews repeat
the same arguments. They also hide which values belong together.

## Decision

- A **data parameter** is any parameter that is not a callback (a function type), a `Modifier` or
  a ViewModel.
- A Composable with **more than two** data parameters gets a `<FunctionName>State` class, for
  example `FormField` → `FormFieldState`. The Composable takes `state: <FunctionName>State` instead
  of these data parameters. Callbacks and `modifier` stay as separate parameters.
- Declare the state class **at the top of the Composable's file**, before the Composable. The state
  class is:
  - an immutable `data class` with `val` properties, and with defaults for optional values.
  - as visible as the Composable: `internal` or `private` (see
    [ADR-0002](ADR-0002-internal-by-default.md)).
- The preview data for such a Composable is a set of `dummy<FunctionName>State…` values (see
  [ADR-0001](ADR-0001-one-composable-per-file.md)). Examples are `dummyFormFieldState` and
  `dummyFormFieldStateWithError`.
- An existing state object counts as one data parameter, but only if the Composable uses all of
  the object. See [ADR-0008](ADR-0008-composables-take-only-what-they-use.md). For this reason,
  `ConnectionContent` takes `ConnectionContentState`, which holds only the data that
  `ConnectionContent` shows.

Example, in `connection/views/FormField.kt`:

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
  values that you can reuse.
- Each larger Composable has one more small class.
- When a change adds a third data parameter to a Composable, the same change adds its state class.
- State classes are plain data. Thus a ViewModel or a mapper can build them later, and you can test
  them without Compose.
