package de.hirthe.gefrierschrankapp.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestoreResult {
    
    private boolean success;
    
    private String message;
    
    private RestoreStats stats;
    
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    
    @Builder.Default
    private List<String> warnings = new ArrayList<>();
    
    @Builder.Default
    private List<ConflictInfo> conflicts = new ArrayList<>();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RestoreStats {
        private int categoriesImported;
        private int categoriesSkipped;
        private int locationsImported;
        private int locationsSkipped;
        private int productsImported;
        private int productsSkipped;
        private int totalProcessed;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConflictInfo {
        private String type; // "category", "location", "product"
        private String name;
        private String resolution;
        private String details;
    }
}