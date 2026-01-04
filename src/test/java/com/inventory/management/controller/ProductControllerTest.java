package com.inventory.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.management.dto.request.ProductRequest;
import com.inventory.management.dto.response.PagedResponse;
import com.inventory.management.dto.response.ProductResponse;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for ProductController.
 */
@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("1299.99"))
                .categoryId(1L)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("1299.99"))
                .categoryId(1L)
                .categoryName("Electronics")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /products - Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(1299.99)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.categoryName", is("Electronics")));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("POST /products - Should return 400 when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        ProductRequest invalidRequest = ProductRequest.builder()
                .name("")
                .description("Description")
                .price(new BigDecimal("100"))
                .categoryId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("POST /products - Should return 400 when price is null")
    void shouldReturn400WhenPriceIsNull() throws Exception {
        // Given
        ProductRequest invalidRequest = ProductRequest.builder()
                .name("Laptop")
                .description("Description")
                .price(null)
                .categoryId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /products - Should return 400 when price is zero or negative")
    void shouldReturn400WhenPriceIsInvalid() throws Exception {
        // Given
        ProductRequest invalidRequest = ProductRequest.builder()
                .name("Laptop")
                .description("Description")
                .price(new BigDecimal("0"))
                .categoryId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /products - Should return 400 when categoryId is null")
    void shouldReturn400WhenCategoryIdIsNull() throws Exception {
        // Given
        ProductRequest invalidRequest = ProductRequest.builder()
                .name("Laptop")
                .description("Description")
                .price(new BigDecimal("100"))
                .categoryId(null)
                .build();

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /products - Should return 404 when category not found")
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category", "id", 999L));

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Category not found with id: '999'")));
    }

    @Test
    @DisplayName("GET /products/{id} - Should get product by ID successfully")
    void shouldGetProductByIdSuccessfully() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.categoryId", is(1)));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /products/{id} - Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        when(productService.getProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product", "id", 999L));

        // When & Then
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Product not found with id: '999'")));
    }

    @Test
    @DisplayName("GET /products - Should get all products with pagination")
    void shouldGetAllProductsWithPagination() throws Exception {
        // Given
        ProductResponse response2 = ProductResponse.builder()
                .id(2L)
                .name("Smartphone")
                .description("Latest smartphone")
                .price(new BigDecimal("899.99"))
                .categoryId(1L)
                .categoryName("Electronics")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PagedResponse<ProductResponse> pagedResponse = PagedResponse.<ProductResponse>builder()
                .content(Arrays.asList(productResponse, response2))
                .page(0)
                .size(20)
                .totalElements(2)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        when(productService.searchProducts(any(), any(), any(), any(), any()))
                .thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.empty", is(false)));
    }

    @Test
    @DisplayName("GET /products - Should search products by name")
    void shouldSearchProductsByName() throws Exception {
        // Given
        PagedResponse<ProductResponse> pagedResponse = PagedResponse.<ProductResponse>builder()
                .content(Collections.singletonList(productResponse))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        when(productService.searchProducts(eq("Laptop"), any(), any(), any(), any()))
                .thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get("/products?search=Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Laptop")));
    }

    @Test
    @DisplayName("GET /products - Should filter products by category")
    void shouldFilterProductsByCategory() throws Exception {
        // Given
        PagedResponse<ProductResponse> pagedResponse = PagedResponse.<ProductResponse>builder()
                .content(Collections.singletonList(productResponse))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        when(productService.searchProducts(any(), eq(1L), any(), any(), any()))
                .thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get("/products?categoryId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].categoryId", is(1)));
    }

    @Test
    @DisplayName("GET /products - Should filter products by price range")
    void shouldFilterProductsByPriceRange() throws Exception {
        // Given
        PagedResponse<ProductResponse> pagedResponse = PagedResponse.<ProductResponse>builder()
                .content(Collections.singletonList(productResponse))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        when(productService.searchProducts(any(), any(), any(), any(), any()))
                .thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get("/products?minPrice=1000&maxPrice=2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("GET /products - Should return empty page when no products match")
    void shouldReturnEmptyPageWhenNoProductsMatch() throws Exception {
        // Given
        PagedResponse<ProductResponse> emptyPage = PagedResponse.<ProductResponse>builder()
                .content(Collections.emptyList())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .empty(true)
                .build();

        when(productService.searchProducts(any(), any(), any(), any(), any()))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/products?search=nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.empty", is(true)));
    }

    @Test
    @DisplayName("GET /products - Should support custom page size and number")
    void shouldSupportCustomPagination() throws Exception {
        // Given
        PagedResponse<ProductResponse> pagedResponse = PagedResponse.<ProductResponse>builder()
                .content(Collections.singletonList(productResponse))
                .page(1)
                .size(10)
                .totalElements(15)
                .totalPages(2)
                .first(false)
                .last(false)
                .empty(false)
                .build();

        when(productService.searchProducts(any(), any(), any(), any(), any()))
                .thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get("/products?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(10)));
    }

    @Test
    @DisplayName("PUT /products/{id} - Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        // Given
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Laptop")
                .description("Updated description")
                .price(new BigDecimal("1399.99"))
                .categoryId(1L)
                .build();

        ProductResponse updatedResponse = ProductResponse.builder()
                .id(1L)
                .name("Updated Laptop")
                .description("Updated description")
                .price(new BigDecimal("1399.99"))
                .categoryId(1L)
                .categoryName("Electronics")
                .createdAt(productResponse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Laptop")))
                .andExpect(jsonPath("$.price", is(1399.99)));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    @DisplayName("PUT /products/{id} - Should return 404 when updating non-existent product")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        // Given
        when(productService.updateProduct(eq(999L), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product", "id", 999L));

        // When & Then
        mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("PUT /products/{id} - Should return 404 when updating with invalid category")
    void shouldReturn404WhenUpdatingWithInvalidCategory() throws Exception {
        // Given
        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category", "id", 999L));

        // When & Then
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /products/{id} - Should delete product successfully")
    void shouldDeleteProductSuccessfully() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /products/{id} - Should return 404 when deleting non-existent product")
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product", "id", 999L))
                .when(productService).deleteProduct(999L);

        // When & Then
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
