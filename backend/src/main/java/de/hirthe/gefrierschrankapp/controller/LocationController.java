package de.hirthe.gefrierschrankapp.controller;

import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Slf4j
public class LocationController {
    
    private final LocationService locationService;
    
    /**
     * Get all locations
     */
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        log.debug("Fetching all locations");
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }
    
    /**
     * Get location by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        log.debug("Fetching location with id: {}", id);
        
        return locationService.getLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get locations by freezer section
     */
    @GetMapping("/section/{freezerSection}")
    public ResponseEntity<List<Location>> getLocationsByFreezerSection(@PathVariable String freezerSection) {
        log.debug("Fetching locations for freezer section: {}", freezerSection);
        List<Location> locations = locationService.getLocationsByFreezerSection(freezerSection);
        return ResponseEntity.ok(locations);
    }
    
    /**
     * Create new location (Admin only)
     */
    @PostMapping
    public ResponseEntity<Location> createLocation(@Valid @RequestBody Location location) {
        log.info("Creating new location: {}", location.getName());
        
        try {
            Location savedLocation = locationService.createLocation(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create location: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update existing location (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Long id, 
            @Valid @RequestBody Location location) {
        
        log.info("Updating location with id: {}", id);
        
        try {
            Location updatedLocation = locationService.updateLocation(id, location);
            return ResponseEntity.ok(updatedLocation);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update location: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete location (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        log.info("Deleting location with id: {}", id);
        
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Location not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Cannot delete location: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get locations with products
     */
    @GetMapping("/with-products")
    public ResponseEntity<List<Location>> getLocationsWithProducts() {
        log.debug("Fetching locations with products");
        List<Location> locations = locationService.getLocationsWithProducts();
        return ResponseEntity.ok(locations);
    }
}