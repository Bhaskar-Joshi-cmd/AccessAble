# 08 — UI/UX Specification

## Design approach
Make the interface simple, calm and readable. This is an information tool, not a social network. Use a small number of pages/sections, clear text labels and a responsive layout. Avoid animations and chart libraries in the MVP.

## Public page sections

### Header
- Project name: AccessAble.
- Short tagline: “Check the latest reported status of campus accessibility facilities.”
- Navigation links: Facilities, Report a Problem, Admin (optional; only if admin form is implemented).

### Summary strip
Display counts for total facilities, available, temporarily unavailable, under maintenance, unknown and pending reports. Count cards must include text and not only colors.

### Facility directory
Each facility card or table row should show:
- Facility name and type.
- Building and floor/location label.
- Current recorded status as text.
- Last verified time or “Not yet verified.”
- A “View details” action and a “Report a problem” action.

Filters: search text, building dropdown, facility type dropdown and status dropdown. Add a “Clear filters” action.

### Facility details
Show full description, building/zone, floor, status, last verified time and a warning that status can change. Use “Not yet verified” when there is no verification time. Use “Status may be out of date” when `lastVerifiedAt` is more than 30 days old. Keep the threshold as a clearly named `STATUS_STALE_DAYS = 30` constant in `app.js`; display the exact timestamp as well.

### Report form
Fields:
- Facility (required, selectable from loaded facilities).
- Issue type (required dropdown).
- Suggested status (required: temporarily unavailable or under maintenance).
- Description (required, 10–1000 characters).
- Submit button.

Do not ask for name, phone number, student ID, disability details or location tracking. On success, display a report ID and “Your report is awaiting review.” Prevent double submission while the request is in progress. Preserve the form fields if submission fails.

### Admin review section (include in the final demo; Postman is acceptable during backend development)
- The administrator enters the admin key manually each session.
- Load pending reports only after the key is entered.
- Display facility, issue type, description, suggested status and reported date.
- Provide Verify and Reject actions, plus an optional internal review note (maximum 500 characters).
- Provide a separate facility-status form with facility selector and status selector. This allows the administrator to mark a facility `AVAILABLE` after repair. It sends only `{ "status": "AVAILABLE" }` to the documented endpoint; do not add a status-update note because there is no facility-status history table.
- Never place a real admin key into static JavaScript, HTML, a query string or browser storage.
- If no admin interface is built, demonstrate this workflow in Postman.

## Loading, success and error states
- Loading: show a short “Loading facilities…” message.
- Empty result: “No facilities match these filters.”
- Network/API error: “We couldn't load the facility list. Check your connection and try again.”
- Validation: tell the user exactly which field needs attention.
- Report success: show confirmation and report ID.
- Admin conflict: say the report has already been reviewed or has changed; refresh the list.

## Accessibility requirements for the UI
- Use semantic headings, buttons, labels and form controls.
- Every input has a visible associated `<label>`.
- Keyboard users can reach all controls; focus outlines must remain visible.
- Status must use text, not color alone.
- Use sufficient contrast and readable font sizes.
- Button text describes actions (e.g. “Submit report”, not “Go”).
- Do not use flashing or motion-only status cues.
- Test at mobile width and with browser zoom.

## Suggested visual hierarchy
1. Page title and one-sentence purpose.
2. Summary counts.
3. Search/filter controls.
4. Facility results.
5. Report form in its own clearly labelled section.

## Implementation constraint
Use plain HTML, CSS and JavaScript. Keep rendering simple: build DOM elements safely and insert user-provided text through `textContent`, not raw `innerHTML` strings. One `app.js` file is acceptable for this small project; separate it later only if it becomes hard to follow.
