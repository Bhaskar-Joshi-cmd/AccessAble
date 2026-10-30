package com.accessable.dto;

/**
 * Request DTO for creating an accessibility problem report.
 * Validates facilityId, issueType, suggestedStatus, and description.
 * The server controls reportStatus (always PENDING) and reportedAt (current time).
 */
public class CreateReportRequest {

    private Long facilityId;
    private String issueType;
    private String suggestedStatus;
    private String description;

    public CreateReportRequest() {
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getSuggestedStatus() {
        return suggestedStatus;
    }

    public void setSuggestedStatus(String suggestedStatus) {
        this.suggestedStatus = suggestedStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}