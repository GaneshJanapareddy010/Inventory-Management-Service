package com.inventory.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Data Transfer Object for SKU creation and update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a SKU (Stock Keeping Unit)")
public class SkuRequest {

    @Schema(description = "Unique SKU code identifier", 
            example = "MBP16-SG-512", 
            required = true)
    @NotBlank(message = "SKU code is required")
    private String skuCode;

    @Schema(description = "ID of the product this SKU belongs to", 
            example = "1", 
            required = true)
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Quantity in stock", 
            example = "100", 
            required = true)
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Schema(description = "Flexible JSON attributes for SKU variants (e.g., color, size, storage)", 
            example = "{\"color\": \"Space Gray\", \"storage\": \"512GB\", \"ram\": \"18GB\"}")
    private Map<String, Object> attributes;
}
