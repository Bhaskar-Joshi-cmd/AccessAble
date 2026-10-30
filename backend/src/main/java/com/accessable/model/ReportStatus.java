package com.accessable.model;

/**
 * The review state of a status report (matches the CHECK constraint on
 * status_reports.report_status). New public reports are always PENDING.
 */
public enum ReportStatus {
    PENDING,
    VERIFIED,
    REJECTED,
    RESOLVED
}