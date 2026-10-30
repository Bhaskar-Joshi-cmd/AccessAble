# AccessAble — Implementation Notes

## Milestone 1 goal (source: document/prompts/IMPLEMENTATION_PROMPT.md and document/10_IMPLEMENTATION_PLAN.md Phase 1)
Generate the Spring Initializr project, Maven Wrapper, configuration placeholders and the three JPA entities matching `document/database/schema_and_seed.sql` exactly. No controllers yet.

## Files created (Milestone 1)
Backend project at `backend/` (package `com.accessable`):

- `backend/pom.xml` — Spring Boot 3.5.16, Java 21; dependencies: Spring Web, Spring Data JPA, PostgreSQL JDBC (runtime), Validation, Spring Boot Test.
- `backend/mvnw`, `backend/mvnw.cmd`, `backend/.mvn/wrapper/maven-wrapper.properties` — Maven Wrapper (Apache Maven 3.10.0, downloaded on first run).
- `backend/src/main/java/com/accessable/AccessAbleApplication.java` — Spring Boot entry point.
- `backend/src/main/resources/application.properties` — DB connection via env vars only (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`); `spring.jpa.hibernate.ddl-auto=validate`; no credentials committed.
- `backend/src/main/java/com/accessable/model/Building.java` — entity mapped to `buildings`.
- `backend/src/main/java/com/accessable/model/Facility.java` — entity mapped to `facilities`, `@ManyToOne` to Building.
- `backend/src/main/java/com/accessable/model/StatusReport.java` — entity mapped to `status_reports`, `@ManyToOne` to Facility.
- `backend/src/main/java/com/accessable/model/FacilityType.java`, `FacilityStatus.java`, `IssueType.java`, `ReportStatus.java` — enums matching the documented fixed values.
- `backend/src/main/java/com/accessable/repository/BuildingRepository.java`, `FacilityRepository.java`, `StatusReportRepository.java` — three `JpaRepository` interfaces.
- `backend/src/test/java/com/accessable/model/BuildingTest.java`, `FacilityTest.java`, `StatusReportTest.java`, `EnumsTest.java` — plain JUnit 5 tests (no database required).
- `.gitignore` — standard Java/Maven ignores (target/, .idea, etc.).
- `IMPLEMENTATION_NOTES.md` — this file.

## Commands run and exact results
Run from `/Users/bhaskarjoshi/Downloads/project/AccessAble/backend`:

```
./mvnw clean compile
```
Result: BUILD SUCCESS (compilation clean, Java 21 release target).

```
./mvnw clean test
```
Result: BUILD SUCCESS
- Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
  - com.accessable.model.StatusReportTest — 1 test
  - com.accessable.model.BuildingTest — 2 tests
  - com.accessable.model.FacilityTest — 1 test
  - com.accessable.model.EnumsTest — 4 tests

## How to verify in VS Code
1. Open the folder `/Users/bhaskarjoshi/Downloads/project/AccessAble` in VS Code.
2. Install the "Extension Pack for Java" and "Spring Boot Extension Pack" (Phase 0).
3. Open `backend/pom.xml`; VS Code should import the Maven project. Use the Terminal to run:
   - `./mvnw clean compile` — confirms the project compiles.
   - `./mvnw clean test` — confirms all 8 unit tests pass.
4. In the VS Code Java view, you can see `com.accessable.model.*` entities and `com.accessable.repository.*` repositories under `backend/src/main/java`.
5. To run the application (requires a Supabase database — see limitations):
   - Export env vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (and `ADMIN_KEY` for later milestones).
   - Run `AccessAbleApplication` or `./mvnw spring-boot:run`.
   - Because `ddl-auto=validate`, startup validates the entity mappings against the Supabase schema; a mismatch fails fast.
6. Test endpoints later (Milestones 3–5) with Postman against `http://localhost:8080/api/...`; no endpoints exist yet in Milestone 1.

## Remaining limitations / next steps (Milestones 2+)
- No Supabase credentials are configured in this environment (`DB_URL`/`DB_USERNAME`/`DB_PASSWORD` are unset), so the full application context cannot be started here — `ddl-auto=validate` needs a live Supabase connection. This is expected and by design; do NOT commit credentials.
- Entity/schema validation against the real Supabase schema must be done by running `document/database/schema_and_seed.sql` in the Supabase SQL Editor on a development project, then starting the app with the env vars set.
- No controllers, services, DTOs, exceptions, or CORS config yet — those are Milestones 2–6.
- End-to-end Postman tests and frontend work are deferred to later milestones.

## Notes for the student
- Every class uses ordinary Java: constructors, getters/setters, enums, `if`-free logic here (business rules come in later milestones). No Lombok, no MapStruct, no Spring Security.

## Milestone 5A — Admin Read API (report listing)

### Files created/modified (Milestone 5A)

**New file:**
- `backend/src/main/java/com/accessable/dto/ReportListResponse.java` — DTO with 11 fields: `id`, `facilityId`, `facilityName`, `buildingName`, `issueType`, `description`, `suggestedStatus`, `reportStatus`, `reportedAt`, `reviewedAt`, `adminNote`

**Modified file:**
- `backend/src/main/java/com/accessable/controller/ReportController.java` — Added `GET /api/reports` endpoint:
  - Requires `X-Admin-Key` header validated via `AdminKeyService`
  - Missing/incorrect key → 401 Unauthorized
  - Missing `status` param defaults to `PENDING`
  - Invalid status → 400 Bad Request
  - Returns `List<ReportListResponse>` sorted by `reportedAt` ascending
  - Empty result → empty JSON array
  - Public `POST /api/reports` preserved unchanged

**New test file:**
- `backend/src/test/java/com/accessable/controller/ReportControllerTest.java` — 8 tests covering: missing key (401), incorrect key (401), correct key (200), status defaults to PENDING, valid status filters, invalid status (400), empty results (empty array)

### Admin key configuration
- `ADMIN_KEY` is read from environment variable via `AdminKeyService`
- If `ADMIN_KEY` is blank/missing, admin access fails closed
- Never hardcode or log the key

### Commands run and exact results

```
./mvnw clean test
```
Result: BUILD SUCCESS
- Tests run: 51, Failures: 0, Errors: 0, Skipped: 0
  - Original 43 tests (building, facility, summary, entities, enums, exception handler)
  - New 8 tests in `ReportControllerTest`

### Live database verification
**NOT performed.** Supabase environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) are not configured in this environment. All tests use mocked services.

### Manual Postman test instructions
Base URL: `http://localhost:8080`

**Setup:** Set env var: `export ADMIN_KEY=your-secret-key`

**A. GET /api/reports — missing key → 401**
```
GET http://localhost:8080/api/reports
```
Expected: `401 Unauthorized` with error body

**B. GET /api/reports — incorrect key → 401**
```
GET http://localhost:8080/api/reports
Header: X-Admin-Key: wrong-key
```
Expected: `401 Unauthorized`

**C. GET /api/reports — correct key → 200**
```
GET http://localhost:8080/api/reports
Header: X-Admin-Key: your-secret-key
```
Expected: `200 OK` with JSON array of report DTOs

**D. GET /api/reports?status=VERIFIED — valid filter → 200**
```
GET http://localhost:8080/api/reports?status=VERIFIED
Header: X-Admin-Key: your-secret-key
```
Expected: `200 OK`

**E. GET /api/reports?status=INVALID → 400**
```
GET http://localhost:8080/api/reports?status=INVALID
Header: X-Admin-Key: your-secret-key
```
Expected: `400 Bad Request`

**F. POST /api/reports — public submission (unchanged) → 201**
```
POST http://localhost:8080/api/reports
Content-Type: application/json
Body: {"facilityId":1,"issueType":"OUT_OF_SERVICE","suggestedStatus":"TEMPORARILY_UNAVAILABLE","description":"The lift is broken"}
```
Expected: `201 Created`

### Remaining limitations
- Admin report review/reject (`PATCH /api/reports/{id}/review`) not implemented yet
- Facility status update (`PATCH /api/facilities/{id}/status`) not implemented yet
- Frontend not implemented yet
- Live Supabase database testing not performed (no credentials configured)
- Relationships are unidirectional `@ManyToOne` only; DTOs (not entities) are returned to the frontend, added in later milestones.
- `report_status` is always `PENDING` for new public reports — enforced in the service layer in a later milestone, not accepted from the client.

## Milestone 2/3 (this session): Public Read API
Implemented Phase 2 from `document/10_IMPLEMENTATION_PLAN.md` — the public read API endpoints.

### Files created
- `dto/BuildingResponse.java` — `id`, `name`, `campusZone`
- `dto/FacilityResponse.java` — all documented facility fields including building details
- `dto/SummaryResponse.java` — aggregate counts only
- `dto/ApiError.java` — standard error response shape
- `exception/ResourceNotFoundException.java` — maps to HTTP 404
- `exception/InvalidFilterException.java` — maps to HTTP 400
- `exception/GlobalExceptionHandler.java` — `@RestControllerAdvice` returning standard error contract
- `controller/BuildingController.java` — `GET /api/buildings`
- `controller/FacilityController.java` — `GET /api/facilities`, `GET /api/facilities/{id}`
- `controller/SummaryController.java` — `GET /api/summary`
- `service/BuildingService.java` — buildings sorted alphabetically by name
- `service/FacilityService.java` — facility filtering (AND logic), search, 404 handling
- `service/SummaryService.java` — aggregate counts from database

### Files modified
- `repository/FacilityRepository.java` — added `countByCurrentStatus(FacilityStatus)` derived query
- `repository/StatusReportRepository.java` — added `countByReportStatus(ReportStatus)` derived query

### Tests added
- `test/java/com/accessable/service/BuildingServiceTest.java` — 3 tests
- `test/java/com/accessable/service/FacilityServiceTest.java` — 17 tests
- `test/java/com/accessable/service/SummaryServiceTest.java` — 2 tests
- `test/java/com/accessable/controller/BuildingControllerTest.java` — 2 tests
- `test/java/com/accessable/controller/FacilityControllerTest.java` — 8 tests
- `test/java/com/accessable/controller/SummaryControllerTest.java` — 2 tests
- `test/java/com/accessable/exception/GlobalExceptionHandlerTest.java` — 1 test

### Commands run and exact results
Run from `/Users/bhaskarjoshi/Downloads/project/AccessAble/backend`:

```
./mvnw clean test
```
Result: **BUILD SUCCESS**
- Tests run: **43**, Failures: **0**, Errors: **0**, Skipped: **0**
  - com.accessable.controller.BuildingControllerTest — 2 tests
  - com.accessable.controller.FacilityControllerTest — 8 tests
  - com.accessable.controller.SummaryControllerTest — 2 tests
  - com.accessable.exception.GlobalExceptionHandlerTest — 1 test
  - com.accessable.service.BuildingServiceTest — 3 tests
  - com.accessable.service.FacilityServiceTest — 17 tests
  - com.accessable.service.SummaryServiceTest — 2 tests
  - com.accessable.model.StatusReportTest — 1 test
  - com.accessable.model.BuildingTest — 2 tests
  - com.accessable.model.FacilityTest — 1 test
### HikariCP Connection Pool Configuration Fix
**Root cause:** After application startup, HikariCP logged two warnings:
1. `Failed to validate connection ... (This connection has been closed.)` - when validating idle/aged pool connections
2. `Thread starvation or clock leap detected (housekeeper delta=47s888ms)` - when the housekeeper thread monitoring idle connections is delayed (common after Mac sleep / JVM pauses)

With no explicit Hikari configuration, Spring Boot uses default settings (`maximum-pool-size=10`, `minimum-idle=10`, `idle-timeout=6min`, `max-lifetime=30min`) which are excessive for a low-traffic student project and may exceed external connection timeouts.

**Fix:** Added conservative HikariCP configuration to `backend/src/main/resources/application.properties`:

```properties
# HikariCP connection pool configuration for low-traffic student project:
# - maximum-pool-size=3: enough for simultaneous requests, conservative
# - minimum-idle=0: no forced idle connections; connections created on demand
# - idle-timeout=60s: remove idle connections after 1 minute
# - max-lifetime=3min: retire connections after 3 minutes (before external timeout)
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=0
spring.datasource.hikari.idle-timeout=60000
spring.datasource.hikari.max-lifetime=180000
```

**Rationale:**
- `maximum-pool-size=3`: Conservative pool size for occasional simultaneous API requests
- `minimum-idle=0`: No forced idle connections; connections created on demand and cleaned up
- `idle-timeout=60s`: Idle connections removed after 1 minute, keeping the pool fresh
- `max-lifetime=180s (3 min)`: Connections retired before any external timeout (Supabase Session Pooler typically has longer limits)

**Keep-alive not needed**: With `minimum-idle=0` and `idle-timeout=60s`, idle connections are quickly discarded rather than validated in-place. Adding keep-alive would add unnecessary overhead.

**Test results:** `./mvnw clean test` → 43 tests pass, BUILD SUCCESS. Configuration does not change API behavior or remove tests.
  - com.accessable.model.EnumsTest — 4 tests

### Live database verification
**NOT performed.** Supabase environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) are not configured in this environment. Because `spring.jpa.hibernate.ddl-auto=validate`, the application context requires a live Supabase connection to start. To verify against the real seeded database:

1. Run `document/database/schema_and_seed.sql` in the Supabase SQL Editor on a development project.
2. Export env vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
3. Start the app: `./mvnw spring-boot:run` or run `AccessAbleApplication`.
4. Test in Postman against `http://localhost:8080/api/...` (see below).

### How to test endpoints in Postman
Base URL: `http://localhost:8080`

**A. GET /api/buildings**
- URL: `GET http://localhost:8080/api/buildings`
- Expected: `200 OK` with JSON array sorted alphabetically by name.

**B. GET /api/facilities** (optional query params: buildingId, type, status, search)
- No filters: `GET http://localhost:8080/api/facilities`
- Filter by building: `GET http://localhost:8080/api/facilities?buildingId=1`
- Filter by type: `GET http://localhost:8080/api/facilities?type=LIFT`
- Filter by status: `GET http://localhost:8080/api/facilities?status=AVAILABLE`
- Search: `GET http://localhost:8080/api/facilities?search=lift`
- Combined (AND): `GET http://localhost:8080/api/facilities?buildingId=1&type=LIFT&status=AVAILABLE`
- Invalid buildingId: `GET http://localhost:8080/api/facilities?buildingId=abc` → `400`
- Invalid type: `GET http://localhost:8080/api/facilities?type=WORKING` → `400`
- Invalid status: `GET http://localhost:8080/api/facilities?status=WORKING` → `400`
- No matches: `GET http://localhost:8080/api/facilities?search=nonexistent` → `200` with `[]`

**C. GET /api/facilities/{id}**
- Existing: `GET http://localhost:8080/api/facilities/4` → `200` with facility fields
- Unknown: `GET http://localhost:8080/api/facilities/999999` → `404`

**D. GET /api/summary**
- URL: `GET http://localhost:8080/api/summary`
- Expected: `200` with counts: `{"totalFacilities":6,"available":4,"temporarilyUnavailable":0,"underMaintenance":1,"unknown":1,"pendingReports":2}`

### Error response format
All errors return this shape:
```json
{"timestamp":"2026-10-26T17:00:00+05:30","status":400,"error":"Bad Request","message":"...","path":"/api/facilities"}
```

### Notes for the student
- Controllers are thin — all query/filtering logic is in the service layer.
- Filtering uses ordinary Java Streams (no search framework) since the dataset is small.
- `buildingId` must be positive; `type` and `status` must match documented enum values exactly (case-sensitive).
- `search` is trimmed and case-insensitive, matching against facility name or building name.
- DTOs (not JPA entities) are returned — no recursive relations or lazy-loading issues.
- `spring.jpa.open-in-view=false` — `@Transactional(readOnly = true)` on services ensures the building relationship is loaded while the transaction is open (`@ManyToOne` is EAGER by default).
- `admin_note` is never exposed in public endpoints.
- No admin-key handling, report submission, or frontend yet — those are later milestones.

## Milestone 5B — Configurable CORS

### Objective
Make the backend CORS allowed-frontend-origins configurable through the existing `app.frontend-origin` property (populated via the `FRONTEND_ORIGIN` environment variable), instead of hardcoding origins in `CorsConfig.java`.

### Files modified
- `backend/src/main/java/com/accessable/config/CorsConfig.java` — now reads `app.frontend-origin` via `@Value`, splits on commas, trims whitespace, and passes the resulting explicit origins to `CorsRegistry.allowedOrigins(...)`. Default value: `http://localhost:8001,http://127.0.0.1:8001`. Permitted methods, headers, credentials (`false`), and `maxAge` (3600s) are unchanged.

### Files added
- `backend/src/test/java/com/accessable/config/CorsConfigTest.java` — 4 tests:
  - `http://localhost:8001` origin is allowed (`Access-Control-Allow-Origin` echoes back)
  - `http://127.0.0.1:8001` origin is allowed
  - Unrelated origin (`http://evil.com`) is not allowed (no `Access-Control-Allow-Origin` header)
  - Request without an `Origin` header gets no CORS header

### Configuration
- Local development (default): `FRONTEND_ORIGIN` unset → allows `http://localhost:8001` and `http://127.0.0.1:8001`
- Deployed frontend: set `FRONTEND_ORIGIN` to the exact deployed frontend origin (e.g. `https://<project>.vercel.app`) in the backend hosting environment
- Multiple origins: comma-separated, e.g. `http://localhost:8001,https://<project>.vercel.app`
- No wildcards (`*`) are permitted

### Admin key (unchanged)
- `ADMIN_KEY` remains supplied through the admin UI and sent in the `X-Admin-Key` request header
- `CorsConfig` does not touch admin-key validation; `AdminKeyService` behavior is unchanged
- No secrets are placed in frontend JavaScript or HTML

### Commands run and exact results
```
./mvnw clean test
```
Result: BUILD SUCCESS (see Step 5 for exact test counts)