package com.inventory.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for SKU responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SKU (Stock Keeping Unit) response object")
public class SkuResponse {

    @Schema(description = "Unique SKU identifier", example = "1")
    private Long id;

    @Schema(description = "Unique SKU code", example = "MBP16-SG-512")
    private String skuCode;

    @Schema(description = "Product ID this SKU belongs to", example = "1")
    private Long productId;

    @Schema(description = "Product name", example = "MacBook Pro 16-inch")
    private String productName;

    @Schema(description = "Quantity in stock", example = "100")
    private Integer quantity;

    @Schema(description = "Flexible JSON attributes for SKU variants", 
            example = "{\"color\": \"Space Gray\", \"storage\": \"512GB\", \"ram\": \"18GB\"}")
    private Map<String, Object> attributes;

    @Schema(description = "Timestamp when SKU was created", example = "2026-01-04T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when SKU was last updated", example = "2026-01-04T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
