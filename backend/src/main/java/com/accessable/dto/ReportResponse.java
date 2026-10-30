package com.accessable.dto;

/**
 * Response DTO for a created report.
 * Contains exactly the 8 documented fields; adminNote is never exposed publicly.
 */
public class ReportResponse {

    private Long id;
    private Long facilityId;
    private String facilityName;
    private String issueType;
    private String suggestedStatus;
    private String description;
    private String reportStatus;
    private String reportedAt;

    public ReportResponse() {
    }

    public ReportResponse(Long id, Long facilityId, String facilityName,
                          String issueType, String suggestedStatus,
                          String description, String reportStatus, String reportedAt) {
        this.id = id;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.issueType = issueType;
        this.suggestedStatus = suggestedStatus;
        this.description = description;
        this.reportStatus = reportStatus;
        this.reportedAt = reportedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
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

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(String reportedAt) {
        this.reportedAt = reportedAt;
    }
}