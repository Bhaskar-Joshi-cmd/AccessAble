# AccessAble — Campus Accessibility Status Tracker

A web application for checking the latest reported status of campus accessibility facilities and reporting issues.

## Project Overview

AccessAble helps students, staff, and visitors discover the accessibility status of campus facilities and report issues for review. The platform consists of a public facility directory, report submission, and an admin console for managing reports and facility statuses.

## Features

- Public facility directory — Browse all facilities with filters by building, type, and status
- Report submission — Submit new accessibility issues (10–1000 character description required)
- Admin: Load pending reports — View unconfirmed reports awaiting administrator review
- Admin: Verify / Reject — Review and decide on pending reports (VERIFY or REJECT with optional admin note)
- Admin: Facility status update — Update a facility's official status (AVAILABLE, TEMPORARILY_UNAVAILABLE, UNDER_MAINTENANCE, UNKNOWN)
- Summary counts — Total, available, temporarily unavailable, under maintenance, unknown, and pending reports

## Technology Stack

- Backend: Java 21, Spring Boot 3.5.16, Spring Data JPA, PostgreSQL
- Frontend: Plain HTML5, CSS3, vanilla JavaScript (no frameworks)
- Database: PostgreSQL (Supabase compatible)
- Build: Maven Wrapper (`mvnw`)

## Project Structure

```text
AccessAble/
├── backend/             # Spring Boot backend
│   ├── src/main/java/   # Java source (models, DTOs, controllers, services)
│   ├── src/test/        # 69 JUnit 5 tests
│   ├── src/main/resources/  # application.properties, SQL schema
│   └── pom.xml          # Maven project config
├── frontend/            # Plain HTML/CSS/JS application
│   ├── app.js           # All frontend logic (milestones 6.3–6.5)
│   ├── index.html       # Full HTML application page
│   └── styles.css       # 453 lines of styling
├── Documents/           # Detailed documentation pack
├── IMPLEMENTATION_NOTES.md  # Milestone-by-milestone implementation notes
└── .gitignore           # Build/output exclusions
```

## Prerequisites

- Java 21 (Maven Wrapper `mvnw` included)
- PostgreSQL (Supabase compatible) — database must be running before starting the backend
- Node.js (optional, for frontend only) — `node --check frontend/app.js` validates syntax
- Text editor — VS Code or other editor for source navigation

## Local Setup

### 1. Environment variables

Create a .env file (or set vars in your shell) with the following names — never commit real values:

| Variable | Purpose | Example |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/accessable` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | *(your password)* |
| `ADMIN_KEY` | Admin authentication key | `secret-admin-key` |
| `PORT` | Server port (optional) | `8080` |
| `FRONTEND_ORIGIN` | Comma-separated list of allowed frontend origins for CORS (no wildcards). Defaults to local dev origins. | `http://localhost:8001,http://127.0.0.1:8001` |

> Important: The `spring.jpa.hibernate.ddl-auto=validate` setting requires the database schema to match `Documents/AccessAble_Agent_Ready_Pack/document/database/schema_and_seed.sql` exactly at startup. The SQL script creates the tables and inserts sample data.

### 2. Initialize the database

Run the starter schema in the Supabase SQL Editor on a development project:

```bash
-- Run this in Supabase SQL Editor
-- Files: Documents/AccessAble_Agent_Ready_Pack/document/database/schema_and_seed.sql
```

The schema creates 3 buildings, 6 facilities, and sample data. Do not modify the schema without updating the Java entities and DTOs accordingly.

### 3. Start the Spring Boot backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs at `http://localhost:8080` by default. If you set `PORT` env var, it will use that port instead.

### 4. Serve the frontend

The frontend is a static HTML/CSS/JS page. No build step is required — just open `frontend/index.html` in a browser, or serve it:

```bash
# Using Python simple HTTP server:
cd frontend
python3 -m http.server 5500
```

Visit `http://127.0.0.1:5500` (or the port you chose for `FRONTEND_ORIGIN`).

### 5. Verify

```bash
# Check frontend JS syntax:
node --check frontend/app.js

# Run backend tests:
cd backend && ./mvnw clean test   # 78 tests, 0 failures
```