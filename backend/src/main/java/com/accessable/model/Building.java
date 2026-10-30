package com.accessable.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Represents a building on campus (table: buildings).
 *
 * Fields match document/06_DATABASE_DESIGN.md and document/database/schema_and_seed.sql exactly:
 *   id            -> BIGINT identity, primary key
 *   name          -> VARCHAR(100), required, unique
 *   campus_zone   -> VARCHAR(100), optional
 *   created_at    -> TIMESTAMPTZ, required, default now()
 */
@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "campus_zone", length = 100)
    private String campusZone;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // No-arg constructor required by JPA/Hibernate
    public Building() {
    }

    public Building(String name, String campusZone) {
        this.name = name;
        this.campusZone = campusZone;
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

    public String getCampusZone() {
        return campusZone;
    }

    public void setCampusZone(String campusZone) {
        this.campusZone = campusZone;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}