package com.inventory.management.controller;

import com.inventory.management.dto.request.CategoryRequest;
import com.inventory.management.dto.response.CategoryResponse;
import com.inventory.management.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Controller for Category operations.
 * Provides CRUD endpoints for managing categories.
 */
@RestController
@RequestMapping("/categories")
@Validated
@Tag(name = "Category", description = "Category management APIs for organizing products into groups")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Create a new category.
     *
     * @param request the category request DTO
     * @return ResponseEntity with created category and HTTP 201 status
     */
    @PostMapping
    @Operation(
            summary = "Create a new category",
            description = "Creates a new category with the provided details. Category names must be unique."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Category details to create",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryRequest.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"name\":\"Electronics\",\"description\":\"Electronic devices and accessories\"}"
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"name\":\"Electronics\",\"description\":\"Electronic devices and accessories\",\"createdAt\":\"2026-01-04T10:30:00\",\"updatedAt\":\"2026-01-04T10:30:00\"}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        
        logger.info("REST request to create category: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return ResponseEntity with category details and HTTP 200 status
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a category by its unique identifier. Returns category details including name and description."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"name\":\"Electronics\",\"description\":\"Electronic devices and accessories\",\"createdAt\":\"2026-01-04T10:30:00\",\"updatedAt\":\"2026-01-04T10:30:00\"}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "Category unique identifier", required = true, example = "1")
            @PathVariable Long id) {
        
        logger.info("REST request to get category with ID: {}", id);
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all categories.
     *
     * @return ResponseEntity with list of all categories and HTTP 200 status
     */
    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieves a list of all categories in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully"
            ),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        logger.info("REST request to get all categories");
        List<CategoryResponse> responses = categoryService.getAllCategories();
        return ResponseEntity.ok(responses);
    }

    /**
     * Update an existing category.
     *
     * @param id the category ID
     * @param request the category request DTO
     * @return ResponseEntity with updated category and HTTP 200 status
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update category",
            description = "Updates an existing category with the provided details. Category names must be unique."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated category details",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryRequest.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"name\":\"Consumer Electronics\",\"description\":\"Electronic devices and accessories for consumers\"}"
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        
        logger.info("REST request to update category with ID: {}", id);
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a category by ID.
     *
     * @param id the category ID
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete category",
            description = "Deletes a category by its unique identifier. Note: Cannot delete categories that have associated products."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category unique identifier", required = true, example = "1")
            @PathVariable Long id) {
        
        logger.info("REST request to delete category with ID: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
