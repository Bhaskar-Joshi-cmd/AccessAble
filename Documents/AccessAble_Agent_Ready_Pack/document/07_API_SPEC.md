# 07 — REST API Specification (Authoritative Contract)

## General rules
- Local base URL: `http://localhost:8080`; production base URL is the Render API URL.
- All request/response bodies use JSON and all paths start with `/api`.
- IDs are positive integer JSON numbers mapped to Java `Long` and PostgreSQL `BIGINT`.
- Enumerated values are exact uppercase strings. Reject invalid values with `400 Bad Request`.
- Timestamps are ISO-8601 strings with an offset, e.g. `2026-10-26T17:00:00+05:30`.
- Do not return JPA entity objects directly. Map them to response DTOs to avoid recursive relations, accidental fields and lazy-loading errors.
- Public endpoints: buildings, facilities, summary, report creation. Every report-list, report-review and facility-status update endpoint requires `X-Admin-Key`.
- A missing or incorrect admin key always returns `401 Unauthorized` with the standard error body; protected data must not be read or changed first.

## Shared fixed values
- Facility types: `LIFT`, `RAMP`, `STEP_FREE_ENTRANCE`, `ACCESSIBLE_RESTROOM`, `ACCESSIBLE_ROUTE`, `OTHER`.
- Facility status: `AVAILABLE`, `TEMPORARILY_UNAVAILABLE`, `UNDER_MAINTENANCE`, `UNKNOWN`.
- Issue types: `OUT_OF_SERVICE`, `ROUTE_BLOCKED`, `ACCESS_BLOCKED`, `FACILITY_UNAVAILABLE`, `OTHER`.
- Suggested status for a public report: `TEMPORARILY_UNAVAILABLE` or `UNDER_MAINTENANCE` only.
- Report status: `PENDING`, `VERIFIED`, `REJECTED`, `RESOLVED`.

## `GET /api/buildings` (public)
Returns an array ordered alphabetically by building name.

Example:
```json
[
  {"id": 1, "name": "Academic Block A", "campusZone": "North Campus"},
  {"id": 2, "name": "Library Block", "campusZone": "Central Campus"}
]
```

## `GET /api/facilities` (public)
Optional query parameters: `buildingId`, `type`, `status`, `search`.
- `buildingId`: positive integer; invalid or non-positive values return `400`.
- `type`: one of the facility types above; invalid value returns `400`.
- `status`: one of the facility statuses above; invalid value returns `400`.
- `search`: trim surrounding spaces and perform case-insensitive substring search against facility name or building name. Empty search is treated as no search filter.
- Filters combine with AND logic. Return an empty array, not an error, when no rows match. Order by building name, then facility name.
- The small campus dataset may be loaded and filtered in a clear, simple service method; do not add a search framework or advanced query abstraction.

Example response item:
```json
{
  "id": 4,
  "name": "Passenger lift",
  "buildingId": 1,
  "buildingName": "Academic Block A",
  "campusZone": "North Campus",
  "facilityType": "LIFT",
  "floorLabel": "Ground to Floor 3",
  "description": "Lift serving the main floors.",
  "currentStatus": "AVAILABLE",
  "lastVerifiedAt": "2026-10-20T11:30:00+05:30"
}
```

## `GET /api/facilities/{id}` (public)
Returns the same facility response fields as a list item. Return `404` if the ID does not exist.

## `GET /api/summary` (public)
Returns counts calculated from the current database state. `pendingReports` is an aggregate count only; this endpoint never returns report descriptions or admin notes.

```json
{
  "totalFacilities": 6,
  "available": 4,
  "temporarilyUnavailable": 0,
  "underMaintenance": 1,
  "unknown": 1,
  "pendingReports": 2
}
```

## `POST /api/reports` (public)
Request fields:
```json
{
  "facilityId": 4,
  "issueType": "OUT_OF_SERVICE",
  "suggestedStatus": "TEMPORARILY_UNAVAILABLE",
  "description": "The lift is not responding when the floor button is pressed."
}
```
Rules:
- `facilityId` is required and must exist; otherwise `400` for missing/invalid ID or `404` for a well-formed but nonexistent ID.
- `issueType` is required and must be in the allowed list.
- `suggestedStatus` is required and must be `TEMPORARILY_UNAVAILABLE` or `UNDER_MAINTENANCE`.
- Trim description; require 10–1000 characters after trimming.
- The server sets `reportStatus=PENDING` and `reportedAt=now`. It must never bind `reportStatus`, `reviewedAt` or `adminNote` from the public request. If an unexpected `reportStatus` field is sent, ignore it or reject the body, but a saved report must always be `PENDING`.
- Creating a report never changes facility status or `lastVerifiedAt`.

Success: `201 Created`, with this public response (no admin note):
```json
{
  "id": 17,
  "facilityId": 4,
  "facilityName": "Passenger lift",
  "issueType": "OUT_OF_SERVICE",
  "suggestedStatus": "TEMPORARILY_UNAVAILABLE",
  "description": "The lift is not responding when the floor button is pressed.",
  "reportStatus": "PENDING",
  "reportedAt": "2026-10-26T17:00:00+05:30"
}
```

## Admin header
All admin endpoints below require `X-Admin-Key: <ADMIN_KEY>`. The value comes from the backend environment variable `ADMIN_KEY`. The browser admin screen may hold a manually entered key in memory for that page session and send it only to the API over HTTPS; it must not store the key, bundle it, log it, or include it in a URL. This is a demonstration-level shared-secret scheme, not production authentication.

## `GET /api/reports?status=PENDING` (admin)
- Default `status` to `PENDING` if omitted.
- Allowed filter values are the report statuses above; invalid values return `400`.
- Return an array ordered by `reportedAt` ascending (oldest first).
- Each admin list item contains: `id`, `facilityId`, `facilityName`, `buildingName`, `issueType`, `description`, `suggestedStatus`, `reportStatus`, `reportedAt`, `reviewedAt`, `adminNote`.
- `adminNote` may be null. Never include it in public facility responses or public report-creation responses.

## `PATCH /api/reports/{id}/review` (admin)
Request:
```json
{"decision": "VERIFY", "adminNote": "Checked at the facility."}
```
`decision` must be `VERIFY` or `REJECT`. `adminNote` is optional, trimmed, and limited to 500 characters; it is visible only to admin endpoints.

Rules:
- The report must exist and currently be `PENDING`; otherwise return `404` or `409` respectively.
- On `VERIFY`: within one database transaction, set report status to `VERIFIED`, set `reviewedAt=now`, save optional note, set facility status to the report's `suggestedStatus`, and set facility `lastVerifiedAt=now`.
- On `REJECT`: set report status to `REJECTED`, set `reviewedAt=now`, save optional note, and do not change facility status or facility `lastVerifiedAt`.
- A report already reviewed cannot be reviewed again. Return `409` and do not change any row.
- Return the updated admin report response fields listed for the admin report list.

## `PATCH /api/facilities/{id}/status` (admin)
Request:
```json
{"status": "AVAILABLE"}
```
`status` is required and must be one of the four facility statuses. There is deliberately no `note` field because the database has no facility-status history table; do not accept and silently discard a note.

Rules:
- Return `404` if the facility does not exist.
- Update facility `currentStatus` and `lastVerifiedAt=now` after an administrator performs a check.
- If new status is `AVAILABLE`, mark all reports for the same facility currently in `VERIFIED` as `RESOLVED` in the same transaction.
- Other facility status values do not change report statuses.
- Return `200` with:
```json
{
  "id": 4,
  "currentStatus": "AVAILABLE",
  "lastVerifiedAt": "2026-10-26T17:00:00+05:30"
}
```

## Error response contract
All handled errors use this shape:
```json
{
  "timestamp": "2026-10-26T17:00:00+05:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Description must contain between 10 and 1000 characters.",
  "path": "/api/reports"
}
```
- `400`: input/validation error or invalid enum/query value.
- `401`: missing or incorrect admin key.
- `404`: building/facility/report ID does not exist.
- `409`: report is already reviewed or requested state transition cannot be applied.
- `500`: generic unexpected error only; do not expose stack traces, SQL, credentials or exception internals.

## Implementation details
- Use request/response DTOs and `@Valid` on request DTOs.
- Use one `@RestControllerAdvice`/global exception handler for validation, missing-resource and conflict errors.
- Keep multi-row state changes inside public service methods annotated with `@Transactional`.
- The service must validate state before saving; do not trust the frontend.
- Restrict CORS to the one exact origin configured for the current environment.
