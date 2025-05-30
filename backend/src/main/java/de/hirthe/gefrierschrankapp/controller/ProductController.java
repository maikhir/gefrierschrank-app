package de.hirthe.gefrierschrankapp.controller;

import de.hirthe.gefrierschrankapp.dto.CreateProductRequest;
import de.hirthe.gefrierschrankapp.dto.ProductDTO;
import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.entity.Product;
import de.hirthe.gefrierschrankapp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Get all products with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String name) {
        
        log.debug("Fetching products with filters - page: {}, size: {}, categoryId: {}, locationId: {}, name: {}", 
                  page, size, categoryId, locationId, name);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Product> products = productService.getProductsWithFilters(categoryId, locationId, name, pageable);
        Page<ProductDTO> productDTOs = products.map(this::convertToDTO);
        
        return ResponseEntity.ok(productDTOs);
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        log.debug("Fetching product with id: {}", id);
        
        return productService.getProductById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new product
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        Product product = convertFromDTO(request);
        Product savedProduct = productService.createProduct(product);
        ProductDTO productDTO = convertToDTO(savedProduct);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }
    
    /**
     * Update existing product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody CreateProductRequest request) {
        
        log.info("Updating product with id: {}", id);
        
        if (!productService.productExists(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Product product = convertFromDTO(request);
        Product updatedProduct = productService.updateProduct(id, product);
        ProductDTO productDTO = convertToDTO(updatedProduct);
        
        return ResponseEntity.ok(productDTO);
    }
    
    /**
     * Delete product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        
        if (!productService.productExists(id)) {
            return ResponseEntity.notFound().build();
        }
        
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get products expiring soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<ProductDTO>> getProductsExpiringSoon(
            @RequestParam(defaultValue = "7") int days) {
        
        log.debug("Fetching products expiring within {} days", days);
        
        List<Product> products = productService.getProductsExpiringSoon(days);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }
    
    /**
     * Get expired products
     */
    @GetMapping("/expired")
    public ResponseEntity<List<ProductDTO>> getExpiredProducts() {
        log.debug("Fetching expired products");
        
        List<Product> products = productService.getExpiredProducts();
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }
    
    /**
     * Get product statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductService.ProductStatistics> getProductStatistics() {
        log.debug("Fetching product statistics");
        
        ProductService.ProductStatistics statistics = productService.getProductStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Convert Product entity to DTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO.ProductDTOBuilder builder = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .frozenDate(product.getFrozenDate())
                .expirationDate(product.getExpirationDate())
                .notes(product.getNotes())
                .imageUrl(product.getImageUrl())
                .barcode(product.getBarcode())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .expired(product.isExpired())
                .expiringSoon(product.isExpiringSoon(7))
                .daysUntilExpiration(product.getDaysUntilExpiration());
        
        // Convert category if present
        if (product.getCategory() != null) {
            builder.category(ProductDTO.CategoryDTO.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .color(product.getCategory().getColor())
                    .defaultStorageDays(product.getCategory().getDefaultStorageDays())
                    .description(product.getCategory().getDescription())
                    .build());
        }
        
        // Convert location if present
        if (product.getLocation() != null) {
            builder.location(ProductDTO.LocationDTO.builder()
                    .id(product.getLocation().getId())
                    .name(product.getLocation().getName())
                    .description(product.getLocation().getDescription())
                    .freezerSection(product.getLocation().getFreezerSection())
                    .sortOrder(product.getLocation().getSortOrder())
                    .build());
        }
        
        return builder.build();
    }
    
    /**
     * Convert CreateProductRequest to Product entity
     */
    private Product convertFromDTO(CreateProductRequest request) {
        Product.ProductBuilder builder = Product.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .frozenDate(request.getFrozenDate() != null ? request.getFrozenDate() : LocalDate.now())
                .expirationDate(request.getExpirationDate())
                .notes(request.getNotes())
                .imageUrl(request.getImageUrl())
                .barcode(request.getBarcode());
        
        // Set category if provided
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            builder.category(category);
        }
        
        // Set location if provided
        if (request.getLocationId() != null) {
            Location location = new Location();
            location.setId(request.getLocationId());
            builder.location(location);
        }
        
        return builder.build();
    }
}