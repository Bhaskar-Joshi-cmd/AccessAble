package com.accessable.service;

import com.accessable.dto.BuildingResponse;
import com.accessable.model.Building;
import com.accessable.repository.BuildingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for BuildingService. Mocks the repository — no database needed.
 * Verifies that buildings are returned sorted alphabetically by name.
 */
@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private BuildingService buildingService;

    @Test
    void getAllBuildingsReturnsSortedAlphabeticallyByName() {
        Building b2 = new Building();
        b2.setId(2L);
        b2.setName("Library Block");

        Building b1 = new Building();
        b1.setId(1L);
        b1.setName("Academic Block A");

        Building b3 = new Building();
        b3.setId(3L);
        b3.setName("Student Centre");

        // Repository returns them in arbitrary order; service must sort.
        // Use a mutable copy because List.of() is immutable.
        when(buildingRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(b2, b1, b3)));

        List<BuildingResponse> result = buildingService.getAllBuildings();

        assertEquals(3, result.size());
        assertEquals("Academic Block A", result.get(0).getName());
        assertEquals("Library Block", result.get(1).getName());
        assertEquals("Student Centre", result.get(2).getName());

        verify(buildingRepository).findAll();
    }

    @Test
    void getAllBuildingsMapsToDtoWithCorrectFields() {
        Building b = new Building();
        b.setId(1L);
        b.setName("Academic Block A");
        b.setCampusZone("North Campus");

        // Use a mutable copy because List.of() is immutable and the service sorts in place.
        when(buildingRepository.findAll()).thenReturn(new ArrayList<>(List.of(b)));

        List<BuildingResponse> result = buildingService.getAllBuildings();

        assertEquals(1, result.size());
        BuildingResponse dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Academic Block A", dto.getName());
        assertEquals("North Campus", dto.getCampusZone());
    }

    @Test
    void getAllBuildingsEmptyWhenNoBuildings() {
        when(buildingRepository.findAll()).thenReturn(new ArrayList<>());

        List<BuildingResponse> result = buildingService.getAllBuildings();

        assertTrue(result.isEmpty());
    }
}