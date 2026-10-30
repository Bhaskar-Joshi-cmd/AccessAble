package com.accessable.model;

/**
 * The type of accessibility issue being reported (matches the CHECK constraint on
 * status_reports.issue_type).
 */
public enum IssueType {
    OUT_OF_SERVICE,
    ROUTE_BLOCKED,
    ACCESS_BLOCKED,
    FACILITY_UNAVAILABLE,
    OTHER
}