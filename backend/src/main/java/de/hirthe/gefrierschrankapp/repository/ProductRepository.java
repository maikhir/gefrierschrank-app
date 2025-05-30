package de.hirthe.gefrierschrankapp.repository;

import de.hirthe.gefrierschrankapp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find all products with pagination
     */
    Page<Product> findAll(Pageable pageable);
    
    /**
     * Find products by category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * Find products by location
     */
    Page<Product> findByLocationId(Long locationId, Pageable pageable);
    
    /**
     * Find products by user (for future multi-user support)
     */
    Page<Product> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Search products by name (case-insensitive)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    /**
     * Find products expiring soon
     */
    @Query("SELECT p FROM Product p WHERE p.expirationDate IS NOT NULL AND p.expirationDate <= :date")
    List<Product> findProductsExpiringBefore(@Param("date") LocalDate date);
    
    /**
     * Find expired products
     */
    @Query("SELECT p FROM Product p WHERE p.expirationDate IS NOT NULL AND p.expirationDate < :today")
    List<Product> findExpiredProducts(@Param("today") LocalDate today);
    
    /**
     * Find products expiring within days
     */
    @Query("SELECT p FROM Product p WHERE p.expirationDate IS NOT NULL " +
           "AND p.expirationDate BETWEEN :today AND :futureDate")
    List<Product> findProductsExpiringWithinDays(@Param("today") LocalDate today, 
                                                  @Param("futureDate") LocalDate futureDate);
    
    /**
     * Find all products with category and location (optimized fetch)
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.location")
    List<Product> findAllWithCategoryAndLocation();
    
    /**
     * Find products by multiple filters
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:locationId IS NULL OR p.location.id = :locationId) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findByFilters(@Param("categoryId") Long categoryId,
                               @Param("locationId") Long locationId,
                               @Param("name") String name,
                               Pageable pageable);
    
    /**
     * Count total products
     */
    long count();
    
    /**
     * Count products by category
     */
    long countByCategoryId(Long categoryId);
    
    /**
     * Count products by location
     */
    long countByLocationId(Long locationId);
}