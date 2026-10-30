package com.accessable.controller;

import com.accessable.dto.FacilityResponse;
import com.accessable.service.AdminKeyService;
import com.accessable.service.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(FacilityController.class)
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private AdminKeyService adminKeyService;

    private FacilityResponse facility() {
        FacilityResponse f = new FacilityResponse();
        f.setId(4L);
        f.setName("Passenger lift");
        f.setBuildingId(1L);
        f.setBuildingName("Academic Block A");
        f.setCampusZone("North Campus");
        f.setFacilityType("LIFT");
        f.setFloorLabel("Ground to Floor 3");
        f.setDescription("Lift serving the main floors.");
        f.setCurrentStatus("AVAILABLE");
        f.setLastVerifiedAt(OffsetDateTime.parse("2026-10-20T11:30:00+05:30"));
        return f;
    }

    @Test
    void getFacilitiesNoFiltersReturnsList() throws Exception {
        when(facilityService.getFacilities(null, null, null, null))
                .thenReturn(List.of(facility()));

        mockMvc.perform(get("/api/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].name").value("Passenger lift"))
                .andExpect(jsonPath("$[0].buildingId").value(1))
                .andExpect(jsonPath("$[0].buildingName").value("Academic Block A"))
                .andExpect(jsonPath("$[0].campusZone").value("North Campus"))
                .andExpect(jsonPath("$[0].facilityType").value("LIFT"))
                .andExpect(jsonPath("$[0].floorLabel").value("Ground to Floor 3"))
                .andExpect(jsonPath("$[0].description").value("Lift serving the main floors."))
                .andExpect(jsonPath("$[0].currentStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].lastVerifiedAt").value("2026-10-20T11:30:00+05:30"));
    }

    @Test
    void getFacilitiesWithBuildingIdFilter() throws Exception {
        when(facilityService.getFacilities("1", null, null, null))
                .thenReturn(List.of(facility()));

        mockMvc.perform(get("/api/facilities").param("buildingId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buildingId").value(1));
    }

    @Test
    void getFacilitiesWithTypeFilter() throws Exception {
        when(facilityService.getFacilities(null, "LIFT", null, null))
                .thenReturn(List.of(facility()));

        mockMvc.perform(get("/api/facilities").param("type", "LIFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].facilityType").value("LIFT"));
    }

    @Test
    void getFacilitiesWithStatusFilter() throws Exception {
        when(facilityService.getFacilities(null, null, "AVAILABLE", null))
                .thenReturn(List.of(facility()));

        mockMvc.perform(get("/api/facilities").param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentStatus").value("AVAILABLE"));
    }

    @Test
    void getFacilitiesWithSearchFilter() throws Exception {
        when(facilityService.getFacilities(null, null, null, "lift"))
                .thenReturn(List.of(facility()));

        mockMvc.perform(get("/api/facilities").param("search", "lift"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Passenger lift"));
    }

    @Test
    void getFacilitiesNoMatchesReturnsEmptyArray() throws Exception {
        when(facilityService.getFacilities(null, null, null, "zzz"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/facilities").param("search", "zzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getFacilityByIdReturnsFacility() throws Exception {
        when(facilityService.getFacilityById(4L)).thenReturn(facility());

        mockMvc.perform(get("/api/facilities/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Passenger lift"))
                .andExpect(jsonPath("$.buildingName").value("Academic Block A"))
                .andExpect(jsonPath("$.currentStatus").value("AVAILABLE"));
    }

    @Test
    void updateFacilityStatusWithMissingKeyReturns401() throws Exception {
        mockMvc.perform(patch("/api/facilities/4/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"AVAILABLE\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateFacilityStatusWithIncorrectKeyReturns401() throws Exception {
        when(adminKeyService.validate("wrong-key")).thenReturn(false);
        mockMvc.perform(patch("/api/facilities/4/status")
                .header("X-Admin-Key", "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"AVAILABLE\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateFacilityStatusWithValidKeyReturns200() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        when(facilityService.updateFacilityStatus(4L, "AVAILABLE"))
                .thenReturn(com.accessable.model.FacilityStatus.AVAILABLE);
        when(facilityService.getFacilityById(4L)).thenReturn(facility());
        mockMvc.perform(patch("/api/facilities/4/status")
                .header("X-Admin-Key", "correct-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"AVAILABLE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateFacilityStatusWithInvalidStatusReturns400() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        when(facilityService.updateFacilityStatus(4L, "INVALID_STATUS"))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid facility status: 'INVALID_STATUS'"));
        mockMvc.perform(patch("/api/facilities/4/status")
                .header("X-Admin-Key", "correct-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFacilityStatusWithNonexistentFacilityReturns404() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        when(facilityService.updateFacilityStatus(999L, "AVAILABLE"))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Facility not found with id: 999"));
        mockMvc.perform(patch("/api/facilities/999/status")
                .header("X-Admin-Key", "correct-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"AVAILABLE\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFacilityStatusWithMissingStatusReturns400() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        mockMvc.perform(patch("/api/facilities/4/status")
                .header("X-Admin-Key", "correct-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}