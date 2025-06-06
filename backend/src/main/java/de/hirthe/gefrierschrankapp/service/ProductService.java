package de.hirthe.gefrierschrankapp.service;

import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.entity.Product;
import de.hirthe.gefrierschrankapp.repository.CategoryRepository;
import de.hirthe.gefrierschrankapp.repository.LocationRepository;
import de.hirthe.gefrierschrankapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ImageService imageService;
    
    /**
     * Get all products with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        log.debug("Fetching all products with pagination");
        return productRepository.findAll(pageable);
    }
    
    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }
    
    /**
     * Search products by name
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        log.debug("Searching products with name containing: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching products for category id: {}", categoryId);
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    
    /**
     * Get products by location
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByLocation(Long locationId, Pageable pageable) {
        log.debug("Fetching products for location id: {}", locationId);
        return productRepository.findByLocationId(locationId, pageable);
    }
    
    /**
     * Get products with filters
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsWithFilters(Long categoryId, Long locationId, String name, Pageable pageable) {
        log.debug("Fetching products with filters - category: {}, location: {}, name: {}", categoryId, locationId, name);
        return productRepository.findByFilters(categoryId, locationId, name, pageable);
    }
    
    /**
     * Create new product
     */
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        
        // Validate and set category
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + product.getCategory().getId()));
            product.setCategory(category);
            
            // Auto-calculate expiration date if not provided
            if (product.getExpirationDate() == null && category.getDefaultStorageDays() != null) {
                product.setExpirationDate(product.getFrozenDate().plusDays(category.getDefaultStorageDays()));
                log.debug("Auto-calculated expiration date: {} (based on category default: {} days)", 
                         product.getExpirationDate(), category.getDefaultStorageDays());
            }
        }
        
        // Validate and set location
        if (product.getLocation() != null && product.getLocation().getId() != null) {
            Location location = locationRepository.findById(product.getLocation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + product.getLocation().getId()));
            product.setLocation(location);
        }
        
        // Set default values
        if (product.getFrozenDate() == null) {
            product.setFrozenDate(LocalDate.now());
        }
        
        if (product.getUnit() == null || product.getUnit().isEmpty()) {
            product.setUnit("pieces");
        }
        
        Product savedProduct = productRepository.save(product);
        log.info("Successfully created product with id: {}", savedProduct.getId());
        return savedProduct;
    }
    
    /**
     * Update existing product
     */
    public Product updateProduct(Long id, Product productUpdate) {
        log.info("Updating product with id: {}", id);
        
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Update basic fields
        existingProduct.setName(productUpdate.getName());
        existingProduct.setQuantity(productUpdate.getQuantity());
        existingProduct.setUnit(productUpdate.getUnit());
        existingProduct.setFrozenDate(productUpdate.getFrozenDate());
        existingProduct.setExpirationDate(productUpdate.getExpirationDate());
        existingProduct.setNotes(productUpdate.getNotes());
        existingProduct.setImageUrl(productUpdate.getImageUrl());
        existingProduct.setBarcode(productUpdate.getBarcode());
        
        // Update category if provided
        if (productUpdate.getCategory() != null && productUpdate.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productUpdate.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + productUpdate.getCategory().getId()));
            existingProduct.setCategory(category);
        } else {
            existingProduct.setCategory(null);
        }
        
        // Update location if provided
        if (productUpdate.getLocation() != null && productUpdate.getLocation().getId() != null) {
            Location location = locationRepository.findById(productUpdate.getLocation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + productUpdate.getLocation().getId()));
            existingProduct.setLocation(location);
        } else {
            existingProduct.setLocation(null);
        }
        
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Successfully updated product with id: {}", updatedProduct.getId());
        return updatedProduct;
    }
    
    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Delete associated image if exists
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                // Extract filename from imageUrl (assuming format: /api/images/{filename})
                String filename = extractFilenameFromUrl(product.getImageUrl());
                if (filename != null) {
                    imageService.deleteImage(filename);
                    log.info("Deleted image {} for product {}", filename, id);
                }
            } catch (Exception e) {
                log.warn("Failed to delete image for product {}: {}", id, e.getMessage());
            }
        }
        
        productRepository.delete(product);
        log.info("Successfully deleted product with id: {}", id);
    }
    
    /**
     * Update product image
     */
    public Product updateProductImage(Long productId, String newImageUrl) {
        log.info("Updating image for product with id: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // Delete old image if exists
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                String oldFilename = extractFilenameFromUrl(product.getImageUrl());
                if (oldFilename != null) {
                    imageService.deleteImage(oldFilename);
                    log.info("Deleted old image {} for product {}", oldFilename, productId);
                }
            } catch (Exception e) {
                log.warn("Failed to delete old image for product {}: {}", productId, e.getMessage());
            }
        }
        
        // Update with new image URL
        product.setImageUrl(newImageUrl);
        Product updatedProduct = productRepository.save(product);
        log.info("Successfully updated image for product with id: {}", productId);
        return updatedProduct;
    }
    
    /**
     * Remove product image
     */
    public Product removeProductImage(Long productId) {
        log.info("Removing image for product with id: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // Delete image if exists
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                String filename = extractFilenameFromUrl(product.getImageUrl());
                if (filename != null) {
                    imageService.deleteImage(filename);
                    log.info("Deleted image {} for product {}", filename, productId);
                }
            } catch (Exception e) {
                log.warn("Failed to delete image for product {}: {}", productId, e.getMessage());
            }
        }
        
        // Remove image URL
        product.setImageUrl(null);
        Product updatedProduct = productRepository.save(product);
        log.info("Successfully removed image for product with id: {}", productId);
        return updatedProduct;
    }
    
    private String extractFilenameFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // Extract filename from URL like "/api/images/{filename}" or "http://host/api/images/{filename}"
        String[] parts = imageUrl.split("/");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            // Remove query parameters if any
            int queryIndex = lastPart.indexOf('?');
            if (queryIndex > 0) {
                lastPart = lastPart.substring(0, queryIndex);
            }
            return lastPart;
        }
        
        return null;
    }
    
    /**
     * Get products expiring soon
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsExpiringSoon(int daysThreshold) {
        log.debug("Fetching products expiring within {} days", daysThreshold);
        LocalDate futureDate = LocalDate.now().plusDays(daysThreshold);
        return productRepository.findProductsExpiringBefore(futureDate);
    }
    
    /**
     * Get expired products
     */
    @Transactional(readOnly = true)
    public List<Product> getExpiredProducts() {
        log.debug("Fetching expired products");
        return productRepository.findExpiredProducts(LocalDate.now());
    }
    
    /**
     * Get products expiring within specific days
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsExpiringWithinDays(int days) {
        log.debug("Fetching products expiring within exactly {} days", days);
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return productRepository.findProductsExpiringWithinDays(today, futureDate);
    }
    
    /**
     * Get all products with category and location (optimized)
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProductsWithDetails() {
        log.debug("Fetching all products with category and location details");
        return productRepository.findAllWithCategoryAndLocation();
    }
    
    /**
     * Get product statistics
     */
    @Transactional(readOnly = true)
    public ProductStatistics getProductStatistics() {
        log.debug("Calculating product statistics");
        
        long totalProducts = productRepository.count();
        List<Product> expiredProducts = getExpiredProducts();
        List<Product> expiringSoon = getProductsExpiringSoon(7); // 7 days threshold
        
        return ProductStatistics.builder()
            .totalProducts(totalProducts)
            .expiredProducts(expiredProducts.size())
            .expiringSoon(expiringSoon.size())
            .build();
    }
    
    /**
     * Check if product exists
     */
    @Transactional(readOnly = true)
    public boolean productExists(Long id) {
        return productRepository.existsById(id);
    }
    
    // Inner class for statistics
    public static class ProductStatistics {
        private final long totalProducts;
        private final long expiredProducts;
        private final long expiringSoon;
        
        private ProductStatistics(long totalProducts, long expiredProducts, long expiringSoon) {
            this.totalProducts = totalProducts;
            this.expiredProducts = expiredProducts;
            this.expiringSoon = expiringSoon;
        }
        
        public static ProductStatisticsBuilder builder() {
            return new ProductStatisticsBuilder();
        }
        
        public long getTotalProducts() { return totalProducts; }
        public long getExpiredProducts() { return expiredProducts; }
        public long getExpiringSoon() { return expiringSoon; }
        
        public static class ProductStatisticsBuilder {
            private long totalProducts;
            private long expiredProducts;
            private long expiringSoon;
            
            public ProductStatisticsBuilder totalProducts(long totalProducts) {
                this.totalProducts = totalProducts;
                return this;
            }
            
            public ProductStatisticsBuilder expiredProducts(long expiredProducts) {
                this.expiredProducts = expiredProducts;
                return this;
            }
            
            public ProductStatisticsBuilder expiringSoon(long expiringSoon) {
                this.expiringSoon = expiringSoon;
                return this;
            }
            
            public ProductStatistics build() {
                return new ProductStatistics(totalProducts, expiredProducts, expiringSoon);
            }
        }
    }
}