package com.inventory.management.mapper;

import com.inventory.management.dto.request.SkuRequest;
import com.inventory.management.dto.response.SkuResponse;
import com.inventory.management.entity.Product;
import com.inventory.management.entity.Sku;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Sku entity and DTOs.
 */
@Component
public class SkuMapper {

    /**
     * Convert SkuRequest to Sku entity.
     *
     * @param request the SKU request
     * @param product the associated product
     * @return Sku entity
     */
    public Sku toEntity(SkuRequest request, Product product) {
        if (request == null) {
            return null;
        }

        return Sku.builder()
                .skuCode(request.getSkuCode())
                .product(product)
                .quantity(request.getQuantity())
                .attributes(request.getAttributes())
                .build();
    }

    /**
     * Convert Sku entity to SkuResponse.
     *
     * @param sku the SKU entity
     * @return SkuResponse DTO
     */
    public SkuResponse toResponse(Sku sku) {
        if (sku == null) {
            return null;
        }

        return SkuResponse.builder()
                .id(sku.getId())
                .skuCode(sku.getSkuCode())
                .productId(sku.getProduct() != null ? sku.getProduct().getId() : null)
                .productName(sku.getProduct() != null ? sku.getProduct().getName() : null)
                .quantity(sku.getQuantity())
                .attributes(sku.getAttributes())
                .createdAt(sku.getCreatedAt())
                .updatedAt(sku.getUpdatedAt())
                .build();
    }

    /**
     * Update existing Sku entity from SkuRequest.
     *
     * @param sku     the existing SKU entity
     * @param request the update request
     * @param product the associated product
     */
    public void updateEntity(Sku sku, SkuRequest request, Product product) {
        if (sku == null || request == null) {
            return;
        }

        sku.setSkuCode(request.getSkuCode());
        sku.setQuantity(request.getQuantity());
        sku.setProduct(product);
        sku.setAttributes(request.getAttributes());
    }
}
