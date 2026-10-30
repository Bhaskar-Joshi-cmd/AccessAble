# 09 — Test Plan

## Test strategy
Test the business rules first through Postman and service/controller tests. Then test the browser flow. Test both successful and invalid requests. Seed data should be fictional and reproducible.

## API and functional test cases

| Test ID | Scenario | Steps/input | Expected result |
|---|---|---|---|
| T-01 | List buildings | `GET /api/buildings` | `200`; returns seed buildings. |
| T-02 | List all facilities | `GET /api/facilities` | `200`; facilities include building and status details. |
| T-03 | Filter by building | Set `buildingId` to a known ID | Every result belongs to that building. |
| T-04 | Filter by status | Set `status=AVAILABLE` | Every result has status `AVAILABLE`. |
| T-05 | Search with no matches | Search random nonexistent text | `200` with empty list, not a server error. |
| T-06 | Facility missing | `GET /api/facilities/999999` | `404`. |
| T-07 | Submit valid report | Existing facility, valid fields | `201`; status is `PENDING`. |
| T-08 | Public report does not update facility | Record facility status, submit report | Official facility status stays unchanged. |
| T-09 | Unknown facility ID | Submit report with a validly formatted but nonexistent `facilityId` | `404`; no report saved. |
| T-10 | Missing facility ID field | Submit a report without `facilityId` | `400`; no report saved. |
| T-11 | Invalid issue type | Submit unsupported issue type | `400`; no report saved. |
| T-12 | Blank description | Description is whitespace | `400`; no report saved. |
| T-13 | Description too short | Fewer than 10 characters | `400`; no report saved. |
| T-14 | Public tries to set VERIFIED | Include `reportStatus=VERIFIED` in body | Server ignores it or rejects the unknown field; report is never created as verified. |
| T-15 | Admin endpoint without key | Call review endpoint without header | `401`; no data changed. |
| T-16 | Admin endpoint wrong key | Send incorrect key | `401`; no data changed. |
| T-17 | Verify pending report | Correct key, `decision=VERIFY` | Report is `VERIFIED`; `reviewedAt` and facility `lastVerifiedAt` are set; facility status equals suggested status. |
| T-18 | Reject pending report | Correct key, `decision=REJECT` | Report is `REJECTED`; `reviewedAt` is set; facility status and `lastVerifiedAt` remain unchanged. |
| T-19 | Review same report twice | Verify a report, then verify again | Second action returns `409`; no second state change. |
| T-20 | Invalid admin decision | Send `decision=APPROVE_MAYBE` | `400`; no changes. |
| T-21 | Mark facility available | Admin sends `PATCH /api/facilities/{id}/status` with `{"status":"AVAILABLE"}` | Status and timestamp update; verified reports for it become `RESOLVED` in the same transaction. |
| T-22 | Facility status invalid | Send `status=WORKING_GREAT` | `400`; no changes. |
| T-23 | Summary counts | `GET /api/summary` | Counts match stored data. |
| T-24 | Database unavailable | Test with invalid DB config in local environment | Application logs a clear server-side error; API does not leak credentials. |
| T-25 | CORS | Call API from approved frontend origin | Browser request succeeds. A non-approved origin is not enabled by wildcard in production. |
| T-26 | HTML injection text | Submit description containing markup text | Text is stored/displayed as text; frontend does not execute it as HTML. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |
| T-27 | Admin list is protected | Call `GET /api/reports` without a key | `401`; no report details or admin notes are returned. |
| T-28 | Invalid filter enum | Call facilities with `?status=WORKING` or reports with `?status=NEW` | `400` with standard error body. |
| T-29 | Status timestamp freshness | Render facilities with null, recent and older-than-30-day `lastVerifiedAt` values | Correct “Not yet verified” or “Status may be out of date” message is shown. |
| T-30 | Direct facility status update with invalid ID | Admin sends valid status for nonexistent facility | `404`; no report or facility data changes. |

## UI manual checklist
- [ ] Page works at desktop and mobile widths.
- [ ] Loading and empty states display correctly.
- [ ] Search and each filter can be combined.
- [ ] Facility status and last verification time are visible.
- [ ] Form fields have labels and required-field messages.
- [ ] A valid report shows a confirmation and report ID.
- [ ] Failed report submission does not erase input or falsely show success.
- [ ] Public report submissions never change official status before review.
- [ ] Admin review requires a key and provides visible success/failure feedback.
- [ ] Key and DB password are absent from source files and network requests to unrelated endpoints.
- [ ] Text remains readable without relying on red/green color alone.

## Acceptance test: end-to-end demonstration
1. Open a seeded facility whose status is `AVAILABLE`.
2. Submit a report suggesting `TEMPORARILY_UNAVAILABLE`.
3. Refresh the directory; the status must still be `AVAILABLE`, and the report is pending.
4. Review the report as admin and select Verify.
5. Refresh the public directory; the facility is now `TEMPORARILY_UNAVAILABLE` and its verification time is updated.
6. Simulate the repair by updating the facility status to `AVAILABLE` as admin.
7. Confirm verified reports for that facility are marked `RESOLVED`.
8. Try an invalid or unauthorized request and show that the system rejects it.

## Test-data reset
Use the Supabase SQL editor to inspect/delete demo reports during development. Do not reset a database containing real or important data. Keep seed records fictional until deployment is complete.
