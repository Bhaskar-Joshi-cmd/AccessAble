package com.accessable.model;

/**
 * The official recorded status of a facility (matches the CHECK constraint on
 * facilities.current_status). Also used for a report's suggested_status, where the
 * service layer allows only TEMPORARILY_UNAVAILABLE or UNDER_MAINTENANCE.
 */
public enum FacilityStatus {
    AVAILABLE,
    TEMPORARILY_UNAVAILABLE,
    UNDER_MAINTENANCE,
    UNKNOWN
}