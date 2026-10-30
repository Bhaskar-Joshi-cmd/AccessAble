package com.accessable.service;

import com.accessable.dto.BuildingResponse;
import com.accessable.model.Building;
import com.accessable.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for building read operations.
 * Keeps the controller thin; all query logic lives here.
 */
@Service
@Transactional(readOnly = true)
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    /**
     * Returns all buildings sorted alphabetically by name.
     * Each building is mapped to a BuildingResponse DTO — never a raw entity.
     */
    public List<BuildingResponse> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();

        buildings.sort(Comparator.comparing(Building::getName));

        return buildings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BuildingResponse toDto(Building building) {
        return new BuildingResponse(
                building.getId(),
                building.getName(),
                building.getCampusZone()
        );
    }
}