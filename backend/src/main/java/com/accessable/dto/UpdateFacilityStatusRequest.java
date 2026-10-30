package com.accessable.dto;

/**
 * Request DTO for updating a facility's official status (admin only).
 * The status field is required and must be one of the four documented facility statuses.
 */
public class UpdateFacilityStatusRequest {

    private String status;

    public UpdateFacilityStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}