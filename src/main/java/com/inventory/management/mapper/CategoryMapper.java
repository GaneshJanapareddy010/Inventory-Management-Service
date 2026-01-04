package com.inventory.management.mapper;

import com.inventory.management.dto.request.CategoryRequest;
import com.inventory.management.dto.response.CategoryResponse;
import com.inventory.management.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Category entity and DTOs.
 * Provides type-safe conversions with explicit field mapping.
 */
@Component
public class CategoryMapper {

    /**
     * Convert CategoryRequest to Category entity.
     * Used for creating new categories.
     *
     * @param request the category request DTO
     * @return Category entity
     */
    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    /**
     * Update existing Category entity from CategoryRequest.
     * Preserves the ID and audit fields.
     *
     * @param entity the existing category entity
     * @param request the category request DTO
     */
    public void updateEntity(Category entity, CategoryRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
    }

    /**
     * Convert Category entity to CategoryResponse DTO.
     * Used for API responses.
     *
     * @param entity the category entity
     * @return CategoryResponse DTO
     */
    public CategoryResponse toResponse(Category entity) {
        if (entity == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
