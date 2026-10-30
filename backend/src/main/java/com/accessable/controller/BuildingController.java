package com.accessable.controller;

import com.accessable.dto.BuildingResponse;
import com.accessable.service.BuildingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for building read endpoints.
 * Thin controller — delegates to BuildingService.
 */
@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    /**
     * GET /api/buildings
     * Returns all buildings sorted alphabetically by name.
     */
    @GetMapping
    public List<BuildingResponse> getAllBuildings() {
        return buildingService.getAllBuildings();
    }
}