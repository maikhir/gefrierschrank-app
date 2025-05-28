package de.hirthe.gefrierschrankapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must be less than 200 characters")
    private String name;
    
    private Long categoryId;
    
    private Long locationId;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantity;
    
    @Size(max = 20, message = "Unit must be less than 20 characters")
    private String unit = "pieces";
    
    @PastOrPresent(message = "Frozen date cannot be in the future")
    private LocalDate frozenDate;
    
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
    
    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
    
    @Size(max = 500, message = "Image URL must be less than 500 characters")
    private String imageUrl;
    
    @Size(max = 50, message = "Barcode must be less than 50 characters")
    private String barcode;
}