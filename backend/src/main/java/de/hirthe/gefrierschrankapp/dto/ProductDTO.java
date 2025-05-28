package de.hirthe.gefrierschrankapp.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private CategoryDTO category;
    private LocationDTO location;
    private BigDecimal quantity;
    private String unit;
    private LocalDate frozenDate;
    private LocalDate expirationDate;
    private String notes;
    private String imageUrl;
    private String barcode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private boolean expired;
    private boolean expiringSoon;
    private int daysUntilExpiration;
    
    @Data
    @Builder
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String color;
        private Integer defaultStorageDays;
        private String description;
    }
    
    @Data
    @Builder
    public static class LocationDTO {
        private Long id;
        private String name;
        private String description;
        private String freezerSection;
        private Integer sortOrder;
    }
}