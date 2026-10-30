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
FRONTEND_ORIGIN=http://localhost:8001,http://127.0.0.1:8001
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
app.frontend-origin=${FRONTEND_ORIGIN:http://localhost:8001,http://127.0.0.1:8001}
spring.jpa.properties.hibernate.default_schema=public
```

Because the SQL script is the sole schema source of truth, keep `spring.jpa.hibernate.ddl-auto=validate` for local and deployed runs. Do not switch to `update`: if an entity does not match the SQL, fix the entity/schema mismatch deliberately and rerun validation.

From the repository root, run `cd backend` then `./mvnw spring-boot:run` (Windows Command Prompt/PowerShell: `mvnw.cmd spring-boot:run`). Confirm the console shows a successful start. Test `GET http://localhost:8080/api/buildings` in Postman. Before any run, ensure the five environment variables above are set.

## C. CORS for local development

CORS allowed origins are read from the `app.frontend-origin` property, which is populated through the `FRONTEND_ORIGIN` environment variable. The default allows the two local frontend addresses used by this project:

- `http://localhost:8001`
- `http://127.0.0.1:8001`

These are different origins in a browser's same-origin policy; both are allowed by default. Set `FRONTEND_ORIGIN` to a comma-separated list of explicit origins if your local frontend runs elsewhere (for example, `http://127.0.0.1:5500` when using a Live Server on port 5500).

Allowed methods: `GET`, `POST`, `PATCH`, `OPTIONS`. Allowed headers: `Content-Type`, `X-Admin-Key`. Credentials are not allowed. No wildcards (`*`) are permitted.

A page opened directly as a `file://` URL can cause browser-origin issues. Run the frontend using a local static server whose origin matches one of the configured `FRONTEND_ORIGIN` values.

## D. Deploy backend to Render
1. In the Render service settings, set **Root Directory** to `backend` if using the suggested single-repository layout (`backend/` and `frontend/`). The `Dockerfile` is located at `backend/Dockerfile` beside `backend/pom.xml`.
2. Push the project to GitHub.
3. In Render, create a **Web Service**, connect the repository, choose Docker as the runtime, and select `Dockerfile` as the Dockerfile path with build context `.` (the `backend` directory).
4. Set environment variables in Render's environment settings:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `ADMIN_KEY`
   - `FRONTEND_ORIGIN` (set after Vercel deployment)
5. Do not put secrets in the Dockerfile or code.
6. Render automatically supplies the `PORT` environment variable. The backend is configured with `server.port=${PORT:8080}`, so it will listen on Render's assigned port. Locally it defaults to `8080`.
7. Test the deployed API in Postman before deploying the frontend. A suitable health-check endpoint is `GET /api/buildings` — it is public, returns HTTP 200 when the backend and database are working, and does not modify database records.
8. Confirm public API responses work and admin endpoints reject requests without the key.

The Dockerfile uses a multi-stage build:
- **Build stage**: `eclipse-temurin:21-jdk`, runs `./mvnw -B clean package` (tests are NOT skipped) to produce `target/backend-0.0.1-SNAPSHOT.jar`
- **Runtime stage**: `eclipse-temurin:21-jre`, copies only the executable JAR, runs as a non-root `appuser`, and starts with `java -jar app.jar`

See `backend/Dockerfile` and `backend/.dockerignore` for the complete configuration.

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
