package de.hirthe.gefrierschrankapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ImageServiceTest {

    @TempDir
    Path tempDir;

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageService();
        // Set the upload directory to our temp directory
        ReflectionTestUtils.setField(imageService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(imageService, "maxFileSize", 5242880L); // 5MB
    }

    @Test
    void testUploadInvalidFileType() {
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "Not an image".getBytes()
        );

        // Should throw IOException for invalid file type
        assertThrows(IOException.class, () -> imageService.uploadImage(file));
    }

    @Test
    void testUploadEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "empty.jpg", 
            "image/jpeg", 
            new byte[0]
        );

        // Should throw IOException for empty file
        assertThrows(IOException.class, () -> imageService.uploadImage(file));
    }

    @Test
    void testUploadWrongFileExtension() {
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.doc", 
            "image/jpeg", 
            "fake image content".getBytes()
        );

        // Should throw IOException for wrong extension
        assertThrows(IOException.class, () -> imageService.uploadImage(file));
    }

    @Test
    void testDeleteNonExistentImage() {
        // Deleting a non-existent image should not throw an exception
        assertDoesNotThrow(() -> imageService.deleteImage("non-existent.jpg"));
    }

    @Test
    void testGetImageContentType() {
        assertEquals("image/jpeg", imageService.getImageContentType("test.jpg"));
        assertEquals("image/jpeg", imageService.getImageContentType("test.jpeg"));
        assertEquals("image/png", imageService.getImageContentType("test.png"));
        assertEquals("image/gif", imageService.getImageContentType("test.gif"));
        assertEquals("image/webp", imageService.getImageContentType("test.webp"));
        assertEquals("application/octet-stream", imageService.getImageContentType("test.unknown"));
    }

}