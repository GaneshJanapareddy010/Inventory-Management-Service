package com.inventory.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.management.dto.request.SkuRequest;
import com.inventory.management.dto.response.SkuResponse;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.exception.ValidationException;
import com.inventory.management.service.SkuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
 * Integration tests for SkuController.
 */
@WebMvcTest(SkuController.class)
@ActiveProfiles("test")
@DisplayName("SkuController Tests")
class SkuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkuService skuService;

    private SkuRequest skuRequest;
    private SkuResponse skuResponse;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
        attributes.put("color", "Silver");
        attributes.put("size", "15-inch");

        skuRequest = SkuRequest.builder()
                .skuCode("LAP-001")
                .productId(1L)
                .quantity(100)
                .attributes(attributes)
                .build();

        skuResponse = SkuResponse.builder()
                .id(1L)
                .skuCode("LAP-001")
                .productId(1L)
                .productName("Laptop")
                .quantity(100)
                .attributes(attributes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /skus - Should create SKU successfully")
    void shouldCreateSkuSuccessfully() throws Exception {
        // Given
        when(skuService.createSku(any(SkuRequest.class))).thenReturn(skuResponse);

        // When & Then
        mockMvc.perform(post("/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skuRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.skuCode", is("LAP-001")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productName", is("Laptop")))
                .andExpect(jsonPath("$.attributes.color", is("Silver")));

        verify(skuService, times(1)).createSku(any(SkuRequest.class));
    }

    @Test
    @DisplayName("POST /skus - Should return 400 when SKU code is blank")
    void shouldReturn400WhenSkuCodeIsBlank() throws Exception {
        // Given
        SkuRequest invalidRequest = SkuRequest.builder()
                .skuCode("")
                .productId(1L)
                .quantity(100)
                .attributes(attributes)
                .build();

        // When & Then
        mockMvc.perform(post("/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("POST /skus - Should return 400 when product ID is null")
    void shouldReturn400WhenProductIdIsNull() throws Exception {
        // Given
        SkuRequest invalidRequest = SkuRequest.builder()
                .skuCode("LAP-001")
                .productId(null)
                .quantity(100)
                .attributes(attributes)
                .build();

        // When & Then
        mockMvc.perform(post("/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /skus - Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        when(skuService.createSku(any(SkuRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product", "id", 999L));

        // When & Then
        mockMvc.perform(post("/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skuRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Product not found with id: '999'")));
    }

    @Test
    @DisplayName("POST /skus - Should return 400 when SKU code already exists")
    void shouldReturn400WhenSkuCodeAlreadyExists() throws Exception {
        // Given
        when(skuService.createSku(any(SkuRequest.class)))
                .thenThrow(new ValidationException("SKU code already exists: LAP-001"));

        // When & Then
        mockMvc.perform(post("/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skuRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("SKU code already exists: LAP-001")));
    }

    @Test
    @DisplayName("GET /skus/{id} - Should get SKU by ID successfully")
    void shouldGetSkuByIdSuccessfully() throws Exception {
        // Given
        when(skuService.getSkuById(1L)).thenReturn(skuResponse);

        // When & Then
        mockMvc.perform(get("/skus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.skuCode", is("LAP-001")))
                .andExpect(jsonPath("$.productId", is(1)));

        verify(skuService, times(1)).getSkuById(1L);
    }

    @Test
    @DisplayName("GET /skus/{id} - Should return 404 when SKU not found")
    void shouldReturn404WhenSkuNotFound() throws Exception {
        // Given
        when(skuService.getSkuById(999L))
                .thenThrow(new ResourceNotFoundException("SKU", "id", 999L));

        // When & Then
        mockMvc.perform(get("/skus/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("SKU not found with id: '999'")));
    }

    @Test
    @DisplayName("GET /skus?productId=1 - Should get all SKUs for a product")
    void shouldGetAllSkusForProduct() throws Exception {
        // Given
        SkuResponse response2 = SkuResponse.builder()
                .id(2L)
                .skuCode("LAP-002")
                .productId(1L)
                .productName("Laptop")
                .attributes(new HashMap<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(skuService.getSkusByProductId(1L)).thenReturn(Arrays.asList(skuResponse, response2));

        // When & Then
        mockMvc.perform(get("/skus?productId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].skuCode", is("LAP-001")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].skuCode", is("LAP-002")));

        verify(skuService, times(1)).getSkusByProductId(1L);
    }

    @Test
    @DisplayName("GET /skus?productId=999 - Should return 404 when product not found")
    void shouldReturn404WhenGettingSkusForNonExistentProduct() throws Exception {
        // Given
        when(skuService.getSkusByProductId(999L))
                .thenThrow(new ResourceNotFoundException("Product", "id", 999L));

        // When & Then
        mockMvc.perform(get("/skus?productId=999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Product not found with id: '999'")));
    }

    @Test
    @DisplayName("GET /skus?productId=1 - Should return empty list when product has no SKUs")
    void shouldReturnEmptyListWhenProductHasNoSkus() throws Exception {
        // Given
        when(skuService.getSkusByProductId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/skus?productId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("PUT /skus/{id} - Should update SKU successfully")
    void shouldUpdateSkuSuccessfully() throws Exception {
        // Given
        SkuRequest updateRequest = SkuRequest.builder()
                .skuCode("LAP-001-UPDATED")
                .productId(1L)
                .quantity(100)
                .attributes(attributes)
                .build();

        SkuResponse updatedResponse = SkuResponse.builder()
                .id(1L)
                .skuCode("LAP-001-UPDATED")
                .productId(1L)
                .productName("Laptop")
                .quantity(100)
                .attributes(attributes)
                .createdAt(skuResponse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(skuService.updateSku(eq(1L), any(SkuRequest.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/skus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.skuCode", is("LAP-001-UPDATED")));

        verify(skuService, times(1)).updateSku(eq(1L), any(SkuRequest.class));
    }

    @Test
    @DisplayName("PUT /skus/{id} - Should return 404 when updating non-existent SKU")
    void shouldReturn404WhenUpdatingNonExistentSku() throws Exception {
        // Given
        when(skuService.updateSku(eq(999L), any(SkuRequest.class)))
                .thenThrow(new ResourceNotFoundException("SKU", "id", 999L));

        // When & Then
        mockMvc.perform(put("/skus/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skuRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("PUT /skus/{id} - Should return 404 when updating with invalid product")
    void shouldReturn404WhenUpdatingWithInvalidProduct() throws Exception {
        // Given
        when(skuService.updateSku(eq(1L), any(SkuRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product", "id", 999L));

        // When & Then
        mockMvc.perform(put("/skus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skuRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /skus/{id} - Should return 400 when updating with duplicate SKU code")
    void shouldReturn400WhenUpdatingWithDuplicateSkuCode() throws Exception {
        // Given
        when(skuService.updateSku(eq(1L), any(SkuRequest.class)))
                .thenThrow(new ValidationException("SKU code already exists: LAP-002"));

        // When & Then
        mockMvc.perform(put("/skus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skuRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("SKU code already exists: LAP-002")));
    }

    @Test
    @DisplayName("DELETE /skus/{id} - Should delete SKU successfully")
    void shouldDeleteSkuSuccessfully() throws Exception {
        // Given
        doNothing().when(skuService).deleteSku(1L);

        // When & Then
        mockMvc.perform(delete("/skus/1"))
                .andExpect(status().isNoContent());

        verify(skuService, times(1)).deleteSku(1L);
    }

    @Test
    @DisplayName("DELETE /skus/{id} - Should return 404 when deleting non-existent SKU")
    void shouldReturn404WhenDeletingNonExistentSku() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("SKU", "id", 999L))
                .when(skuService).deleteSku(999L);

        // When & Then
        mockMvc.perform(delete("/skus/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
