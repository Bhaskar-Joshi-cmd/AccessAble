package com.accessable.dto;

/**
 * Request DTO for reviewing a status report (approve or reject).
 */
public class ReviewReportRequest {

    private String decision;
    private String adminNote;

    public ReviewReportRequest() {
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}