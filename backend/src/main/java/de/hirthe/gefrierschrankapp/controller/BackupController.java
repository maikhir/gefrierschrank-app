package de.hirthe.gefrierschrankapp.controller;

import de.hirthe.gefrierschrankapp.dto.BackupDTO;
import de.hirthe.gefrierschrankapp.dto.RestoreRequest;
import de.hirthe.gefrierschrankapp.dto.RestoreResult;
import de.hirthe.gefrierschrankapp.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class BackupController {

    private final BackupService backupService;
    private final ObjectMapper objectMapper;

    @GetMapping("/export")
    public ResponseEntity<Resource> exportBackup(@RequestParam(defaultValue = "json") String format) {
        try {
            log.info("Creating backup export in format: {}", format);
            
            BackupDTO backup = backupService.createBackup();
            
            if ("zip".equalsIgnoreCase(format)) {
                return createZipBackup(backup);
            } else {
                return createJsonBackup(backup);
            }
            
        } catch (Exception e) {
            log.error("Error creating backup export", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/import")
    public ResponseEntity<RestoreResult> importBackup(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(defaultValue = "false") boolean clearExistingData,
                                                     @RequestParam(defaultValue = "SKIP") RestoreRequest.ConflictResolution conflictResolution,
                                                     @RequestParam(defaultValue = "false") boolean preserveIds) {
        try {
            log.info("Importing backup from file: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
            
            if (file.isEmpty()) {
                RestoreResult result = RestoreResult.builder()
                    .success(false)
                    .message("File is empty")
                    .build();
                result.getErrors().add("No file provided or file is empty");
                return ResponseEntity.badRequest().body(result);
            }

            BackupDTO backup;
            String filename = file.getOriginalFilename();
            
            if (filename != null && filename.toLowerCase().endsWith(".zip")) {
                backup = extractBackupFromZip(file);
            } else {
                backup = extractBackupFromJson(file);
            }

            RestoreRequest.RestoreOptions options = RestoreRequest.RestoreOptions.builder()
                .clearExistingData(clearExistingData)
                .conflictResolution(conflictResolution)
                .preserveIds(preserveIds)
                .build();

            RestoreRequest request = RestoreRequest.builder()
                .backup(backup)
                .options(options)
                .build();

            RestoreResult result = backupService.restoreFromBackup(request);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            log.error("Error importing backup", e);
            RestoreResult result = RestoreResult.builder()
                .success(false)
                .message("Import failed: " + e.getMessage())
                .build();
            result.getErrors().add(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<RestoreResult> restoreFromBackup(@Valid @RequestBody RestoreRequest request) {
        try {
            log.info("Restoring backup data");
            
            RestoreResult result = backupService.restoreFromBackup(request);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
        } catch (Exception e) {
            log.error("Error restoring backup", e);
            RestoreResult result = RestoreResult.builder()
                .success(false)
                .message("Restore failed: " + e.getMessage())
                .build();
            result.getErrors().add(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAllData() {
        try {
            log.info("Clearing all data");
            backupService.clearAllData();
            return ResponseEntity.ok("All data cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error clearing data: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateBackup(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String filename = file.getOriginalFilename();
            BackupDTO backup;
            
            if (filename != null && filename.toLowerCase().endsWith(".zip")) {
                backup = extractBackupFromZip(file);
            } else {
                backup = extractBackupFromJson(file);
            }

            // Basic validation
            if (backup == null) {
                return ResponseEntity.badRequest().body("Invalid backup format");
            }

            if (backup.getCategories() == null || backup.getLocations() == null || backup.getProducts() == null) {
                return ResponseEntity.badRequest().body("Backup missing required data collections");
            }

            String summary = String.format("Valid backup found: %d categories, %d locations, %d products",
                backup.getCategories().size(), backup.getLocations().size(), backup.getProducts().size());
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            log.error("Error validating backup", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid backup file: " + e.getMessage());
        }
    }

    private ResponseEntity<Resource> createJsonBackup(BackupDTO backup) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        String jsonContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(backup);
        byte[] data = jsonContent.getBytes();
        
        String filename = "gefrierschrank-backup-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".json";
        
        ByteArrayResource resource = new ByteArrayResource(data);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .contentLength(data.length)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(resource);
    }

    private ResponseEntity<Resource> createZipBackup(BackupDTO backup) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        
        // Add backup.json to zip
        ZipEntry entry = new ZipEntry("backup.json");
        zos.putNextEntry(entry);
        
        String jsonContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(backup);
        zos.write(jsonContent.getBytes());
        zos.closeEntry();
        
        // Add metadata file
        ZipEntry metaEntry = new ZipEntry("metadata.txt");
        zos.putNextEntry(metaEntry);
        
        String metadata = String.format(
            "Gefrierschrank App Backup\n" +
            "Created: %s\n" +
            "Version: %s\n" +
            "Categories: %d\n" +
            "Locations: %d\n" +
            "Products: %d\n",
            backup.getBackupTimestamp(),
            backup.getVersion(),
            backup.getCategories().size(),
            backup.getLocations().size(),
            backup.getProducts().size()
        );
        
        zos.write(metadata.getBytes());
        zos.closeEntry();
        zos.close();
        
        byte[] data = baos.toByteArray();
        
        String filename = "gefrierschrank-backup-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".zip";
        
        ByteArrayResource resource = new ByteArrayResource(data);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(data.length)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(resource);
    }

    private BackupDTO extractBackupFromJson(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        return mapper.readValue(file.getInputStream(), BackupDTO.class);
    }

    private BackupDTO extractBackupFromZip(MultipartFile file) throws IOException {
        // For simplicity, we'll assume the first JSON file in the ZIP is the backup
        // In a more robust implementation, we could look for a specific filename
        throw new UnsupportedOperationException("ZIP import not yet implemented - use JSON format");
    }
}