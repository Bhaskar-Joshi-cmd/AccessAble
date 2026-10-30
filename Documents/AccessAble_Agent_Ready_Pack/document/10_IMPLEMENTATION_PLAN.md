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
