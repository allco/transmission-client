# ADR-0008: Composables take exactly the data they use (interface segregation)

- **Status:** Accepted
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
- The code that maps the upstream state to the Composable state belongs to the owner of both:
  - Today this code is a `private fun UpstreamState.toXxxState()` in the file of the screen, next
    to `XxxScreen`.
  - It may move to the ViewModel when several Composables share it, or when it needs tests.
- Only the container, `XxxScreen`, touches the ViewModel. It reads one-off signals such as `saved`.
  It gives `XxxContent` only what `XxxContent` needs.
- Pass callbacks individually: one callback for each user action that the Composable can trigger.

Example: earlier, `ConnectionContent` took `ConnectionFormState`. It never read the `saved` flag of
that state. It used `showErrors` to decide itself which errors to show. Now it takes
`ConnectionContentState`: the field values, the errors to show, and `rpcUrl: String?`.
`ConnectionScreen` maps the form state to it with `ConnectionFormState.toContentState()`.

## Consequences

- Each Composable states its needs in its signature. It recomposes only when those values change.
- Previews build the Composable state directly. They do not need to know the upstream validation
  rules.
- The code has more small state classes and mapping functions. The mapping is plain code. Thus it
  is easy to test without Compose.
- When an upstream model gets a new field, a Composable does not change unless it needs to show
  that field.
