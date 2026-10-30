package com.accessable.service;

import com.accessable.dto.SummaryResponse;
import com.accessable.model.FacilityStatus;
import com.accessable.model.ReportStatus;
import com.accessable.repository.FacilityRepository;
import com.accessable.repository.StatusReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for SummaryService. Mocks the repositories — no database needed.
 * Verifies aggregate counts including zero counts.
 */
@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private StatusReportRepository statusReportRepository;

    @InjectMocks
    private SummaryService summaryService;

    @Test
    void getSummaryReturnsCorrectCounts() {
        when(facilityRepository.count()).thenReturn(6L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.AVAILABLE)).thenReturn(4L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.TEMPORARILY_UNAVAILABLE)).thenReturn(0L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.UNDER_MAINTENANCE)).thenReturn(1L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.UNKNOWN)).thenReturn(1L);
        when(statusReportRepository.countByReportStatus(ReportStatus.PENDING)).thenReturn(2L);

        SummaryResponse result = summaryService.getSummary();

        assertEquals(6, result.getTotalFacilities());
        assertEquals(4, result.getAvailable());
        assertEquals(0, result.getTemporarilyUnavailable());
        assertEquals(1, result.getUnderMaintenance());
        assertEquals(1, result.getUnknown());
        assertEquals(2, result.getPendingReports());
    }

    @Test
    void getSummaryZeroCountsWhenDatabaseEmpty() {
        when(facilityRepository.count()).thenReturn(0L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.AVAILABLE)).thenReturn(0L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.TEMPORARILY_UNAVAILABLE)).thenReturn(0L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.UNDER_MAINTENANCE)).thenReturn(0L);
        when(facilityRepository.countByCurrentStatus(FacilityStatus.UNKNOWN)).thenReturn(0L);
        when(statusReportRepository.countByReportStatus(ReportStatus.PENDING)).thenReturn(0L);

        SummaryResponse result = summaryService.getSummary();

        assertEquals(0, result.getTotalFacilities());
        assertEquals(0, result.getAvailable());
        assertEquals(0, result.getTemporarilyUnavailable());
        assertEquals(0, result.getUnderMaintenance());
        assertEquals(0, result.getUnknown());
        assertEquals(0, result.getPendingReports());
    }
}