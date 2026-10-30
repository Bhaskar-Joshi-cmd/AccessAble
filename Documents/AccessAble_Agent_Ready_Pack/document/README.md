# AccessAble — Agent-Ready Project Pack

This package is designed to be placed at the root of a project repository. The root `AGENTS.md` and `.github/copilot-instructions.md` tell a VS Code coding agent to read the documents and follow the requirements.

## Recommended environment
- VS Code.
- JDK 21.
- Spring Boot 3.5.16.
- Spring Web, Spring Data JPA, PostgreSQL Driver, Validation, Spring Boot Test.
- Maven Wrapper (`mvnw` / `mvnw.cmd`).
- Plain HTML/CSS/JavaScript frontend.
- Supabase PostgreSQL, Render backend, Vercel frontend, Postman for API testing.

## First read
1. `AGENTS.md`
2. `document/00_AGENT_READ_FIRST.md`
3. `document/01_PROJECT_BRIEF.md`, `02_PRD.md`, `03_SRS.md`, `04_SCOPE_AND_USER_FLOWS.md`
4. `document/06_DATABASE_DESIGN.md`, `document/database/schema_and_seed.sql`
5. `document/07_API_SPEC.md`
6. `document/05_ARCHITECTURE.md`, `08_UI_UX_SPEC.md`, `09_TEST_PLAN.md`
7. `document/10_IMPLEMENTATION_PLAN.md`, `11_DEPLOYMENT_GUIDE.md`, `12_SECURITY_AND_PRIVACY.md`, `13_CODING_GUIDELINES.md`
8. `document/prompts/IMPLEMENTATION_PROMPT.md`

## Files included
- Product brief, PRD, SRS, scope and workflow rules.
- Architecture, database design, SQL schema + fictional seed data.
- Exact API contract, UI behaviour, test plan, implementation plan and deployment guide.
- Security/privacy requirements, beginner-friendly coding standards and Java syllabus mapping.
- Agent instructions, reusable coding prompt, VS Code extension recommendations, application properties template, environment variable reference, Dockerfile template, and Postman collection.

## Important deployment architecture
Vercel is used only for static HTML/CSS/JS. The Java/Spring Boot application runs on Render as a Docker service. Supabase hosts PostgreSQL. Do not try to deploy Spring Boot as a normal Vercel Function.

## Data setup
Run `document/database/schema_and_seed.sql` in the Supabase SQL Editor on a development project. The script creates tables in the `public` schema, turns on RLS without anonymous policies, and inserts fictional campus records. The Spring Boot app connects directly through PostgreSQL/JDBC and uses JPA/Hibernate with `ddl-auto=validate`.

## Secrets
`document/examples/.env.example` contains placeholders only and is not automatically loaded by Spring Boot. Set the variables in the terminal, VS Code launch configuration, or Render's environment settings. Never commit real secrets.

## Done means verified
Do not mark the project complete until the backend builds/tests, all Must acceptance cases pass, the public report -> admin review -> status update flow works, and the deployed frontend is successfully calling the deployed API.
