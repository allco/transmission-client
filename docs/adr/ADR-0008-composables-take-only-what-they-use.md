# ADR-0008: Composables take exactly the data they use (interface segregation)

- **Status:** Accepted
- **Date:** 2026-10-08

## Context

The easy way to feed a Composable is to pass it whatever object is at hand: the ViewModel's whole
state, a domain model, a repository result. The Composable then depends on fields it never reads,
and it recomposes when they change. It also starts deriving things itself, such as "should this
error be visible yet?" or "build the URL". Previews get harder too, because they must build the
whole upstream object and know its rules.

## Decision

- Every Composable's parameters are exactly the data it renders plus the callbacks it fires.
  Nothing more. This is the interface segregation principle applied to UI.
- **No pass-through objects.** Don't pass a ViewModel state, domain model or data class into a
  view if the view uses only part of it, or has to apply logic to it to get what it shows. Give
  the view its own state in display-ready form instead:
  - for a screen's renderer, `XxxContent`, a `XxxContentState`;
  - in general, the `<FunctionName>State` from
    [ADR-0003](ADR-0003-state-class-for-composables-with-many-parameters.md).
- Display-ready means the view does no business logic:
  - Errors that shouldn't be shown yet are already `null`.
  - Derived values, such as the RPC URL preview, are already computed. They're `null` when they
    shouldn't be shown.
- Mapping from upstream state to the view state belongs to whoever owns both:
  - Today that's a `private fun UpstreamState.toXxxState()` in the screen's file, next to
    `XxxScreen`.
  - It may move to the ViewModel once several views share it, or when it needs testing.
- Only the container, `XxxScreen`, touches the ViewModel. It reads one-off signals such as
  `saved`, and passes on only what `XxxContent` needs.
- Callbacks are passed individually, one per user action the Composable can trigger.

Example: `ConnectionContent` used to take `ConnectionFormState`. It never read that state's
`saved` flag, and it decided error visibility from `showErrors` itself. It now takes
`ConnectionContentState`: field values, the errors to show, and `rpcUrl: String?`.
`ConnectionScreen` maps the form state to it with `ConnectionFormState.toContentState()`.

## Consequences

- Each view states its needs in its signature, and recomposes only when those values change.
- Previews build the view state directly, with no knowledge of upstream validation rules.
- More small state classes and mapping functions. The mapping is plain code, so it's easy to test
  without Compose.
- When an upstream model gains a field, views don't change unless they need to show it.
