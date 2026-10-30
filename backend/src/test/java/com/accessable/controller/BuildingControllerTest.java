package com.accessable.controller;

import com.accessable.dto.BuildingResponse;
import com.accessable.service.BuildingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer test for BuildingController. Uses MockMvc and mocks the service —
 * no database needed. Verifies JSON shape and sorting.
 */
@WebMvcTest(BuildingController.class)
class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;

    @Test
    void getAllBuildingsReturnsJsonArraySortedByName() throws Exception {
        BuildingResponse b1 = new BuildingResponse(1L, "Academic Block A", "North Campus");
        BuildingResponse b2 = new BuildingResponse(2L, "Library Block", "Central Campus");

        when(buildingService.getAllBuildings()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Academic Block A"))
                .andExpect(jsonPath("$[0].campusZone").value("North Campus"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Library Block"))
                .andExpect(jsonPath("$[1].campusZone").value("Central Campus"));
    }

    @Test
    void getAllBuildingsEmptyWhenNoBuildings() throws Exception {
        when(buildingService.getAllBuildings()).thenReturn(List.of());

        mockMvc.perform(get("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}