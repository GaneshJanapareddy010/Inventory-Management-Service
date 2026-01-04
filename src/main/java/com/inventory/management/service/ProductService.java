package com.inventory.management.service;

import com.inventory.management.dto.request.ProductRequest;
import com.inventory.management.dto.response.PagedResponse;
import com.inventory.management.dto.response.ProductResponse;
import com.inventory.management.entity.Category;
import com.inventory.management.entity.Product;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.exception.ValidationException;
import com.inventory.management.mapper.ProductMapper;
import com.inventory.management.repository.CategoryRepository;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.repository.specification.ProductSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Product business logic.
 * Handles product CRUD operations with category validation and search/filter.
 */
@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    /**
     * Create a new product.
     *
     * @param request the product request DTO
     * @return ProductResponse the created product
     * @throws ResourceNotFoundException if category not found
     */
    public ProductResponse createProduct(ProductRequest request) {
        logger.debug("Creating product: {}", request.getName());

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    logger.warn("Category not found with ID: {}", request.getCategoryId());
                    return new ResourceNotFoundException("Category", "id", request.getCategoryId());
                });

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", savedProduct.getId());

        return productMapper.toResponse(savedProduct);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return ProductResponse the found product
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        logger.debug("Fetching product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });

        return productMapper.toResponse(product);
    }

    /**
     * Search and filter products with pagination.
     *
     * @param search search term for product name
     * @param categoryId filter by category ID
     * @param minPrice filter by minimum price
     * @param maxPrice filter by maximum price
     * @param pageable pagination parameters
     * @return PagedResponse with products
     */
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(String search,
                                                          Long categoryId,
                                                          BigDecimal minPrice,
                                                          BigDecimal maxPrice,
                                                          Pageable pageable) {
        logger.debug("Searching products: search={}, categoryId={}, minPrice={}, maxPrice={}, page={}, size={}",
                search, categoryId, minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize());

        // Build specification using AND conditions
        Specification<Product> spec = Specification.where(ProductSpecification.hasNameLike(search))
                .and(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.hasPriceGreaterThanOrEqual(minPrice))
                .and(ProductSpecification.hasPriceLessThanOrEqual(maxPrice));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductResponse> products = productPage.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        logger.debug("Found {} products out of {} total", products.size(), productPage.getTotalElements());

        return PagedResponse.<ProductResponse>builder()
                .content(products)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .empty(productPage.isEmpty())
                .build();
    }

    /**
     * Update an existing product.
     *
     * @param id the product ID
     * @param request the product request DTO
     * @return ProductResponse the updated product
     * @throws ResourceNotFoundException if product or category not found
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        logger.debug("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    logger.warn("Category not found with ID: {}", request.getCategoryId());
                    return new ResourceNotFoundException("Category", "id", request.getCategoryId());
                });

        productMapper.updateEntity(product, request);
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully with ID: {}", id);

        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Delete a product by ID.
     *
     * @param id the product ID
     * @throws ResourceNotFoundException if product not found
     */
    public void deleteProduct(Long id) {
        logger.debug("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });

        productRepository.delete(product);
        logger.info("Product deleted successfully with ID: {}", id);
    }
}
