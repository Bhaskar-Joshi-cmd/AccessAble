package com.accessable.controller;

import com.accessable.dto.FacilityResponse;
import com.accessable.dto.FacilityStatusResponse;
import com.accessable.dto.UpdateFacilityStatusRequest;
import com.accessable.service.AdminKeyService;
import com.accessable.service.FacilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Controller for facility read endpoints.
 * Thin controller — delegates to FacilityService.
 */
@RestController
@RequestMapping("/api/facilities")
public class FacilityController {

    private final FacilityService facilityService;
    private final AdminKeyService adminKeyService;

    public FacilityController(FacilityService facilityService, AdminKeyService adminKeyService) {
        this.facilityService = facilityService;
        this.adminKeyService = adminKeyService;
    }

    /**
     * GET /api/facilities
     * Optional filters: buildingId, type, status, search.
     * Filters combine with AND logic.
     */
    @GetMapping
    public List<FacilityResponse> getFacilities(
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        return facilityService.getFacilities(buildingId, type, status, search);
    }

    /**
     * GET /api/facilities/{id}
     * Returns a single facility by ID, or 404 if not found.
     */
    @GetMapping("/{id}")
    public FacilityResponse getFacilityById(@PathVariable Long id) {
        return facilityService.getFacilityById(id);
    }

    /**
     * PATCH /api/facilities/{id}/status
     * Updates a facility's official status (admin only).
     * If status is AVAILABLE, marks all VERIFIED reports as RESOLVED.
     */
    @PatchMapping("/{id}/status")
    public FacilityStatusResponse updateFacilityStatus(
            @PathVariable Long id,
            @RequestBody UpdateFacilityStatusRequest request,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        // Validate admin key
        if (adminKey == null || !adminKeyService.validate(adminKey)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Missing or invalid admin key.");
        }

        // Validate status
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "status is required.");
        }

        // Call service (throws 404 for nonexistent facility, 400 for invalid status)
        facilityService.updateFacilityStatus(id, request.getStatus());

        // Get updated facility details for response
        FacilityResponse facility = facilityService.getFacilityById(id);

        return new FacilityStatusResponse(
                facility.getId(),
                facility.getCurrentStatus(),
                facility.getLastVerifiedAt()
        );
    }
}