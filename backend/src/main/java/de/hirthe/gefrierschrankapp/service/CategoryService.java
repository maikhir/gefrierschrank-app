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
        
        if (category.getIcon() == null || category.getIcon().isEmpty()) {
            category.setIcon("ArchiveBoxIcon"); // Default icon
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
        existingCategory.setIcon(categoryUpdate.getIcon());
        
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
     * Reinitialize default categories (create missing ones)
     */
    public void reinitializeDefaultCategories() {
        log.info("Reinitializing default categories");
        createDefaultCategories();
    }
    
    /**
     * Create default categories (skips existing ones)
     */
    private void createDefaultCategories() {
        List<Category> defaultCategories = List.of(
            Category.builder()
                .name("Fleisch")
                .description("Fleisch und Geflügel")
                .color("#EF4444")
                .defaultStorageDays(180)
                .icon("ArchiveBoxIcon")
                .build(),
            Category.builder()
                .name("Gemüse")
                .description("Gefrorenes Gemüse")
                .color("#22C55E")
                .defaultStorageDays(365)
                .icon("ArchiveBoxIcon")
                .build(),
            Category.builder()
                .name("Fertiggerichte")
                .description("Fertige Mahlzeiten")
                .color("#F59E0B")
                .defaultStorageDays(90)
                .icon("ArchiveBoxIcon")
                .build(),
            Category.builder()
                .name("Brot & Backwaren")
                .description("Brot, Brötchen und Backwaren")
                .color("#8B5CF6")
                .defaultStorageDays(90)
                .icon("ArchiveBoxIcon")
                .build(),
            Category.builder()
                .name("Eis & Desserts")
                .description("Eis und gefrorene Nachspeisen")
                .color("#EC4899")
                .defaultStorageDays(365)
                .icon("ArchiveBoxIcon")
                .build(),
            Category.builder()
                .name("Reste")
                .description("Übriggebliebene Mahlzeiten")
                .color("#6B7280")
                .defaultStorageDays(30)
                .icon("ArchiveBoxIcon")
                .build()
        );
        
        int createdCount = 0;
        for (Category defaultCategory : defaultCategories) {
            // Check if category with this name already exists
            if (!categoryRepository.existsByNameIgnoreCase(defaultCategory.getName())) {
                categoryRepository.save(defaultCategory);
                createdCount++;
                log.debug("Created default category: {}", defaultCategory.getName());
            } else {
                log.debug("Category already exists, skipping: {}", defaultCategory.getName());
            }
        }
        
        log.info("Created {} new default categories", createdCount);
    }
}