package com.inventory.management.service;

import com.inventory.management.dto.request.CategoryRequest;
import com.inventory.management.dto.response.CategoryResponse;
import com.inventory.management.entity.Category;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.exception.ValidationException;
import com.inventory.management.mapper.CategoryMapper;
import com.inventory.management.repository.CategoryRepository;
import com.inventory.management.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Category business logic.
 * Handles category CRUD operations with validation and error handling.
 */
@Service
@Transactional
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, 
                          ProductRepository productRepository,
                          CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Create a new category.
     *
     * @param request the category request DTO
     * @return CategoryResponse the created category
     * @throws ValidationException if category name already exists
     */
    public CategoryResponse createCategory(CategoryRequest request) {
        logger.debug("Creating category with name: {}", request.getName());

        // Check for duplicate category name
        if (categoryRepository.existsByName(request.getName())) {
            logger.warn("Attempt to create duplicate category: {}", request.getName());
            throw new ValidationException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);

        logger.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return CategoryResponse the found category
     * @throws ResourceNotFoundException if category not found
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        logger.debug("Fetching category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Category not found with ID: {}", id);
                    return new ResourceNotFoundException("Category", "id", id);
                });

        return categoryMapper.toResponse(category);
    }

    /**
     * Get all categories.
     *
     * @return List of CategoryResponse
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        logger.debug("Fetching all categories");

        List<Category> categories = categoryRepository.findAll();
        logger.debug("Found {} categories", categories.size());

        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing category.
     *
     * @param id the category ID
     * @param request the category request DTO
     * @return CategoryResponse the updated category
     * @throws ResourceNotFoundException if category not found
     * @throws ValidationException if category name already exists for another category
     */
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        logger.debug("Updating category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Category not found with ID: {}", id);
                    return new ResourceNotFoundException("Category", "id", id);
                });

        // Check for duplicate name (excluding current category)
        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            logger.warn("Attempt to update category with duplicate name: {}", request.getName());
            throw new ValidationException("Category with name '" + request.getName() + "' already exists");
        }

        categoryMapper.updateEntity(category, request);
        Category updatedCategory = categoryRepository.save(category);

        logger.info("Category updated successfully with ID: {}", id);
        return categoryMapper.toResponse(updatedCategory);
    }

    /**
     * Delete a category by ID.
     *
     * @param id the category ID
     * @throws ResourceNotFoundException if category not found
     * @throws ValidationException if category has associated products
     */
    public void deleteCategory(Long id) {
        logger.debug("Deleting category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Category not found with ID: {}", id);
                    return new ResourceNotFoundException("Category", "id", id);
                });

        // Check if category has associated products
        if (productRepository.existsByCategoryId(id)) {
            logger.warn("Cannot delete category with ID {} - has associated products", id);
            throw new ValidationException("Cannot delete category '" + category.getName() + 
                    "' because it has associated products. Please delete or reassign the products first.");
        }

        categoryRepository.delete(category);
        logger.info("Category deleted successfully with ID: {}", id);
    }
}
