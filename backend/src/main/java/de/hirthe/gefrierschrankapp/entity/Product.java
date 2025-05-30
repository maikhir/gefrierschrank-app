package de.hirthe.gefrierschrankapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must be less than 200 characters")
    @Column(nullable = false)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    
    // Quantity and measurements
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity = BigDecimal.ONE;
    
    @Size(max = 20, message = "Unit must be less than 20 characters")
    @Column(length = 20)
    private String unit = "pieces"; // 'kg', 'g', 'pieces', 'liters', 'ml'
    
    // Date management
    @NotNull(message = "Frozen date is required")
    @PastOrPresent(message = "Frozen date cannot be in the future")
    @Column(name = "frozen_date", nullable = false)
    private LocalDate frozenDate = LocalDate.now();
    
    // Note: No @Future validation here to allow expired products for testing/import
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    
    // Additional information
    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
    
    @Size(max = 500, message = "Image URL must be less than 500 characters")
    @Column(name = "image_url")
    private String imageUrl;
    
    @Size(max = 50, message = "Barcode must be less than 50 characters")
    private String barcode;
    
    // Future user assignment (for multi-user support)
    @Column(name = "user_id")
    private Long userId; // Will be used when we implement authentication
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Business logic methods
    public boolean isExpiringSoon(int daysThreshold) {
        if (expirationDate == null) {
            return false;
        }
        return expirationDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }
    
    public boolean isExpired() {
        if (expirationDate == null) {
            return false;
        }
        return expirationDate.isBefore(LocalDate.now());
    }
    
    public int getDaysUntilExpiration() {
        if (expirationDate == null) {
            return Integer.MAX_VALUE; // Never expires
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }
}