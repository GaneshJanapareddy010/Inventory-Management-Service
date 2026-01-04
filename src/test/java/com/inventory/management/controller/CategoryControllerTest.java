package com.inventory.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.management.dto.request.CategoryRequest;
import com.inventory.management.dto.response.CategoryResponse;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.exception.ValidationException;
import com.inventory.management.service.CategoryService;
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
import java.util.List;

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
 * Integration tests for CategoryController.
 * Tests REST endpoints with MockMvc.
 */
@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
@DisplayName("CategoryController Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryRequest = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices and accessories")
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /categories - Should create category successfully")
    void shouldCreateCategorySuccessfully() throws Exception {
        // Given
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);

        // When & Then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andExpect(jsonPath("$.description", is("Electronic devices and accessories")));

        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("POST /categories - Should return 400 when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name("")
                .description("Description")
                .build();

        // When & Then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    @DisplayName("POST /categories - Should return 400 when name is too short")
    void shouldReturn400WhenNameIsTooShort() throws Exception {
        // Given
        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name("A")
                .description("Description")
                .build();

        // When & Then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("POST /categories - Should return 400 when name is too long")
    void shouldReturn400WhenNameIsTooLong() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append('A');
        }
        String longName = sb.toString();
        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name(longName)
                .description("Description")
                .build();

        // When & Then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /categories - Should return 400 when description is too long")
    void shouldReturn400WhenDescriptionIsTooLong() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            sb.append('A');
        }
        String longDescription = sb.toString();
        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name("Electronics")
                .description(longDescription)
                .build();

        // When & Then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /categories - Should return 400 when duplicate name")
    void shouldReturn400WhenDuplicateName() throws Exception {
        // Given
        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenThrow(new ValidationException("Category with name 'Electronics' already exists"));

        // When & Then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Category with name 'Electronics' already exists")));
    }

    @Test
    @DisplayName("GET /categories/{id} - Should get category by ID successfully")
    void shouldGetCategoryByIdSuccessfully() throws Exception {
        // Given
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);

        // When & Then
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andExpect(jsonPath("$.description", is("Electronic devices and accessories")));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return 404 when category not found")
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        // Given
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Category", "id", 999L));

        // When & Then
        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Category not found with id: '999'")));
    }

    @Test
    @DisplayName("GET /categories - Should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() throws Exception {
        // Given
        CategoryResponse response2 = CategoryResponse.builder()
                .id(2L)
                .name("Apparel")
                .description("Clothing and accessories")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<CategoryResponse> responses = Arrays.asList(categoryResponse, response2);
        when(categoryService.getAllCategories()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Electronics")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Apparel")));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("GET /categories - Should return empty list when no categories exist")
    void shouldReturnEmptyListWhenNoCategoriesExist() throws Exception {
        // Given
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Should update category successfully")
    void shouldUpdateCategorySuccessfully() throws Exception {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Updated Electronics")
                .description("Updated description")
                .build();

        CategoryResponse updatedResponse = CategoryResponse.builder()
                .id(1L)
                .name("Updated Electronics")
                .description("Updated description")
                .createdAt(categoryResponse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(categoryService.updateCategory(eq(1L), any(CategoryRequest.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Electronics")))
                .andExpect(jsonPath("$.description", is("Updated description")));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryRequest.class));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Should return 404 when updating non-existent category")
    void shouldReturn404WhenUpdatingNonExistentCategory() throws Exception {
        // Given
        when(categoryService.updateCategory(eq(999L), any(CategoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category", "id", 999L));

        // When & Then
        mockMvc.perform(put("/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Should return 400 when updating with duplicate name")
    void shouldReturn400WhenUpdatingWithDuplicateName() throws Exception {
        // Given
        when(categoryService.updateCategory(eq(1L), any(CategoryRequest.class)))
                .thenThrow(new ValidationException("Category with name 'Electronics' already exists"));

        // When & Then
        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Should return 400 when request body is invalid")
    void shouldReturn400WhenUpdateRequestBodyIsInvalid() throws Exception {
        // Given
        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name("")
                .description("Description")
                .build();

        // When & Then
        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Should delete category successfully")
    void shouldDeleteCategorySuccessfully() throws Exception {
        // Given
        doNothing().when(categoryService).deleteCategory(1L);

        // When & Then
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Should return 404 when deleting non-existent category")
    void shouldReturn404WhenDeletingNonExistentCategory() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Category", "id", 999L))
                .when(categoryService).deleteCategory(999L);

        // When & Then
        mockMvc.perform(delete("/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Category not found with id: '999'")));
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Should return 400 when category has products")
    void shouldReturn400WhenDeletingCategoryWithProducts() throws Exception {
        // Given
        doThrow(new ValidationException("Cannot delete category 'Electronics' because it has associated products. Please delete or reassign the products first."))
                .when(categoryService).deleteCategory(1L);

        // When & Then
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Cannot delete category 'Electronics' because it has associated products. Please delete or reassign the products first.")));
    }
}
