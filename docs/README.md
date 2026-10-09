# Docs

These docs record project knowledge that the code does not make clear.

## Architecture decision records

The [`adr/`](adr/) folder holds the patterns that this codebase follows, and the reasons for them.
Read these records before you add code. Each record is short and has a number. The number of a
record never changes.

The table groups the ADRs by topic. In each group, the accepted ADRs come first.

### Composables

| # | Decision | Status |
|---|---|---|
| [ADR-0010](adr/ADR-0010-composable-conventions.md) | Composable conventions: files, previews, screens, state classes, the data that a Composable takes | Accepted |
| [ADR-0001](adr/ADR-0001-one-composable-per-file.md) | One non-trivial Composable per file, with its previews | Superseded by ADR-0010 |
| [ADR-0003](adr/ADR-0003-state-class-for-composables-with-many-parameters.md) | Composables with more than two data parameters take a `<FunctionName>State` | Superseded by ADR-0010 |
| [ADR-0008](adr/ADR-0008-composables-take-only-what-they-use.md) | Composables take exactly the data they use (interface segregation) | Superseded by ADR-0010 |

### Code structure

| # | Decision | Status |
|---|---|---|
| [ADR-0002](adr/ADR-0002-internal-by-default.md) | Everything that is not exposed is `internal` | Accepted |
| [ADR-0005](adr/ADR-0005-file-or-folder-per-component.md) | A component is one file, or a folder named after it when it has parts | Accepted (amended) |
| [ADR-0006](adr/ADR-0006-features-at-the-package-root.md) | Features are top-level packages, with the ViewModel at the root and Composables in `views/` | Accepted (amended) |
| [ADR-0004](adr/ADR-0004-feature-package-layout.md) | Feature packages keep the ViewModel at the root and Composables in `views/` | Superseded by ADR-0006 |

### Build

| # | Decision | Status |
|---|---|---|
| [ADR-0007](adr/ADR-0007-gradle-daemon-on-corretto-from-direct-links.md) | The Gradle daemon runs on Amazon Corretto, downloaded from direct CDN links | Accepted |

### Docs

| # | Decision | Status |
|---|---|---|
| [ADR-0009](adr/ADR-0009-write-docs-in-simplified-technical-english.md) | Write docs and comments in Simplified Technical English, with Mermaid diagrams | Accepted (amended) |

To add a decision, do these steps:

1. Copy [`adr/template.md`](adr/template.md) to `adr/ADR-<number>-<title-in-kebab-case>.md`.
2. Use the next four-digit number, for example `ADR-0011-use-ktor-for-rpc.md`.
3. Complete the sections of the new file.
4. Add a row for the new ADR to the table of its group above. Add a group if no group fits.

To change a decision, do these steps:

1. Write a new ADR that supersedes the old ADR.
2. Set the status of the old ADR to "Superseded by ADR-NNNN".
3. Do not rewrite the old ADR.

## Proposals

The [`proposals/`](proposals/) folder holds ideas that the team has not decided yet. A proposal
gives the problem, the options and the proposed solution. Do not implement an open proposal.

| # | Proposal | Status |
|---|---|---|
| [PROPOSAL-0001](proposals/PROPOSAL-0001-server-configuration-storage.md) | Store the server configurations in multiplatform-settings, and passwords in secure storage | Open |

To add a proposal, do these steps:

1. Create `proposals/PROPOSAL-<number>-<title-in-kebab-case>.md`. Use the next four-digit number.
2. Give these sections: Problem, Options, Proposal, Consequences and Open questions.
3. Set the status to "Open".
4. Add a row for the new proposal to the table above.

When the team accepts a proposal, do these steps:

1. Write an ADR that records the decision.
2. Set the status of the proposal to "Accepted as ADR-NNNN".

When the team rejects a proposal, set its status to "Rejected" and give the reason in the file.

## References

The [`reference/`](reference/) folder holds external material that the code relies on.

- [Features](reference/features.md): the features of this app, with the status, the package and
  the RPC methods of each feature. The list includes planned features.
- [Documentation](reference/documentation.md): how to write the docs, the comments and the commit
  messages. It gives the STE modes, the words of this project and the rules for Mermaid diagrams.
- [Transmission RPC API](reference/transmission-rpc-api.md): the two wire formats (JSON-RPC 2.0 for
  4.1+, legacy for ≤ 4.0) and the session-id handshake. It also gives all methods and fields, and
  notes for this client.
