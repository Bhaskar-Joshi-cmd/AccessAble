# 01 — Project Brief

## Name
**AccessAble — Campus Accessibility Status Tracker**

## Problem statement
Campus accessibility information is often incomplete or out of date. A lift may be out of service, an entrance may be blocked, or an accessible washroom may temporarily be unavailable. A static list of facilities cannot communicate these short-term changes. People need a simple way to view the latest known status and report problems for review.

## Proposed solution
AccessAble maintains a small catalogue of accessibility facilities by building. Visitors can search the catalogue, view a facility's current recorded status and last verification time, and submit a report. An administrator reviews pending reports and updates the official facility status when appropriate.

## Main users
- **Campus visitor/student:** finds facility information and submits a problem report.
- **Administrator/maintenance coordinator:** reviews submitted reports, verifies or rejects them, and updates official facility status.

## MVP features
1. Browse buildings and facilities.
2. Filter facilities by building, facility type and recorded status.
3. View facility details and last-verified time.
4. Submit a problem report for an existing facility.
5. Admin reviews pending reports and verifies/rejects them.
6. Admin updates a facility status after a repair or new inspection.
7. View a simple count of facilities by status and pending reports.

## Platform decision
- Plain HTML/CSS/JavaScript frontend on Vercel.
- Java 21 + Spring Boot 3.5.16 REST backend on Render using Docker.
- VS Code is the development IDE; no IDE switch is required.
- VS Code is the development IDE; no IDE switch is required.
- VS Code is the development IDE; no IDE switch is required.
- VS Code is the development IDE; no IDE switch is required. Use Maven Wrapper; do not add Lombok or a frontend framework.
- Supabase PostgreSQL database.
- Spring Data JPA/Hibernate for normal database operations.
- Postman for API demonstrations and admin operations during early testing.

## Success looks like
A demo user can find a facility and submit a report; an administrator can review it; a verified report changes the official status; and the public facility page shows the updated state and timestamp. Invalid data is rejected with a useful message. Database credentials are never sent to the browser.

## Assumptions
- This is a student mini-project and initial demo, not a certified accessibility information service.
- The campus and facility catalogue is added as sample data or by a project administrator.
- Reports do not need a reporter account in the MVP.
- The frontend does not expose an admin secret. Admin actions are available on a small admin screen where the administrator enters the secret at runtime, or directly through Postman.
- Campus administrators must verify a report before the public status changes.
