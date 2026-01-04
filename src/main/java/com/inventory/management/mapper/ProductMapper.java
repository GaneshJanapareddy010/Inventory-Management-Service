package com.inventory.management.mapper;

import com.inventory.management.dto.request.ProductRequest;
import com.inventory.management.dto.response.ProductResponse;
import com.inventory.management.entity.Category;
import com.inventory.management.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Product entity and DTOs.
 */
@Component
public class ProductMapper {

    /**
     * Convert ProductRequest to Product entity.
     * Note: Category must be set separately in the service layer.
     *
     * @param request the product request DTO
     * @return Product entity
     */
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

    /**
     * Update existing Product entity from ProductRequest.
     * Note: Category must be updated separately in the service layer.
     *
     * @param entity the existing product entity
     * @param request the product request DTO
     */
    public void updateEntity(Product entity, ProductRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
    }

    /**
     * Convert Product entity to ProductResponse DTO.
     *
     * @param entity the product entity
     * @return ProductResponse DTO
     */
    public ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }

        ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        // Handle category (may be lazy-loaded)
        if (entity.getCategory() != null) {
            builder.categoryId(entity.getCategory().getId())
                   .categoryName(entity.getCategory().getName());
        }

        return builder.build();
    }
}
