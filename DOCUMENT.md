# Change Documentation

This file records all implementation changes made for the assessment.

## How To Use

- Add one entry per meaningful change.
- Keep entries in reverse chronological order (newest first).
- Include technical rationale and verification evidence.

## Change Log

### 2026-05-16 - Add reusable products and enrich order responses

- Summary: Added first-class product support with a reusable `product` model, an `order_product` join table, new `/products` create/list/detail endpoints, product-aware order responses, and ID-based order creation that links existing products to an order.
- Files: `src/main/java/com/example/store/controller/OrderController.java`, `src/main/java/com/example/store/controller/ProductController.java`, `src/main/java/com/example/store/service/ProductQueryService.java`, `src/main/java/com/example/store/repository/OrderRepository.java`, `src/main/java/com/example/store/repository/ProductRepository.java`, `src/main/java/com/example/store/mapper/OrderMapper.java`, `src/main/java/com/example/store/entity/Order.java`, `src/main/java/com/example/store/entity/Product.java`, `src/main/java/com/example/store/dto/OrderDTO.java`, `src/main/java/com/example/store/dto/OrderSummaryDTO.java`, `src/main/java/com/example/store/dto/OrderProductDTO.java`, `src/main/java/com/example/store/dto/ProductDTO.java`, `src/main/java/com/example/store/dto/CreateOrderRequest.java`, `src/main/java/com/example/store/dto/CreateProductRequest.java`, `src/main/resources/db/changelog/db.changelog-master.yaml`, `src/main/resources/db/changelog/db.changelog-5.yaml`, `src/test/java/com/example/store/controller/OrderContollerTests.java`, `src/test/java/com/example/store/controller/ProductControllerTests.java`, `src/test/java/com/example/store/service/OrderQueryServiceTests.java`, `src/test/java/com/example/store/service/ProductQueryServiceTests.java`, `src/test/resources/order-api.http`, `src/test/resources/product-api.http`, `OpenAPI.yaml`
- Reason: The assessment now requires products to be modeled independently, exposed through their own endpoints, linked to orders, and returned from both the product and order APIs.
- Impact: `POST /order` now accepts `customerId` plus `productIds` instead of a nested customer entity, `GET /order` and `GET /order/{id}` now return products, and `/products` exposes product records with the order IDs that contain them. The database schema now includes `product` and `order_product`.
- Verification: Added controller and service tests for the new product and order-product behavior. Attempted `./gradlew.bat test --tests "com.example.store.controller.OrderControllerTests" --tests "com.example.store.controller.ProductControllerTests" --tests "com.example.store.service.OrderQueryServiceTests" --tests "com.example.store.service.ProductQueryServiceTests"`, but local verification is still blocked during Gradle build-script evaluation because the environment only has `C:\Program Files\Java\jdk-24`, and Gradle fails while creating `:jacocoTestReport` at `build.gradle` line 66 with `Type T not present`.
- Risks/Follow-ups: Existing clients must switch to the new `POST /order` request shape and create products before referencing their IDs from new orders. Once a Java 17 runtime is available locally, re-run the focused Gradle test command to confirm compilation and behavior end to end.

### 2026-05-16 - Optimize GET read paths with pagination and explicit fetching

- Summary: Converted `GET /order` to a paginated endpoint, added explicit order/customer fetch paths to avoid lazy-loading during response mapping, changed `GET /customer` to return lightweight summaries, moved customer word-substring search into the database, and added supporting PostgreSQL indexes.
- Files: `src/main/java/com/example/store/controller/OrderController.java`, `src/main/java/com/example/store/controller/CustomerController.java`, `src/main/java/com/example/store/service/OrderQueryService.java`, `src/main/java/com/example/store/service/CustomerSearchService.java`, `src/main/java/com/example/store/repository/OrderRepository.java`, `src/main/java/com/example/store/repository/CustomerRepository.java`, `src/main/java/com/example/store/mapper/OrderMapper.java`, `src/main/java/com/example/store/mapper/CustomerMapper.java`, `src/main/java/com/example/store/dto/CustomerSummaryDTO.java`, `src/main/java/com/example/store/dto/OrderSummaryDTO.java`, `src/main/resources/db/changelog/db.changelog-master.yaml`, `src/main/resources/db/changelog/db.changelog-4.yaml`, `src/test/java/com/example/store/controller/OrderContollerTests.java`, `src/test/java/com/example/store/controller/CustomerControllerTests.java`, `src/test/java/com/example/store/service/CustomerSearchServiceTests.java`, `src/test/java/com/example/store/service/OrderQueryServiceTests.java`, `OpenAPI.yaml`, `build.gradle`
- Reason: Production GET endpoints were doing unbounded reads and mapper-triggered lazy relationship loads against a remote high-latency database.
- Impact: `GET /order` now returns a paginated payload with Spring paging params and a hard max page size. `GET /customer` now returns summary DTOs without embedded orders. Order detail and list reads now use explicit fetch paths, and PostgreSQL indexes support the main join and substring-search patterns.
- Verification: Ran `./gradlew.bat spotlessApply test` successfully after the implementation changes. This covered compilation, controller/service tests, and JaCoCo report generation.
- Risks/Follow-ups: If downstream clients depended on the old unpaginated order contract or embedded customer orders in collection responses, they will need to adjust to the new GET payloads.

### 2026-05-16 - Expand customer search test coverage

- Summary: Added controller tests for empty, whitespace-only, uppercase, multi-match, and second-word query scenarios, plus service tests for trimmed queries and safely ignoring null or blank customer names.
- Files: `src/test/java/com/example/store/controller/CustomerControllerTests.java`, `src/test/java/com/example/store/service/CustomerSearchServiceTests.java`
- Reason: Strengthen confidence in the customer query-search task without broadening the production feature contract.
- Impact: Test coverage now validates additional HTTP-level search behaviors and service-level normalization/defensive filtering cases. No API, schema, or runtime behavior changes.
- Verification: Attempted `./gradlew.bat test --tests "com.example.store.controller.CustomerControllerTests" --tests "com.example.store.service.CustomerSearchServiceTests"` after adding the new cases, but local verification is still blocked before test execution because Gradle is running under `C:\Program Files\Java\jdk-24` and fails while creating `:jacocoTestReport` at `build.gradle` line 62 with `Type T not present`.
- Risks/Follow-ups: Re-run the focused Gradle test command on a Java 17 runtime that matches the project toolchain to confirm the expanded test suite compiles and passes.

### 2026-05-16 - Add customer query search by name word substring

- Summary: Added optional `query` support to `GET /customer`, introduced a small search service that filters matches to a single whitespace-delimited name word, and expanded controller plus service tests for search behavior.
- Files: `src/main/java/com/example/store/controller/CustomerController.java`, `src/main/java/com/example/store/repository/CustomerRepository.java`, `src/main/java/com/example/store/service/CustomerSearchService.java`, `src/test/java/com/example/store/controller/CustomerControllerTests.java`, `src/test/java/com/example/store/service/CustomerSearchServiceTests.java`, `OpenAPI.yaml`, `src/test/resources/customer-api.http`
- Reason: Task 2 requires the customer endpoint to support query-string search against substrings of one of the words in a customer name without breaking the existing unfiltered endpoint.
- Impact: API behavior now supports `GET /customer?query=...` as a case-insensitive filter, while `GET /customer` and blank queries still return all customers. No schema or database migration changes.
- Verification: Added focused WebMvc and service-unit tests for matching, no-match, blank-query, case-insensitive, and cross-word rejection scenarios. Attempted `./gradlew.bat test --tests "com.example.store.controller.CustomerControllerTests" --tests "com.example.store.service.CustomerSearchServiceTests"`, but local verification is blocked before test execution because Gradle is running under `C:\Program Files\Java\jdk-24`, and the build fails in script evaluation with `Unsupported class file major version 68`.
- Risks/Follow-ups: Re-run the focused Gradle test command on a Java 17 runtime that matches the project toolchain to confirm compilation and runtime behavior end to end.

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
