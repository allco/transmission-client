# ADR-0009: Write docs and comments in Simplified Technical English

- **Status:** Accepted (amended 2026-10-09)
- **Date:** 2026-10-08

## Context

People and AI agents read the docs, the ADRs, `AGENTS.md` and the code comments in this repository.
An agent cannot ask the author what a sentence means. A non-native reader has the same problem.
Long sentences, passive voice and synonyms for one action make a text easy to misread.

ASD-STE100 (Simplified Technical English, STE) is a controlled-language standard from the
aerospace industry. It removes words with more than one meaning and sentences with more than one
possible structure. The `asd-ste100` skill applies the STE rules to general English.

## Decision

- The repository includes the `asd-ste100` skill in `.claude/skills/asd-ste100/`. The files are a
  copy of the upstream skill. `UPSTREAM.md` gives the source commit. Do not edit the copied files.
- Use the skill for all text that we write:
  - Markdown files, including the ADRs and `docs/reference/`.
  - KDoc and code comments.
  - Commit messages.
- Use the **Strict** mode for text that tells a reader what to do: `AGENTS.md`, procedures,
  commands and their descriptions, code comments and KDoc.
- Use the **STE-flavored** mode for explanatory text: `README.md`, ADRs and `docs/reference/`.
  Apply the structural rules in full. Treat the word-choice rules as advice.
- The structural rules are:
  - Use the active voice. Name the actor.
  - Use simple tenses. Keep a compound tense only when it carries meaning, for example "may have
    failed".
  - Write one instruction in one sentence.
  - Keep a sentence to 20 words or fewer for an instruction, and to 25 words or fewer for a
    description.
  - Do not use semicolons.
  - Use one name for one thing in a document.
  - Do not use phrasal verbs, noun forms of actions, or marketing adjectives.
- Before you commit, run the linter on each changed Markdown file:

  ```shell
  python3 -I .claude/skills/asd-ste100/scripts/ste-lint.py <file.md>
  ```

  Fix each finding. Do not fix a finding when the rewrite changes the meaning, for example a
  quoted protocol term or an identifier. The linter counts words per line. Thus it does not find
  a long sentence that continues on the next line. Check the sentence length yourself.
- Use a Mermaid diagram when the subject has a shape: a flow, a sequence, a set of states or a
  structure. A reader understands a diagram faster than the same facts in text. Do not add images
  of diagrams. Apply the STE rules to the labels of the diagram.
- [`docs/reference/documentation.md`](../reference/documentation.md) gives the procedure, the
  words of this project and the rules for diagrams.
- Do not rewrite these items:
  - Code, identifiers, commands and quoted protocol names.
  - Text that a quote marks as the words of another source.
  - Files that the repository copies from other projects.

## Consequences

- Agents and people can read the docs with one possible meaning for each sentence.
- The docs are longer in some places, because the rules do not let a writer omit words.
- Text has a flat tone. The rules are not for marketing text, and this repository has none.
- The linter is a minimum check. It does not prove that a text obeys STE.
