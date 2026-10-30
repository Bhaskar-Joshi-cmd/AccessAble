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
