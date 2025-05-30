package de.hirthe.gefrierschrankapp.repository;

import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ProductRepository productRepository;
    
    private Category testCategory;
    private Location testLocation;
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        // Create test category
        testCategory = Category.builder()
                .name("Test Category")
                .color("#FF0000")
                .defaultStorageDays(90)
                .description("Test category for unit tests")
                .build();
        testCategory = entityManager.persistAndFlush(testCategory);
        
        // Create test location
        testLocation = Location.builder()
                .name("Test Location")
                .description("Test location for unit tests")
                .freezerSection("upper")
                .sortOrder(1)
                .build();
        testLocation = entityManager.persistAndFlush(testLocation);
        
        // Create test product
        testProduct = Product.builder()
                .name("Test Product")
                .category(testCategory)
                .location(testLocation)
                .quantity(new BigDecimal("2.5"))
                .unit("kg")
                .frozenDate(LocalDate.now().minusDays(10))
                .expirationDate(LocalDate.now().plusDays(80))
                .notes("Test product notes")
                .build();
        testProduct = entityManager.persistAndFlush(testProduct);
        
        entityManager.clear();
    }
    
    @Test
    void shouldFindAllProducts() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findAll(pageable);
        
        // Then
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getName()).isEqualTo("Test Product");
    }
    
    @Test
    void shouldFindProductsByCategory() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByCategoryId(testCategory.getId(), pageable);
        
        // Then
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getCategory().getId()).isEqualTo(testCategory.getId());
    }
    
    @Test
    void shouldFindProductsByLocation() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByLocationId(testLocation.getId(), pageable);
        
        // Then
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getLocation().getId()).isEqualTo(testLocation.getId());
    }
    
    @Test
    void shouldSearchProductsByName() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByNameContainingIgnoreCase("test", pageable);
        
        // Then
        assertThat(products.getContent()).hasSize(1);
        assertThat(products.getContent().get(0).getName()).containsIgnoringCase("test");
    }
    
    @Test
    void shouldFindProductsExpiringBefore() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(90);
        
        // When
        List<Product> products = productRepository.findProductsExpiringBefore(futureDate);
        
        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getExpirationDate()).isBefore(futureDate);
    }
    
    @Test
    void shouldFindExpiredProducts() {
        // Given - Create expired product
        Product expiredProduct = Product.builder()
                .name("Expired Product")
                .category(testCategory)
                .quantity(BigDecimal.ONE)
                .unit("pieces")
                .frozenDate(LocalDate.now().minusDays(100))
                .expirationDate(LocalDate.now().minusDays(1))
                .build();
        entityManager.persistAndFlush(expiredProduct);
        
        // When
        List<Product> expiredProducts = productRepository.findExpiredProducts(LocalDate.now());
        
        // Then
        assertThat(expiredProducts).hasSize(1);
        assertThat(expiredProducts.get(0).getName()).isEqualTo("Expired Product");
    }
    
    @Test
    void shouldFindProductsExpiringWithinDays() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate in7Days = today.plusDays(7);
        
        // Create product expiring in 5 days
        Product expiringProduct = Product.builder()
                .name("Expiring Soon Product")
                .category(testCategory)
                .quantity(BigDecimal.ONE)
                .unit("pieces")
                .frozenDate(today.minusDays(10))
                .expirationDate(today.plusDays(5))
                .build();
        entityManager.persistAndFlush(expiringProduct);
        
        // When
        List<Product> products = productRepository.findProductsExpiringWithinDays(today, in7Days);
        
        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Expiring Soon Product");
    }
    
    @Test
    void shouldFindProductsWithFilters() {
        // When - Search with category filter
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByFilters(
                testCategory.getId(), null, null, pageable);
        
        // Then
        assertThat(products.getContent()).hasSize(1);
        
        // When - Search with location filter
        products = productRepository.findByFilters(
                null, testLocation.getId(), null, pageable);
        
        // Then
        assertThat(products.getContent()).hasSize(1);
        
        // When - Search with name filter
        products = productRepository.findByFilters(
                null, null, "test", pageable);
        
        // Then
        assertThat(products.getContent()).hasSize(1);
    }
    
    @Test
    void shouldFindAllWithCategoryAndLocation() {
        // When
        List<Product> products = productRepository.findAllWithCategoryAndLocation();
        
        // Then
        assertThat(products).hasSize(1);
        Product product = products.get(0);
        assertThat(product.getCategory()).isNotNull();
        assertThat(product.getLocation()).isNotNull();
        assertThat(product.getCategory().getName()).isEqualTo("Test Category");
        assertThat(product.getLocation().getName()).isEqualTo("Test Location");
    }
    
    @Test
    void shouldCountProducts() {
        // When
        long totalCount = productRepository.count();
        long categoryCount = productRepository.countByCategoryId(testCategory.getId());
        long locationCount = productRepository.countByLocationId(testLocation.getId());
        
        // Then
        assertThat(totalCount).isEqualTo(1);
        assertThat(categoryCount).isEqualTo(1);
        assertThat(locationCount).isEqualTo(1);
    }
}