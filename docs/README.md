# Docs

Project knowledge that isn't obvious from the code.

## Architecture decision records

[`adr/`](adr/) holds the patterns this codebase follows and why. Read these before adding code.
Each record is short, numbered and never renumbered.

| # | Decision | Status |
|---|---|---|
| [ADR-0001](adr/ADR-0001-one-composable-per-file.md) | One non-trivial Composable per file, with its previews | Accepted |
| [ADR-0002](adr/ADR-0002-internal-by-default.md) | Everything not exposed is `internal` | Accepted |
| [ADR-0003](adr/ADR-0003-state-class-for-composables-with-many-parameters.md) | Composables with more than two data parameters take a `<FunctionName>State` | Accepted |
| [ADR-0004](adr/ADR-0004-feature-package-layout.md) | Feature packages keep the ViewModel at the root and composables in `views/` | Superseded by ADR-0006 |
| [ADR-0005](adr/ADR-0005-file-or-folder-per-component.md) | A component is one file, or a folder named after it when it has parts | Accepted (amended) |
| [ADR-0006](adr/ADR-0006-features-at-the-package-root.md) | Features are top-level packages, with the ViewModel at the root and composables in `views/` | Accepted |

To add a decision, copy [`adr/template.md`](adr/template.md) to
`adr/ADR-<number>-<title-in-kebab-case>.md`, using the next four-digit number (e.g.
`ADR-0007-use-ktor-for-rpc.md`). Fill it in and add a row above. To change a decision, write a new
ADR that supersedes the old one, and set the old one's status to "Superseded by ADR-NNNN" rather
than rewriting it.

## References

[`reference/`](reference/) holds external material the code is built against.

- [Transmission RPC API](reference/transmission-rpc-api.md): both wire formats (JSON-RPC 2.0 for
  4.1+, legacy for ≤ 4.0), the session-id handshake, all methods and fields, and notes for this
  client.
