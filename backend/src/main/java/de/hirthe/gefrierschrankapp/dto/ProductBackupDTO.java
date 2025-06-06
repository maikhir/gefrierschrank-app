package de.hirthe.gefrierschrankapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBackupDTO {
    
    private Long id;
    
    private String name;
    
    private Long categoryId;
    
    private String categoryName;
    
    private Long locationId;
    
    private String locationName;
    
    private BigDecimal quantity;
    
    private String unit;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate frozenDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    
    private String notes;
    
    private String imageUrl;
    
    private String barcode;
    
    private Long userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}