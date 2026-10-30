# 13 — Coding Guidelines: Keep It Beginner-Friendly

## General rules
- Prefer clarity over cleverness.
- Use meaningful names such as `FacilityService` and `findFacilityById`, not `FS` or `doThing`.
- Keep methods short and focused on one task.
- Write comments that explain why a rule exists, not comments that restate every line.
- Avoid features the project does not require.
- Learn every class added to the repository well enough to explain its purpose.

## Java style
- Use Java 21 and Spring Boot 3.5.16 to match the project configuration and Dockerfile. Do not change versions unless explicitly approved.
- Use ordinary classes, constructors, getters and setters initially; avoid Lombok until you understand what it generates.
- Use `private` fields and expose only needed methods.
- Use enums for fixed status values, or validate strings consistently; don't mix spellings/case.
- Use `List`/`ArrayList` for lists, but do not create custom data structures when standard collections work.
- Use `Optional` only where it makes lookup behavior clear; don't force it into every method.
- Use `try/catch` for cases where the program can respond meaningfully. Do not catch every exception and ignore it.
- Use simple `if` statements and loops before introducing complex streams. Use Streams for straightforward filtering/sorting where they improve readability.
- Do not put database credentials in `application.properties`; reference environment variables.

## Spring Boot rules
- Controller: HTTP route and request/response handling only.
- Service: business rules.
- Repository: database query and save methods.
- Entity: database data mapping; avoid placing HTTP-specific logic inside entities.
- DTO: fields the client is allowed to send.
- Validate request DTOs with Jakarta Bean Validation and show friendly messages.
- Use `@Transactional` for verification operations that update more than one database row.
- Use a simple `@RestControllerAdvice` for expected errors.
- Prefer built-in Spring Data JPA repository methods; write custom SQL only if required.
- Avoid microservices, reactive `WebClient`, generic repository frameworks, unnecessary factories, event buses, complex generics and reflection.

## Frontend rules
- Use plain HTML, CSS and JavaScript.
- Keep API base URL in one place.
- Use `fetch` and `async/await` if that is the pattern the student has learned; handle network errors.
- Show loading and error states.
- Do not put secrets into frontend code.
- Render user-provided descriptions as text, never as trusted HTML.
- Avoid state-management libraries and build tools for this small app.

## Database rules
- Use lowercase snake_case table/column names.
- Keep primary keys and foreign keys explicit.
- Use `NOT NULL`, `CHECK`, `UNIQUE` and foreign-key constraints where they represent real rules.
- Avoid deleting review history when a report is rejected or resolved.
- Test schema/entity field-name agreement.

## What not to add to the first version
No AI, React, Redux, microservices, JWT framework, OAuth, WebSockets, Docker Compose, Kubernetes, CI/CD pipelines, complex logging platforms, cache, message broker, reactive APIs or elaborate design patterns. Docker is used only because it makes the Java backend deployable to the chosen host.

## A healthy build loop
1. Make one small change.
2. Compile/run the project.
3. Test the endpoint in Postman or browser.
4. Read the error before asking for generated fixes.
5. Explain the code in your own words.
6. Commit when the feature works.
