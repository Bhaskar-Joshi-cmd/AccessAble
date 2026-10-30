package com.accessable.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Plain JUnit test for the Facility entity. No database is needed.
 */
class FacilityTest {

    @Test
    void gettersAndSettersWork() {
        Building building = new Building();
        building.setId(1L);

        Facility facility = new Facility();
        facility.setId(4L);
        facility.setBuilding(building);
        facility.setName("Passenger lift");
        facility.setFacilityType(FacilityType.LIFT);
        facility.setFloorLabel("Ground to Floor 3");
        facility.setDescription("Lift serving the main floors.");
        facility.setCurrentStatus(FacilityStatus.AVAILABLE);
        facility.setLastVerifiedAt(null);

        assertEquals(4L, facility.getId());
        assertSame(building, facility.getBuilding());
        assertEquals("Passenger lift", facility.getName());
        assertEquals(FacilityType.LIFT, facility.getFacilityType());
        assertEquals("Ground to Floor 3", facility.getFloorLabel());
        assertEquals("Lift serving the main floors.", facility.getDescription());
        assertEquals(FacilityStatus.AVAILABLE, facility.getCurrentStatus());
        assertNull(facility.getLastVerifiedAt());
    }
}