# AccessAble — Complete Agent-Ready Documentation

This combined file is for convenient reading. Individual documents and `AGENTS.md` remain the main working files.


---

# 00 — Agent Read First: Source of Truth and Build Contract

This file is the implementation contract for any coding agent working on AccessAble. Read it before editing code.

## Order of authority
1. `07_API_SPEC.md` is the exact HTTP and JSON contract.
2. `06_DATABASE_DESIGN.md` plus `database/schema_and_seed.sql` are the source of truth for tables and columns.
3. `02_PRD.md`, `03_SRS.md`, and `04_SCOPE_AND_USER_FLOWS.md` define expected behavior and scope.
4. `05_ARCHITECTURE.md`, `08_UI_UX_SPEC.md`, `09_TEST_PLAN.md`, and `11_DEPLOYMENT_GUIDE.md` define structure, interface, verification and deployment.
5. If a mismatch is discovered, do not guess silently: follow the API/database contracts above, add a brief note to `IMPLEMENTATION_NOTES.md`, and tell the project owner.

## Fixed implementation decisions
- IDE: VS Code.
- Java: JDK 21.
- Spring Boot: 3.5.16.
- Build: Maven Wrapper. Include `mvnw`, `mvnw.cmd`, and `.mvn/wrapper/` files.
- Backend dependencies: Spring Web, Spring Data JPA, PostgreSQL JDBC Driver, Validation, Spring Boot Test.
- Do not add Spring Security, Lombok, MapStruct, a frontend build system, React, or extra frameworks to the MVP. The admin key is a clearly labelled demo-only control.
- Frontend: single-page plain HTML/CSS/JavaScript; no Node/npm build step.
- Database: Supabase PostgreSQL, existing `public` schema, prepared by `document/database/schema_and_seed.sql`.
- Schema management: `spring.jpa.hibernate.ddl-auto=validate`; do not use `update` or create another schema.
- Deployment: backend on Render using Docker; static frontend on Vercel; database on Supabase.
- Package root: Java package `com.accessable`.

## Required repo layout
```text
AccessAble/
  AGENTS.md
  .github/copilot-instructions.md
  README.md
  document/                 # these requirements, SQL and setup docs
  backend/
    pom.xml
    mvnw, mvnw.cmd, .mvn/wrapper/
    Dockerfile
    src/main/java/com/accessable/...
    src/main/resources/application.properties
    src/test/java/com/accessable/...
  frontend/
    index.html
    styles.css
    app.js
```

Do not put Java source files in the repository root. Do not place database credentials or `ADMIN_KEY` into any committed source/config file.

## Required implementation layers
- `model/`: `Building`, `Facility`, `StatusReport` JPA entities only.
- `repository/`: three `JpaRepository` interfaces.
- `dto/`: request and response DTO classes. Never expose entity relations directly to JSON.
- `service/`: business rules and database operations.
- `controller/`: HTTP mapping, request validation entry point, and response code.
- `exception/`: not-found/conflict exceptions and one `@RestControllerAdvice` that returns the standard error contract.
- `config/`: CORS for the exact configured frontend origin.
- Use ordinary Java classes, constructors, getters/setters, enums, `if` statements and loops. Do not add an abstraction unless it removes real duplication.

## Exact business rules to preserve
- Public report creation always saves `PENDING` from server code, irrespective of any client-supplied status field.
- A public report must never modify the facility's official status or `last_verified_at`.
- `VERIFY` is valid only for a `PENDING` report. It sets the report to `VERIFIED`, sets `reviewed_at`, applies `suggested_status` to the facility and updates `last_verified_at`, all in one transaction.
- `REJECT` is valid only for a `PENDING` report. It sets `REJECTED` and `reviewed_at` but does not change facility status or `last_verified_at`.
- Any attempt to review a non-pending report returns `409` and makes no changes.
- A protected facility status change updates `current_status` and `last_verified_at`. If the new status is `AVAILABLE`, change all that facility's `VERIFIED` reports to `RESOLVED` in the same transaction. No other status update changes report statuses.
- Reject unknown IDs with `404`; validation errors with `400`; missing/invalid admin key with `401`.
- Every admin route checks the key server-side before retrieving or mutating protected data.
- `admin_note` is only visible in admin report endpoints. The facility status endpoint has no note field because no facility-status history table exists.
- The public summary returns aggregate counts only; it never returns report descriptions or admin notes.

## Required verification before claiming a milestone complete
1. Run `./mvnw clean test` (Windows: `mvnw.cmd clean test`).
2. Fix compilation/test failures rather than hiding them or removing failing tests.
3. Start the app with the documented environment variables and test the milestone endpoints in Postman.
4. For database-dependent testing, use the configured Supabase development project and the schema script; never run destructive SQL against production data.
5. Record exact manual tests performed and remaining limitations in `IMPLEMENTATION_NOTES.md`.
6. Do not claim that the whole project is done just because files were generated. End-to-end acceptance must pass.

## AI agent behavior
- Read `document/00_AGENT_READ_FIRST.md` and the relevant individual docs before coding.
- Work one milestone at a time according to `document/10_IMPLEMENTATION_PLAN.md`.
- Before coding a milestone, list planned files and why. After coding, list files changed, commands run, test results, and how the student can demonstrate/explain it.
- Do not rewrite unrelated working code or generate the entire application in one bulk change.
- Do not fabricate test results or claim deployment succeeded without actual tool output.


---

# 01 — Project Brief

## Name
**AccessAble — Campus Accessibility Status Tracker**

## Problem statement
Campus accessibility information is often incomplete or out of date. A lift may be out of service, an entrance may be blocked, or an accessible washroom may temporarily be unavailable. A static list of facilities cannot communicate these short-term changes. People need a simple way to view the latest known status and report problems for review.

## Proposed solution
AccessAble maintains a small catalogue of accessibility facilities by building. Visitors can search the catalogue, view a facility's current recorded status and last verification time, and submit a report. An administrator reviews pending reports and updates the official facility status when appropriate.

## Main users
- **Campus visitor/student:** finds facility information and submits a problem report.
- **Administrator/maintenance coordinator:** reviews submitted reports, verifies or rejects them, and updates official facility status.

## MVP features
1. Browse buildings and facilities.
2. Filter facilities by building, facility type and recorded status.
3. View facility details and last-verified time.
4. Submit a problem report for an existing facility.
5. Admin reviews pending reports and verifies/rejects them.
6. Admin updates a facility status after a repair or new inspection.
7. View a simple count of facilities by status and pending reports.

## Platform decision
- Plain HTML/CSS/JavaScript frontend on Vercel.
- Java 21 + Spring Boot 3.5.16 REST backend on Render using Docker.
- VS Code is the development IDE; no IDE switch is required.
- VS Code is the development IDE; no IDE switch is required.
- VS Code is the development IDE; no IDE switch is required.
- VS Code is the development IDE; no IDE switch is required. Use Maven Wrapper; do not add Lombok or a frontend framework.
- Supabase PostgreSQL database.
- Spring Data JPA/Hibernate for normal database operations.
- Postman for API demonstrations and admin operations during early testing.

## Success looks like
A demo user can find a facility and submit a report; an administrator can review it; a verified report changes the official status; and the public facility page shows the updated state and timestamp. Invalid data is rejected with a useful message. Database credentials are never sent to the browser.

## Assumptions
- This is a student mini-project and initial demo, not a certified accessibility information service.
- The campus and facility catalogue is added as sample data or by a project administrator.
- Reports do not need a reporter account in the MVP.
- The frontend does not expose an admin secret. Admin actions are available on a small admin screen where the administrator enters the secret at runtime, or directly through Postman.
- Campus administrators must verify a report before the public status changes.


---

# 02 — Product Requirements Document (PRD)

## 1. Product overview
AccessAble is a small web application that helps users check the most recently verified availability of accessibility-related facilities on a campus. It also provides a structured way to submit and review reports about temporary issues.

## 2. Fixed technical choices
- Development IDE: VS Code with the Extension Pack for Java and Spring Boot Extension Pack.
- Java: JDK 21.
- Spring Boot: 3.5.16 (keep this version fixed unless the project owner explicitly approves a change).
- Build tool: Maven using the Maven Wrapper (`mvnw` / `mvnw.cmd`).
- Backend: Spring Web, Spring Data JPA, PostgreSQL JDBC driver, Jakarta Validation.
- Tests: Spring Boot Test/JUnit; add no testing framework beyond the standard starter unless justified.
- Frontend: plain HTML, CSS and JavaScript; no Node build step.
- Database: Supabase PostgreSQL.

## 3. Goals
- Make accessibility facility information easier to find.
- Make temporary problems visible without treating unverified reports as facts.
- Give administrators a simple review process.
- Demonstrate core Java and Spring Boot concepts through a meaningful, manageable application.
- Keep the system easy for a student developer to understand and maintain.

## 4. Non-goals
The first version will not provide live sensors, navigation or indoor maps, GPS tracking, image uploads, chat, email/SMS notifications, AI-based prioritization, payments, a public social feed, complex multi-role permissions, or a mobile application. The admin screen uses a simple shared key for this academic demo; it is not a full account/role-management system.

## 5. User stories

### Visitor/student
- As a visitor, I want to browse facilities by building so I can find the relevant location.
- As a visitor, I want to filter facilities by type and status so I can narrow the results.
- As a visitor, I want to see when a status was last verified so I know whether the information may be old.
- As a visitor, I want to report a broken or blocked facility so the responsible people can review it.
- As a visitor, I want to see that my report is pending review rather than assume that it has already changed the official status.

### Administrator
- As an administrator, I want to see pending reports so I can investigate them.
- As an administrator, I want to verify or reject a report so inaccurate reports do not directly change public information.
- As an administrator, I want to update a facility's status after checking it or completing a repair.
- As an administrator, I want a simple overview of current statuses and pending reports.

## 6. Product requirements

### P1 — Facility directory
The system shall display facilities with name, building, floor/location, facility type, current recorded status and last-verified timestamp when available.

### P2 — Search and filters
The system shall support filtering by building, type and status. Search must work with case-insensitive text matching for facility/building names. A result that has no data must show an informative empty state.

### P3 — Report submission
A visitor shall be able to choose an existing facility, select a problem type, choose a suggested status, and enter a description. The report is created with status `PENDING`. It must not change the facility's official status at submission time.

### P4 — Report review
An administrator shall be able to view pending reports and mark a report `VERIFIED` or `REJECTED`. Verifying a report updates the associated facility status to the suggested status and updates `last_verified_at`. Rejecting it leaves the facility status unchanged.

### P5 — Facility status maintenance
An administrator shall be able to update a facility's status after inspection or repair. Setting a facility to `AVAILABLE` shall update the verification timestamp and close previously verified reports for that facility as `RESOLVED`.

### P6 — Summary
The system shall display basic totals: total facilities, available facilities, temporarily unavailable facilities, facilities under maintenance, and pending reports.

## 7. Usability principles
- Use plain language instead of technical database terms.
- Status text must not depend on color alone.
- Use a mobile-friendly layout.
- Label every form field and show errors beside the relevant field or in a clear message.
- Keep the report form short.
- Display the last verification time in a human-readable format.

## 8. Success criteria
- All MVP user stories can be demonstrated from a clean database with sample rows.
- A submitted report stays pending until reviewed.
- Verifying a report updates the facility status exactly once and preserves the report record.
- A rejected report does not alter the facility status.
- Invalid or missing required fields produce a clear 4xx response.
- No admin key, Supabase password or service credential is present in frontend files or the Git repository.
- A person unfamiliar with the code can follow the request flow from controller to service to repository.

## 9. Risks and mitigations
| Risk | Mitigation |
|---|---|
| Facility status becomes stale | Show `last_verified_at`; use an `UNKNOWN` state when status has not been checked. |
| False or duplicate reports | Leave reports pending until reviewed; administrator decides whether to verify. |
| Public report endpoint is spammed | Validate input; add rate limiting or CAPTCHA only if the app is used beyond a small demo. |
| Secrets leak into frontend | Keep secrets only in server environment variables; never bundle them into Vercel frontend assets. |
| Deployment configuration is confusing | Deploy and test the backend API first, then connect the frontend. |


---

# 03 — Software Requirements Specification (SRS)

## 1. System context
AccessAble consists of a browser-based frontend, a Spring Boot REST API and a PostgreSQL database hosted by Supabase. The frontend calls only the REST API. The backend validates requests and reads/writes database records. The browser must never connect to PostgreSQL using a database password.

## 2. Actors
- **Public user:** reads facility information and submits reports.
- **Administrator:** performs protected review and facility status operations.
- **External services:** Vercel serves static frontend files; Render runs the Java API; Supabase hosts PostgreSQL.

## 3. Functional requirements

| ID | Requirement | Priority |
|---|---|---|
| FR-01 | Return a list of buildings. | Must |
| FR-02 | Return facilities with building details. | Must |
| FR-03 | Filter facilities by building ID, facility type and current status. | Must |
| FR-04 | Return a single facility by ID. | Must |
| FR-05 | Validate and create a report linked to an existing facility. | Must |
| FR-06 | Set a new report's status to `PENDING` on the server, ignoring any client-supplied review status. | Must |
| FR-07 | Require a valid admin key for report list/review and facility status update endpoints. | Must |
| FR-08 | Verify a pending report and update the facility status plus verification timestamp. | Must |
| FR-09 | Reject a pending report without changing the facility status. | Must |
| FR-10 | Update a facility's official status through a protected endpoint. | Must |
| FR-11 | When a facility is set to `AVAILABLE`, mark its previously `VERIFIED` reports `RESOLVED`. | Must |
| FR-12 | Return simple summary counts. | Must |
| FR-13 | Return consistent error responses for validation errors, missing records and unauthorized admin actions. | Must |

## 4. Non-functional requirements
- **NFR-01 Learnability:** Code must use straightforward classes and small methods, avoiding clever abstractions.
- **NFR-02 Maintainability:** Controllers handle HTTP input/output; services contain business rules; repositories handle database access.
- **NFR-03 Data integrity:** Foreign keys connect facilities to buildings and reports to facilities.
- **NFR-04 Security:** Database password and admin key are stored as server environment variables. All deployed traffic uses HTTPS.
- **NFR-05 Privacy:** Do not collect a reporter's name, phone number or account information in the MVP.
- **NFR-06 Accessibility:** Keyboard focus must be visible; forms use labels; statuses have text labels; layout supports mobile screens.
- **NFR-07 Reliability:** Invalid submissions cannot update official facility status.
- **NFR-08 Performance:** For a small demo dataset, ordinary list/filter operations should usually complete within a few seconds under normal network conditions. This is a target, not a guarantee.
- **NFR-09 Portability:** Configuration such as database URL and allowed frontend origin must be supplied through environment variables.
- **NFR-10 Reproducibility:** All developers and deployment must use JDK 21 and Spring Boot 3.5.16 via Maven Wrapper.
- **NFR-10 Reproducibility:** All developers and deployment must use JDK 21 and Spring Boot 3.5.16 via Maven Wrapper.
- **NFR-10 Reproducibility:** All developers and deployment must use JDK 21 and Spring Boot 3.5.16 via Maven Wrapper.
- **NFR-10 Reproducibility:** All developers and deployment must use JDK 21 and Spring Boot 3.5.16 via Maven Wrapper.

## 5. Data validation requirements
- Facility ID must refer to an existing facility.
- Report description is required and must contain 10–1000 characters after trimming.
- `issueType` must be one of the documented values.
- `suggestedStatus` must be `TEMPORARILY_UNAVAILABLE` or `UNDER_MAINTENANCE` for a public report.
- Facility type and facility status must be known enum-like values.
- Admin decision must be `VERIFY` or `REJECT` when reviewing a pending report.
- Unknown IDs return `404`; invalid values return `400`; a missing/invalid admin key returns `401`.

## 6. Use cases

### UC-01 — Browse facilities
**Actor:** public user.  
**Precondition:** API is available.  
**Main flow:** user opens the page; frontend requests buildings and facilities; API returns matching records; frontend displays status and last verification time.  
**Alternate flow:** no records match; frontend displays “No facilities match these filters.”

### UC-02 — Submit a problem report
**Actor:** public user.  
**Precondition:** the selected facility exists.  
**Main flow:** user selects facility, issue type, suggested status and description; frontend sends `POST /api/reports`; backend validates input; backend saves a `PENDING` report; frontend confirms the report was submitted for review.  
**Failure flow:** invalid fields result in a useful message and no report is created.

### UC-03 — Verify a report
**Actor:** administrator.  
**Precondition:** administrator has the current admin key; report exists and is `PENDING`.  
**Main flow:** admin views pending reports; selects Verify; backend checks the key and report state; backend marks it `VERIFIED`, updates facility status and timestamp; API returns the updated report.  
**Failure flow:** if already reviewed, return `409 Conflict` and make no duplicate update.

### UC-04 — Reject a report
**Actor:** administrator.  
**Precondition:** report exists and is `PENDING`.  
**Main flow:** admin chooses Reject and optionally writes an admin note; report becomes `REJECTED`; facility status remains unchanged.

### UC-05 — Mark a facility available after repair
**Actor:** administrator.  
**Precondition:** facility exists and has been checked.  
**Main flow:** admin chooses `AVAILABLE`; backend updates status and `last_verified_at`, and resolves previously verified reports for that facility.

## 7. Acceptance criteria
The product is accepted when all Must requirements pass the test cases in `09_TEST_PLAN.md`, sample facility data can be loaded, and the end-to-end flow can be demonstrated using the deployed or local app.


---

# 04 — Scope, Statuses and User Flows

## In scope for the first version
- Building directory and facility directory.
- Filter by building, facility type and recorded status.
- Facility detail including floor/location notes and last verification time.
- Public problem report form.
- Pending report review by an administrator.
- Protected verification/rejection and facility status update endpoints.
- Basic counts/summary.
- Plain HTML/CSS/JavaScript frontend.

## Out of scope
- GPS, interactive campus maps and turn-by-turn routes.
- Uploading photos or medical/disability information.
- User accounts, social login and full role-management system.
- Email, SMS, WhatsApp or push notifications.
- AI-generated recommendations, machine learning or real-time IoT sensor data.
- Complex analytics, ratings, comments or a mobile app.
- Claims that the system certifies compliance with accessibility legislation.

## Status values

### Facility status
- `AVAILABLE` — last known checked status indicates the facility is usable.
- `TEMPORARILY_UNAVAILABLE` — the facility or route is currently reported unusable.
- `UNDER_MAINTENANCE` — the facility is being repaired or inspected.
- `UNKNOWN` — the current state has not been verified or is not known.

### Report status
- `PENDING` — submitted and awaiting review.
- `VERIFIED` — administrator confirmed the report and applied its suggested facility status.
- `REJECTED` — administrator reviewed it and decided not to apply it.
- `RESOLVED` — issue was recorded as fixed after the facility was checked and marked available.

### Facility types
`LIFT`, `RAMP`, `STEP_FREE_ENTRANCE`, `ACCESSIBLE_RESTROOM`, `ACCESSIBLE_ROUTE`, `OTHER`.

### Issue types
`OUT_OF_SERVICE`, `ROUTE_BLOCKED`, `ACCESS_BLOCKED`, `FACILITY_UNAVAILABLE`, `OTHER`.

## Public-user flow
1. Open AccessAble.
2. View facility summary and list.
3. Optionally filter by building/type/status or search by name.
4. Open a facility to review location, current recorded status and last verification time.
5. Select “Report a problem.”
6. Choose the facility, issue type and suggested status; enter a clear description.
7. Submit the report.
8. See a confirmation that the report is pending review.

## Administrator flow
1. Open the administrator review area or use Postman during API development.
2. Enter the admin key at runtime; do not save it in the source code or frontend bundle.
3. View pending reports.
4. Check the report and the physical facility information.
5. Verify or reject the report with an optional internal note.
6. If a repair is completed, update the facility to `AVAILABLE`.
7. Confirm the updated public status and timestamp.

## State transition rules
- New report: always `PENDING`.
- `PENDING` → `VERIFIED` only after an administrator verifies it.
- `PENDING` → `REJECTED` after administrator rejection.
- A verified report becomes `RESOLVED` when an administrator marks its facility `AVAILABLE` after checking it.
- A report that is already reviewed cannot be verified/rejected again; return `409 Conflict`.
- Public report submission never changes a facility's official status.
- `last_verified_at` changes only when an administrator updates/validates the official facility status, not when an unreviewed report is submitted.

## User-visible wording
Prefer “Last verified on…” and “Report awaiting review” over “live status.” If the timestamp is old or absent, show “Status may be out of date” or “Not yet verified.” Do not suggest that an unavailable facility is the only route or offer emergency guidance.


---

# 05 — Architecture and Folder Structure

## Architecture in plain words

The browser displays pages and sends HTTP requests. The Spring Boot application receives requests, validates the input, applies project rules, and uses repositories to read/write Supabase PostgreSQL. The database stores the official facility state and the history of submitted reports.

```text
Student / Visitor browser
        |
        | HTTPS requests (fetch)
        v
Vercel static frontend (HTML + CSS + JavaScript)
        |
        | REST API calls; JSON
        v
Render: Java 21 + Spring Boot 3.5.16 API
  Controller -> Service -> Repository
        |
        | JDBC connection used internally by JPA/Hibernate
        v
Supabase PostgreSQL
  buildings -> facilities -> status_reports
```

## Responsibilities
- **Frontend:** display data, gather form input, call API, display success/error messages. It must not contain database passwords or the server admin key.
- **Controller:** defines endpoints, reads parameters and request bodies, returns HTTP status codes/JSON.
- **Service:** contains the business rules, including report verification and facility status transitions.
- **Repository:** extends Spring Data JPA repositories and performs standard database operations.
- **Entity/model:** Java classes mapped to database tables.
- **DTO/request classes:** represent input received by the API, so clients cannot set review-only fields like report status.
- **AdminKeyService:** checks the admin key supplied in a request header for protected endpoints.
- **GlobalExceptionHandler:** converts expected errors into consistent responses.

## Suggested beginner-friendly backend structure

```text
backend/
  src/main/java/com/accessable/
    AccessAbleApplication.java
    controller/
      BuildingController.java
      FacilityController.java
      StatusReportController.java
      SummaryController.java
    service/
      FacilityService.java
      StatusReportService.java
      AdminKeyService.java
    repository/
      BuildingRepository.java
      FacilityRepository.java
      StatusReportRepository.java
    model/
      Building.java
      Facility.java
      StatusReport.java
    dto/
      CreateReportRequest.java
      ReviewReportRequest.java
      UpdateFacilityStatusRequest.java
      ApiError.java
    exception/
      GlobalExceptionHandler.java
    config/
      CorsConfig.java
  src/main/resources/application.properties
  pom.xml
  Dockerfile  # copy from document/starter-files/Dockerfile when creating the backend

frontend/
  index.html
  styles.css
  app.js
```

Names may be adjusted to match the actual generated Spring Initializr project, but keep the same responsibility boundaries.

## Request flow example: submit report
1. `app.js` validates required form fields and sends `POST /api/reports`.
2. `StatusReportController` receives `CreateReportRequest`.
3. Controller delegates to `StatusReportService`.
4. Service confirms the facility exists and validates the allowed suggested status.
5. Service creates a `StatusReport` whose status is `PENDING`.
6. `StatusReportRepository` saves it.
7. API returns `201 Created` with report ID and status.
8. Frontend shows that the report is awaiting administrator review.

## Request flow example: verify report
1. Admin UI or Postman sends the request with `X-Admin-Key`.
2. Service checks the key before changing data.
3. Service checks that the report exists and is still `PENDING`.
4. Service marks it `VERIFIED`, updates the associated facility status and `last_verified_at`, then saves both records in a single transaction.
5. API returns the updated report. If it was already reviewed, the service returns a conflict instead of applying the operation again.

## Design decisions to keep the code simple
- Use ordinary Java classes, constructors, getters/setters, `if` statements, loops and clear method names.
- Use Spring Data JPA repositories for simple CRUD.
- Use one service class per main resource; do not create generic frameworks or a complex inheritance hierarchy.
- Use enums or validated strings for fixed status values. If using Java enums, map them as strings in PostgreSQL.
- Use `LocalDateTime` or `OffsetDateTime` consistently. Recommended: `OffsetDateTime`/PostgreSQL `timestamptz` for timestamps.
- Use `@Transactional` only around operations that update both a report and a facility.
- Do not add WebSockets, microservices, reactive code, event buses or background workers to the MVP.


---

# 06 — Database Design

## Database choice
Use the PostgreSQL database supplied by Supabase. The Java application connects to it from the backend using the PostgreSQL JDBC driver, Spring Data JPA and Hibernate. The browser must never connect directly to PostgreSQL.

## Entity relationship

```text
BUILDINGS 1 -------- many FACILITIES 1 -------- many STATUS_REPORTS
```

- One building can contain many facilities.
- Each facility belongs to one building.
- One facility can have many status reports over time.
- Each report belongs to one facility.

## Table: `buildings`

| Column | Type | Rules | Meaning |
|---|---|---|---|
| `id` | BIGINT identity | Primary key | Unique building ID |
| `name` | VARCHAR(100) | Required, unique | Building name |
| `campus_zone` | VARCHAR(100) | Optional | Campus zone/block description |
| `created_at` | TIMESTAMPTZ | Required, default now | Row creation time |

## Table: `facilities`

| Column | Type | Rules | Meaning |
|---|---|---|---|
| `id` | BIGINT identity | Primary key | Unique facility ID |
| `building_id` | BIGINT | Required, foreign key | Building containing the facility |
| `name` | VARCHAR(120) | Required | Facility name |
| `facility_type` | VARCHAR(40) | Required | One documented facility type |
| `floor_label` | VARCHAR(30) | Required | Ground, First, Floor 2, etc. |
| `description` | VARCHAR(500) | Optional | Brief location/access notes |
| `current_status` | VARCHAR(40) | Required | Current official recorded status |
| `last_verified_at` | TIMESTAMPTZ | Optional | Last administrator verification time |
| `created_at` | TIMESTAMPTZ | Required, default now | Row creation time |

Uniqueness recommendation: `(building_id, name, floor_label)` should identify a facility uniquely.

## Table: `status_reports`

| Column | Type | Rules | Meaning |
|---|---|---|---|
| `id` | BIGINT identity | Primary key | Unique report ID |
| `facility_id` | BIGINT | Required, foreign key | Facility being reported |
| `issue_type` | VARCHAR(40) | Required | Type of problem |
| `description` | VARCHAR(1000) | Required | User explanation |
| `suggested_status` | VARCHAR(40) | Required | Proposed status if verified |
| `report_status` | VARCHAR(20) | Required, default `PENDING` | Review state |
| `reported_at` | TIMESTAMPTZ | Required, default now | Submission time |
| `reviewed_at` | TIMESTAMPTZ | Optional | Administrator review time |
| `admin_note` | VARCHAR(500) | Optional | Internal review note, not shown publicly |

The reporter's name, phone number and email are intentionally not collected in the MVP.

## Fixed values

**`facility_type`:** `LIFT`, `RAMP`, `STEP_FREE_ENTRANCE`, `ACCESSIBLE_RESTROOM`, `ACCESSIBLE_ROUTE`, `OTHER`.

**`current_status`:** `AVAILABLE`, `TEMPORARILY_UNAVAILABLE`, `UNDER_MAINTENANCE`, `UNKNOWN`.

**`issue_type`:** `OUT_OF_SERVICE`, `ROUTE_BLOCKED`, `ACCESS_BLOCKED`, `FACILITY_UNAVAILABLE`, `OTHER`.

**`suggested_status`:** `TEMPORARILY_UNAVAILABLE`, `UNDER_MAINTENANCE`.

**`report_status`:** `PENDING`, `VERIFIED`, `REJECTED`, `RESOLVED`.

## Exact Java entity mapping
Use the following Java fields so the agent does not invent a different schema:
- `Building`: `Long id`, `String name`, `String campusZone`, `OffsetDateTime createdAt`.
- `Facility`: `Long id`, `Building building`, `String name`, `FacilityType facilityType`, `String floorLabel`, `String description`, `FacilityStatus currentStatus`, `OffsetDateTime lastVerifiedAt`, `OffsetDateTime createdAt`.
- `StatusReport`: `Long id`, `Facility facility`, `IssueType issueType`, `String description`, `FacilityStatus suggestedStatus`, `ReportStatus reportStatus`, `OffsetDateTime reportedAt`, `OffsetDateTime reviewedAt`, `String adminNote`.

Map enums using `@Enumerated(EnumType.STRING)`. Because `suggestedStatus` uses `FacilityStatus`, validate it strictly to only `TEMPORARILY_UNAVAILABLE` or `UNDER_MAINTENANCE` when a public report is created. Use explicit `@Table`/`@Column` names matching the SQL. Keep relationships unidirectional with `@ManyToOne`; do not add `@OneToMany` collections merely for symmetry. API DTOs, not entities, are returned to the frontend.

## Integrity and querying rules
- Use foreign keys; do not save a report for a nonexistent facility.
- The supplied SQL script creates indexes on `facilities.building_id`, `facilities.facility_type`, `facilities.current_status`, `status_reports.report_status` and `status_reports.facility_id`. Keep these indexes; they are already included in the starter schema.
- Use parameterized ORM operations; do not build SQL by concatenating user text.
- A submitted report is always saved as `PENDING` on the backend. Do not accept `report_status`, `reviewed_at` or `admin_note` from a public request.
- Verifying a report updates `status_reports` and `facilities` within one transaction.
- Marking a facility `AVAILABLE` after checking it updates `last_verified_at` and marks its previously verified reports `RESOLVED`.
- Preserve old reports instead of deleting them when an issue is rejected or fixed; this keeps a simple history.

## Schema setup
Use `database/schema_and_seed.sql` in Supabase SQL Editor to create the three tables and insert sample data. The SQL uses `CREATE TABLE IF NOT EXISTS` and sample inserts that avoid repeating the same rows. For this project, the SQL script is the sole schema-creation source of truth and `spring.jpa.hibernate.ddl-auto=validate` must be used. Do not change it to `update` or ask Hibernate to create a second schema; validation should fail at startup if the entities do not match the SQL.


---

# 07 — REST API Specification (Authoritative Contract)

## General rules
- Local base URL: `http://localhost:8080`; production base URL is the Render API URL.
- All request/response bodies use JSON and all paths start with `/api`.
- IDs are positive integer JSON numbers mapped to Java `Long` and PostgreSQL `BIGINT`.
- Enumerated values are exact uppercase strings. Reject invalid values with `400 Bad Request`.
- Timestamps are ISO-8601 strings with an offset, e.g. `2026-10-26T17:00:00+05:30`.
- Do not return JPA entity objects directly. Map them to response DTOs to avoid recursive relations, accidental fields and lazy-loading errors.
- Public endpoints: buildings, facilities, summary, report creation. Every report-list, report-review and facility-status update endpoint requires `X-Admin-Key`.
- A missing or incorrect admin key always returns `401 Unauthorized` with the standard error body; protected data must not be read or changed first.

## Shared fixed values
- Facility types: `LIFT`, `RAMP`, `STEP_FREE_ENTRANCE`, `ACCESSIBLE_RESTROOM`, `ACCESSIBLE_ROUTE`, `OTHER`.
- Facility status: `AVAILABLE`, `TEMPORARILY_UNAVAILABLE`, `UNDER_MAINTENANCE`, `UNKNOWN`.
- Issue types: `OUT_OF_SERVICE`, `ROUTE_BLOCKED`, `ACCESS_BLOCKED`, `FACILITY_UNAVAILABLE`, `OTHER`.
- Suggested status for a public report: `TEMPORARILY_UNAVAILABLE` or `UNDER_MAINTENANCE` only.
- Report status: `PENDING`, `VERIFIED`, `REJECTED`, `RESOLVED`.

## `GET /api/buildings` (public)
Returns an array ordered alphabetically by building name.

Example:
```json
[
  {"id": 1, "name": "Academic Block A", "campusZone": "North Campus"},
  {"id": 2, "name": "Library Block", "campusZone": "Central Campus"}
]
```

## `GET /api/facilities` (public)
Optional query parameters: `buildingId`, `type`, `status`, `search`.
- `buildingId`: positive integer; invalid or non-positive values return `400`.
- `type`: one of the facility types above; invalid value returns `400`.
- `status`: one of the facility statuses above; invalid value returns `400`.
- `search`: trim surrounding spaces and perform case-insensitive substring search against facility name or building name. Empty search is treated as no search filter.
- Filters combine with AND logic. Return an empty array, not an error, when no rows match. Order by building name, then facility name.
- The small campus dataset may be loaded and filtered in a clear, simple service method; do not add a search framework or advanced query abstraction.

Example response item:
```json
{
  "id": 4,
  "name": "Passenger lift",
  "buildingId": 1,
  "buildingName": "Academic Block A",
  "campusZone": "North Campus",
  "facilityType": "LIFT",
  "floorLabel": "Ground to Floor 3",
  "description": "Lift serving the main floors.",
  "currentStatus": "AVAILABLE",
  "lastVerifiedAt": "2026-10-20T11:30:00+05:30"
}
```

## `GET /api/facilities/{id}` (public)
Returns the same facility response fields as a list item. Return `404` if the ID does not exist.

## `GET /api/summary` (public)
Returns counts calculated from the current database state. `pendingReports` is an aggregate count only; this endpoint never returns report descriptions or admin notes.

```json
{
  "totalFacilities": 6,
  "available": 4,
  "temporarilyUnavailable": 0,
  "underMaintenance": 1,
  "unknown": 1,
  "pendingReports": 2
}
```

## `POST /api/reports` (public)
Request fields:
```json
{
  "facilityId": 4,
  "issueType": "OUT_OF_SERVICE",
  "suggestedStatus": "TEMPORARILY_UNAVAILABLE",
  "description": "The lift is not responding when the floor button is pressed."
}
```
Rules:
- `facilityId` is required and must exist; otherwise `400` for missing/invalid ID or `404` for a well-formed but nonexistent ID.
- `issueType` is required and must be in the allowed list.
- `suggestedStatus` is required and must be `TEMPORARILY_UNAVAILABLE` or `UNDER_MAINTENANCE`.
- Trim description; require 10–1000 characters after trimming.
- The server sets `reportStatus=PENDING` and `reportedAt=now`. It must never bind `reportStatus`, `reviewedAt` or `adminNote` from the public request. If an unexpected `reportStatus` field is sent, ignore it or reject the body, but a saved report must always be `PENDING`.
- Creating a report never changes facility status or `lastVerifiedAt`.

Success: `201 Created`, with this public response (no admin note):
```json
{
  "id": 17,
  "facilityId": 4,
  "facilityName": "Passenger lift",
  "issueType": "OUT_OF_SERVICE",
  "suggestedStatus": "TEMPORARILY_UNAVAILABLE",
  "description": "The lift is not responding when the floor button is pressed.",
  "reportStatus": "PENDING",
  "reportedAt": "2026-10-26T17:00:00+05:30"
}
```

## Admin header
All admin endpoints below require `X-Admin-Key: <ADMIN_KEY>`. The value comes from the backend environment variable `ADMIN_KEY`. The browser admin screen may hold a manually entered key in memory for that page session and send it only to the API over HTTPS; it must not store the key, bundle it, log it, or include it in a URL. This is a demonstration-level shared-secret scheme, not production authentication.

## `GET /api/reports?status=PENDING` (admin)
- Default `status` to `PENDING` if omitted.
- Allowed filter values are the report statuses above; invalid values return `400`.
- Return an array ordered by `reportedAt` ascending (oldest first).
- Each admin list item contains: `id`, `facilityId`, `facilityName`, `buildingName`, `issueType`, `description`, `suggestedStatus`, `reportStatus`, `reportedAt`, `reviewedAt`, `adminNote`.
- `adminNote` may be null. Never include it in public facility responses or public report-creation responses.

## `PATCH /api/reports/{id}/review` (admin)
Request:
```json
{"decision": "VERIFY", "adminNote": "Checked at the facility."}
```
`decision` must be `VERIFY` or `REJECT`. `adminNote` is optional, trimmed, and limited to 500 characters; it is visible only to admin endpoints.

Rules:
- The report must exist and currently be `PENDING`; otherwise return `404` or `409` respectively.
- On `VERIFY`: within one database transaction, set report status to `VERIFIED`, set `reviewedAt=now`, save optional note, set facility status to the report's `suggestedStatus`, and set facility `lastVerifiedAt=now`.
- On `REJECT`: set report status to `REJECTED`, set `reviewedAt=now`, save optional note, and do not change facility status or facility `lastVerifiedAt`.
- A report already reviewed cannot be reviewed again. Return `409` and do not change any row.
- Return the updated admin report response fields listed for the admin report list.

## `PATCH /api/facilities/{id}/status` (admin)
Request:
```json
{"status": "AVAILABLE"}
```
`status` is required and must be one of the four facility statuses. There is deliberately no `note` field because the database has no facility-status history table; do not accept and silently discard a note.

Rules:
- Return `404` if the facility does not exist.
- Update facility `currentStatus` and `lastVerifiedAt=now` after an administrator performs a check.
- If new status is `AVAILABLE`, mark all reports for the same facility currently in `VERIFIED` as `RESOLVED` in the same transaction.
- Other facility status values do not change report statuses.
- Return `200` with:
```json
{
  "id": 4,
  "currentStatus": "AVAILABLE",
  "lastVerifiedAt": "2026-10-26T17:00:00+05:30"
}
```

## Error response contract
All handled errors use this shape:
```json
{
  "timestamp": "2026-10-26T17:00:00+05:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Description must contain between 10 and 1000 characters.",
  "path": "/api/reports"
}
```
- `400`: input/validation error or invalid enum/query value.
- `401`: missing or incorrect admin key.
- `404`: building/facility/report ID does not exist.
- `409`: report is already reviewed or requested state transition cannot be applied.
- `500`: generic unexpected error only; do not expose stack traces, SQL, credentials or exception internals.

## Implementation details
- Use request/response DTOs and `@Valid` on request DTOs.
- Use one `@RestControllerAdvice`/global exception handler for validation, missing-resource and conflict errors.
- Keep multi-row state changes inside public service methods annotated with `@Transactional`.
- The service must validate state before saving; do not trust the frontend.
- Restrict CORS to the one exact origin configured for the current environment.


---

# 08 — UI/UX Specification

## Design approach
Make the interface simple, calm and readable. This is an information tool, not a social network. Use a small number of pages/sections, clear text labels and a responsive layout. Avoid animations and chart libraries in the MVP.

## Public page sections

### Header
- Project name: AccessAble.
- Short tagline: “Check the latest reported status of campus accessibility facilities.”
- Navigation links: Facilities, Report a Problem, Admin (optional; only if admin form is implemented).

### Summary strip
Display counts for total facilities, available, temporarily unavailable, under maintenance, unknown and pending reports. Count cards must include text and not only colors.

### Facility directory
Each facility card or table row should show:
- Facility name and type.
- Building and floor/location label.
- Current recorded status as text.
- Last verified time or “Not yet verified.”
- A “View details” action and a “Report a problem” action.

Filters: search text, building dropdown, facility type dropdown and status dropdown. Add a “Clear filters” action.

### Facility details
Show full description, building/zone, floor, status, last verified time and a warning that status can change. Use “Not yet verified” when there is no verification time. Use “Status may be out of date” when `lastVerifiedAt` is more than 30 days old. Keep the threshold as a clearly named `STATUS_STALE_DAYS = 30` constant in `app.js`; display the exact timestamp as well.

### Report form
Fields:
- Facility (required, selectable from loaded facilities).
- Issue type (required dropdown).
- Suggested status (required: temporarily unavailable or under maintenance).
- Description (required, 10–1000 characters).
- Submit button.

Do not ask for name, phone number, student ID, disability details or location tracking. On success, display a report ID and “Your report is awaiting review.” Prevent double submission while the request is in progress. Preserve the form fields if submission fails.

### Admin review section (include in the final demo; Postman is acceptable during backend development)
- The administrator enters the admin key manually each session.
- Load pending reports only after the key is entered.
- Display facility, issue type, description, suggested status and reported date.
- Provide Verify and Reject actions, plus an optional internal review note (maximum 500 characters).
- Provide a separate facility-status form with facility selector and status selector. This allows the administrator to mark a facility `AVAILABLE` after repair. It sends only `{ "status": "AVAILABLE" }` to the documented endpoint; do not add a status-update note because there is no facility-status history table.
- Never place a real admin key into static JavaScript, HTML, a query string or browser storage.
- If no admin interface is built, demonstrate this workflow in Postman.

## Loading, success and error states
- Loading: show a short “Loading facilities…” message.
- Empty result: “No facilities match these filters.”
- Network/API error: “We couldn't load the facility list. Check your connection and try again.”
- Validation: tell the user exactly which field needs attention.
- Report success: show confirmation and report ID.
- Admin conflict: say the report has already been reviewed or has changed; refresh the list.

## Accessibility requirements for the UI
- Use semantic headings, buttons, labels and form controls.
- Every input has a visible associated `<label>`.
- Keyboard users can reach all controls; focus outlines must remain visible.
- Status must use text, not color alone.
- Use sufficient contrast and readable font sizes.
- Button text describes actions (e.g. “Submit report”, not “Go”).
- Do not use flashing or motion-only status cues.
- Test at mobile width and with browser zoom.

## Suggested visual hierarchy
1. Page title and one-sentence purpose.
2. Summary counts.
3. Search/filter controls.
4. Facility results.
5. Report form in its own clearly labelled section.

## Implementation constraint
Use plain HTML, CSS and JavaScript. Keep rendering simple: build DOM elements safely and insert user-provided text through `textContent`, not raw `innerHTML` strings. One `app.js` file is acceptable for this small project; separate it later only if it becomes hard to follow.


---

# 09 — Test Plan

## Test strategy
Test the business rules first through Postman and service/controller tests. Then test the browser flow. Test both successful and invalid requests. Seed data should be fictional and reproducible.

## API and functional test cases

| Test ID | Scenario | Steps/input | Expected result |
|---|---|---|---|
| T-01 | List buildings | `GET /api/buildings` | `200`; returns seed buildings. |
| T-02 | List all facilities | `GET /api/facilities` | `200`; facilities include building and status details. |
| T-03 | Filter by building | Set `buildingId` to a known ID | Every result belongs to that building. |
| T-04 | Filter by status | Set `status=AVAILABLE` | Every result has status `AVAILABLE`. |
| T-05 | Search with no matches | Search random nonexistent text | `200` with empty list, not a server error. |
| T-06 | Facility missing | `GET /api/facilities/999999` | `404`. |
| T-07 | Submit valid report | Existing facility, valid fields | `201`; status is `PENDING`. |
| T-08 | Public report does not update facility | Record facility status, submit report | Official facility status stays unchanged. |
| T-09 | Unknown facility ID | Submit report with a validly formatted but nonexistent `facilityId` | `404`; no report saved. |
| T-10 | Missing facility ID field | Submit a report without `facilityId` | `400`; no report saved. |
| T-11 | Invalid issue type | Submit unsupported issue type | `400`; no report saved. |
| T-12 | Blank description | Description is whitespace | `400`; no report saved. |
| T-13 | Description too short | Fewer than 10 characters | `400`; no report saved. |
| T-14 | Public tries to set VERIFIED | Include `reportStatus=VERIFIED` in body | Server ignores it or rejects the unknown field; report is never created as verified. |
| T-15 | Admin endpoint without key | Call review endpoint without header | `401`; no data changed. |
| T-16 | Admin endpoint wrong key | Send incorrect key | `401`; no data changed. |
| T-17 | Verify pending report | Correct key, `decision=VERIFY` | Report is `VERIFIED`; `reviewedAt` and facility `lastVerifiedAt` are set; facility status equals suggested status. |
| T-18 | Reject pending report | Correct key, `decision=REJECT` | Report is `REJECTED`; `reviewedAt` is set; facility status and `lastVerifiedAt` remain unchanged. |
| T-19 | Review same report twice | Verify a report, then verify again | Second action returns `409`; no second state change. |
| T-20 | Invalid admin decision | Send `decision=APPROVE_MAYBE` | `400`; no changes. |
| T-21 | Mark facility available | Admin sends `PATCH /api/facilities/{id}/status` with `{"status":"AVAILABLE"}` | Status and timestamp update; verified reports for it become `RESOLVED` in the same transaction. |
| T-22 | Facility status invalid | Send `status=WORKING_GREAT` | `400`; no changes. |
| T-23 | Summary counts | `GET /api/summary` | Counts match stored data. |
| T-24 | Database unavailable | Test with invalid DB config in local environment | Application logs a clear server-side error; API does not leak credentials. |
| T-25 | CORS | Call API from approved frontend origin | Browser request succeeds. A non-approved origin is not enabled by wildcard in production. |
| T-26 | HTML injection text | Submit description containing markup text | Text is stored/displayed as text; frontend does not execute it as HTML. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |

## UI manual checklist
- [ ] Page works at desktop and mobile widths.
- [ ] Loading and empty states display correctly.
- [ ] Search and each filter can be combined.
- [ ] Facility status and last verification time are visible.
- [ ] Form fields have labels and required-field messages.
- [ ] A valid report shows a confirmation and report ID.
- [ ] Failed report submission does not erase input or falsely show success.
- [ ] Public report submissions never change official status before review.
- [ ] Admin review requires a key and provides visible success/failure feedback.
- [ ] Key and DB password are absent from source files and network requests to unrelated endpoints.
- [ ] Text remains readable without relying on red/green color alone.

## Acceptance test: end-to-end demonstration
1. Open a seeded facility whose status is `AVAILABLE`.
2. Submit a report suggesting `TEMPORARILY_UNAVAILABLE`.
3. Refresh the directory; the status must still be `AVAILABLE`, and the report is pending.
4. Review the report as admin and select Verify.
5. Refresh the public directory; the facility is now `TEMPORARILY_UNAVAILABLE` and its verification time is updated.
6. Simulate the repair by updating the facility status to `AVAILABLE` as admin.
7. Confirm verified reports for that facility are marked `RESOLVED`.
8. Try an invalid or unauthorized request and show that the system rejects it.

## Test-data reset
Use the Supabase SQL editor to inspect/delete demo reports during development. Do not reset a database containing real or important data. Keep seed records fictional until deployment is complete.


---

# 10 — Beginner-Friendly Implementation Plan

## Working rule
Build one small part, run it, and understand it before adding the next part. Do not generate the entire application in one large code dump. Commit each working milestone to Git.

## Phase 0 — Prepare tools
- Use VS Code (no IDE switch needed), install JDK 21, the Extension Pack for Java, the Spring Boot Extension Pack, Git, and Postman. Use Maven Wrapper generated with the project. A local static server such as VS Code Live Server is useful for the frontend.
- Create a Supabase development project.
- Create a GitHub repository.
- Generate a Spring Boot project with Spring Web, Spring Data JPA, PostgreSQL Driver and Validation. Add DevTools only if useful. Avoid Lombok at first so getters, constructors and fields remain visible.

**Done when:** empty Spring Boot app starts locally.

## Phase 1 — Database and entities
- Read `06_DATABASE_DESIGN.md`.
- Run `database/schema_and_seed.sql` in the Supabase SQL Editor on the development database.
- Configure environment variables locally.
- Create `Building`, `Facility` and `StatusReport` entity classes with explicit table/column names and relationships.
- Create three `JpaRepository` interfaces.
- Check that the app can read the seeded data.

**Done when:** a temporary test or first endpoint can retrieve one building from Supabase.

## Phase 2 — Public read API
- Create `BuildingController` for `GET /api/buildings`.
- Create `FacilityController` for list, filters and single-facility details.
- Keep the controller thin; put simple query/business logic in `FacilityService`.
- Test all read endpoints in Postman before continuing.

**Done when:** the API returns real seeded database data, filters work, and unknown IDs return `404`.

## Phase 3 — Reporting and admin workflow
- Create `CreateReportRequest` with validation annotations.
- Implement public report creation; backend assigns `PENDING`.
- Add `AdminKeyService` and protect report listing/review and status update methods.
- Implement report verify/reject rules and the facility update in a transaction.
- Add a small global exception handler for validation, missing records and conflict errors.
- Add summary counts if the other API operations are stable.

**Done when:** end-to-end workflow passes the API acceptance tests in `09_TEST_PLAN.md`.

## Phase 4 — Frontend
- Create a basic `index.html`, `styles.css` and `app.js`.
- Start with a static layout, then load facilities using `fetch`.
- Add filters, facility detail display and report form.
- Add clear loading/success/error messages.
- Add a small admin review section that asks for the key at runtime and sends it only in `X-Admin-Key` to admin endpoints. Do not persist it in local storage or include it in source code. Use Postman to test the same actions before connecting the admin screen.
- Configure CORS for local development.

**Done when:** visitors can view and report issues through the browser.

## Phase 5 — Deployment
- Commit the working version to GitHub.
- Create `backend/Dockerfile` from `document/starter-files/Dockerfile` and deploy the Java backend to Render. (The documentation pack provides a Dockerfile template; it is not itself a built backend project.)
- Add database and admin environment variables in Render's service settings.
- Check `GET /api/buildings` and test public/admin APIs in Postman against the deployed API URL.
- Deploy the static frontend to Vercel.
- Set the frontend API base URL to the Render API URL and update backend CORS to the exact Vercel origin.
- Test the entire workflow after deployment.

**Done when:** the public site works over HTTPS and the backend writes to Supabase.

## Minimal definition of done
- Main requirements in the PRD work.
- All Must API test cases pass.
- There are no passwords or admin keys in Git.
- The code has meaningful names and small methods.
- The student can explain the request flow, entities, relationship, validation, status transition and deployment arrangement.
- README includes local setup, environment variables and how to run tests.

## If time is short
Prioritize: read facilities -> filter facilities -> create pending report -> admin verify/reject -> deploy -> simple frontend. Leave CSV export, advanced charts, email, multithreading and elaborate admin UI until after these features work.


---

# 11 — Local Setup and Deployment Guide

## Chosen deployment layout

- **Vercel:** static frontend files (`index.html`, `styles.css`, `app.js`).
- **Render:** Java 21 + Spring Boot 3.5.16 REST API, deployed from `backend/Dockerfile` copied from `document/starter-files/Dockerfile`.
- **Supabase:** PostgreSQL database.

Vercel's official serverless function runtime list does not include Java. Using Vercel for the frontend avoids forcing a Java backend into an unsupported runtime. Render documents Docker deployment for JVM-based applications. Supabase has an official Spring Boot quickstart.

## A. Create Supabase database
1. Create a Supabase project and keep its database password private.
2. Open **SQL Editor** and run `document/database/schema_and_seed.sql` from the project root (or `database/schema_and_seed.sql` if your current working directory is `document/`) on the development project.
3. In **Connect**, choose a suitable connection string. For an external host that may need IPv4 reachability, the Supabase shared pooler session-mode connection is often a practical choice; follow the dashboard's connection instructions instead of guessing the pooler host or username.
4. Convert the connection details to JDBC format for Spring Boot:
   `jdbc:postgresql://<HOST>:<PORT>/postgres?sslmode=require`
   Use the exact host, port, username and database shown in the Supabase dashboard. The shared pooler's username may contain the project reference.
5. Do not put the URL password in Git. Provide the URL, username and password as local environment variables.

## B. Local backend configuration
Create environment variables in your terminal or VS Code Java launch configuration. `application.properties` does not automatically load a `.env` file; the `.env.example` in the pack is a reference list only. Never commit real values. Use a separate local-development value for `FRONTEND_ORIGIN` and set the production Vercel origin in Render.

```text
DB_URL=jdbc:postgresql://<HOST>:<PORT>/postgres?sslmode=require
DB_USERNAME=<username-from-supabase-connect>
DB_PASSWORD=<your-database-password>
ADMIN_KEY=<long-local-demo-secret>
FRONTEND_ORIGIN=http://127.0.0.1:5500
```

Replace placeholders with your own values; do not paste the secrets into a committed file. The application's `application.properties` should read them using placeholders, for example:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
server.port=${PORT:8080}
app.admin-key=${ADMIN_KEY}
app.frontend-origin=${FRONTEND_ORIGIN:http://127.0.0.1:5500}
spring.jpa.properties.hibernate.default_schema=public
```

Because the SQL script is the sole schema source of truth, keep `spring.jpa.hibernate.ddl-auto=validate` for local and deployed runs. Do not switch to `update`: if an entity does not match the SQL, fix the entity/schema mismatch deliberately and rerun validation.

From the repository root, run `cd backend` then `./mvnw spring-boot:run` (Windows Command Prompt/PowerShell: `mvnw.cmd spring-boot:run`). Confirm the console shows a successful start. Test `GET http://localhost:8080/api/buildings` in Postman. Before any run, ensure the five environment variables above are set.

## C. CORS for local development
The CORS config must allow only the one exact origin supplied by `FRONTEND_ORIGIN`, and allow methods `GET`, `POST`, `PATCH`, `OPTIONS` plus headers `Content-Type` and `X-Admin-Key`. A page opened directly as a file can cause browser-origin issues. Run the frontend using a local static server (for example, the VS Code Live Server extension) and add that exact local origin to CORS. During development it may be `http://localhost:5500` or `http://127.0.0.1:5500`; these are different origins. Do not use an unrestricted `*` CORS policy for protected admin functionality.

## D. Deploy backend to Render
1. In the Render service settings, set **Root Directory** to `backend` if using the suggested single-repository layout (`backend/` and `frontend/`). Ensure `backend/Dockerfile` exists beside `backend/pom.xml`.
2. Push the project to GitHub.
3. In Render, create a **Web Service**, connect the repository, choose Docker as the runtime, and select the right Dockerfile path.
4. Set environment variables in Render's environment settings:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `ADMIN_KEY`
   - `FRONTEND_ORIGIN` (set after Vercel deployment)
5. Do not put secrets in the Dockerfile or code.
6. Deploy and inspect build/runtime logs. The service must listen on the port exposed through `PORT` where supported by the host configuration; Spring Boot can be configured with `server.port=${PORT:8080}`.
7. Test the deployed API in Postman before deploying the frontend.
8. Confirm public API responses work and admin endpoints reject requests without the key.

The same starter is included at `document/starter-files/Dockerfile`. Copy it to `backend/Dockerfile` beside `backend/pom.xml`. The template for a Maven Spring Boot project is:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
```

This is intentionally a small Dockerfile. If the actual jar names or Maven setup differ, adapt the copy/build commands and test locally before deployment.

## E. Deploy frontend to Vercel
1. If using the suggested single-repository layout, set Vercel **Root Directory** to `frontend`, where `index.html`, `styles.css` and `app.js` are located.
2. Ensure the API base URL in `frontend/app.js` points to the deployed Render API URL, not `localhost`. Since this is a plain static site, no frontend build-time environment-variable substitution is assumed; changing this one non-secret config value before deployment is acceptable.
3. Import the frontend repository or directory in Vercel. Select the simple/static frontend option and deploy.
4. Copy the final production domain, e.g. `https://<your-project>.vercel.app`.
5. Update `FRONTEND_ORIGIN` in Render to that exact origin and redeploy/restart as required.
6. Verify that the browser can request public endpoints. If the browser reports a CORS error, check the exact origin and backend configuration.

## F. Production smoke test
- Load the public frontend in a private/incognito window.
- Check facilities and filters.
- Submit a test report.
- Confirm the report appears as pending in admin review/Postman.
- Verify it using the admin key.
- Confirm public status and timestamp update.
- Mark facility available and confirm verified reports become resolved.
- Check that invalid requests do not change the database.
- Inspect frontend source/network requests to ensure the Supabase password and admin key are not exposed.

## Troubleshooting checklist
- **Database connection fails:** check the Supabase host, pooler mode, username, password, SSL setting and IP compatibility. Copy these values from Supabase's Connect dialog.
- **Tables/columns mismatch:** compare JPA `@Table`/`@Column` names with `schema_and_seed.sql`; check Hibernate validation logs.
- **CORS failure:** verify `Origin` exactly matches `FRONTEND_ORIGIN` and backend CORS permits it.
- **404 on frontend requests:** inspect the API base URL and endpoint path.
- **401 admin error:** verify `ADMIN_KEY` on Render and the `X-Admin-Key` header sent by Postman/UI.
- **Service deploys but returns errors:** check Render logs; never share secret-bearing logs publicly.
- **API slow after idle:** free hosting tiers may sleep/cold-start. This is a hosting characteristic, not an application bug; verify the current plan limits.


---

# 12 — Security and Privacy Notes

## Security model for the mini-project
The MVP intentionally does not implement a full user-account system. Public users can read facility information and submit reports. Administrator operations are protected by one server-side shared admin key. This keeps the Java implementation small enough for a syllabus project, but it is a **demonstration-level control, not a complete production authentication system**.

## Required rules
1. Keep `DB_PASSWORD` and `ADMIN_KEY` in local environment variables and deployment settings, never in Java source, frontend files or Git.
2. The database connection is only made by Spring Boot. The browser must not receive the PostgreSQL password or use a privileged Supabase service-role key. Enable Row Level Security (RLS) on the three tables and do not add broad anonymous policies, because this project accesses data through the Java backend rather than the Supabase browser API.
3. Protect admin endpoints on the backend; hiding an admin button in the frontend is not security.
4. Compare the supplied admin key on the server and reject missing/incorrect keys before reading or changing protected data.
5. Never log secrets or return them in error messages.
6. Validate all submitted input in the backend. Frontend validation is for user experience only.
7. Use HTTPS for deployed services and SSL for the database connection.
8. Use `textContent` (or safe DOM APIs) for untrusted report descriptions, not string-based `innerHTML` rendering.
9. Limit error output to a helpful message; do not return exception stack traces or database URLs.
10. Allow CORS only for the intended frontend origins.

RLS is defense-in-depth for accidental direct Data API access; it does not replace keeping database credentials private or protecting the Java API. The backend uses its server-side database connection and must enforce its own API/admin rules.

## Data minimization
Do not collect names, emails, phone numbers, student IDs, disability status, medical data, exact personal location or photos in the MVP. A report needs only the facility ID, issue type, suggested status, description and timestamps.

## Admin key handling
- Ask the administrator to type the key when using an admin browser screen; keep it in memory only for that page session and clear it on logout/refresh.
- Never hard-code the key, commit a `.env` file, store it in `localStorage`, or place it in a URL.
- Send it only in the `X-Admin-Key` header to the intended backend endpoint over HTTPS.
- Rotate it if it is exposed.
- For a real campus service with multiple administrators, replace the shared-key demonstration with proper authentication and authorization before collecting operational data.

## Abuse and reliability limitations
A public report endpoint can receive spam. Backend validation is mandatory, but a public deployment that accepts submissions from anyone should add rate limits, CAPTCHA or authenticated reporting if abuse becomes an issue. An administrator must review reports. The app should not display an unverified report as an official facility status.

## Real-world use disclaimer
The app is an information tracker, not an accessibility certification or emergency system. Status may be outdated or incorrect. Always show when the status was last verified and avoid wording that guarantees a facility is usable.

## Deployment checklist
- [ ] Repository contains no credentials or `.env` files.
- [ ] Secrets are set in backend environment configuration.
- [ ] Browser assets contain no database password/service-role key/admin key.
- [ ] Admin endpoints reject missing and incorrect keys.
- [ ] Backend validates all inputs.
- [ ] Frontend safely renders user-provided text.
- [ ] Production uses HTTPS and database SSL.
- [ ] CORS is restricted to intended origins.
- [ ] Only fictional/sample information is present in the public demo.


---

# 13 — Coding Guidelines: Keep It Beginner-Friendly

## General rules
- Prefer clarity over cleverness.
- Use meaningful names such as `FacilityService` and `findFacilityById`, not `FS` or `doThing`.
- Keep methods short and focused on one task.
- Write comments that explain why a rule exists, not comments that restate every line.
- Avoid features the project does not require.
- Learn every class added to the repository well enough to explain its purpose.

## Java style
- Use Java 21 and Spring Boot 3.5.16 to match the project configuration and Dockerfile. Do not change versions unless explicitly approved.
- Use ordinary classes, constructors, getters and setters initially; avoid Lombok until you understand what it generates.
- Use `private` fields and expose only needed methods.
- Use enums for fixed status values, or validate strings consistently; don't mix spellings/case.
- Use `List`/`ArrayList` for lists, but do not create custom data structures when standard collections work.
- Use `Optional` only where it makes lookup behavior clear; don't force it into every method.
- Use `try/catch` for cases where the program can respond meaningfully. Do not catch every exception and ignore it.
- Use simple `if` statements and loops before introducing complex streams. Use Streams for straightforward filtering/sorting where they improve readability.
- Do not put database credentials in `application.properties`; reference environment variables.

## Spring Boot rules
- Controller: HTTP route and request/response handling only.
- Service: business rules.
- Repository: database query and save methods.
- Entity: database data mapping; avoid placing HTTP-specific logic inside entities.
- DTO: fields the client is allowed to send.
- Validate request DTOs with Jakarta Bean Validation and show friendly messages.
- Use `@Transactional` for verification operations that update more than one database row.
- Use a simple `@RestControllerAdvice` for expected errors.
- Prefer built-in Spring Data JPA repository methods; write custom SQL only if required.
- Avoid microservices, reactive `WebClient`, generic repository frameworks, unnecessary factories, event buses, complex generics and reflection.

## Frontend rules
- Use plain HTML, CSS and JavaScript.
- Keep API base URL in one place.
- Use `fetch` and `async/await` if that is the pattern the student has learned; handle network errors.
- Show loading and error states.
- Do not put secrets into frontend code.
- Render user-provided descriptions as text, never as trusted HTML.
- Avoid state-management libraries and build tools for this small app.

## Database rules
- Use lowercase snake_case table/column names.
- Keep primary keys and foreign keys explicit.
- Use `NOT NULL`, `CHECK`, `UNIQUE` and foreign-key constraints where they represent real rules.
- Avoid deleting review history when a report is rejected or resolved.
- Test schema/entity field-name agreement.

## What not to add to the first version
No AI, React, Redux, microservices, JWT framework, OAuth, WebSockets, Docker Compose, Kubernetes, CI/CD pipelines, complex logging platforms, cache, message broker, reactive APIs or elaborate design patterns. Docker is used only because it makes the Java backend deployable to the chosen host.

## A healthy build loop
1. Make one small change.
2. Compile/run the project.
3. Test the endpoint in Postman or browser.
4. Read the error before asking for generated fixes.
5. Explain the code in your own words.
6. Commit when the feature works.


---

# 14 — Java Syllabus Mapping

This mapping aims to use concepts naturally instead of inserting advanced code only to claim syllabus coverage. The main syllabus includes Core Java/OOP, exceptions, collections, multithreading, I/O, lambda/Streams, JDBC, MVC, Spring Core, JPA/Hibernate, REST and design patterns. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping.

| Syllabus topic | How AccessAble can demonstrate it | Importance |
|---|---|---|
| Classes and objects | `Building`, `Facility`, `StatusReport` entities | Core |
| Constructors, fields, methods | Entity/DTO creation and service methods | Core |
| Encapsulation and access modifiers | Private fields, getters/setters, controlled updates | Core |
| Abstraction | Controller/service/repository separation and interface types such as repositories | Core |
| Inheritance and polymorphism | Not needed heavily in the MVP; optionally demonstrate with a small abstract `Facility` example only if the instructor explicitly expects it. Do not create an artificial hierarchy. | Optional |
| Overloading and overriding | Can be demonstrated in a separate simple class example or by a naturally overridden method; do not add meaningless overloads. | Optional |
| Exception handling | Invalid input, missing facility/report, conflict, database errors | Core |
| Collections | List results and summary counts; Spring repositories return collections | Core |
| Wrapper classes | IDs/counts use `Long`/`Integer` object types where needed | Core naturally |
| Lambda expressions/Streams | Filter/sort a list for a small report or use stream operations in a summary if readable | Optional |
| Date/time types | `reportedAt`, `reviewedAt`, `lastVerifiedAt` | Core |
| JDBC | JPA uses JDBC under the hood, but that is not the same as writing direct JDBC code. If direct JDBC is required for assessment, add a separate tiny JDBC demonstration rather than implementing all CRUD twice. | Course-specific |
| CRUD | Buildings/facilities/reports via repositories and service methods | Core |
| MVC layering | Controller, service and repository layers; frontend is separate | Core |
| Spring IoC/DI | Spring creates and injects controllers, services and repositories | Core |
| `@Service`, `@Repository`, `@Autowired`/constructor injection | Use standard Spring stereotypes and constructor injection | Core |
| Hibernate/JPA | `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@OneToMany` as appropriate | Core |
| REST methods | GET for reads, POST for creating a report, PATCH for reviewed status updates | Core |
| JSON | Request DTOs and JSON responses | Core |
| `@Transactional` | Verify report and update facility in one transaction | Useful extension |
| `@ExceptionHandler` / `@ControllerAdvice` | Consistent JSON error responses | Useful extension |
| Strategy pattern | Not required for MVP; do not force one into the domain. Can be presented in a separate small exercise if the course needs an explicit design-pattern example. | Optional |
| File I/O | Optional CSV report export after MVP works | Optional |
| Serialization | Not needed for the web application's main storage; JSON mapping is handled by Spring/Jackson. Could be a separate lab demo. | Optional |
| Multithreading/synchronization | Not required for the MVP. Adding a background thread to a small status tracker creates complexity without a clear need. | Not recommended for MVP |
| Servlet/JSP | Spring Boot REST controller handles the API; the frontend is static HTML. If the instructor specifically requires JSP/Servlet, treat that as a separate small lab activity. | Course-specific |

## Best explanation in a viva
“I chose Spring Boot because the syllabus includes REST APIs and Spring. The controller receives requests, the service applies report-review rules, the repository saves records through JPA/Hibernate, and PostgreSQL keeps the data persistent. A public report stays pending until an administrator verifies it, so users cannot directly change official facility status. I used a simple frontend and kept authentication intentionally limited to a protected admin key for the academic demo.”

## Scope warning
Do not claim the project implements every topic in the syllabus. The main value is a working, understandable application. Add optional concepts only after the required workflow is correct and only when you can explain the code confidently.


---

# AccessAble — VS Code Coding Agent Prompt

Before writing code, read `AGENTS.md`, `document/00_AGENT_READ_FIRST.md`, and all document files relevant to the next milestone. Treat the documentation as the source of truth. Do not silently change a requirement to make coding easier.

You are helping a beginner engineering student build **AccessAble — Campus Accessibility Status Tracker**. Every class must be easy for the student to understand and explain in a viva.

## Fixed stack
- JDK 21; Spring Boot 3.5.16.
- Maven project with Maven Wrapper.
- Dependencies: Spring Web, Spring Data JPA, PostgreSQL Driver, Validation, Spring Boot Test.
- Backend Java package: `com.accessable`.
- Supabase PostgreSQL, using `document/database/schema_and_seed.sql` and `ddl-auto=validate`.
- Frontend: plain HTML, CSS and JavaScript (no React, Node or bundler).
- Backend deployment: Render Docker service. Frontend deployment: Vercel static hosting.
- Development in VS Code.

## Style constraints
Use normal Java classes, simple constructors/getters/setters, meaningful names, small methods, enums for fixed values, simple `if` statements and loops. No Lombok, MapStruct, Spring Security, JWT, reactive APIs, microservices, event buses, reflection, complex generic abstractions, or features not in the documents. Use DTOs for API bodies/responses and never return JPA entity graphs directly.

## Build protocol
For each milestone:
1. State the goal and exact files to create/change.
2. Implement only that milestone.
3. Explain non-obvious code in simple language.
4. Run `./mvnw clean test` (or `mvnw.cmd clean test` on Windows); fix failures.
5. Provide run instructions and the relevant Postman/browser test with exact expected output.
6. Report truthfully which tests ran and passed; do not claim tests/deployment that did not happen.
7. Stop after one milestone so the student can run it before continuing.

## Milestones
1. Generate the Spring Initializr project, Maven Wrapper, configuration placeholders and the three JPA entities matching the SQL exactly. Do not implement controllers yet.
2. Add the three repositories and test entity/schema consistency.
3. Implement public `GET /api/buildings`, `GET /api/facilities`, `GET /api/facilities/{id}`, filters and `GET /api/summary`.
4. Implement public `POST /api/reports` with backend validation and server-assigned `PENDING` state.
5. Implement protected `GET /api/reports`, `PATCH /api/reports/{id}/review` and `PATCH /api/facilities/{id}/status`, including the exact state rules and transactions in `document/07_API_SPEC.md`.
6. Add consistent error responses and automated service tests.
7. Build the responsive plain HTML/CSS/JS public page and connect the public endpoints.
8. Add the runtime-key admin panel, report review and facility status update. Do not save the key in browser storage or source code.
9. Run the complete test plan; create the Dockerfile and README; deploy backend, then frontend; execute production smoke tests.

Never add new features or silently change the API/database contract. If a technical blocker appears, explain the cause, suggest the smallest beginner-friendly fix, and wait for the owner to choose only when a product decision is genuinely necessary.
