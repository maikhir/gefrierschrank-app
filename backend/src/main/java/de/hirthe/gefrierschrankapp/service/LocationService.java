package de.hirthe.gefrierschrankapp.service;

import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LocationService {
    
    private final LocationRepository locationRepository;
    
    /**
     * Get all locations ordered by sort order and name
     */
    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        log.debug("Fetching all locations");
        return locationRepository.findAllByOrderBySortOrderAscNameAsc();
    }
    
    /**
     * Get location by ID
     */
    @Transactional(readOnly = true)
    public Optional<Location> getLocationById(Long id) {
        log.debug("Fetching location with id: {}", id);
        return locationRepository.findById(id);
    }
    
    /**
     * Get location by name (case-insensitive)
     */
    @Transactional(readOnly = true)
    public Optional<Location> getLocationByName(String name) {
        log.debug("Fetching location with name: {}", name);
        return locationRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Get locations by freezer section
     */
    @Transactional(readOnly = true)
    public List<Location> getLocationsByFreezerSection(String freezerSection) {
        log.debug("Fetching locations for freezer section: {}", freezerSection);
        return locationRepository.findByFreezerSectionOrderBySortOrderAsc(freezerSection);
    }
    
    /**
     * Create new location
     */
    public Location createLocation(Location location) {
        log.info("Creating new location: {}", location.getName());
        
        // Check if location with same name already exists
        if (locationRepository.existsByNameIgnoreCase(location.getName())) {
            throw new IllegalArgumentException("Location with name '" + location.getName() + "' already exists");
        }
        
        // Set default sort order if not provided
        if (location.getSortOrder() == null) {
            location.setSortOrder(0);
        }
        
        Location savedLocation = locationRepository.save(location);
        log.info("Successfully created location with id: {}", savedLocation.getId());
        return savedLocation;
    }
    
    /**
     * Update existing location
     */
    public Location updateLocation(Long id, Location locationUpdate) {
        log.info("Updating location with id: {}", id);
        
        Location existingLocation = locationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));
        
        // Check if new name conflicts with existing location (excluding current)
        if (!existingLocation.getName().equalsIgnoreCase(locationUpdate.getName()) &&
            locationRepository.existsByNameIgnoreCase(locationUpdate.getName())) {
            throw new IllegalArgumentException("Location with name '" + locationUpdate.getName() + "' already exists");
        }
        
        // Update fields
        existingLocation.setName(locationUpdate.getName());
        existingLocation.setDescription(locationUpdate.getDescription());
        existingLocation.setFreezerSection(locationUpdate.getFreezerSection());
        existingLocation.setSortOrder(locationUpdate.getSortOrder());
        
        Location updatedLocation = locationRepository.save(existingLocation);
        log.info("Successfully updated location with id: {}", updatedLocation.getId());
        return updatedLocation;
    }
    
    /**
     * Delete location
     */
    public void deleteLocation(Long id) {
        log.info("Deleting location with id: {}", id);
        
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));
        
        // Check if location has products
        long productCount = locationRepository.countProductsInLocation(id);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete location that contains " + productCount + " products");
        }
        
        locationRepository.delete(location);
        log.info("Successfully deleted location with id: {}", id);
    }
    
    /**
     * Get locations with product counts
     */
    @Transactional(readOnly = true)
    public List<Location> getLocationsWithProducts() {
        log.debug("Fetching locations with products");
        return locationRepository.findAllWithProducts();
    }
    
    /**
     * Check if location exists
     */
    @Transactional(readOnly = true)
    public boolean locationExists(Long id) {
        return locationRepository.existsById(id);
    }
    
    /**
     * Initialize default locations if none exist
     */
    public void initializeDefaultLocations() {
        log.info("Initializing default locations");
        
        if (locationRepository.count() == 0) {
            List<Location> defaultLocations = List.of(
                Location.builder()
                    .name("Oberes Fach")
                    .description("Oberes Gefrierfach")
                    .freezerSection("upper")
                    .sortOrder(1)
                    .build(),
                Location.builder()
                    .name("Mittleres Fach")
                    .description("Mittleres Gefrierfach")
                    .freezerSection("middle")
                    .sortOrder(2)
                    .build(),
                Location.builder()
                    .name("Unteres Fach")
                    .description("Unteres Gefrierfach")
                    .freezerSection("lower")
                    .sortOrder(3)
                    .build(),
                Location.builder()
                    .name("Schublade 1")
                    .description("Erste Gefrierschublade")
                    .freezerSection("drawer1")
                    .sortOrder(4)
                    .build(),
                Location.builder()
                    .name("Schublade 2")
                    .description("Zweite Gefrierschublade")
                    .freezerSection("drawer2")
                    .sortOrder(5)
                    .build(),
                Location.builder()
                    .name("Türfach")
                    .description("Gefrierfach in der Tür")
                    .freezerSection("door")
                    .sortOrder(6)
                    .build()
            );
            
            locationRepository.saveAll(defaultLocations);
            log.info("Created {} default locations", defaultLocations.size());
        }
    }
}