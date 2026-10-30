package com.accessable.model;

/**
 * The kind of facility (matches the CHECK constraint on facilities.facility_type).
 * Kept as a Java enum so invalid values cannot be stored or sent over the API.
 */
public enum FacilityType {
    LIFT,
    RAMP,
    STEP_FREE_ENTRANCE,
    ACCESSIBLE_RESTROOM,
    ACCESSIBLE_ROUTE,
    OTHER
}