# ADR-0008: Composables take exactly the data they use (interface segregation)

- **Status:** Superseded by [ADR-0010](ADR-0010-composable-conventions.md). Amended 2026-10-08: the ViewModel makes the
  content state, and a one-time signal is an event.
- **Date:** 2026-10-08

## Context

The easy way to give data to a Composable is to pass it whatever object is at hand. Examples are
the whole state of the ViewModel, a domain model or a repository result. The Composable then
depends on fields that it never reads. It recomposes when these fields change. It also starts to
derive things itself, for example "should this error be visible yet?" or "build the URL".

Previews also become harder, because they must build the whole upstream object and know its rules.

## Decision

- The parameters of every Composable are exactly the data that it shows plus the callbacks that it
  fires. There are no other parameters. This is the interface segregation principle, applied to UI.
- **No pass-through objects.** Do not pass a ViewModel state, domain model or data class to a
  Composable that uses only part of it. Also do not pass such an object if the Composable must
  apply logic to it to get what it shows. Instead, give the Composable its own state, ready to show:
  - for the renderer of a screen, `XxxContent`, a `XxxContentState`.
  - in general, the `<FunctionName>State` from
    [ADR-0003](ADR-0003-state-class-for-composables-with-many-parameters.md).
- "Ready to show" means that the Composable does no business logic:
  - An error that the Composable should not show yet is already `null`.
  - The state already contains derived values, such as the RPC URL preview. A derived value is
    `null` when the Composable should not show it.
- The ViewModel makes the state of `XxxContent` directly. For example, `ConnectionViewModel`
  exposes `StateFlow<ConnectionContentState>`. There is no other state class between the ViewModel
  and the Composable.
- The ViewModel keeps its working data private. Examples are the text that the user typed and the
  validation rules.
- Only the container, `XxxScreen`, touches the ViewModel. It collects the state and gives the state
  to `XxxContent`.
- A one-time signal is an event, not a state field. An example is "the ViewModel saved the
  connection":
  - The ViewModel sends the event as a `XxxEvent` through a `Flow`.
  - `XxxScreen` collects the events and reacts to them, for example with a snackbar.
- Pass callbacks individually: one callback for each user action that the Composable can trigger.

Example: earlier, `ConnectionContent` took a `ConnectionFormState`. `ConnectionContent` never read
the `saved` flag of that state. It used the `showErrors` flag to decide which errors to show. Now
`ConnectionViewModel` makes a `ConnectionContentState`: the field values, the errors to show, and
`rpcUrl: String?`. When `ConnectionViewModel` saves the connection, it sends
`ConnectionEvent.Saved`.

## Consequences

- Each Composable states its needs in its signature. It recomposes only when those values change.
- Previews build the Composable state directly. They do not need to know the upstream validation
  rules.
- The code has more small state classes. The ViewModel makes them with plain code. Thus a test can
  check the state without Compose.
- When an upstream model gets a new field, a Composable does not change unless it needs to show
  that field.
