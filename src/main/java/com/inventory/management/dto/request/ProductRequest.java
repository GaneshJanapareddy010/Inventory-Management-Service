package com.inventory.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Request DTO for creating or updating a product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating or updating a product")
public class ProductRequest {

    @Schema(description = "Product name", 
            example = "MacBook Pro 16-inch", 
            required = true,
            minLength = 2,
            maxLength = 200)
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    private String name;

    @Schema(description = "Product description", 
            example = "High-performance laptop with M3 Pro chip, 18GB RAM, and 512GB SSD",
            maxLength = 1000)
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Schema(description = "Product price (must be greater than 0)", 
            example = "2499.99", 
            required = true,
            minimum = "0",
            exclusiveMinimum = true)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Schema(description = "ID of the category this product belongs to", 
            example = "1", 
            required = true)
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
