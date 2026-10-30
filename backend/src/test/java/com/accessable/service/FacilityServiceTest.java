package com.accessable.service;

import com.accessable.dto.FacilityResponse;
import com.accessable.exception.InvalidFilterException;
import com.accessable.exception.ResourceNotFoundException;
import com.accessable.model.Facility;
import com.accessable.model.FacilityStatus;
import com.accessable.model.FacilityType;
import com.accessable.model.ReportStatus;
import com.accessable.model.StatusReport;
import com.accessable.repository.FacilityRepository;
import com.accessable.repository.StatusReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for FacilityService. Mocks the repository — no database needed.
 * Covers filters, search, validation, AND logic and 404 handling.
 */
@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private StatusReportRepository statusReportRepository;

    @InjectMocks
    private FacilityService facilityService;

    private Facility makeFacility(Long id, String name, Long buildingId, String buildingName,
                                  String campusZone, FacilityType type, FacilityStatus status) {
        com.accessable.model.Building building = new com.accessable.model.Building();
        building.setId(buildingId);
        building.setName(buildingName);
        building.setCampusZone(campusZone);

        Facility f = new Facility();
        f.setId(id);
        f.setName(name);
        f.setBuilding(building);
        f.setFacilityType(type);
        f.setFloorLabel("Ground");
        f.setDescription("A test facility.");
        f.setCurrentStatus(status);
        return f;
    }

    // ---------- no filters ----------

    @Test
    void getFacilitiesNoFiltersReturnsAll() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 1L, "Academic Block A", "North",
                FacilityType.RAMP, FacilityStatus.UNDER_MAINTENANCE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities(null, null, null, null);

        assertEquals(2, result.size());
        verify(facilityRepository).findAll();
    }

    // ---------- buildingId filter ----------

    @Test
    void getFacilitiesFilterByBuildingId() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 2L, "Library Block", "Central",
                FacilityType.RAMP, FacilityStatus.AVAILABLE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities("1", null, null, null);

        assertEquals(1, result.size());
        assertEquals("Lift", result.get(0).getName());
    }

    @Test
    void getFacilitiesInvalidBuildingIdNonNumericReturns400() {
        assertThrows(InvalidFilterException.class,
                () -> facilityService.getFacilities("abc", null, null, null));
    }

    @Test
    void getFacilitiesInvalidBuildingIdNonPositiveReturns400() {
        assertThrows(InvalidFilterException.class,
                () -> facilityService.getFacilities("0", null, null, null));
        assertThrows(InvalidFilterException.class,
                () -> facilityService.getFacilities("-5", null, null, null));
    }

    // ---------- type filter ----------

    @Test
    void getFacilitiesFilterByType() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 1L, "Academic Block A", "North",
                FacilityType.RAMP, FacilityStatus.AVAILABLE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities(null, "LIFT", null, null);

        assertEquals(1, result.size());
        assertEquals(FacilityType.LIFT.name(), result.get(0).getFacilityType());
    }

    @Test
    void getFacilitiesInvalidTypeReturns400() {
        assertThrows(InvalidFilterException.class,
                () -> facilityService.getFacilities(null, "WORKING", null, null));
    }

    // ---------- status filter ----------

    @Test
    void getFacilitiesFilterByStatus() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 1L, "Academic Block A", "North",
                FacilityType.RAMP, FacilityStatus.UNDER_MAINTENANCE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities(null, null, "AVAILABLE", null);

        assertEquals(1, result.size());
        assertEquals(FacilityStatus.AVAILABLE.name(), result.get(0).getCurrentStatus());
    }

    @Test
    void getFacilitiesInvalidStatusReturns400() {
        assertThrows(InvalidFilterException.class,
                () -> facilityService.getFacilities(null, null, "WORKING", null));
    }

    // ---------- search filter ----------

    @Test
    void getFacilitiesSearchMatchesFacilityNameCaseInsensitive() {
        Facility f1 = makeFacility(1L, "Passenger lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 1L, "Academic Block A", "North",
                FacilityType.RAMP, FacilityStatus.AVAILABLE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities(null, null, null, "LIFT");

        assertEquals(1, result.size());
        assertEquals("Passenger lift", result.get(0).getName());
    }

    @Test
    void getFacilitiesSearchMatchesBuildingNameCaseInsensitive() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 2L, "Library Block", "Central",
                FacilityType.RAMP, FacilityStatus.AVAILABLE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities(null, null, null, "library");

        assertEquals(1, result.size());
        assertEquals("Ramp", result.get(0).getName());
    }

    @Test
    void getFacilitiesSearchTrimsWhitespace() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);

        when(facilityRepository.findAll()).thenReturn(List.of(f1));

        List<FacilityResponse> result = facilityService.getFacilities(null, null, null, "  lift  ");

        assertEquals(1, result.size());
    }

    @Test
    void getFacilitiesEmptySearchTreatedAsNoFilter() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 1L, "Academic Block A", "North",
                FacilityType.RAMP, FacilityStatus.AVAILABLE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities(null, null, null, "   ");

        assertEquals(2, result.size());
    }

    // ---------- AND logic ----------

    @Test
    void getFacilitiesMultipleFiltersCombineWithAnd() {
        Facility f1 = makeFacility(1L, "Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Ramp", 1L, "Academic Block A", "North",
                FacilityType.RAMP, FacilityStatus.UNDER_MAINTENANCE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities("1", "LIFT", "AVAILABLE", null);

        assertEquals(1, result.size());
        assertEquals("Lift", result.get(0).getName());
        assertEquals(FacilityStatus.AVAILABLE.name(), result.get(0).getCurrentStatus());
    }

    // ---------- empty results ----------

    @Test
    void updateFacilityStatusSuccess() {
        Facility facility = makeFacility(1L, "Test Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);

        when(facilityRepository.findById(1L)).thenReturn(java.util.Optional.of(facility));
        when(facilityRepository.save(any(Facility.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FacilityStatus result = facilityService.updateFacilityStatus(1L, "UNDER_MAINTENANCE");

        assertEquals(FacilityStatus.UNDER_MAINTENANCE, result);
        assertEquals(FacilityStatus.UNDER_MAINTENANCE, facility.getCurrentStatus());
        assertNotNull(facility.getLastVerifiedAt());

        verify(facilityRepository).save(facility);
        verify(statusReportRepository, never()).findByFacilityIdAndReportStatus(any(), any());
    }

    @Test
    void updateFacilityStatusNonexistentReturns404() {
        when(facilityRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> facilityService.updateFacilityStatus(999L, "AVAILABLE"));
    }

    @Test
    void updateFacilityStatusInvalidStatusReturns400() {
        Facility facility = makeFacility(1L, "Test Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);

        when(facilityRepository.findById(1L)).thenReturn(java.util.Optional.of(facility));

        assertThrows(InvalidFilterException.class,
                () -> facilityService.updateFacilityStatus(1L, "INVALID_STATUS"));
    }

    @Test
    void updateFacilityStatusToAvailableResolvesVerifiedReports() {
        Facility facility = makeFacility(1L, "Test Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.UNDER_MAINTENANCE);

        StatusReport verifiedReport = new StatusReport();
        verifiedReport.setId(1L);
        verifiedReport.setReportStatus(ReportStatus.VERIFIED);
        verifiedReport.setFacility(facility);

        when(facilityRepository.findById(1L)).thenReturn(java.util.Optional.of(facility));
        when(statusReportRepository.findByFacilityIdAndReportStatus(1L, ReportStatus.VERIFIED))
                .thenReturn(List.of(verifiedReport));
        when(facilityRepository.save(any(Facility.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(statusReportRepository.saveAll(any(List.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FacilityStatus result = facilityService.updateFacilityStatus(1L, "AVAILABLE");

        assertEquals(FacilityStatus.AVAILABLE, result);
        assertEquals(FacilityStatus.AVAILABLE, facility.getCurrentStatus());
        assertEquals(ReportStatus.RESOLVED, verifiedReport.getReportStatus());

        verify(statusReportRepository).saveAll(List.of(verifiedReport));
        verify(facilityRepository).save(facility);
    }

    @Test
    void updateFacilityStatusNonAvailableDoesNotResolveReports() {
        Facility facility = makeFacility(1L, "Test Lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.UNDER_MAINTENANCE);

        when(facilityRepository.findById(1L)).thenReturn(java.util.Optional.of(facility));
        when(facilityRepository.save(any(Facility.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FacilityStatus result = facilityService.updateFacilityStatus(1L, "UNDER_MAINTENANCE");

        assertEquals(FacilityStatus.UNDER_MAINTENANCE, result);
        assertEquals(FacilityStatus.UNDER_MAINTENANCE, facility.getCurrentStatus());

        verify(statusReportRepository, never()).findByFacilityIdAndReportStatus(any(), any());
        verify(facilityRepository).save(facility);
    }

    // ---------- sort order ----------

    @Test
    void getFacilitiesSortedByBuildingNameThenFacilityName() {
        Facility f1 = makeFacility(1L, "Zebra", 2L, "Library Block", "Central",
                FacilityType.RAMP, FacilityStatus.AVAILABLE);
        Facility f2 = makeFacility(2L, "Alpha", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FacilityResponse> result = facilityService.getFacilities(null, null, null, null);

        assertEquals(2, result.size());
        assertEquals("Academic Block A", result.get(0).getBuildingName());
        assertEquals("Alpha", result.get(0).getName());
        assertEquals("Library Block", result.get(1).getBuildingName());
        assertEquals("Zebra", result.get(1).getName());
    }

    // ---------- getFacilityById ----------

    @Test
    void getFacilityByIdReturnsFacility() {
        Facility f = makeFacility(4L, "Passenger lift", 1L, "Academic Block A", "North",
                FacilityType.LIFT, FacilityStatus.AVAILABLE);

        when(facilityRepository.findById(4L)).thenReturn(java.util.Optional.of(f));

        FacilityResponse result = facilityService.getFacilityById(4L);

        assertEquals(4L, result.getId());
        assertEquals("Passenger lift", result.getName());
        assertEquals(1L, result.getBuildingId());
        assertEquals("Academic Block A", result.getBuildingName());
        assertEquals("North", result.getCampusZone());
        assertEquals(FacilityType.LIFT.name(), result.getFacilityType());
        assertEquals(FacilityStatus.AVAILABLE.name(), result.getCurrentStatus());
    }

    @Test
    void getFacilityByIdUnknownIdReturns404() {
        when(facilityRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> facilityService.getFacilityById(999L));
    }
}
