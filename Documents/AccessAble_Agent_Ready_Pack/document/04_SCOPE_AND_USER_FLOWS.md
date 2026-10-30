# 04 — Scope, Statuses and User Flows

## In scope for the first version
- Building directory and facility directory.
- Filter by building, facility type and recorded status.
- Facility detail including floor/location notes and last verification time.
- Public problem report form.
- Pending report review by an administrator.
- Protected verification/rejection and facility status update endpoints.
- Basic counts/summary.
- Plain HTML/CSS/JavaScript frontend.

## Out of scope
- GPS, interactive campus maps and turn-by-turn routes.
- Uploading photos or medical/disability information.
- User accounts, social login and full role-management system.
- Email, SMS, WhatsApp or push notifications.
- AI-generated recommendations, machine learning or real-time IoT sensor data.
- Complex analytics, ratings, comments or a mobile app.
- Claims that the system certifies compliance with accessibility legislation.

## Status values

### Facility status
- `AVAILABLE` — last known checked status indicates the facility is usable.
- `TEMPORARILY_UNAVAILABLE` — the facility or route is currently reported unusable.
- `UNDER_MAINTENANCE` — the facility is being repaired or inspected.
- `UNKNOWN` — the current state has not been verified or is not known.

### Report status
- `PENDING` — submitted and awaiting review.
- `VERIFIED` — administrator confirmed the report and applied its suggested facility status.
- `REJECTED` — administrator reviewed it and decided not to apply it.
- `RESOLVED` — issue was recorded as fixed after the facility was checked and marked available.

### Facility types
`LIFT`, `RAMP`, `STEP_FREE_ENTRANCE`, `ACCESSIBLE_RESTROOM`, `ACCESSIBLE_ROUTE`, `OTHER`.

### Issue types
`OUT_OF_SERVICE`, `ROUTE_BLOCKED`, `ACCESS_BLOCKED`, `FACILITY_UNAVAILABLE`, `OTHER`.

## Public-user flow
1. Open AccessAble.
2. View facility summary and list.
3. Optionally filter by building/type/status or search by name.
4. Open a facility to review location, current recorded status and last verification time.
5. Select “Report a problem.”
6. Choose the facility, issue type and suggested status; enter a clear description.
7. Submit the report.
8. See a confirmation that the report is pending review.

## Administrator flow
1. Open the administrator review area or use Postman during API development.
2. Enter the admin key at runtime; do not save it in the source code or frontend bundle.
3. View pending reports.
4. Check the report and the physical facility information.
5. Verify or reject the report with an optional internal note.
6. If a repair is completed, update the facility to `AVAILABLE`.
7. Confirm the updated public status and timestamp.

## State transition rules
- New report: always `PENDING`.
- `PENDING` → `VERIFIED` only after an administrator verifies it.
- `PENDING` → `REJECTED` after administrator rejection.
- A verified report becomes `RESOLVED` when an administrator marks its facility `AVAILABLE` after checking it.
- A report that is already reviewed cannot be verified/rejected again; return `409 Conflict`.
- Public report submission never changes a facility's official status.
- `last_verified_at` changes only when an administrator updates/validates the official facility status, not when an unreviewed report is submitted.

## User-visible wording
Prefer “Last verified on…” and “Report awaiting review” over “live status.” If the timestamp is old or absent, show “Status may be out of date” or “Not yet verified.” Do not suggest that an unavailable facility is the only route or offer emergency guidance.
