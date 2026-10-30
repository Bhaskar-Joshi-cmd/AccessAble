# 06 — Database Design

## Database choice
Use the PostgreSQL database supplied by Supabase. The Java application connects to it from the backend using the PostgreSQL JDBC driver, Spring Data JPA and Hibernate. The browser must never connect directly to PostgreSQL.

## Entity relationship

```text
BUILDINGS 1 -------- many FACILITIES 1 -------- many STATUS_REPORTS
```

- One building can contain many facilities.
- Each facility belongs to one building.
- One facility can have many status reports over time.
- Each report belongs to one facility.

## Table: `buildings`

| Column | Type | Rules | Meaning |
|---|---|---|---|
| `id` | BIGINT identity | Primary key | Unique building ID |
| `name` | VARCHAR(100) | Required, unique | Building name |
| `campus_zone` | VARCHAR(100) | Optional | Campus zone/block description |
| `created_at` | TIMESTAMPTZ | Required, default now | Row creation time |

## Table: `facilities`

| Column | Type | Rules | Meaning |
|---|---|---|---|
| `id` | BIGINT identity | Primary key | Unique facility ID |
| `building_id` | BIGINT | Required, foreign key | Building containing the facility |
| `name` | VARCHAR(120) | Required | Facility name |
| `facility_type` | VARCHAR(40) | Required | One documented facility type |
| `floor_label` | VARCHAR(30) | Required | Ground, First, Floor 2, etc. |
| `description` | VARCHAR(500) | Optional | Brief location/access notes |
| `current_status` | VARCHAR(40) | Required | Current official recorded status |
| `last_verified_at` | TIMESTAMPTZ | Optional | Last administrator verification time |
| `created_at` | TIMESTAMPTZ | Required, default now | Row creation time |

Uniqueness recommendation: `(building_id, name, floor_label)` should identify a facility uniquely.

## Table: `status_reports`

| Column | Type | Rules | Meaning |
|---|---|---|---|
| `id` | BIGINT identity | Primary key | Unique report ID |
| `facility_id` | BIGINT | Required, foreign key | Facility being reported |
| `issue_type` | VARCHAR(40) | Required | Type of problem |
| `description` | VARCHAR(1000) | Required | User explanation |
| `suggested_status` | VARCHAR(40) | Required | Proposed status if verified |
| `report_status` | VARCHAR(20) | Required, default `PENDING` | Review state |
| `reported_at` | TIMESTAMPTZ | Required, default now | Submission time |
| `reviewed_at` | TIMESTAMPTZ | Optional | Administrator review time |
| `admin_note` | VARCHAR(500) | Optional | Internal review note, not shown publicly |

The reporter's name, phone number and email are intentionally not collected in the MVP.

## Fixed values

**`facility_type`:** `LIFT`, `RAMP`, `STEP_FREE_ENTRANCE`, `ACCESSIBLE_RESTROOM`, `ACCESSIBLE_ROUTE`, `OTHER`.

**`current_status`:** `AVAILABLE`, `TEMPORARILY_UNAVAILABLE`, `UNDER_MAINTENANCE`, `UNKNOWN`.

**`issue_type`:** `OUT_OF_SERVICE`, `ROUTE_BLOCKED`, `ACCESS_BLOCKED`, `FACILITY_UNAVAILABLE`, `OTHER`.

**`suggested_status`:** `TEMPORARILY_UNAVAILABLE`, `UNDER_MAINTENANCE`.

**`report_status`:** `PENDING`, `VERIFIED`, `REJECTED`, `RESOLVED`.

## Exact Java entity mapping
Use the following Java fields so the agent does not invent a different schema:
- `Building`: `Long id`, `String name`, `String campusZone`, `OffsetDateTime createdAt`.
- `Facility`: `Long id`, `Building building`, `String name`, `FacilityType facilityType`, `String floorLabel`, `String description`, `FacilityStatus currentStatus`, `OffsetDateTime lastVerifiedAt`, `OffsetDateTime createdAt`.
- `StatusReport`: `Long id`, `Facility facility`, `IssueType issueType`, `String description`, `FacilityStatus suggestedStatus`, `ReportStatus reportStatus`, `OffsetDateTime reportedAt`, `OffsetDateTime reviewedAt`, `String adminNote`.

Map enums using `@Enumerated(EnumType.STRING)`. Because `suggestedStatus` uses `FacilityStatus`, validate it strictly to only `TEMPORARILY_UNAVAILABLE` or `UNDER_MAINTENANCE` when a public report is created. Use explicit `@Table`/`@Column` names matching the SQL. Keep relationships unidirectional with `@ManyToOne`; do not add `@OneToMany` collections merely for symmetry. API DTOs, not entities, are returned to the frontend.

## Integrity and querying rules
- Use foreign keys; do not save a report for a nonexistent facility.
- The supplied SQL script creates indexes on `facilities.building_id`, `facilities.facility_type`, `facilities.current_status`, `status_reports.report_status` and `status_reports.facility_id`. Keep these indexes; they are already included in the starter schema.
- Use parameterized ORM operations; do not build SQL by concatenating user text.
- A submitted report is always saved as `PENDING` on the backend. Do not accept `report_status`, `reviewed_at` or `admin_note` from a public request.
- Verifying a report updates `status_reports` and `facilities` within one transaction.
- Marking a facility `AVAILABLE` after checking it updates `last_verified_at` and marks its previously verified reports `RESOLVED`.
- Preserve old reports instead of deleting them when an issue is rejected or fixed; this keeps a simple history.

## Schema setup
Use `database/schema_and_seed.sql` in Supabase SQL Editor to create the three tables and insert sample data. The SQL uses `CREATE TABLE IF NOT EXISTS` and sample inserts that avoid repeating the same rows. For this project, the SQL script is the sole schema-creation source of truth and `spring.jpa.hibernate.ddl-auto=validate` must be used. Do not change it to `update` or ask Hibernate to create a second schema; validation should fail at startup if the entities do not match the SQL.
