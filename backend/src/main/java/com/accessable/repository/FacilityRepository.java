package com.accessable.repository;

import com.accessable.model.Facility;
import com.accessable.model.FacilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the facilities table.
 * Adds a derived query method to count facilities by their current status.
 */
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    long countByCurrentStatus(FacilityStatus status);
}