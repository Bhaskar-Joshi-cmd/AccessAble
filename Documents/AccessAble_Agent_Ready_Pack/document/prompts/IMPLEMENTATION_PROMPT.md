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
