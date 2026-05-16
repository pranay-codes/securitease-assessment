# Change Documentation

This file records all implementation changes made for the assessment.

## How To Use

- Add one entry per meaningful change.
- Keep entries in reverse chronological order (newest first).
- Include technical rationale and verification evidence.

## Change Log

### 2026-05-16 - Expand practical subset coverage for order lookup by ID

- Summary: Strengthened the single-order controller tests with full response-shape assertions, JSON content-type coverage, non-numeric path handling, and empty-list coverage for the order collection endpoint.
- Files: `src/test/java/com/example/store/controller/OrderContollerTests.java`
- Reason: Tighten confidence in `GET /order/{id}` using the agreed practical subset without broadening into a larger API test matrix.
- Impact: Test coverage now checks the returned order ID, nested customer ID, JSON response type, invalid non-numeric path input, and empty-list behavior for `GET /order`.
- Verification: Attempted `./gradlew test --tests com.example.store.controller.OrderControllerTests`, but the run remains blocked in this environment because Gradle is executing with Java 24 while the project toolchain targets Java 17, causing Jacoco task initialization to fail at `build.gradle` line 62 before tests run.
- Risks/Follow-ups: Re-run the focused Gradle test command on a Java 17 runtime to confirm the new practical-subset tests pass.

### 2026-05-16 - Add order lookup by ID endpoint

- Summary: Added `GET /order/{id}` to fetch a single order, plus controller tests for found and not-found behavior.
- Files: `src/main/java/com/example/store/controller/OrderController.java`, `src/test/java/com/example/store/controller/OrderContollerTests.java`, `OpenAPI.yaml`, `src/test/resources/order-api.http`
- Reason: Task 1 requires the order API to support lookup of a specific order by numeric ID.
- Impact: API behavior now includes `200 OK` with an `OrderDTO` for an existing order and `404 Not Found` for a missing order. No schema changes.
- Verification: Added WebMvc tests for success and not-found flows. Attempted `./gradlew test --tests com.example.store.controller.OrderControllerTests`, but the run is currently blocked in this environment because Gradle is executing with Java 24 while the project toolchain targets Java 17, causing Jacoco task initialization to fail before tests run.
- Risks/Follow-ups: Re-run the focused Gradle test command on a Java 17 runtime to complete runtime verification.

### 2026-05-16 - Initial setup

- Created `DOCUMENT.md` in the project root.
- Purpose: provide a single source of truth for tracking assessment changes.

## Entry Template

Copy this block for each new change:

```markdown
### YYYY-MM-DD - Short title

- Summary: What was changed.
- Files: `path/to/file1`, `path/to/file2`
- Reason: Why this change was needed.
- Impact: Behavior, API, schema, or performance effects.
- Verification: Tests/checks run and their outcome.
- Risks/Follow-ups: Known limitations or next actions.
```
