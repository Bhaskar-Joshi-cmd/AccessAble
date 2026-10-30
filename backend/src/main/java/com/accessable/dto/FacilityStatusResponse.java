package com.accessable.dto;

import java.time.OffsetDateTime;

/**
 * Response DTO for a facility status update (admin endpoint).
 * Contains exactly the three documented fields: id, currentStatus, lastVerifiedAt.
 */
public class FacilityStatusResponse {

    private Long id;
    private String currentStatus;
    private OffsetDateTime lastVerifiedAt;

    public FacilityStatusResponse() {
    }

    public FacilityStatusResponse(Long id, String currentStatus, OffsetDateTime lastVerifiedAt) {
        this.id = id;
        this.currentStatus = currentStatus;
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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