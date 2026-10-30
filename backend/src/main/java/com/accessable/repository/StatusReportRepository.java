package com.accessable.repository;


import java.util.List;
import com.accessable.model.ReportStatus;
import com.accessable.model.StatusReport;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the status_reports table.
 * Adds a derived query method to count reports by their review status.
 */
public interface StatusReportRepository extends JpaRepository<StatusReport, Long> {
    List<StatusReport> findByReportStatusOrderByReportedAtAsc(
        ReportStatus reportStatus);

    long countByReportStatus(ReportStatus status);

    List<StatusReport> findByFacilityIdAndReportStatus(
        Long facilityId, ReportStatus reportStatus);
}