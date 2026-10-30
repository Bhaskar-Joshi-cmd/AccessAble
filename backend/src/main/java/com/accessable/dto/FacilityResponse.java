package com.accessable.dto;

import java.time.OffsetDateTime;

/**
 * Response DTO for a facility.
 * Contains exactly the fields documented in 07_API_SPEC.md, including
 * building details, facility type, current status and lastVerifiedAt.
 * admin notes are intentionally not included.
 */
public class FacilityResponse {

    private Long id;
    private String name;
    private Long buildingId;
    private String buildingName;
    private String campusZone;
    private String facilityType;
    private String floorLabel;
    private String description;
    private String currentStatus;
    private OffsetDateTime lastVerifiedAt;

    public FacilityResponse() {
    }

    public FacilityResponse(Long id, String name, Long buildingId, String buildingName,
                            String campusZone, String facilityType, String floorLabel,
                            String description, String currentStatus, OffsetDateTime lastVerifiedAt) {
        this.id = id;
        this.name = name;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.campusZone = campusZone;
        this.facilityType = facilityType;
        this.floorLabel = floorLabel;
        this.description = description;
        this.currentStatus = currentStatus;
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getCampusZone() {
        return campusZone;
    }

    public void setCampusZone(String campusZone) {
        this.campusZone = campusZone;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getFloorLabel() {
        return floorLabel;
    }

    public void setFloorLabel(String floorLabel) {
        this.floorLabel = floorLabel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public OffsetDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(OffsetDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }
}