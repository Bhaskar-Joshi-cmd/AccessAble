package com.accessable.dto;

/**
 * Response DTO for the public summary endpoint.
 * Returns aggregate counts only; no report descriptions or admin notes.
 */
public class SummaryResponse {

    private int totalFacilities;
    private int available;
    private int temporarilyUnavailable;
    private int underMaintenance;
    private int unknown;
    private int pendingReports;

    public SummaryResponse() {
    }

    public SummaryResponse(int totalFacilities, int available, int temporarilyUnavailable,
                           int underMaintenance, int unknown, int pendingReports) {
        this.totalFacilities = totalFacilities;
        this.available = available;
        this.temporarilyUnavailable = temporarilyUnavailable;
        this.underMaintenance = underMaintenance;
        this.unknown = unknown;
        this.pendingReports = pendingReports;
    }

    public int getTotalFacilities() {
        return totalFacilities;
    }

    public void setTotalFacilities(int totalFacilities) {
        this.totalFacilities = totalFacilities;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getTemporarilyUnavailable() {
        return temporarilyUnavailable;
    }

    public void setTemporarilyUnavailable(int temporarilyUnavailable) {
        this.temporarilyUnavailable = temporarilyUnavailable;
    }

    public int getUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(int underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public int getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(int pendingReports) {
        this.pendingReports = pendingReports;
    }
}