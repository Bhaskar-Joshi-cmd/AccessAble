package com.accessable.service;

import com.accessable.dto.SummaryResponse;
import com.accessable.model.FacilityStatus;
import com.accessable.model.ReportStatus;
import com.accessable.repository.FacilityRepository;
import com.accessable.repository.StatusReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for the public summary endpoint.
 * Returns aggregate counts only — never report descriptions or admin notes.
 */
@Service
@Transactional(readOnly = true)
public class SummaryService {

    private final FacilityRepository facilityRepository;
    private final StatusReportRepository statusReportRepository;

    public SummaryService(FacilityRepository facilityRepository,
                          StatusReportRepository statusReportRepository) {
        this.facilityRepository = facilityRepository;
        this.statusReportRepository = statusReportRepository;
    }

    /**
     * Returns current counts from the database.
     */
    public SummaryResponse getSummary() {
        int totalFacilities = (int) facilityRepository.count();
        int available = (int) facilityRepository.countByCurrentStatus(FacilityStatus.AVAILABLE);
        int temporarilyUnavailable = (int) facilityRepository.countByCurrentStatus(
                FacilityStatus.TEMPORARILY_UNAVAILABLE);
        int underMaintenance = (int) facilityRepository.countByCurrentStatus(FacilityStatus.UNDER_MAINTENANCE);
        int unknown = (int) facilityRepository.countByCurrentStatus(FacilityStatus.UNKNOWN);
        int pendingReports = (int) statusReportRepository.countByReportStatus(ReportStatus.PENDING);

        return new SummaryResponse(
                totalFacilities,
                available,
                temporarilyUnavailable,
                underMaintenance,
                unknown,
                pendingReports
        );
    }
}