package de.hirthe.gefrierschrankapp.controller;

import de.hirthe.gefrierschrankapp.service.ImageService;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Uploading image: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
            
            ImageService.ImageUploadResult result = imageService.uploadImage(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", result.getFilename());
            response.put("size", result.getSize());
            response.put("contentType", result.getContentType());
            response.put("width", result.getWidth());
            response.put("height", result.getHeight());
            // Return just the filename - let frontend construct URLs
            response.put("thumbnailUrl", result.getFilename() + "?size=thumbnail");
            response.put("mediumUrl", result.getFilename() + "?size=medium");
            response.put("originalUrl", result.getFilename());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Failed to upload image", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error during image upload", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Internal server error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename,
            @RequestParam(defaultValue = "original") String size) {
        
        try {
            ImageService.ImageSize imageSize;
            try {
                imageSize = ImageService.ImageSize.valueOf(size.toUpperCase());
            } catch (IllegalArgumentException e) {
                imageSize = ImageService.ImageSize.ORIGINAL;
            }
            
            byte[] imageBytes = imageService.getImage(filename, imageSize);
            String contentType = imageService.getImageContentType(filename);
            
            ByteArrayResource resource = new ByteArrayResource(imageBytes);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(imageBytes.length)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000") // Cache for 1 year
                .body(resource);
                
        } catch (IOException e) {
            log.warn("Image not found: {}", filename);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error serving image: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable String filename) {
        try {
            imageService.deleteImage(filename);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to delete image: {}", filename, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/info/{filename}")
    public ResponseEntity<Map<String, Object>> getImageInfo(@PathVariable String filename) {
        try {
            byte[] imageBytes = imageService.getImage(filename, ImageService.ImageSize.ORIGINAL);
            String contentType = imageService.getImageContentType(filename);
            
            Map<String, Object> info = new HashMap<>();
            info.put("filename", filename);
            info.put("size", imageBytes.length);
            info.put("contentType", contentType);
            info.put("thumbnailUrl", "/api/images/" + filename + "?size=thumbnail");
            info.put("mediumUrl", "/api/images/" + filename + "?size=medium");
            info.put("originalUrl", "/api/images/" + filename);
            
            return ResponseEntity.ok(info);
            
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting image info: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}