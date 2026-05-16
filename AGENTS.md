# AGENTS.md

## Purpose

This file defines basic operating rules for contributors and coding agents working in this repository.

## Working Rules

- Keep changes small, clear, and reviewable.
- Prefer production-safe behavior and avoid breaking existing endpoints.
- Preserve existing style and structure unless refactoring is necessary.
- Do not commit secrets, credentials, or machine-specific config.

## Documentation Rule (Required)

- Every meaningful code or config change must be documented in `DOCUMENT.md`.
- Add entries in reverse chronological order (newest first).
- Each entry should include:
  - Summary of change
  - Files touched
  - Reason for change
  - Impact (API, schema, behavior, performance)
  - Verification steps/results
  - Risks or follow-up actions

## Quality Expectations

- Run relevant tests/checks for changed behavior before finalizing work.
- Record verification outcomes in `DOCUMENT.md`.
- If something cannot be verified locally, state that clearly in `DOCUMENT.md`.
