# 12 — Security and Privacy Notes

## Security model for the mini-project
The MVP intentionally does not implement a full user-account system. Public users can read facility information and submit reports. Administrator operations are protected by one server-side shared admin key. This keeps the Java implementation small enough for a syllabus project, but it is a **demonstration-level control, not a complete production authentication system**.

## Required rules
1. Keep `DB_PASSWORD` and `ADMIN_KEY` in local environment variables and deployment settings, never in Java source, frontend files or Git.
2. The database connection is only made by Spring Boot. The browser must not receive the PostgreSQL password or use a privileged Supabase service-role key. Enable Row Level Security (RLS) on the three tables and do not add broad anonymous policies, because this project accesses data through the Java backend rather than the Supabase browser API.
3. Protect admin endpoints on the backend; hiding an admin button in the frontend is not security.
4. Compare the supplied admin key on the server and reject missing/incorrect keys before reading or changing protected data.
5. Never log secrets or return them in error messages.
6. Validate all submitted input in the backend. Frontend validation is for user experience only.
7. Use HTTPS for deployed services and SSL for the database connection.
8. Use `textContent` (or safe DOM APIs) for untrusted report descriptions, not string-based `innerHTML` rendering.
9. Limit error output to a helpful message; do not return exception stack traces or database URLs.
10. Allow CORS only for the intended frontend origins.

RLS is defense-in-depth for accidental direct Data API access; it does not replace keeping database credentials private or protecting the Java API. The backend uses its server-side database connection and must enforce its own API/admin rules.

## Data minimization
Do not collect names, emails, phone numbers, student IDs, disability status, medical data, exact personal location or photos in the MVP. A report needs only the facility ID, issue type, suggested status, description and timestamps.

## Admin key handling
- Ask the administrator to type the key when using an admin browser screen; keep it in memory only for that page session and clear it on logout/refresh.
- Never hard-code the key, commit a `.env` file, store it in `localStorage`, or place it in a URL.
- Send it only in the `X-Admin-Key` header to the intended backend endpoint over HTTPS.
- Rotate it if it is exposed.
- For a real campus service with multiple administrators, replace the shared-key demonstration with proper authentication and authorization before collecting operational data.

## Abuse and reliability limitations
A public report endpoint can receive spam. Backend validation is mandatory, but a public deployment that accepts submissions from anyone should add rate limits, CAPTCHA or authenticated reporting if abuse becomes an issue. An administrator must review reports. The app should not display an unverified report as an official facility status.

## Real-world use disclaimer
The app is an information tracker, not an accessibility certification or emergency system. Status may be outdated or incorrect. Always show when the status was last verified and avoid wording that guarantees a facility is usable.

## Deployment checklist
- [ ] Repository contains no credentials or `.env` files.
- [ ] Secrets are set in backend environment configuration.
- [ ] Browser assets contain no database password/service-role key/admin key.
- [ ] Admin endpoints reject missing and incorrect keys.
- [ ] Backend validates all inputs.
- [ ] Frontend safely renders user-provided text.
- [ ] Production uses HTTPS and database SSL.
- [ ] CORS is restricted to intended origins.
- [ ] Only fictional/sample information is present in the public demo.
