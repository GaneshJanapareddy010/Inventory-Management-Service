package com.inventory.management.mapper;

import com.inventory.management.dto.request.CategoryRequest;
import com.inventory.management.dto.response.CategoryResponse;
import com.inventory.management.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CategoryMapper.
 * Tests entity-DTO conversion logic.
 */
@DisplayName("CategoryMapper Tests")
class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapper();
    }

    @Test
    @DisplayName("Should convert CategoryRequest to Category entity")
    void shouldConvertRequestToEntity() {
        // Given
        CategoryRequest request = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices and accessories")
                .build();

        // When
        Category entity = categoryMapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Electronics");
        assertThat(entity.getDescription()).isEqualTo("Electronic devices and accessories");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should return null when converting null request to entity")
    void shouldReturnNullWhenConvertingNullRequestToEntity() {
        // When
        Category entity = categoryMapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should update entity from CategoryRequest")
    void shouldUpdateEntityFromRequest() {
        // Given
        Category entity = Category.builder()
                .id(1L)
                .name("Old Name")
                .description("Old description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CategoryRequest request = CategoryRequest.builder()
                .name("Updated Name")
                .description("Updated description")
                .build();

        LocalDateTime originalCreatedAt = entity.getCreatedAt();
        Long originalId = entity.getId();

        // When
        categoryMapper.updateEntity(entity, request);

        // Then
        assertThat(entity.getId()).isEqualTo(originalId);
        assertThat(entity.getName()).isEqualTo("Updated Name");
        assertThat(entity.getDescription()).isEqualTo("Updated description");
        assertThat(entity.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("Should handle null entity when updating")
    void shouldHandleNullEntityWhenUpdating() {
        // Given
        CategoryRequest request = CategoryRequest.builder()
                .name("Electronics")
                .description("Description")
                .build();

        // When & Then - Should not throw exception
        categoryMapper.updateEntity(null, request);
    }

    @Test
    @DisplayName("Should handle null request when updating")
    void shouldHandleNullRequestWhenUpdating() {
        // Given
        Category entity = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Description")
                .build();

        String originalName = entity.getName();

        // When & Then - Should not throw exception
        categoryMapper.updateEntity(entity, null);

        // Entity should remain unchanged
        assertThat(entity.getName()).isEqualTo(originalName);
    }

    @Test
    @DisplayName("Should convert Category entity to CategoryResponse")
    void shouldConvertEntityToResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Category entity = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        CategoryResponse response = categoryMapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Electronics");
        assertThat(response.getDescription()).isEqualTo("Electronic devices and accessories");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should return null when converting null entity to response")
    void shouldReturnNullWhenConvertingNullEntityToResponse() {
        // When
        CategoryResponse response = categoryMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should handle entity with null description")
    void shouldHandleEntityWithNullDescription() {
        // Given
        Category entity = Category.builder()
                .id(1L)
                .name("Electronics")
                .description(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        CategoryResponse response = categoryMapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDescription()).isNull();
    }
}
