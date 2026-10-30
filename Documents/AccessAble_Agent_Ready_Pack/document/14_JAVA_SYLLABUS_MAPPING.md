# 14 — Java Syllabus Mapping

This mapping aims to use concepts naturally instead of inserting advanced code only to claim syllabus coverage. The main syllabus includes Core Java/OOP, exceptions, collections, multithreading, I/O, lambda/Streams, JDBC, MVC, Spring Core, JPA/Hibernate, REST and design patterns. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping. AccessAble intentionally does not force every topic into the MVP; see the table below for the honest mapping.

| Syllabus topic | How AccessAble can demonstrate it | Importance |
|---|---|---|
| Classes and objects | `Building`, `Facility`, `StatusReport` entities | Core |
| Constructors, fields, methods | Entity/DTO creation and service methods | Core |
| Encapsulation and access modifiers | Private fields, getters/setters, controlled updates | Core |
| Abstraction | Controller/service/repository separation and interface types such as repositories | Core |
| Inheritance and polymorphism | Not needed heavily in the MVP; optionally demonstrate with a small abstract `Facility` example only if the instructor explicitly expects it. Do not create an artificial hierarchy. | Optional |
| Overloading and overriding | Can be demonstrated in a separate simple class example or by a naturally overridden method; do not add meaningless overloads. | Optional |
| Exception handling | Invalid input, missing facility/report, conflict, database errors | Core |
| Collections | List results and summary counts; Spring repositories return collections | Core |
| Wrapper classes | IDs/counts use `Long`/`Integer` object types where needed | Core naturally |
| Lambda expressions/Streams | Filter/sort a list for a small report or use stream operations in a summary if readable | Optional |
| Date/time types | `reportedAt`, `reviewedAt`, `lastVerifiedAt` | Core |
| JDBC | JPA uses JDBC under the hood, but that is not the same as writing direct JDBC code. If direct JDBC is required for assessment, add a separate tiny JDBC demonstration rather than implementing all CRUD twice. | Course-specific |
| CRUD | Buildings/facilities/reports via repositories and service methods | Core |
| MVC layering | Controller, service and repository layers; frontend is separate | Core |
| Spring IoC/DI | Spring creates and injects controllers, services and repositories | Core |
| `@Service`, `@Repository`, `@Autowired`/constructor injection | Use standard Spring stereotypes and constructor injection | Core |
| Hibernate/JPA | `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@OneToMany` as appropriate | Core |
| REST methods | GET for reads, POST for creating a report, PATCH for reviewed status updates | Core |
| JSON | Request DTOs and JSON responses | Core |
| `@Transactional` | Verify report and update facility in one transaction | Useful extension |
| `@ExceptionHandler` / `@ControllerAdvice` | Consistent JSON error responses | Useful extension |
| Strategy pattern | Not required for MVP; do not force one into the domain. Can be presented in a separate small exercise if the course needs an explicit design-pattern example. | Optional |
| File I/O | Optional CSV report export after MVP works | Optional |
| Serialization | Not needed for the web application's main storage; JSON mapping is handled by Spring/Jackson. Could be a separate lab demo. | Optional |
| Multithreading/synchronization | Not required for the MVP. Adding a background thread to a small status tracker creates complexity without a clear need. | Not recommended for MVP |
| Servlet/JSP | Spring Boot REST controller handles the API; the frontend is static HTML. If the instructor specifically requires JSP/Servlet, treat that as a separate small lab activity. | Course-specific |

## Best explanation in a viva
“I chose Spring Boot because the syllabus includes REST APIs and Spring. The controller receives requests, the service applies report-review rules, the repository saves records through JPA/Hibernate, and PostgreSQL keeps the data persistent. A public report stays pending until an administrator verifies it, so users cannot directly change official facility status. I used a simple frontend and kept authentication intentionally limited to a protected admin key for the academic demo.”

## Scope warning
Do not claim the project implements every topic in the syllabus. The main value is a working, understandable application. Add optional concepts only after the required workflow is correct and only when you can explain the code confidently.
