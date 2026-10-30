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
