# Docs

Project knowledge that isn't obvious from the code.

## Architecture decision records

[`adr/`](adr/) holds the patterns this codebase follows and why. Read these before adding code.
Each record is short, numbered and never renumbered.

| # | Decision | Status |
|---|---|---|
| [0001](adr/0001-one-composable-per-file.md) | One non-trivial Composable per file, with its previews | Accepted |
| [0002](adr/0002-internal-by-default.md) | Everything not exposed is `internal` | Accepted |

To add a decision, copy [`adr/template.md`](adr/template.md) to the next number, fill it in, and
add a row above. To change a decision, write a new ADR that supersedes the old one and set the old
one's status to "Superseded by NNNN" rather than rewriting it.

## References

[`reference/`](reference/) holds external material the code is built against.

- [Transmission RPC API](reference/transmission-rpc-api.md): both wire formats (JSON-RPC 2.0 for
  4.1+, legacy for ≤ 4.0), the session-id handshake, all methods and fields, and notes for this
  client.
