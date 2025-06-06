package de.hirthe.gefrierschrankapp.service;

import de.hirthe.gefrierschrankapp.dto.*;
import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.entity.Product;
import de.hirthe.gefrierschrankapp.repository.CategoryRepository;
import de.hirthe.gefrierschrankapp.repository.LocationRepository;
import de.hirthe.gefrierschrankapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    
    private static final String BACKUP_VERSION = "1.0";

    @Transactional(readOnly = true)
    public BackupDTO createBackup() {
        log.info("Creating backup of all data");
        
        List<Category> categories = categoryRepository.findAll();
        List<Location> locations = locationRepository.findAll();
        List<Product> products = productRepository.findAll();
        
        List<CategoryBackupDTO> categoryBackups = categories.stream()
            .map(this::convertCategoryToBackupDTO)
            .collect(Collectors.toList());
            
        List<LocationBackupDTO> locationBackups = locations.stream()
            .map(this::convertLocationToBackupDTO)
            .collect(Collectors.toList());
            
        List<ProductBackupDTO> productBackups = products.stream()
            .map(this::convertProductToBackupDTO)
            .collect(Collectors.toList());
        
        BackupDTO.BackupMetadata metadata = BackupDTO.BackupMetadata.builder()
            .totalCategories(categories.size())
            .totalLocations(locations.size())
            .totalProducts(products.size())
            .applicationVersion(BACKUP_VERSION)
            .description("Complete data backup")
            .build();
        
        BackupDTO backup = BackupDTO.builder()
            .backupTimestamp(LocalDateTime.now())
            .version(BACKUP_VERSION)
            .metadata(metadata)
            .categories(categoryBackups)
            .locations(locationBackups)
            .products(productBackups)
            .build();
            
        log.info("Backup created successfully with {} categories, {} locations, {} products", 
                categories.size(), locations.size(), products.size());
        
        return backup;
    }

    @Transactional
    public RestoreResult restoreFromBackup(RestoreRequest request) {
        log.info("Starting restore from backup");
        
        RestoreResult.RestoreStats stats = RestoreResult.RestoreStats.builder().build();
        RestoreResult result = RestoreResult.builder()
            .stats(stats)
            .build();
        
        try {
            BackupDTO backup = request.getBackup();
            RestoreRequest.RestoreOptions options = request.getOptions();
            
            // Validate backup
            if (!validateBackup(backup, result)) {
                result.setSuccess(false);
                result.setMessage("Backup validation failed");
                return result;
            }
            
            // Clear existing data if requested
            if (options.isClearExistingData()) {
                clearAllData();
                log.info("Cleared existing data");
            }
            
            // Restore categories first (products depend on them)
            Map<Long, Long> categoryIdMapping = restoreCategories(backup.getCategories(), options, result);
            
            // Restore locations
            Map<Long, Long> locationIdMapping = restoreLocations(backup.getLocations(), options, result);
            
            // Restore products with updated category/location references
            restoreProducts(backup.getProducts(), categoryIdMapping, locationIdMapping, options, result);
            
            result.setSuccess(true);
            result.setMessage("Restore completed successfully");
            
            log.info("Restore completed: {} categories, {} locations, {} products imported",
                    stats.getCategoriesImported(), stats.getLocationsImported(), stats.getProductsImported());
            
        } catch (Exception e) {
            log.error("Error during restore", e);
            result.setSuccess(false);
            result.setMessage("Restore failed: " + e.getMessage());
            result.getErrors().add(e.getMessage());
        }
        
        return result;
    }

    private boolean validateBackup(BackupDTO backup, RestoreResult result) {
        if (backup == null) {
            result.getErrors().add("Backup data is null");
            return false;
        }
        
        if (backup.getVersion() == null || !backup.getVersion().equals(BACKUP_VERSION)) {
            result.getWarnings().add("Backup version mismatch. Expected: " + BACKUP_VERSION + 
                                   ", Found: " + backup.getVersion());
        }
        
        if (backup.getCategories() == null || backup.getLocations() == null || backup.getProducts() == null) {
            result.getErrors().add("Backup contains null data collections");
            return false;
        }
        
        return true;
    }

    @Transactional
    public void clearAllData() {
        log.info("Clearing all existing data");
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        locationRepository.deleteAll();
    }

    private Map<Long, Long> restoreCategories(List<CategoryBackupDTO> categoryBackups, 
                                            RestoreRequest.RestoreOptions options, 
                                            RestoreResult result) {
        Map<Long, Long> idMapping = new HashMap<>();
        
        for (CategoryBackupDTO categoryBackup : categoryBackups) {
            try {
                Optional<Category> existing = categoryRepository.findByName(categoryBackup.getName());
                
                if (existing.isPresent()) {
                    handleCategoryConflict(existing.get(), categoryBackup, options, result, idMapping);
                } else {
                    Category category = convertBackupDTOToCategory(categoryBackup, options.isPreserveIds());
                    category = categoryRepository.save(category);
                    idMapping.put(categoryBackup.getId(), category.getId());
                    result.getStats().setCategoriesImported(result.getStats().getCategoriesImported() + 1);
                }
            } catch (Exception e) {
                log.error("Error restoring category: " + categoryBackup.getName(), e);
                result.getErrors().add("Failed to restore category '" + categoryBackup.getName() + "': " + e.getMessage());
                result.getStats().setCategoriesSkipped(result.getStats().getCategoriesSkipped() + 1);
            }
        }
        
        return idMapping;
    }

    private Map<Long, Long> restoreLocations(List<LocationBackupDTO> locationBackups, 
                                           RestoreRequest.RestoreOptions options, 
                                           RestoreResult result) {
        Map<Long, Long> idMapping = new HashMap<>();
        
        for (LocationBackupDTO locationBackup : locationBackups) {
            try {
                Optional<Location> existing = locationRepository.findByName(locationBackup.getName());
                
                if (existing.isPresent()) {
                    handleLocationConflict(existing.get(), locationBackup, options, result, idMapping);
                } else {
                    Location location = convertBackupDTOToLocation(locationBackup, options.isPreserveIds());
                    location = locationRepository.save(location);
                    idMapping.put(locationBackup.getId(), location.getId());
                    result.getStats().setLocationsImported(result.getStats().getLocationsImported() + 1);
                }
            } catch (Exception e) {
                log.error("Error restoring location: " + locationBackup.getName(), e);
                result.getErrors().add("Failed to restore location '" + locationBackup.getName() + "': " + e.getMessage());
                result.getStats().setLocationsSkipped(result.getStats().getLocationsSkipped() + 1);
            }
        }
        
        return idMapping;
    }

    private void restoreProducts(List<ProductBackupDTO> productBackups, 
                               Map<Long, Long> categoryIdMapping, 
                               Map<Long, Long> locationIdMapping, 
                               RestoreRequest.RestoreOptions options, 
                               RestoreResult result) {
        for (ProductBackupDTO productBackup : productBackups) {
            try {
                Product product = convertBackupDTOToProduct(productBackup, categoryIdMapping, locationIdMapping, options.isPreserveIds());
                product = productRepository.save(product);
                result.getStats().setProductsImported(result.getStats().getProductsImported() + 1);
            } catch (Exception e) {
                log.error("Error restoring product: " + productBackup.getName(), e);
                result.getErrors().add("Failed to restore product '" + productBackup.getName() + "': " + e.getMessage());
                result.getStats().setProductsSkipped(result.getStats().getProductsSkipped() + 1);
            }
        }
    }

    private void handleCategoryConflict(Category existing, CategoryBackupDTO backup, 
                                      RestoreRequest.RestoreOptions options, 
                                      RestoreResult result, 
                                      Map<Long, Long> idMapping) {
        RestoreResult.ConflictInfo conflict = RestoreResult.ConflictInfo.builder()
            .type("category")
            .name(backup.getName())
            .build();
        
        switch (options.getConflictResolution()) {
            case SKIP:
                idMapping.put(backup.getId(), existing.getId());
                conflict.setResolution("skipped");
                result.getStats().setCategoriesSkipped(result.getStats().getCategoriesSkipped() + 1);
                break;
            case OVERWRITE:
                updateCategoryFromBackup(existing, backup);
                categoryRepository.save(existing);
                idMapping.put(backup.getId(), existing.getId());
                conflict.setResolution("overwritten");
                result.getStats().setCategoriesImported(result.getStats().getCategoriesImported() + 1);
                break;
            case RENAME:
                String newName = findAvailableName(backup.getName(), name -> categoryRepository.findByName(name).isPresent());
                Category newCategory = convertBackupDTOToCategory(backup, options.isPreserveIds());
                newCategory.setName(newName);
                newCategory = categoryRepository.save(newCategory);
                idMapping.put(backup.getId(), newCategory.getId());
                conflict.setResolution("renamed to " + newName);
                result.getStats().setCategoriesImported(result.getStats().getCategoriesImported() + 1);
                break;
        }
        
        result.getConflicts().add(conflict);
    }

    private void handleLocationConflict(Location existing, LocationBackupDTO backup, 
                                      RestoreRequest.RestoreOptions options, 
                                      RestoreResult result, 
                                      Map<Long, Long> idMapping) {
        RestoreResult.ConflictInfo conflict = RestoreResult.ConflictInfo.builder()
            .type("location")
            .name(backup.getName())
            .build();
        
        switch (options.getConflictResolution()) {
            case SKIP:
                idMapping.put(backup.getId(), existing.getId());
                conflict.setResolution("skipped");
                result.getStats().setLocationsSkipped(result.getStats().getLocationsSkipped() + 1);
                break;
            case OVERWRITE:
                updateLocationFromBackup(existing, backup);
                locationRepository.save(existing);
                idMapping.put(backup.getId(), existing.getId());
                conflict.setResolution("overwritten");
                result.getStats().setLocationsImported(result.getStats().getLocationsImported() + 1);
                break;
            case RENAME:
                String newName = findAvailableName(backup.getName(), name -> locationRepository.findByName(name).isPresent());
                Location newLocation = convertBackupDTOToLocation(backup, options.isPreserveIds());
                newLocation.setName(newName);
                newLocation = locationRepository.save(newLocation);
                idMapping.put(backup.getId(), newLocation.getId());
                conflict.setResolution("renamed to " + newName);
                result.getStats().setLocationsImported(result.getStats().getLocationsImported() + 1);
                break;
        }
        
        result.getConflicts().add(conflict);
    }

    private String findAvailableName(String baseName, java.util.function.Function<String, Boolean> exists) {
        String newName = baseName;
        int counter = 1;
        
        while (exists.apply(newName)) {
            newName = baseName + " (" + counter + ")";
            counter++;
        }
        
        return newName;
    }

    // Conversion methods
    private CategoryBackupDTO convertCategoryToBackupDTO(Category category) {
        return CategoryBackupDTO.builder()
            .id(category.getId())
            .name(category.getName())
            .color(category.getColor())
            .defaultStorageDays(category.getDefaultStorageDays())
            .description(category.getDescription())
            .icon(category.getIcon())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }

    private LocationBackupDTO convertLocationToBackupDTO(Location location) {
        return LocationBackupDTO.builder()
            .id(location.getId())
            .name(location.getName())
            .description(location.getDescription())
            .freezerSection(location.getFreezerSection())
            .sortOrder(location.getSortOrder())
            .createdAt(location.getCreatedAt())
            .updatedAt(location.getUpdatedAt())
            .build();
    }

    private ProductBackupDTO convertProductToBackupDTO(Product product) {
        return ProductBackupDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
            .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
            .locationId(product.getLocation() != null ? product.getLocation().getId() : null)
            .locationName(product.getLocation() != null ? product.getLocation().getName() : null)
            .quantity(product.getQuantity())
            .unit(product.getUnit())
            .frozenDate(product.getFrozenDate())
            .expirationDate(product.getExpirationDate())
            .notes(product.getNotes())
            .imageUrl(product.getImageUrl())
            .barcode(product.getBarcode())
            .userId(product.getUserId())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }

    private Category convertBackupDTOToCategory(CategoryBackupDTO dto, boolean preserveId) {
        Category category = Category.builder()
            .name(dto.getName())
            .color(dto.getColor())
            .defaultStorageDays(dto.getDefaultStorageDays())
            .description(dto.getDescription())
            .icon(dto.getIcon())
            .build();
        
        if (preserveId && dto.getId() != null) {
            category.setId(dto.getId());
        }
        
        return category;
    }

    private Location convertBackupDTOToLocation(LocationBackupDTO dto, boolean preserveId) {
        Location location = Location.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .freezerSection(dto.getFreezerSection())
            .sortOrder(dto.getSortOrder())
            .build();
        
        if (preserveId && dto.getId() != null) {
            location.setId(dto.getId());
        }
        
        return location;
    }

    private Product convertBackupDTOToProduct(ProductBackupDTO dto, Map<Long, Long> categoryIdMapping, 
                                            Map<Long, Long> locationIdMapping, boolean preserveId) {
        Product product = Product.builder()
            .name(dto.getName())
            .quantity(dto.getQuantity())
            .unit(dto.getUnit())
            .frozenDate(dto.getFrozenDate())
            .expirationDate(dto.getExpirationDate())
            .notes(dto.getNotes())
            .imageUrl(dto.getImageUrl())
            .barcode(dto.getBarcode())
            .userId(dto.getUserId())
            .build();
        
        // Map category and location IDs
        if (dto.getCategoryId() != null) {
            Long newCategoryId = categoryIdMapping.get(dto.getCategoryId());
            if (newCategoryId != null) {
                Category category = categoryRepository.findById(newCategoryId).orElse(null);
                product.setCategory(category);
            }
        }
        
        if (dto.getLocationId() != null) {
            Long newLocationId = locationIdMapping.get(dto.getLocationId());
            if (newLocationId != null) {
                Location location = locationRepository.findById(newLocationId).orElse(null);
                product.setLocation(location);
            }
        }
        
        if (preserveId && dto.getId() != null) {
            product.setId(dto.getId());
        }
        
        return product;
    }

    private void updateCategoryFromBackup(Category existing, CategoryBackupDTO backup) {
        existing.setColor(backup.getColor());
        existing.setDefaultStorageDays(backup.getDefaultStorageDays());
        existing.setDescription(backup.getDescription());
        existing.setIcon(backup.getIcon());
    }

    private void updateLocationFromBackup(Location existing, LocationBackupDTO backup) {
        existing.setDescription(backup.getDescription());
        existing.setFreezerSection(backup.getFreezerSection());
        existing.setSortOrder(backup.getSortOrder());
    }
}