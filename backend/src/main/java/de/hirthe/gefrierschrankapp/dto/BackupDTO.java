package de.hirthe.gefrierschrankapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupDTO {
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime backupTimestamp;
    
    private String version;
    
    private BackupMetadata metadata;
    
    private List<CategoryBackupDTO> categories;
    
    private List<LocationBackupDTO> locations;
    
    private List<ProductBackupDTO> products;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BackupMetadata {
        private long totalProducts;
        private long totalCategories;
        private long totalLocations;
        private String applicationVersion;
        private String description;
    }
}