# VS Code Setup

1. Install JDK 21 and confirm `java -version` shows 21.
2. In VS Code Extensions, install **Extension Pack for Java** and **Spring Boot Extension Pack**.
3. Optional: install Live Server to serve the static frontend locally. Use the exact origin it prints in `FRONTEND_ORIGIN`.
4. Open the repository root (the folder containing `AGENTS.md`).
5. Generate/open the Spring Boot Maven project under `backend/`. Keep the Maven Wrapper in that folder.
6. After the agent creates the backend, run `./mvnw clean test` inside `backend/` (Windows: `mvnw.cmd clean test`).
7. Set local environment variables outside committed files. `application.properties` reads them using `${VARIABLE_NAME}` syntax.
