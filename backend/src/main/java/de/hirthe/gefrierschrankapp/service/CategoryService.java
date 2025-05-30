package de.hirthe.gefrierschrankapp.service;

import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.repository.CategoryRepository;
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
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Get all categories ordered by name
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        log.debug("Fetching category with id: {}", id);
        return categoryRepository.findById(id);
    }
    
    /**
     * Get category by name (case-insensitive)
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        log.debug("Fetching category with name: {}", name);
        return categoryRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Create new category
     */
    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());
        
        // Check if category with same name already exists
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        
        // Set default values if not provided
        if (category.getColor() == null || category.getColor().isEmpty()) {
            category.setColor("#3B82F6"); // Default blue
        }
        
        if (category.getDefaultStorageDays() == null) {
            category.setDefaultStorageDays(90); // Default 3 months
        }
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Successfully created category with id: {}", savedCategory.getId());
        return savedCategory;
    }
    
    /**
     * Update existing category
     */
    public Category updateCategory(Long id, Category categoryUpdate) {
        log.info("Updating category with id: {}", id);
        
        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        // Check if new name conflicts with existing category (excluding current)
        if (!existingCategory.getName().equalsIgnoreCase(categoryUpdate.getName()) &&
            categoryRepository.existsByNameIgnoreCase(categoryUpdate.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryUpdate.getName() + "' already exists");
        }
        
        // Update fields
        existingCategory.setName(categoryUpdate.getName());
        existingCategory.setDescription(categoryUpdate.getDescription());
        existingCategory.setColor(categoryUpdate.getColor());
        existingCategory.setDefaultStorageDays(categoryUpdate.getDefaultStorageDays());
        
        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Successfully updated category with id: {}", updatedCategory.getId());
        return updatedCategory;
    }
    
    /**
     * Delete category
     */
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        // Check if category has products
        long productCount = categoryRepository.countProductsInCategory(id);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete category that contains " + productCount + " products");
        }
        
        categoryRepository.delete(category);
        log.info("Successfully deleted category with id: {}", id);
    }
    
    /**
     * Get categories with product counts
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoriesWithProducts() {
        log.debug("Fetching categories with products");
        return categoryRepository.findAllWithProducts();
    }
    
    /**
     * Check if category exists
     */
    @Transactional(readOnly = true)
    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }
    
    /**
     * Initialize default categories if none exist
     */
    public void initializeDefaultCategories() {
        log.info("Initializing default categories");
        
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = List.of(
                Category.builder()
                    .name("Fleisch")
                    .description("Fleisch und Geflügel")
                    .color("#EF4444")
                    .defaultStorageDays(180)
                    .build(),
                Category.builder()
                    .name("Gemüse")
                    .description("Gefrorenes Gemüse")
                    .color("#22C55E")
                    .defaultStorageDays(365)
                    .build(),
                Category.builder()
                    .name("Fertiggerichte")
                    .description("Fertige Mahlzeiten")
                    .color("#F59E0B")
                    .defaultStorageDays(90)
                    .build(),
                Category.builder()
                    .name("Brot & Backwaren")
                    .description("Brot, Brötchen und Backwaren")
                    .color("#8B5CF6")
                    .defaultStorageDays(90)
                    .build(),
                Category.builder()
                    .name("Eis & Desserts")
                    .description("Eis und gefrorene Nachspeisen")
                    .color("#EC4899")
                    .defaultStorageDays(365)
                    .build(),
                Category.builder()
                    .name("Reste")
                    .description("Übriggebliebene Mahlzeiten")
                    .color("#6B7280")
                    .defaultStorageDays(30)
                    .build()
            );
            
            categoryRepository.saveAll(defaultCategories);
            log.info("Created {} default categories", defaultCategories.size());
        }
    }
}