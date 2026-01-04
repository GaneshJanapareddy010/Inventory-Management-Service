package com.inventory.management.service;

import com.inventory.management.dto.request.CategoryRequest;
import com.inventory.management.dto.response.CategoryResponse;
import com.inventory.management.entity.Category;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.exception.ValidationException;
import com.inventory.management.mapper.CategoryMapper;
import com.inventory.management.repository.CategoryRepository;
import com.inventory.management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CategoryService.
 * Tests business logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryRequest categoryRequest;
    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryRequest = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices and accessories")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() {
        // Given
        when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(false);
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.createCategory(categoryRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
        assertThat(result.getDescription()).isEqualTo("Electronic devices and accessories");

        verify(categoryRepository, times(1)).existsByName(categoryRequest.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toEntity(categoryRequest);
        verify(categoryMapper, times(1)).toResponse(category);
    }

    @Test
    @DisplayName("Should throw ValidationException when creating category with duplicate name")
    void shouldThrowValidationExceptionWhenCreatingDuplicateCategory() {
        // Given
        when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Category with name 'Electronics' already exists");

        verify(categoryRepository, times(1)).existsByName(categoryRequest.getName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void shouldGetCategoryByIdSuccessfully() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // When
        CategoryResponse result = categoryService.getCategoryById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryMapper, times(1)).toResponse(category);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found by ID")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFound() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: '999'");

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryMapper, never()).toResponse(any(Category.class));
    }

    @Test
    @DisplayName("Should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() {
        // Given
        Category category2 = Category.builder()
                .id(2L)
                .name("Apparel")
                .description("Clothing and accessories")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CategoryResponse response2 = CategoryResponse.builder()
                .id(2L)
                .name("Apparel")
                .description("Clothing and accessories")
                .createdAt(category2.getCreatedAt())
                .updatedAt(category2.getUpdatedAt())
                .build();

        List<Category> categories = Arrays.asList(category, category2);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);
        when(categoryMapper.toResponse(category2)).thenReturn(response2);

        // When
        List<CategoryResponse> result = categoryService.getAllCategories();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
        assertThat(result.get(1).getName()).isEqualTo("Apparel");

        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(2)).toResponse(any(Category.class));
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void shouldReturnEmptyListWhenNoCategoriesExist() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<CategoryResponse> result = categoryService.getAllCategories();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Updated Electronics")
                .description("Updated description")
                .build();

        Category updatedCategory = Category.builder()
                .id(1L)
                .name("Updated Electronics")
                .description("Updated description")
                .createdAt(category.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        CategoryResponse updatedResponse = CategoryResponse.builder()
                .id(1L)
                .name("Updated Electronics")
                .description("Updated description")
                .createdAt(updatedCategory.getCreatedAt())
                .updatedAt(updatedCategory.getUpdatedAt())
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndIdNot(updateRequest.getName(), 1L)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toResponse(updatedCategory)).thenReturn(updatedResponse);

        // When
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Electronics");
        assertThat(result.getDescription()).isEqualTo("Updated description");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByNameAndIdNot(updateRequest.getName(), 1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).updateEntity(category, updateRequest);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent category")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentCategory() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(999L, categoryRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: '999'");

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when updating with duplicate name")
    void shouldThrowValidationExceptionWhenUpdatingWithDuplicateName() {
        // Given
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Apparel")
                .description("Updated description")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndIdNot(updateRequest.getName(), 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Category with name 'Apparel' already exists");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByNameAndIdNot(updateRequest.getName(), 1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategorySuccessfully() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(false);

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).existsByCategoryId(1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent category")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentCategory() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: '999'");

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when deleting category with products")
    void shouldThrowValidationExceptionWhenDeletingCategoryWithProducts() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot delete category")
                .hasMessageContaining("has associated products");

        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).existsByCategoryId(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
