package com.accessable.repository;

import com.accessable.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the buildings table.
 * Spring Data JPA provides save, findById, findAll, delete, etc. automatically.
 */
public interface BuildingRepository extends JpaRepository<Building, Long> {
}