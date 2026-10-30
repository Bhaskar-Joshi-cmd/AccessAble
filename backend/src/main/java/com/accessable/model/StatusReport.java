package com.accessable.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Represents one accessibility report for a facility (table: status_reports).
 *
 * Relationship: each report belongs to exactly one facility (@ManyToOne, unidirectional).
 * admin_note is only visible through admin endpoints; public DTOs never expose it.
 *
 * Fields match document/06_DATABASE_DESIGN.md and schema_and_seed.sql exactly.
 * report_status defaults to PENDING in the database; the backend also forces PENDING
 * when a public report is created (never trusts the client-supplied value).
 */
@Entity
@Table(name = "status_reports")
public class StatusReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "issue_type", nullable = false, length = 40)
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private IssueType issueType;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "suggested_status", nullable = false, length = 40)
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private FacilityStatus suggestedStatus;

    @Column(name = "report_status", nullable = false, length = 20)
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private ReportStatus reportStatus;

    @Column(name = "reported_at", nullable = false)
    private OffsetDateTime reportedAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Column(name = "admin_note", length = 500)
    private String adminNote;

    // No-arg constructor required by JPA/Hibernate
    public StatusReport() {
    }

    public StatusReport(Facility facility, IssueType issueType, String description,
                        FacilityStatus suggestedStatus, ReportStatus reportStatus,
                        OffsetDateTime reportedAt, OffsetDateTime reviewedAt, String adminNote) {
        this.facility = facility;
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

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FacilityStatus getSuggestedStatus() {
        return suggestedStatus;
    }

    public void setSuggestedStatus(FacilityStatus suggestedStatus) {
        this.suggestedStatus = suggestedStatus;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public OffsetDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(OffsetDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public OffsetDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(OffsetDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}