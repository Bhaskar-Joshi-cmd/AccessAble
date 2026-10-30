package com.accessable.service;

import com.accessable.dto.FacilityResponse;
import com.accessable.exception.InvalidFilterException;
import com.accessable.exception.ResourceNotFoundException;
import com.accessable.model.Facility;
import com.accessable.model.FacilityStatus;
import com.accessable.model.FacilityType;
import com.accessable.model.ReportStatus;
import com.accessable.model.StatusReport;
import com.accessable.repository.FacilityRepository;
import com.accessable.repository.StatusReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for facility read operations.
 * Filtering is done with ordinary Java Streams — no search framework needed
 * for the small campus dataset.
 */
@Service
@Transactional(readOnly = true)
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final StatusReportRepository statusReportRepository;

    public FacilityService(FacilityRepository facilityRepository,
                           StatusReportRepository statusReportRepository) {
        this.facilityRepository = facilityRepository;
        this.statusReportRepository = statusReportRepository;
    }

    /**
     * Returns facilities matching all supplied filters, combined with AND logic.
     *
     * @param buildingId optional; must be a positive integer if supplied
     * @param type       optional; must match a documented facility type exactly
     * @param status     optional; must match a documented facility status exactly
     * @param search     optional; trimmed, case-insensitive substring match on
     *                   facility name or building name
     */
    public List<FacilityResponse> getFacilities(String buildingId, String type, String status, String search) {
        // Validate and normalize each filter before querying.
        Long buildingIdLong = parseBuildingId(buildingId);
        FacilityType facilityType = parseFacilityType(type);
        FacilityStatus facilityStatus = parseFacilityStatus(status);
        String searchTerm = normalizeSearch(search);

        List<Facility> facilities = facilityRepository.findAll();

        return facilities.stream()
                .filter(f -> buildingIdLong == null || f.getBuilding().getId().equals(buildingIdLong))
                .filter(f -> facilityType == null || f.getFacilityType() == facilityType)
                .filter(f -> facilityStatus == null || f.getCurrentStatus() == facilityStatus)
                .filter(f -> matchesSearch(f, searchTerm))
                .sorted(Comparator
                        .comparing((Facility f) -> f.getBuilding().getName())
                        .thenComparing(Facility::getName))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns the facility with the given ID, or throws 404 if it does not exist.
     */
    public FacilityResponse getFacilityById(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with id: " + id));
        return toDto(facility);
    }

    /**
     * Updates a facility's official status.
     * If the new status is AVAILABLE, marks all VERIFIED reports for
     * that facility as RESOLVED (in the same transaction).
     *
     * @param facilityId the facility to update
     * @param status     the new status (must be a valid FacilityStatus)
     * @return the updated facility's current status
     */
    @Transactional
    public FacilityStatus updateFacilityStatus(Long facilityId, String status) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facility not found with id: " + facilityId));

        FacilityStatus newStatus;
        try {
            newStatus = FacilityStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new InvalidFilterException("Invalid facility status: '" + status
                    + "'. Must be one of: AVAILABLE, TEMPORARILY_UNAVAILABLE, "
                    + "UNDER_MAINTENANCE, UNKNOWN.");
        }

        facility.setCurrentStatus(newStatus);
        facility.setLastVerifiedAt(java.time.OffsetDateTime.now());

        if (newStatus == FacilityStatus.AVAILABLE) {
            List<StatusReport> verifiedReports =
                    statusReportRepository.findByFacilityIdAndReportStatus(
                            facilityId, ReportStatus.VERIFIED);
            for (StatusReport report : verifiedReports) {
                report.setReportStatus(ReportStatus.RESOLVED);
            }
            statusReportRepository.saveAll(verifiedReports);
        }

        facilityRepository.save(facility);
        return newStatus;
    }

    // ---------- filter validation ----------

    private Long parseBuildingId(String buildingId) {
        if (buildingId == null || buildingId.trim().isEmpty()) {
            return null;
        }
        try {
            long value = Long.parseLong(buildingId.trim());
            if (value <= 0) {
                throw new InvalidFilterException("buildingId must be a positive integer.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new InvalidFilterException("buildingId must be a positive integer.");
        }
    }

    private FacilityType parseFacilityType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        try {
            return FacilityType.valueOf(type.trim());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFilterException("Invalid facility type: '" + type.trim() + "'.");
        }
    }

    private FacilityStatus parseFacilityStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return FacilityStatus.valueOf(status.trim());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFilterException("Invalid facility status: '" + status.trim() + "'.");
        }
    }

    private String normalizeSearch(String search) {
        if (search == null) {
            return "";
        }
        return search.trim();
    }

    private boolean matchesSearch(Facility facility, String searchTerm) {
        if (searchTerm.isEmpty()) {
            return true;
        }
        String lower = searchTerm.toLowerCase();
        String facilityName = facility.getName() != null ? facility.getName().toLowerCase() : "";
        String buildingName = facility.getBuilding() != null && facility.getBuilding().getName() != null
                ? facility.getBuilding().getName().toLowerCase() : "";
        return facilityName.contains(lower) || buildingName.contains(lower);
    }

    // ---------- DTO mapping ----------

    private FacilityResponse toDto(Facility facility) {
        var building = facility.getBuilding();
        return new FacilityResponse(
                facility.getId(),
                facility.getName(),
                building != null ? building.getId() : null,
                building != null ? building.getName() : null,
                building != null ? building.getCampusZone() : null,
                facility.getFacilityType() != null ? facility.getFacilityType().name() : null,
                facility.getFloorLabel(),
                facility.getDescription(),
                facility.getCurrentStatus() != null ? facility.getCurrentStatus().name() : null,
                facility.getLastVerifiedAt()
        );
    }
}