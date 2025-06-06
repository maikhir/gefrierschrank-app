package de.hirthe.gefrierschrankapp.service;

import de.hirthe.gefrierschrankapp.dto.BackupDTO;
import de.hirthe.gefrierschrankapp.dto.RestoreRequest;
import de.hirthe.gefrierschrankapp.dto.RestoreResult;
import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.entity.Product;
import de.hirthe.gefrierschrankapp.repository.CategoryRepository;
import de.hirthe.gefrierschrankapp.repository.LocationRepository;
import de.hirthe.gefrierschrankapp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BackupServiceTest {

    @Autowired
    private BackupService backupService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ProductRepository productRepository;

    private Category testCategory;
    private Location testLocation;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Clean up any existing data
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        locationRepository.deleteAll();

        // Create test data
        testCategory = Category.builder()
                .name("Test Category")
                .color("#FF0000")
                .defaultStorageDays(30)
                .description("Test category description")
                .build();
        testCategory = categoryRepository.save(testCategory);

        testLocation = Location.builder()
                .name("Test Location")
                .description("Test location description")
                .freezerSection("upper")
                .sortOrder(1)
                .build();
        testLocation = locationRepository.save(testLocation);

        testProduct = Product.builder()
                .name("Test Product")
                .category(testCategory)
                .location(testLocation)
                .quantity(BigDecimal.valueOf(2.5))
                .unit("kg")
                .frozenDate(LocalDate.now().minusDays(5))
                .expirationDate(LocalDate.now().plusDays(25))
                .notes("Test product notes")
                .build();
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void testCreateBackup() {
        // When
        BackupDTO backup = backupService.createBackup();

        // Then
        assertNotNull(backup);
        assertNotNull(backup.getBackupTimestamp());
        assertEquals("1.0", backup.getVersion());
        assertNotNull(backup.getMetadata());

        // Check metadata
        assertEquals(1, backup.getMetadata().getTotalCategories());
        assertEquals(1, backup.getMetadata().getTotalLocations());
        assertEquals(1, backup.getMetadata().getTotalProducts());

        // Check categories
        assertEquals(1, backup.getCategories().size());
        assertEquals("Test Category", backup.getCategories().get(0).getName());
        assertEquals("#FF0000", backup.getCategories().get(0).getColor());

        // Check locations
        assertEquals(1, backup.getLocations().size());
        assertEquals("Test Location", backup.getLocations().get(0).getName());
        assertEquals("upper", backup.getLocations().get(0).getFreezerSection());

        // Check products
        assertEquals(1, backup.getProducts().size());
        assertEquals("Test Product", backup.getProducts().get(0).getName());
        assertEquals(BigDecimal.valueOf(2.5), backup.getProducts().get(0).getQuantity());
        assertEquals("kg", backup.getProducts().get(0).getUnit());
    }

    @Test
    void testRestoreFromBackup() {
        // Given
        BackupDTO backup = backupService.createBackup();
        
        // Clear existing data
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        locationRepository.deleteAll();
        
        // Verify data is cleared
        assertEquals(0, categoryRepository.count());
        assertEquals(0, locationRepository.count());
        assertEquals(0, productRepository.count());

        RestoreRequest.RestoreOptions options = RestoreRequest.RestoreOptions.builder()
                .clearExistingData(false)
                .conflictResolution(RestoreRequest.ConflictResolution.SKIP)
                .preserveIds(false)
                .build();

        RestoreRequest request = RestoreRequest.builder()
                .backup(backup)
                .options(options)
                .build();

        // When
        RestoreResult result = backupService.restoreFromBackup(request);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getStats());

        // Check restored data counts
        assertEquals(1, result.getStats().getCategoriesImported());
        assertEquals(1, result.getStats().getLocationsImported());
        assertEquals(1, result.getStats().getProductsImported());

        assertEquals(0, result.getStats().getCategoriesSkipped());
        assertEquals(0, result.getStats().getLocationsSkipped());
        assertEquals(0, result.getStats().getProductsSkipped());

        // Verify data in database
        assertEquals(1, categoryRepository.count());
        assertEquals(1, locationRepository.count());
        assertEquals(1, productRepository.count());

        // Verify category data
        Category restoredCategory = categoryRepository.findAll().get(0);
        assertEquals("Test Category", restoredCategory.getName());
        assertEquals("#FF0000", restoredCategory.getColor());

        // Verify location data
        Location restoredLocation = locationRepository.findAll().get(0);
        assertEquals("Test Location", restoredLocation.getName());
        assertEquals("upper", restoredLocation.getFreezerSection());

        // Verify product data
        Product restoredProduct = productRepository.findAll().get(0);
        assertEquals("Test Product", restoredProduct.getName());
        assertEquals(BigDecimal.valueOf(2.5), restoredProduct.getQuantity());
        assertEquals("kg", restoredProduct.getUnit());
    }

    @Test
    void testRestoreWithConflicts() {
        // Given - Create a backup with existing data
        BackupDTO backup = backupService.createBackup();

        RestoreRequest.RestoreOptions options = RestoreRequest.RestoreOptions.builder()
                .clearExistingData(false)
                .conflictResolution(RestoreRequest.ConflictResolution.SKIP)
                .preserveIds(false)
                .build();

        RestoreRequest request = RestoreRequest.builder()
                .backup(backup)
                .options(options)
                .build();

        // When - Try to restore with existing data (should create conflicts)
        RestoreResult result = backupService.restoreFromBackup(request);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getStats());

        // Should skip existing items
        assertEquals(0, result.getStats().getCategoriesImported());
        assertEquals(0, result.getStats().getLocationsImported());
        assertEquals(1, result.getStats().getProductsImported()); // Products can have duplicates

        assertEquals(1, result.getStats().getCategoriesSkipped());
        assertEquals(1, result.getStats().getLocationsSkipped());

        // Should have conflicts
        assertNotNull(result.getConflicts());
        assertTrue(result.getConflicts().size() >= 2); // At least category and location conflicts
    }

    @Test
    void testClearAllData() {
        // Given - Ensure we have data
        assertEquals(1, categoryRepository.count());
        assertEquals(1, locationRepository.count());
        assertEquals(1, productRepository.count());

        // When
        backupService.clearAllData();

        // Then
        assertEquals(0, categoryRepository.count());
        assertEquals(0, locationRepository.count());
        assertEquals(0, productRepository.count());
    }
}