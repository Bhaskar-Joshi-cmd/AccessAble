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
