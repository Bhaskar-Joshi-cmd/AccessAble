
package com.accessable.service;

import com.accessable.dto.CreateReportRequest;
import com.accessable.dto.ReportListResponse;
import com.accessable.dto.ReportResponse;
import com.accessable.dto.ReviewReportRequest;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final FacilityRepository facilityRepository;
    private final StatusReportRepository statusReportRepository;

    public ReportService(
            FacilityRepository facilityRepository,
            StatusReportRepository statusReportRepository) {

        this.facilityRepository = facilityRepository;
        this.statusReportRepository = statusReportRepository;
    }

    // PUBLIC FEATURE: Create a new accessibility report.
    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {

        if (request == null) {
            throw new InvalidFilterException(
                    "Request body is required.");
        }

        Long facilityId = validateFacilityId(request.getFacilityId());

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facility not found with id: " + facilityId));

        IssueType issueType =
                validateIssueType(request.getIssueType());

        FacilityStatus suggestedStatus =
                validateSuggestedStatus(request.getSuggestedStatus());

        String description =
                validateAndTrimDescription(request.getDescription());

        OffsetDateTime reportedAt = OffsetDateTime.now();

        StatusReport report = new StatusReport(
                facility,
                issueType,
                description,
                suggestedStatus,
                ReportStatus.PENDING,
                reportedAt,
                null,
                null
        );

        StatusReport saved = statusReportRepository.save(report);

        return new ReportResponse(
                saved.getId(),
                saved.getFacility().getId(),
                saved.getFacility().getName(),
                saved.getIssueType().name(),
                saved.getSuggestedStatus().name(),
                saved.getDescription(),
                saved.getReportStatus().name(),
                saved.getReportedAt().toString()
        );
    }

    // ADMIN FEATURE: List reports filtered by their status.
    @Transactional(readOnly = true)
    public List<ReportListResponse> listReports(String status) {

        String statusValue;

        if (status == null || status.isBlank()) {
            statusValue = "PENDING";
        } else {
            statusValue = status.trim();
        }

        ReportStatus selectedStatus;

        try {
            selectedStatus = ReportStatus.valueOf(statusValue);
        } catch (IllegalArgumentException ex) {
            throw new InvalidFilterException(
                    "Invalid report status: " + statusValue);
        }

        List<StatusReport> reports =
                statusReportRepository
                        .findByReportStatusOrderByReportedAtAsc(
                                selectedStatus);

        List<ReportListResponse> responses = new ArrayList<>();

        for (StatusReport report : reports) {

            Facility facility = report.getFacility();

            String buildingName = null;
            Building building = facility.getBuilding();

            if (building != null) {
                buildingName = building.getName();
            }

            String issueType = null;
            if (report.getIssueType() != null) {
                issueType = report.getIssueType().name();
            }

            String suggestedStatus = null;
            if (report.getSuggestedStatus() != null) {
                suggestedStatus =
                        report.getSuggestedStatus().name();
            }

            String reportStatus = null;
            if (report.getReportStatus() != null) {
                reportStatus = report.getReportStatus().name();
            }

            String reportedAt = null;
            if (report.getReportedAt() != null) {
                reportedAt = report.getReportedAt().toString();
            }

            String reviewedAt = null;
            if (report.getReviewedAt() != null) {
                reviewedAt = report.getReviewedAt().toString();
            }

            ReportListResponse response = new ReportListResponse(
                    report.getId(),
                    facility.getId(),
                    facility.getName(),
                    buildingName,
                    issueType,
                    report.getDescription(),
                    suggestedStatus,
                    reportStatus,
                    reportedAt,
                    reviewedAt,
                    report.getAdminNote()
            );

            responses.add(response);
        }

        return responses;
    }

    private Long validateFacilityId(Long facilityId) {

        if (facilityId == null || facilityId <= 0) {
            throw new InvalidFilterException(
                    "facilityId must be a positive integer.");
        }

        return facilityId;
    }

    private IssueType validateIssueType(String issueTypeValue) {

        if (issueTypeValue == null
                || issueTypeValue.trim().isEmpty()) {

            throw new InvalidFilterException(
                    "issueType is required.");
        }

        String value = issueTypeValue.trim();

        try {
            return IssueType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new InvalidFilterException(
                    "Invalid issueType: " + value);
        }
    }

    private FacilityStatus validateSuggestedStatus(
            String statusValue) {

        if (statusValue == null || statusValue.trim().isEmpty()) {
            throw new InvalidFilterException(
                    "suggestedStatus is required.");
        }

        String value = statusValue.trim();

        if ("TEMPORARILY_UNAVAILABLE".equals(value)) {
            return FacilityStatus.TEMPORARILY_UNAVAILABLE;
        }

        if ("UNDER_MAINTENANCE".equals(value)) {
            return FacilityStatus.UNDER_MAINTENANCE;
        }

        throw new InvalidFilterException(
                "suggestedStatus must be TEMPORARILY_UNAVAILABLE "
                        + "or UNDER_MAINTENANCE.");
    }

    private String validateAndTrimDescription(String description) {

        if (description == null) {
            throw new InvalidFilterException(
                    "Description is required.");
        }

        String trimmed = description.trim();

        if (trimmed.length() < 10) {
            throw new InvalidFilterException(
                    "Description must contain at least 10 characters "
                            + "after trimming.");
        }

        if (trimmed.length() > 1000) {
            throw new InvalidFilterException(
                    "Description must not exceed 1000 characters "
                            + "after trimming.");
        }

        return trimmed;
    }

    // ADMIN FEATURE: Review (verify or reject) a pending report.
    @Transactional
    public void reviewReport(Long id, String decision, String adminNote) {

        StatusReport report = statusReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Report not found with id: " + id));

        // Only PENDING reports can be reviewed.
        if (!ReportStatus.PENDING.equals(report.getReportStatus())) {
            throw new ConflictException(
                    "Report already reviewed with status: "
                            + report.getReportStatus());
        }

        OffsetDateTime now = OffsetDateTime.now();
        report.setReviewedAt(now);
        report.setAdminNote(adminNote != null ? adminNote.trim() : null);

        if ("VERIFY".equals(decision)) {
            report.setReportStatus(ReportStatus.VERIFIED);
            Facility facility = report.getFacility();
            facility.setCurrentStatus(report.getSuggestedStatus());
            facility.setLastVerifiedAt(now);
            facilityRepository.save(facility);
        } else if ("REJECT".equals(decision)) {
            report.setReportStatus(ReportStatus.REJECTED);
        } else {
            throw new InvalidFilterException(
                    "Invalid decision: " + decision
                            + ". Must be VERIFY or REJECT.");
        }

        statusReportRepository.save(report);
    }
}
