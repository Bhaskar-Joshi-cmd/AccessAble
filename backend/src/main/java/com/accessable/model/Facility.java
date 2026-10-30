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
 * Represents a facility inside a building (table: facilities).
 *
 * Relationship: each facility belongs to exactly one building (@ManyToOne, unidirectional).
 * We deliberately do NOT add a @OneToMany list of reports on Facility — API responses use DTOs.
 *
 * Fields match document/06_DATABASE_DESIGN.md and schema_and_seed.sql exactly.
 */
@Entity
@Table(name = "facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "facility_type", nullable = false, length = 40)
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private FacilityType facilityType;

    @Column(name = "floor_label", nullable = false, length = 30)
    private String floorLabel;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "current_status", nullable = false, length = 40)
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private FacilityStatus currentStatus;

    @Column(name = "last_verified_at")
    private OffsetDateTime lastVerifiedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // No-arg constructor required by JPA/Hibernate
    public Facility() {
    }

    public Facility(Building building, String name, FacilityType facilityType, String floorLabel,
                    String description, FacilityStatus currentStatus, OffsetDateTime lastVerifiedAt,
                    OffsetDateTime createdAt) {
        this.building = building;
        this.name = name;
        this.facilityType = facilityType;
        this.floorLabel = floorLabel;
        this.description = description;
        this.currentStatus = currentStatus;
        this.lastVerifiedAt = lastVerifiedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
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

    public FacilityStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(FacilityStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public OffsetDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(OffsetDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}