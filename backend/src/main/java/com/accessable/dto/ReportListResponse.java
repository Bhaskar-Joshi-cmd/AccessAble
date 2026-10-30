
package com.accessable.dto;

public class ReportListResponse {

    private Long id;
    private Long facilityId;
    private String facilityName;
    private String buildingName;
    private String issueType;
    private String description;
    private String suggestedStatus;
    private String reportStatus;
    private String reportedAt;
    private String reviewedAt;
    private String adminNote;

    public ReportListResponse() {
    }

    public ReportListResponse(
            Long id,
            Long facilityId,
            String facilityName,
            String buildingName,
            String issueType,
            String description,
            String suggestedStatus,
            String reportStatus,
            String reportedAt,
            String reviewedAt,
            String adminNote) {
        this.id = id;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.buildingName = buildingName;
        this.issueType = issueType;
        this.description = description;
        this.suggestedStatus = suggestedStatus;
        this.reportStatus = reportStatus;
        this.reportedAt = reportedAt;
        this.reviewedAt = reviewedAt;
        this.adminNote = adminNote;
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

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestedStatus() {
        return suggestedStatus;
    }

    public void setSuggestedStatus(String suggestedStatus) {
        this.suggestedStatus = suggestedStatus;
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

    public String getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(String reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}
