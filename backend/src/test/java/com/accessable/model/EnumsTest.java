package com.accessable.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Plain JUnit test that the fixed-value enums contain exactly the values documented
 * in document/06_DATABASE_DESIGN.md and document/07_API_SPEC.md.
 */
class EnumsTest {

    @Test
    void facilityTypeHasDocumentedValues() {
        assertEquals(6, FacilityType.values().length);
        assertEquals(FacilityType.LIFT, FacilityType.valueOf("LIFT"));
        assertEquals(FacilityType.RAMP, FacilityType.valueOf("RAMP"));
        assertEquals(FacilityType.STEP_FREE_ENTRANCE, FacilityType.valueOf("STEP_FREE_ENTRANCE"));
        assertEquals(FacilityType.ACCESSIBLE_RESTROOM, FacilityType.valueOf("ACCESSIBLE_RESTROOM"));
        assertEquals(FacilityType.ACCESSIBLE_ROUTE, FacilityType.valueOf("ACCESSIBLE_ROUTE"));
        assertEquals(FacilityType.OTHER, FacilityType.valueOf("OTHER"));
    }

    @Test
    void facilityStatusHasDocumentedValues() {
        assertEquals(4, FacilityStatus.values().length);
        assertEquals(FacilityStatus.AVAILABLE, FacilityStatus.valueOf("AVAILABLE"));
        assertEquals(FacilityStatus.TEMPORARILY_UNAVAILABLE, FacilityStatus.valueOf("TEMPORARILY_UNAVAILABLE"));
        assertEquals(FacilityStatus.UNDER_MAINTENANCE, FacilityStatus.valueOf("UNDER_MAINTENANCE"));
        assertEquals(FacilityStatus.UNKNOWN, FacilityStatus.valueOf("UNKNOWN"));
    }

    @Test
    void issueTypeHasDocumentedValues() {
        assertEquals(5, IssueType.values().length);
        assertEquals(IssueType.OUT_OF_SERVICE, IssueType.valueOf("OUT_OF_SERVICE"));
        assertEquals(IssueType.ROUTE_BLOCKED, IssueType.valueOf("ROUTE_BLOCKED"));
        assertEquals(IssueType.ACCESS_BLOCKED, IssueType.valueOf("ACCESS_BLOCKED"));
        assertEquals(IssueType.FACILITY_UNAVAILABLE, IssueType.valueOf("FACILITY_UNAVAILABLE"));
        assertEquals(IssueType.OTHER, IssueType.valueOf("OTHER"));
    }

    @Test
    void reportStatusHasDocumentedValues() {
        assertEquals(4, ReportStatus.values().length);
        assertEquals(ReportStatus.PENDING, ReportStatus.valueOf("PENDING"));
        assertEquals(ReportStatus.VERIFIED, ReportStatus.valueOf("VERIFIED"));
        assertEquals(ReportStatus.REJECTED, ReportStatus.valueOf("REJECTED"));
        assertEquals(ReportStatus.RESOLVED, ReportStatus.valueOf("RESOLVED"));
    }
}