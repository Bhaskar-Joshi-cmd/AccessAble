package com.accessable.service;

import com.accessable.dto.CreateReportRequest;
import com.accessable.dto.ReportListResponse;
import com.accessable.dto.ReportResponse;
import com.accessable.exception.ConflictException;
import com.accessable.exception.InvalidFilterException;
import com.accessable.exception.ResourceNotFoundException;
import com.accessable.model.Building;
import com.accessable.model.Facility;
import com.accessable.model.FacilityStatus;
import com.accessable.model.IssueType;
import com.accessable.model.ReportStatus;
import com.accessable.model.StatusReport;
import com.accessable.repository.FacilityRepository;
import com.accessable.repository.StatusReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private StatusReportRepository statusReportRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void createReportSavesPendingReport() {
        CreateReportRequest request = new CreateReportRequest();
        request.setFacilityId(1L);
        request.setIssueType("OUT_OF_SERVICE");
        request.setSuggestedStatus("TEMPORARILY_UNAVAILABLE");
        request.setDescription("The lift is not working.");

        Facility facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Lift");

        when(facilityRepository.findById(1L)).thenReturn(java.util.Optional.of(facility));
        when(statusReportRepository.save(any(StatusReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReportResponse response = reportService.createReport(request);

        assertEquals(1L, response.getFacilityId());
        assertEquals("Test Lift", response.getFacilityName());
        assertEquals("OUT_OF_SERVICE", response.getIssueType());
        assertEquals("TEMPORARILY_UNAVAILABLE", response.getSuggestedStatus());
        assertEquals("The lift is not working.", response.getDescription());
        assertEquals("PENDING", response.getReportStatus());
        assertNotNull(response.getReportedAt());

        verify(statusReportRepository).save(any(StatusReport.class));
    }

    @Test
    void createReportWithNonexistentFacilityReturns404() {
        CreateReportRequest request = new CreateReportRequest();
        request.setFacilityId(999L);
        request.setIssueType("OUT_OF_SERVICE");
        request.setSuggestedStatus("TEMPORARILY_UNAVAILABLE");
        request.setDescription("The lift is not working.");

        when(facilityRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reportService.createReport(request));
    }

    @Test
    void verifyPendingReportUpdatesFacilityStatus() {
        StatusReport report = new StatusReport();
        report.setId(1L);
        report.setReportStatus(ReportStatus.PENDING);
        report.setSuggestedStatus(FacilityStatus.TEMPORARILY_UNAVAILABLE);

        Facility facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Lift");
        facility.setCurrentStatus(FacilityStatus.AVAILABLE);

        report.setFacility(facility);

        when(statusReportRepository.findById(1L)).thenReturn(java.util.Optional.of(report));
        when(statusReportRepository.save(any(StatusReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(facilityRepository.save(any(Facility.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reportService.reviewReport(1L, "VERIFY", "Checked and confirmed.");

        assertEquals(ReportStatus.VERIFIED, report.getReportStatus());
        assertNotNull(report.getReviewedAt());
        assertEquals("Checked and confirmed.", report.getAdminNote());
        assertEquals(FacilityStatus.TEMPORARILY_UNAVAILABLE, facility.getCurrentStatus());
        assertNotNull(facility.getLastVerifiedAt());

        verify(statusReportRepository).save(report);
        verify(facilityRepository).save(facility);
    }

    @Test
    void rejectPendingReportDoesNotChangeFacilityStatus() {
        StatusReport report = new StatusReport();
        report.setId(1L);
        report.setReportStatus(ReportStatus.PENDING);
        report.setSuggestedStatus(FacilityStatus.TEMPORARILY_UNAVAILABLE);

        Facility facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Lift");
        facility.setCurrentStatus(FacilityStatus.AVAILABLE);
        facility.setLastVerifiedAt(java.time.OffsetDateTime.parse("2026-10-20T10:00:00Z"));

        report.setFacility(facility);

        when(statusReportRepository.findById(1L)).thenReturn(java.util.Optional.of(report));
        when(statusReportRepository.save(any(StatusReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reportService.reviewReport(1L, "REJECT", "Not a real issue.");

        assertEquals(ReportStatus.REJECTED, report.getReportStatus());
        assertNotNull(report.getReviewedAt());
        assertEquals("Not a real issue.", report.getAdminNote());
        assertEquals(FacilityStatus.AVAILABLE, facility.getCurrentStatus());
        assertEquals(java.time.OffsetDateTime.parse("2026-10-20T10:00:00Z"), facility.getLastVerifiedAt());

        verify(statusReportRepository).save(report);
        verify(facilityRepository, never()).save(any());
    }

    @Test
    void reviewNonExistentReportReturns404() {
        when(statusReportRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reportService.reviewReport(999L, "VERIFY", "Test"));
    }

    @Test
    void reviewAlreadyVerifiedReportReturns409() {
        StatusReport report = new StatusReport();
        report.setId(1L);
        report.setReportStatus(ReportStatus.VERIFIED);

        when(statusReportRepository.findById(1L)).thenReturn(java.util.Optional.of(report));

        assertThrows(ConflictException.class,
                () -> reportService.reviewReport(1L, "VERIFY", "Test"));
    }

    @Test
    void reviewAlreadyRejectedReportReturns409() {
        StatusReport report = new StatusReport();
        report.setId(1L);
        report.setReportStatus(ReportStatus.REJECTED);

        when(statusReportRepository.findById(1L)).thenReturn(java.util.Optional.of(report));

        assertThrows(ConflictException.class,
                () -> reportService.reviewReport(1L, "REJECT", "Test"));
    }

    @Test
    void reviewWithInvalidDecisionReturns400() {
        StatusReport report = new StatusReport();
        report.setId(1L);
        report.setReportStatus(ReportStatus.PENDING);

        when(statusReportRepository.findById(1L)).thenReturn(java.util.Optional.of(report));

        assertThrows(InvalidFilterException.class,
                () -> reportService.reviewReport(1L, "APPROVE", "Test"));
    }
}