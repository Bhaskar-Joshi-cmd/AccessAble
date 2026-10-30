# AccessAble Agent-Ready Pack

This is the project documentation package to use with a coding agent in VS Code. Open this folder (the one containing `AGENTS.md`) as the workspace, then instruct the agent: “Read `AGENTS.md` and `document/00_AGENT_READ_FIRST.md`, then complete Milestone 1 only.”

The intended application layout that the agent creates is `backend/` for the Spring Boot Maven application and `frontend/` for plain HTML/CSS/JS. The `document/` folder contains the source-of-truth specifications, SQL seed, Postman collection and templates.

Stack fixed by the documents: JDK 21, Spring Boot 3.5.16, Maven Wrapper, Supabase PostgreSQL, Render for the Java API and Vercel for the static frontend. Do not use a normal Vercel Function for Spring Boot.
