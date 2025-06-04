package de.hirthe.gefrierschrankapp.controller;

import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused") // REST endpoints are used via HTTP calls
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * Get all categories
     */
    @GetMapping
    @SuppressWarnings("unused") // REST endpoint
    public ResponseEntity<List<Category>> getAllCategories() {
        log.debug("Fetching all categories");
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    @SuppressWarnings("unused") // REST endpoint
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        log.debug("Fetching category with id: {}", id);
        
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new category (Admin only)
     */
    @PostMapping
    @SuppressWarnings("unused") // REST endpoint
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        log.info("Creating new category: {}", category.getName());
        
        try {
            Category savedCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update existing category (Admin only)
     */
    @PutMapping("/{id}")
    @SuppressWarnings("unused") // REST endpoint
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody Category category) {
        
        log.info("Updating category with id: {}", id);
        
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete category (Admin only)
     */
    @DeleteMapping("/{id}")
    @SuppressWarnings("unused") // REST endpoint
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Deleting category with id: {}", id);
        
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Category not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Cannot delete category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get categories with products
     */
    @GetMapping("/with-products")
    @SuppressWarnings("unused") // REST endpoint
    public ResponseEntity<List<Category>> getCategoriesWithProducts() {
        log.debug("Fetching categories with products");
        List<Category> categories = categoryService.getCategoriesWithProducts();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Reinitialize default categories (Admin only)
     */
    @PostMapping("/initialize-defaults")
    @SuppressWarnings("unused") // REST endpoint
    public ResponseEntity<String> initializeDefaultCategories() {
        log.info("Reinitializing default categories");
        
        try {
            categoryService.reinitializeDefaultCategories();
            return ResponseEntity.ok("Default categories reinitialized successfully");
        } catch (Exception e) {
            log.error("Failed to reinitialize default categories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to reinitialize default categories: " + e.getMessage());
        }
    }
}