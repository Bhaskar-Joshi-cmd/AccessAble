package com.accessable.dto;

/**
 * Response DTO for a building.
 * Only the fields documented in 07_API_SPEC.md are exposed:
 * id, name and campusZone.
 */
public class BuildingResponse {

    private Long id;
    private String name;
    private String campusZone;

    public BuildingResponse() {
    }

    public BuildingResponse(Long id, String name, String campusZone) {
        this.id = id;
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
}