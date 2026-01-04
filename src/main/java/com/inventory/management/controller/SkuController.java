package com.inventory.management.controller;

import com.inventory.management.dto.request.SkuRequest;
import com.inventory.management.dto.response.SkuResponse;
import com.inventory.management.service.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for SKU management operations.
 */
@RestController
@RequestMapping("/skus")
@Tag(name = "SKU Management", description = "APIs for managing Stock Keeping Units (SKUs) with flexible attributes")
public class SkuController {

    private final SkuService skuService;

    public SkuController(SkuService skuService) {
        this.skuService = skuService;
    }

    /**
     * Create a new SKU.
     *
     * @param request the SKU request
     * @return created SKU response
     */
    @PostMapping
    @Operation(
            summary = "Create a new SKU",
            description = "Creates a new SKU for a product with flexible attributes stored as JSON. SKU codes must be unique."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "SKU details to create",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SkuRequest.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"skuCode\":\"MBP16-SG-512\",\"productId\":1,\"quantity\":100,\"attributes\":{\"color\":\"Space Gray\",\"storage\":\"512GB\",\"ram\":\"18GB\",\"warranty\":\"3 years\"}}"
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "SKU created successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SkuResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"skuCode\":\"MBP16-SG-512\",\"productId\":1,\"productName\":\"MacBook Pro 16-inch\",\"quantity\":100,\"attributes\":{\"color\":\"Space Gray\",\"storage\":\"512GB\",\"ram\":\"18GB\",\"warranty\":\"3 years\"},\"createdAt\":\"2026-01-04T10:30:00\",\"updatedAt\":\"2026-01-04T10:30:00\"}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<SkuResponse> createSku(@Valid @RequestBody SkuRequest request) {
        SkuResponse response = skuService.createSku(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get SKU by ID.
     *
     * @param id the SKU ID
     * @return SKU response
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get SKU by ID",
            description = "Retrieves a SKU by its unique identifier, including all attributes and associated product details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SKU found",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SkuResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"skuCode\":\"MBP16-SG-512\",\"productId\":1,\"productName\":\"MacBook Pro 16-inch\",\"quantity\":100,\"attributes\":{\"color\":\"Space Gray\",\"storage\":\"512GB\",\"ram\":\"18GB\"},\"createdAt\":\"2026-01-04T10:30:00\",\"updatedAt\":\"2026-01-04T10:30:00\"}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<SkuResponse> getSkuById(
            @Parameter(description = "SKU unique identifier", required = true, example = "1")
            @PathVariable Long id) {
        SkuResponse response = skuService.getSkuById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all SKUs for a product.
     *
     * @param productId the product ID
     * @return list of SKU responses
     */
    @GetMapping
    @Operation(
            summary = "Get SKUs by product",
            description = "Retrieves all SKUs for a specific product. Use this to view all variants of a product (e.g., different colors, sizes, configurations)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SKUs retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SkuResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<List<SkuResponse>> getSkusByProductId(
            @Parameter(description = "Product ID to filter SKUs", required = true, example = "1")
            @RequestParam Long productId) {
        List<SkuResponse> responses = skuService.getSkusByProductId(productId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Update an existing SKU.
     *
     * @param id      the SKU ID
     * @param request the update request
     * @return updated SKU response
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update SKU",
            description = "Updates an existing SKU's attributes. SKU codes must remain unique."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated SKU details",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SkuRequest.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"skuCode\":\"MBP16-SG-1TB\",\"productId\":1,\"quantity\":150,\"attributes\":{\"color\":\"Space Gray\",\"storage\":\"1TB\",\"ram\":\"18GB\",\"warranty\":\"3 years\"}}"
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SKU updated successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SkuResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<SkuResponse> updateSku(
            @Parameter(description = "SKU unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SkuRequest request) {
        SkuResponse response = skuService.updateSku(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a SKU.
     *
     * @param id the SKU ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete SKU",
            description = "Deletes a SKU by its unique identifier. This removes the specific variant while keeping the product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "SKU deleted successfully"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<Void> deleteSku(
            @Parameter(description = "SKU unique identifier", required = true, example = "1")
            @PathVariable Long id) {
        skuService.deleteSku(id);
        return ResponseEntity.noContent().build();
    }
}
