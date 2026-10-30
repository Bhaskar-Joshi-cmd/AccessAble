// AccessAble — client-side interactions for the public directory and admin workflows.
const API_BASE_URL = 'http://localhost:8080';
const STATUS_STALE_DAYS = 30;

let facilities = [];
let allFacilities = [];
let buildings = [];
let pendingReports = [];
let selectedReportId = null;
let adminKey = '';

const searchInput = document.getElementById('search-input');
const buildingFilter = document.getElementById('building-filter');
const typeFilter = document.getElementById('type-filter');
const statusFilter = document.getElementById('status-filter');
const clearFiltersBtn = document.getElementById('clear-filters-btn');
const loadingMessage = document.getElementById('loading-message');
const facilitiesList = document.getElementById('facilities-list');
const emptyMessage = document.getElementById('empty-message');
const errorMessage = document.getElementById('error-message');
const resultsCount = document.getElementById('results-count');

const totalFacilitiesEl = document.getElementById('total-facilities');
const availableFacilitiesEl = document.getElementById('available-facilities');
const temporarilyUnavailableFacilitiesEl = document.getElementById('temporarily-unavailable-facilities');
const underMaintenanceFacilitiesEl = document.getElementById('under-maintenance-facilities');
const unknownFacilitiesEl = document.getElementById('unknown-facilities');
const pendingReportsEl = document.getElementById('pending-reports');

const reportForm = document.getElementById('report-form');
const reportFacilitySelect = document.getElementById('report-facility');
const reportIssueType = document.getElementById('report-issue-type');
const reportSuggestedStatus = document.getElementById('report-suggested-status');
const reportDescription = document.getElementById('report-description');
const charCount = document.getElementById('char-count');
const reportError = document.getElementById('report-error');
const reportSuccess = document.getElementById('report-success');
const reportSubmitBtn = document.getElementById('report-submit-btn');

const adminKeyInput = document.getElementById('admin-key-input');
const loadPendingReportsBtn = document.getElementById('load-pending-reports-btn');
const adminLoading = document.getElementById('admin-loading');
const adminError = document.getElementById('admin-error');
const adminFeedback = document.getElementById('admin-feedback');
const pendingReportsList = document.getElementById('pending-reports-list');
const adminReviewForm = document.getElementById('admin-review-form');
const reviewForm = document.getElementById('review-report-form');
const reviewDecision = document.getElementById('review-decision');
const reviewNote = document.getElementById('review-note');
const reviewSubmitBtn = document.getElementById('review-submit-btn');
const selectedReportLabel = document.getElementById('selected-report-label');
const adminFacilitySelect = document.getElementById('admin-facility-select');
const adminStatusSelect = document.getElementById('admin-status-select');
const facilityStatusForm = document.getElementById('facility-status-form');
const facilityStatusSubmitBtn = document.getElementById('facility-status-submit-btn');
const adminFacilityStatusResult = document.getElementById('admin-facility-status-result');
const facilityDetails = document.getElementById('facility-details');

function createElement(tag, className, text) {
    const element = document.createElement(tag);
    if (className) element.className = className;
    if (text !== undefined && text !== null) element.textContent = String(text);
    return element;
}

function setFeedback(element, message, type) {
    if (!element) return;
    element.textContent = message;
    element.classList.remove('is-error', 'is-success', 'is-info');
    element.classList.add(type || 'is-info');
    element.hidden = false;
}

function clearFeedback(element) {
    if (!element) return;
    element.hidden = true;
    element.textContent = '';
    element.classList.remove('is-error', 'is-success', 'is-info');
}

function friendlyStatus(value) {
    if (!value) return 'Unknown';
    return String(value)
        .toLowerCase()
        .split('_')
        .map(part => part.charAt(0).toUpperCase() + part.slice(1))
        .join(' ');
}

function friendlyFacilityType(value) {
    const names = {
        LIFT: 'Lift',
        RAMP: 'Ramp',
        STEP_FREE_ENTRANCE: 'Step-free entrance',
        ACCESSIBLE_RESTROOM: 'Accessible washroom',
        ACCESSIBLE_ROUTE: 'Accessible route',
        OTHER: 'Other'
    };
    return names[value] || friendlyStatus(value);
}

function statusClass(status) {
    return ['AVAILABLE', 'TEMPORARILY_UNAVAILABLE', 'UNDER_MAINTENANCE', 'UNKNOWN'].includes(status)
        ? status
        : 'UNKNOWN';
}

function facilitySymbol(type) {
    const symbols = {
        LIFT: '↕',
        RAMP: '↗',
        STEP_FREE_ENTRANCE: '⇥',
        ACCESSIBLE_RESTROOM: '＋',
        ACCESSIBLE_ROUTE: '⌁',
        OTHER: '◇'
    };
    return symbols[type] || '◇';
}

async function readResponseBody(response) {
    const bodyText = await response.text();
    if (!bodyText.trim()) return null;

    try {
        return JSON.parse(bodyText);
    } catch (error) {
        return bodyText;
    }
}

async function fetchApi(endpoint, options = {}) {
    let response;
    try {
        response = await fetch(API_BASE_URL + endpoint, options);
    } catch (error) {
        throw new Error('Could not connect to the API. Check that the backend is running and try again.');
    }

    const data = await readResponseBody(response);

    if (!response.ok) {
        let message = '';
        if (data && typeof data === 'object') {
            message = data.message || data.detail || data.error || '';
        } else if (typeof data === 'string') {
            message = data;
        }

        throw new Error('HTTP ' + response.status + (message ? ': ' + message : ' — request failed.'));
    }

    return data;
}

function showLoading() {
    loadingMessage.hidden = false;
    facilitiesList.hidden = true;
    emptyMessage.hidden = true;
    errorMessage.hidden = true;
}

function hideLoading() {
    loadingMessage.hidden = true;
}

function showError(message) {
    errorMessage.textContent = message;
    errorMessage.hidden = false;
    facilitiesList.hidden = true;
    emptyMessage.hidden = true;
    loadingMessage.hidden = true;
}

function showEmpty() {
    emptyMessage.hidden = false;
    facilitiesList.hidden = true;
    errorMessage.hidden = true;
    loadingMessage.hidden = true;
}

function showFacilitiesList() {
    facilitiesList.hidden = false;
    emptyMessage.hidden = true;
    errorMessage.hidden = true;
    loadingMessage.hidden = true;
}

async function loadBuildings() {
    try {
        const data = await fetchApi('/api/buildings');
        buildings = Array.isArray(data) ? data : [];

        const currentValue = buildingFilter.value;
        buildingFilter.replaceChildren();
        buildingFilter.appendChild(new Option('All buildings', ''));
        buildings.forEach(building => {
            buildingFilter.appendChild(new Option(building.name, String(building.id)));
        });
        buildingFilter.value = currentValue;
    } catch (error) {
        console.error('Failed to load buildings:', error);
    }
}

async function loadSummary() {
    try {
        const data = await fetchApi('/api/summary');
        if (!data) throw new Error('The summary response was empty.');

        totalFacilitiesEl.textContent = data.totalFacilities ?? '—';
        availableFacilitiesEl.textContent = data.available ?? '—';
        temporarilyUnavailableFacilitiesEl.textContent = data.temporarilyUnavailable ?? '—';
        underMaintenanceFacilitiesEl.textContent = data.underMaintenance ?? '—';
        unknownFacilitiesEl.textContent = data.unknown ?? '—';
        pendingReportsEl.textContent = data.pendingReports ?? '—';
    } catch (error) {
        console.error('Failed to load summary:', error);
        [totalFacilitiesEl, availableFacilitiesEl, temporarilyUnavailableFacilitiesEl,
            underMaintenanceFacilitiesEl, unknownFacilitiesEl, pendingReportsEl]
            .forEach(element => { element.textContent = '—'; });
    }
}

async function loadFacilities() {
    showLoading();
    try {
        const params = new URLSearchParams();
        if (currentFilters.search) params.append('search', currentFilters.search);
        if (currentFilters.buildingId) params.append('buildingId', currentFilters.buildingId);
        if (currentFilters.type) params.append('type', currentFilters.type);
        if (currentFilters.status) params.append('status', currentFilters.status);

        const queryString = params.toString();
        const endpoint = queryString ? '/api/facilities?' + queryString : '/api/facilities';
        const data = await fetchApi(endpoint);
        facilities = Array.isArray(data) ? data : [];

        if (!queryString) {
            allFacilities = facilities.slice();
            populateFacilityDropdowns();
        } else if (allFacilities.length === 0) {
            allFacilities = facilities.slice();
            populateFacilityDropdowns();
        }

        renderFacilities(facilities);
    } catch (error) {
        console.error('Failed to load facilities:', error);
        showError('We couldn’t load the facility list. Check your connection and try again.');
        if (resultsCount) resultsCount.textContent = 'Unable to load';
    }
}

function renderFacilities(facilityList) {
    facilitiesList.replaceChildren();

    if (resultsCount) {
        resultsCount.textContent = facilityList.length + (facilityList.length === 1 ? ' facility' : ' facilities');
    }

    if (!facilityList || facilityList.length === 0) {
        showEmpty();
        return;
    }

    facilityList.forEach(facility => {
        const card = createElement('article', 'facility-card');
        const top = createElement('div', 'facility-card-top');
        const identity = createElement('div', 'facility-identity');
        const symbol = createElement('span', 'facility-card-icon', facilitySymbol(facility.facilityType));
        symbol.setAttribute('aria-hidden', 'true');

        const name = createElement('h3', '', facility.name || 'Unnamed facility');
        identity.append(symbol, name, createElement('span', 'facility-type', friendlyFacilityType(facility.facilityType)));

        const badge = createElement('span', 'status-badge ' + statusClass(facility.currentStatus), friendlyStatus(facility.currentStatus));
        top.append(identity, badge);
        card.appendChild(top);

        if (facility.description) {
            card.appendChild(createElement('p', 'facility-card-description', facility.description));
        }

        const meta = createElement('div', 'facility-meta');
        const location = createElement('div', 'facility-meta-row');
        location.append(createElement('span', '', '⌖'), createElement('span', '', [facility.buildingName, facility.floorLabel].filter(Boolean).join(' · ') || 'Location not specified'));
        meta.appendChild(location);

        const verified = createElement('div', 'facility-meta-row');
        verified.append(createElement('span', '', '◷'), createElement('span', 'last-verified', 'Last verified: ' + formatLastVerified(facility.lastVerifiedAt)));
        meta.appendChild(verified);
        card.appendChild(meta);

        const detailsButton = createElement('button', 'btn btn-quiet', 'View details →');
        detailsButton.type = 'button';
        detailsButton.addEventListener('click', () => loadFacilityDetails(facility.id));
        card.appendChild(detailsButton);

        facilitiesList.appendChild(card);
    });

    showFacilitiesList();
}

function formatLastVerified(timestamp) {
    if (!timestamp) return 'Not yet verified';
    const date = new Date(timestamp);
    if (Number.isNaN(date.getTime())) return String(timestamp);

    const diffDays = Math.floor((Date.now() - date.getTime()) / (1000 * 60 * 60 * 24));
    const readableDate = date.toLocaleString(undefined, {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: 'numeric', minute: '2-digit'
    });
    if (diffDays > STATUS_STALE_DAYS) {
        return 'Status may be out of date · ' + readableDate;
    }
    return readableDate;
}

async function loadFacilityDetails(facilityId) {
    try {
        const facility = await fetchApi('/api/facilities/' + encodeURIComponent(facilityId));
        if (!facility) throw new Error('No facility details were returned.');
        renderFacilityDetails(facility);
        document.getElementById('facility-details-section').scrollIntoView({ behavior: 'smooth', block: 'start' });
    } catch (error) {
        console.error('Failed to load facility details:', error);
        facilityDetails.replaceChildren();
        facilityDetails.appendChild(createElement('p', 'feedback-message is-error', 'Could not load facility details. Please try again.'));
    }
}

function renderFacilityDetails(facility) {
    facilityDetails.replaceChildren();

    const top = createElement('div', 'details-profile-top');
    const symbol = createElement('span', 'facility-card-icon details-icon', facilitySymbol(facility.facilityType));
    const nameBlock = createElement('div');
    nameBlock.append(createElement('p', 'eyebrow', 'SELECTED FACILITY'), createElement('h3', '', facility.name || 'Unnamed facility'));
    top.append(symbol, nameBlock, createElement('span', 'status-badge ' + statusClass(facility.currentStatus), friendlyStatus(facility.currentStatus)));
    facilityDetails.appendChild(top);

    const info = createElement('div', 'facility-info');
    const fields = [
        ['Building', facility.buildingName || 'Not specified'],
        ['Campus zone', facility.campusZone || 'Not specified'],
        ['Facility type', friendlyFacilityType(facility.facilityType)],
        ['Floor / location', facility.floorLabel || 'Not specified'],
        ['Description', facility.description || 'No description provided'],
        ['Last verified', formatLastVerified(facility.lastVerifiedAt)]
    ];

    fields.forEach(([label, value]) => {
        const item = createElement('div', 'facility-info-item');
        item.append(createElement('span', 'facility-info-label', label), createElement('p', '', value));
        info.appendChild(item);
    });
    facilityDetails.appendChild(info);

    const actions = createElement('div', 'details-actions');
    const reportButton = createElement('a', 'btn btn-primary', 'Report a problem →');
    reportButton.href = '#report-section';
    reportButton.addEventListener('click', () => {
        reportFacilitySelect.value = String(facility.id);
        reportFacilitySelect.focus({ preventScroll: true });
    });
    actions.appendChild(reportButton);
    facilityDetails.appendChild(actions);
}

function populateFacilityDropdowns() {
    const source = allFacilities.length ? allFacilities : facilities;
    const selectedReportFacility = reportFacilitySelect.value;
    const selectedAdminFacility = adminFacilitySelect.value;

    reportFacilitySelect.replaceChildren(new Option('Select a facility…', ''));
    adminFacilitySelect.replaceChildren(new Option('Select a facility…', ''));

    source.forEach(facility => {
        const label = facility.name + (facility.buildingName ? ' · ' + facility.buildingName : '');
        reportFacilitySelect.appendChild(new Option(label, String(facility.id)));
        adminFacilitySelect.appendChild(new Option(label, String(facility.id)));
    });

    reportFacilitySelect.value = selectedReportFacility;
    adminFacilitySelect.value = selectedAdminFacility;
}

const currentFilters = { search: '', buildingId: '', type: '', status: '' };

function filterFacilities() {
    currentFilters.search = searchInput.value.trim();
    currentFilters.buildingId = buildingFilter.value;
    currentFilters.type = typeFilter.value;
    currentFilters.status = statusFilter.value;
    loadFacilities();
}

function clearFilters() {
    searchInput.value = '';
    buildingFilter.value = '';
    typeFilter.value = '';
    statusFilter.value = '';
    currentFilters.search = '';
    currentFilters.buildingId = '';
    currentFilters.type = '';
    currentFilters.status = '';
    loadFacilities();
}

function getAdminKey() {
    adminKey = adminKeyInput.value.trim();
    return adminKey;
}

async function loadPendingReports() {
    const key = getAdminKey();
    if (!key) {
        setFeedback(adminError, 'Enter your admin key to load the review queue.', 'is-error');
        adminKeyInput.focus();
        return false;
    }

    clearFeedback(adminError);
    adminLoading.hidden = false;
    pendingReportsList.setAttribute('aria-busy', 'true');
    loadPendingReportsBtn.disabled = true;
    loadPendingReportsBtn.textContent = 'Loading reports…';

    try {
        const response = await fetchApi('/api/reports?status=PENDING', {
            method: 'GET',
            headers: { 'X-Admin-Key': key }
        });
        pendingReports = Array.isArray(response) ? response : [];
        renderPendingReports(pendingReports);
        adminLoading.hidden = true;
        pendingReportsList.hidden = false;
        return true;
    } catch (error) {
        adminLoading.hidden = true;
        setFeedback(adminError, 'Could not load pending reports. ' + error.message, 'is-error');
        return false;
    } finally {
        pendingReportsList.removeAttribute('aria-busy');
        loadPendingReportsBtn.disabled = false;
        loadPendingReportsBtn.innerHTML = 'Load pending reports <span aria-hidden="true">→</span>';
    }
}

function renderPendingReports(reports) {
    pendingReportsList.replaceChildren();

    if (!reports.length) {
        const empty = createElement('div', 'admin-empty-state');
        empty.append(createElement('span', '', '✓'), createElement('p', '', 'The queue is clear. There are no pending reports right now.'));
        pendingReportsList.appendChild(empty);
        return;
    }

    reports.forEach(report => {
        const item = createElement('article', 'pending-report-item');
        const header = createElement('div', 'pending-report-header');
        const identity = createElement('div');
        identity.append(
            createElement('h4', '', 'Report #' + report.id),
            createElement('span', 'report-date', 'Submitted ' + formatReportDate(report.reportedAt))
        );
        header.append(identity, createElement('span', 'report-status-label', 'Pending review'));
        item.appendChild(header);

        const details = createElement('dl', 'report-details');
        appendReportDetail(details, 'Facility', report.facilityName || 'Unknown');
        appendReportDetail(details, 'Building', report.buildingName || 'Unknown');
        appendReportDetail(details, 'Issue type', friendlyStatus(report.issueType));
        appendReportDetail(details, 'Suggested status', friendlyStatus(report.suggestedStatus));
        item.appendChild(details);

        const description = createElement('p', 'report-description-full', report.description || 'No description provided.');
        item.appendChild(description);

        const actions = createElement('div', 'report-actions');
        const button = createElement('button', 'btn btn-secondary btn-sm review-report-btn', 'Review report →');
        button.type = 'button';
        button.dataset.action = 'review';
        button.dataset.reportId = String(report.id);
        actions.appendChild(button);
        item.appendChild(actions);
        pendingReportsList.appendChild(item);
    });
}

function appendReportDetail(parent, label, value) {
    const wrapper = createElement('div', 'report-detail');
    wrapper.append(createElement('dt', '', label), createElement('dd', '', value));
    parent.appendChild(wrapper);
}

function formatReportDate(timestamp) {
    if (!timestamp) return 'date unavailable';
    const date = new Date(timestamp);
    if (Number.isNaN(date.getTime())) return String(timestamp);
    return date.toLocaleString(undefined, { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' });
}

function selectReportForReportReview(reportId) {
    const report = pendingReports.find(item => String(item.id) === String(reportId));
    if (!report) {
        setFeedback(adminError, 'That report is no longer in the current review queue. Reload the list and try again.', 'is-error');
        return;
    }

    selectedReportId = report.id;
    reviewForm.reset();
    clearFeedback(adminError);
    adminReviewForm.hidden = false;
    selectedReportLabel.textContent = 'Report #' + report.id + ' · ' + (report.facilityName || 'Unknown facility');
    reviewDecision.value = '';
    reviewNote.value = '';
    adminReviewForm.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    reviewDecision.focus({ preventScroll: true });
}

async function refreshPublicData() {
    try {
        const allData = await fetchApi('/api/facilities');
        if (Array.isArray(allData)) {
            allFacilities = allData;
            populateFacilityDropdowns();
        }
    } catch (error) {
        console.error('Could not refresh the facility dropdown data:', error);
    }

    await Promise.all([loadFacilities(), loadSummary()]);
}

async function initializePage() {
    showLoading();
    await Promise.all([loadBuildings(), loadSummary(), loadFacilities()]);
    hideLoading();
}

searchInput.addEventListener('input', filterFacilities);
buildingFilter.addEventListener('change', filterFacilities);
typeFilter.addEventListener('change', filterFacilities);
statusFilter.addEventListener('change', filterFacilities);
clearFiltersBtn.addEventListener('click', clearFilters);

reportDescription.addEventListener('input', () => {
    charCount.textContent = reportDescription.value.length + ' / 1000';
});

reportForm.addEventListener('submit', async event => {
    event.preventDefault();
    clearFeedback(reportError);
    clearFeedback(reportSuccess);

    const facilityId = reportFacilitySelect.value;
    const issueType = reportIssueType.value;
    const suggestedStatus = reportSuggestedStatus.value;
    const description = reportDescription.value.trim();

    if (!facilityId || !issueType || !suggestedStatus || !description) {
        setFeedback(reportError, 'Complete all required fields before submitting your report.', 'is-error');
        return;
    }
    if (description.length < 10 || description.length > 1000) {
        setFeedback(reportError, 'Your description must contain between 10 and 1,000 characters.', 'is-error');
        reportDescription.focus();
        return;
    }

    reportSubmitBtn.disabled = true;
    reportSubmitBtn.textContent = 'Submitting report…';

    try {
        const data = await fetchApi('/api/reports', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ facilityId: Number(facilityId), issueType, suggestedStatus, description })
        });

        if (!data || data.id === undefined || data.id === null) {
            throw new Error('The report may have been created, but the API did not return a report ID. Check the pending queue before retrying.');
        }

        reportForm.reset();
        charCount.textContent = '0 / 1000';
        setFeedback(reportSuccess, 'Report #' + data.id + ' submitted successfully. It is now awaiting administrator review.', 'is-success');
        await loadSummary();
    } catch (error) {
        setFeedback(reportError, 'Report submission failed. ' + error.message, 'is-error');
    } finally {
        reportSubmitBtn.disabled = false;
        reportSubmitBtn.innerHTML = 'Submit report <span aria-hidden="true">→</span>';
    }
});

adminKeyInput.addEventListener('input', () => {
    adminKey = adminKeyInput.value.trim();
    clearFeedback(adminError);
});

loadPendingReportsBtn.addEventListener('click', loadPendingReports);

pendingReportsList.addEventListener('click', event => {
    const button = event.target.closest('button[data-action="review"]');
    if (button) selectReportForReportReview(button.dataset.reportId);
});

reviewForm.addEventListener('submit', async event => {
    event.preventDefault();
    clearFeedback(adminError);
    clearFeedback(adminFeedback);

    const reportId = selectedReportId;
    const decision = reviewDecision.value;
    const note = reviewNote.value.trim();
    const key = getAdminKey();

    if (reportId === null || reportId === undefined) {
        setFeedback(adminError, 'Choose a pending report to review first.', 'is-error');
        return;
    }
    if (!decision) {
        setFeedback(adminError, 'Choose Verify or Reject before submitting.', 'is-error');
        reviewDecision.focus();
        return;
    }
    if (!key) {
        setFeedback(adminError, 'Enter your admin key before submitting a review.', 'is-error');
        adminKeyInput.focus();
        return;
    }

    reviewSubmitBtn.disabled = true;
    reviewSubmitBtn.textContent = 'Submitting review…';

    try {
        await fetchApi('/api/reports/' + encodeURIComponent(reportId) + '/review', {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'X-Admin-Key': key },
            body: JSON.stringify({ decision, adminNote: note || null })
        });

        setFeedback(adminFeedback, 'Report #' + reportId + ' ' + (decision === 'VERIFY' ? 'verified' : 'rejected') + ' successfully.', 'is-success');
        selectedReportId = null;
        adminReviewForm.hidden = true;
        reviewForm.reset();

        await Promise.all([loadSummary(), loadPendingReports()]);
        if (decision === 'VERIFY') await refreshPublicData();
    } catch (error) {
        setFeedback(adminError, 'Review failed. ' + error.message, 'is-error');
    } finally {
        reviewSubmitBtn.disabled = false;
        reviewSubmitBtn.innerHTML = 'Submit review <span aria-hidden="true">→</span>';
    }
});

facilityStatusForm.addEventListener('submit', async event => {
    event.preventDefault();
    clearFeedback(adminFacilityStatusResult);

    const facilityId = adminFacilitySelect.value;
    const status = adminStatusSelect.value;
    const key = getAdminKey();

    if (!facilityId || !status || !key) {
        let message = 'Select a facility and status.';
        if (!key) message = 'Enter your admin key before updating a facility.';
        else if (!facilityId) message = 'Select a facility first.';
        else if (!status) message = 'Select the new status first.';
        setFeedback(adminFacilityStatusResult, message, 'is-error');
        return;
    }

    facilityStatusSubmitBtn.disabled = true;
    facilityStatusSubmitBtn.textContent = 'Updating status…';

    try {
        await fetchApi('/api/facilities/' + encodeURIComponent(facilityId) + '/status', {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'X-Admin-Key': key },
            body: JSON.stringify({ status })
        });

        const facility = allFacilities.find(item => String(item.id) === String(facilityId));
        const facilityName = facility ? facility.name : 'Facility #' + facilityId;
        setFeedback(adminFacilityStatusResult, facilityName + ' is now marked ' + friendlyStatus(status) + '.', 'is-success');
        await refreshPublicData();
        await loadPendingReports();
    } catch (error) {
        setFeedback(adminFacilityStatusResult, 'Status update failed. ' + error.message, 'is-error');
    } finally {
        facilityStatusSubmitBtn.disabled = false;
        facilityStatusSubmitBtn.innerHTML = 'Update status <span aria-hidden="true">→</span>';
    }
});

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializePage, { once: true });
} else {
    initializePage();
}
