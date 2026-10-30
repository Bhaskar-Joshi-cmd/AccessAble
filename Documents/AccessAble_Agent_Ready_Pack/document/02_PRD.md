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
