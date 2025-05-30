package de.hirthe.gefrierschrankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hirthe.gefrierschrankapp.dto.CreateProductRequest;
import de.hirthe.gefrierschrankapp.entity.Category;
import de.hirthe.gefrierschrankapp.entity.Location;
import de.hirthe.gefrierschrankapp.entity.Product;
import de.hirthe.gefrierschrankapp.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProductService productService;
    
    private Product testProduct;
    private CreateProductRequest createRequest;
    
    @BeforeEach
    void setUp() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .color("#FF0000")
                .defaultStorageDays(90)
                .build();
        
        Location location = Location.builder()
                .id(1L)
                .name("Test Location")
                .freezerSection("upper")
                .build();
        
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .category(category)
                .location(location)
                .quantity(new BigDecimal("2.5"))
                .unit("kg")
                .frozenDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(90))
                .notes("Test notes")
                .build();
        
        createRequest = new CreateProductRequest();
        createRequest.setName("New Product");
        createRequest.setCategoryId(1L);
        createRequest.setLocationId(1L);
        createRequest.setQuantity(new BigDecimal("1.0"));
        createRequest.setUnit("pieces");
        createRequest.setFrozenDate(LocalDate.now());
    }
    
    @Test
    @WithMockUser
    void shouldGetAllProducts() throws Exception {
        // Given
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productService.getProductsWithFilters(any(), any(), any(), any()))
                .thenReturn(productPage);
        
        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"))
                .andExpect(jsonPath("$.content[0].quantity").value(2.5))
                .andExpect(jsonPath("$.content[0].unit").value("kg"));
    }
    
    @Test
    @WithMockUser
    void shouldGetProductById() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.category.name").value("Test Category"))
                .andExpect(jsonPath("$.location.name").value("Test Location"));
    }
    
    @Test
    @WithMockUser
    void shouldReturnNotFoundForNonExistentProduct() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser
    void shouldCreateProduct() throws Exception {
        // Given
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);
        
        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.quantity").value(2.5));
    }
    
    @Test
    @WithMockUser
    void shouldValidateCreateProductRequest() throws Exception {
        // Given - Invalid request (empty name)
        createRequest.setName("");
        
        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void shouldValidateQuantity() throws Exception {
        // Given - Invalid quantity
        createRequest.setQuantity(new BigDecimal("-1.0"));
        
        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void shouldUpdateProduct() throws Exception {
        // Given
        when(productService.productExists(1L)).thenReturn(true);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(testProduct);
        
        // When & Then
        mockMvc.perform(put("/api/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }
    
    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws Exception {
        // Given
        when(productService.productExists(999L)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(put("/api/products/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser
    void shouldDeleteProduct() throws Exception {
        // Given
        when(productService.productExists(1L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/products/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        // Given
        when(productService.productExists(999L)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(delete("/api/products/999").with(csrf()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser
    void shouldGetProductsExpiringSoon() throws Exception {
        // Given
        when(productService.getProductsExpiringSoon(7)).thenReturn(List.of(testProduct));
        
        // When & Then
        mockMvc.perform(get("/api/products/expiring-soon")
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
    
    @Test
    @WithMockUser
    void shouldGetExpiredProducts() throws Exception {
        // Given
        when(productService.getExpiredProducts()).thenReturn(List.of(testProduct));
        
        // When & Then
        mockMvc.perform(get("/api/products/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
    
    @Test
    @WithMockUser
    void shouldGetProductStatistics() throws Exception {
        // Given
        ProductService.ProductStatistics stats = ProductService.ProductStatistics.builder()
                .totalProducts(10L)
                .expiredProducts(2L)
                .expiringSoon(3L)
                .build();
        when(productService.getProductStatistics()).thenReturn(stats);
        
        // When & Then
        mockMvc.perform(get("/api/products/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(10))
                .andExpect(jsonPath("$.expiredProducts").value(2))
                .andExpect(jsonPath("$.expiringSoon").value(3));
    }
}