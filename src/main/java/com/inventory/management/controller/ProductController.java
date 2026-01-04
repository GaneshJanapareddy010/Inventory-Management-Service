package com.inventory.management.controller;

import com.inventory.management.dto.request.ProductRequest;
import com.inventory.management.dto.response.PagedResponse;
import com.inventory.management.dto.response.ProductResponse;
import com.inventory.management.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import java.math.BigDecimal;

/**
 * REST Controller for Product operations.
 * Provides CRUD endpoints and search/filter with pagination.
 */
@RestController
@RequestMapping("/products")
@Validated
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create a new product.
     *
     * @param request the product request DTO
     * @return ResponseEntity with created product and HTTP 201 status
     */
    @PostMapping
    @Operation(
            summary = "Create a new product", 
            description = "Creates a new product in the specified category. The category must exist before creating a product.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product details to create",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProductRequest.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            name = "Create MacBook Pro",
                            value = "{\n  \"name\": \"MacBook Pro 16-inch\",\n  \"description\": \"High-performance laptop with M3 Pro chip, 18GB RAM, and 512GB SSD\",\n  \"price\": 2499.99,\n  \"categoryId\": 1\n}"
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation error", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", description = "Category not found", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", description = "Internal server error", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        logger.info("REST request to create product: {}", request.getName());
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return ResponseEntity with product details and HTTP 200 status
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID", 
            description = "Retrieves detailed information about a specific product including its category name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProductResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Product Details",
                                    value = "{\n  \"id\": 1,\n  \"name\": \"MacBook Pro 16-inch\",\n  \"description\": \"High-performance laptop\",\n  \"price\": 2499.99,\n  \"categoryId\": 1,\n  \"categoryName\": \"Electronics\",\n  \"createdAt\": \"2026-01-04T10:30:00\",\n  \"updatedAt\": \"2026-01-04T10:30:00\"\n}"
                            ))),
            @ApiResponse(responseCode = "404", description = "Product not found", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", description = "Internal server error", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Unique product identifier", required = true, example = "1")
            @PathVariable Long id) {
        logger.info("REST request to get product with ID: {}", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Search and filter products with pagination.
     *
     * @param search search term for product name
     * @param categoryId filter by category ID
     * @param minPrice filter by minimum price
     * @param maxPrice filter by maximum price
     * @param page page number (0-indexed)
     * @param size page size
     * @param sortBy sort field (default: name)
     * @param sortDir sort direction (asc/desc, default: asc)
     * @return ResponseEntity with paginated products
     */
    @GetMapping
    @Operation(
            summary = "Search and filter products", 
            description = "Retrieve products with optional filtering by name, category, and price range. " +
                    "Results are paginated and sortable. All filter parameters are optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully (may be empty list)",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameter type", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "500", description = "Internal server error", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @Parameter(description = "Search term for product name (case-insensitive, partial match)", example = "MacBook")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by category ID", example = "1")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "Minimum price filter (inclusive)", example = "1000.00")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum price filter (inclusive)", example = "3000.00")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size (number of items per page)", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort by field (e.g., name, price, createdAt)", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction: asc (ascending) or desc (descending)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("REST request to search products: search={}, categoryId={}, page={}, size={}",
                search, categoryId, page, size);

        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<ProductResponse> response = productService.searchProducts(
                search, categoryId, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing product.
     *
     * @param id the product ID
     * @param request the product request DTO
     * @return ResponseEntity with updated product and HTTP 200 status
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates an existing product with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Product or category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        logger.info("REST request to update product with ID: {}", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a product by ID.
     *
     * @param id the product ID
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        logger.info("REST request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
