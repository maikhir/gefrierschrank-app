package de.hirthe.gefrierschrankapp.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestoreRequest {
    
    private BackupDTO backup;
    
    private RestoreOptions options;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RestoreOptions {
        @Builder.Default
        private boolean clearExistingData = false;
        
        @Builder.Default
        private boolean skipConflicts = false;
        
        @Builder.Default
        private boolean preserveIds = false;
        
        @Builder.Default
        private ConflictResolution conflictResolution = ConflictResolution.SKIP;
    }
    
    public enum ConflictResolution {
        SKIP,           // Skip conflicting items
        OVERWRITE,      // Overwrite existing items
        RENAME,         // Add suffix to conflicting names
        MERGE           // Merge data where possible
    }
}