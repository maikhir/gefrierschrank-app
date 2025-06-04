package de.hirthe.gefrierschrankapp.service;

import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.entity.Product;
import de.hirthe.gefrierschrankapp.repository.CategoryRepository;
import de.hirthe.gefrierschrankapp.repository.LocationRepository;
import de.hirthe.gefrierschrankapp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private LocationRepository locationRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private Category testCategory;
    private Location testLocation;
    
    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Test Category")
                .defaultStorageDays(90)
                .build();
        
        testLocation = Location.builder()
                .id(1L)
                .name("Test Location")
                .build();
        
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .category(testCategory)
                .location(testLocation)
                .quantity(new BigDecimal("2.5"))
                .unit("kg")
                .frozenDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(90))
                .build();
    }
    
    @Test
    void shouldGetAllProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        
        // When
        Page<Product> result = productService.getAllProducts(pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
        verify(productRepository).findAll(pageable);
    }
    
    @Test
    void shouldGetProductById() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When
        Optional<Product> result = productService.getProductById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Product");
        verify(productRepository).findById(1L);
    }
    
    @Test
    void shouldCreateProduct() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .category(testCategory)
                .location(testLocation)
                .quantity(BigDecimal.ONE)
                .unit("pieces")
                .frozenDate(LocalDate.now())
                .build();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);
        
        // When
        Product result = productService.createProduct(newProduct);
        
        // Then
        assertThat(result.getName()).isEqualTo("New Product");
        verify(categoryRepository).findById(1L);
        verify(locationRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldCreateProductWithAutoCalculatedExpiration() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .category(testCategory)
                .quantity(BigDecimal.ONE)
                .unit("pieces")
                .frozenDate(LocalDate.now())
                .build();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(2L);
            return savedProduct;
        });
        
        // When
        Product result = productService.createProduct(newProduct);
        
        // Then
        assertThat(result.getExpirationDate()).isEqualTo(LocalDate.now().plusDays(90));
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidCategory() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .category(testCategory)
                .quantity(BigDecimal.ONE)
                .build();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> productService.createProduct(newProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found");
        
        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }
    
    @Test
    void shouldUpdateProduct() {
        // Given
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Old Name")
                .quantity(BigDecimal.ONE)
                .build();
        
        Product updateData = Product.builder()
                .name("Updated Name")
                .category(testCategory)
                .location(testLocation)
                .quantity(new BigDecimal("3.0"))
                .unit("kg")
                .build();
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        
        // When
        Product result = productService.updateProduct(1L, updateData);
        
        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void shouldDeleteProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When
        productService.deleteProduct(1L);
        
        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(testProduct);
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
        
        verify(productRepository).findById(1L);
        verify(productRepository, never()).delete(any());
    }
    
    @Test
    void shouldGetProductsExpiringSoon() {
        // Given
        List<Product> expiringSoonProducts = List.of(testProduct);
        when(productRepository.findProductsExpiringBefore(any(LocalDate.class)))
                .thenReturn(expiringSoonProducts);
        
        // When
        List<Product> result = productService.getProductsExpiringSoon(7);
        
        // Then
        assertThat(result).hasSize(1);
        verify(productRepository).findProductsExpiringBefore(any(LocalDate.class));
    }
    
    @Test
    void shouldGetExpiredProducts() {
        // Given
        List<Product> expiredProducts = List.of(testProduct);
        when(productRepository.findExpiredProducts(any(LocalDate.class)))
                .thenReturn(expiredProducts);
        
        // When
        List<Product> result = productService.getExpiredProducts();
        
        // Then
        assertThat(result).hasSize(1);
        verify(productRepository).findExpiredProducts(any(LocalDate.class));
    }
    
    @Test
    void shouldGetProductStatistics() {
        // Given
        when(productRepository.count()).thenReturn(10L);
        when(productRepository.findExpiredProducts(any(LocalDate.class)))
                .thenReturn(List.of(testProduct));
        when(productRepository.findProductsExpiringBefore(any(LocalDate.class)))
                .thenReturn(List.of(testProduct, testProduct));
        
        // When
        ProductService.ProductStatistics stats = productService.getProductStatistics();
        
        // Then
        assertThat(stats.getTotalProducts()).isEqualTo(10L);
        assertThat(stats.getExpiredProducts()).isEqualTo(1L);
        assertThat(stats.getExpiringSoon()).isEqualTo(2L);
    }
    
    @Test
    void shouldCheckIfProductExists() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(2L)).thenReturn(false);
        
        // When & Then
        assertThat(productService.productExists(1L)).isTrue();
        assertThat(productService.productExists(2L)).isFalse();
    }
}