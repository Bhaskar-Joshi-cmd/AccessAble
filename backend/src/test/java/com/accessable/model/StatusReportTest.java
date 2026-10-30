package com.accessable.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Plain JUnit test for the StatusReport entity. No database is needed.
 */
class StatusReportTest {

    @Test
    void gettersAndSettersWork() {
        Facility facility = new Facility();
        facility.setId(4L);

        StatusReport report = new StatusReport();
        report.setId(10L);
        report.setFacility(facility);
        report.setIssueType(IssueType.OUT_OF_SERVICE);
        report.setDescription("The lift does not stop at Floor 2.");
        report.setSuggestedStatus(FacilityStatus.TEMPORARILY_UNAVAILABLE);
        report.setReportStatus(ReportStatus.PENDING);

        assertEquals(10L, report.getId());
        assertSame(facility, report.getFacility());
        assertEquals(IssueType.OUT_OF_SERVICE, report.getIssueType());
        assertEquals("The lift does not stop at Floor 2.", report.getDescription());
        assertEquals(FacilityStatus.TEMPORARILY_UNAVAILABLE, report.getSuggestedStatus());
        assertEquals(ReportStatus.PENDING, report.getReportStatus());
        assertNull(report.getReviewedAt());
        assertNull(report.getAdminNote());
    }
}