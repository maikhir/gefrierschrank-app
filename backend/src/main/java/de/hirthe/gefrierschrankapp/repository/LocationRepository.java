package de.hirthe.gefrierschrankapp.repository;

import de.hirthe.gefrierschrankapp.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    /**
     * Find location by name (case-insensitive)
     */
    Optional<Location> findByNameIgnoreCase(String name);
    
    /**
     * Check if location exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find all locations ordered by sort order, then by name
     */
    List<Location> findAllByOrderBySortOrderAscNameAsc();
    
    /**
     * Find locations by freezer section
     */
    List<Location> findByFreezerSectionOrderBySortOrderAsc(String freezerSection);
    
    /**
     * Find locations with product count
     */
    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.products")
    List<Location> findAllWithProducts();
    
    /**
     * Count products in location
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.location.id = :locationId")
    long countProductsInLocation(Long locationId);
}