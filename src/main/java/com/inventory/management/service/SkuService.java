package com.inventory.management.service;

import com.inventory.management.dto.request.SkuRequest;
import com.inventory.management.dto.response.SkuResponse;
import com.inventory.management.entity.Product;
import com.inventory.management.entity.Sku;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.exception.ValidationException;
import com.inventory.management.mapper.SkuMapper;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.repository.SkuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing SKUs.
 */
@Service
@Slf4j
@Transactional
public class SkuService {

    private final SkuRepository skuRepository;
    private final ProductRepository productRepository;
    private final SkuMapper skuMapper;

    public SkuService(SkuRepository skuRepository, ProductRepository productRepository, SkuMapper skuMapper) {
        this.skuRepository = skuRepository;
        this.productRepository = productRepository;
        this.skuMapper = skuMapper;
    }

    /**
     * Create a new SKU.
     *
     * @param request the SKU request
     * @return created SKU response
     */
    public SkuResponse createSku(SkuRequest request) {
        log.debug("Creating SKU: {}", request.getSkuCode());

        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", request.getProductId());
                    return new ResourceNotFoundException("Product", "id", request.getProductId());
                });

        // Check for duplicate SKU code
        if (skuRepository.existsBySkuCode(request.getSkuCode())) {
            log.warn("Attempt to create duplicate SKU code: {}", request.getSkuCode());
            throw new ValidationException("SKU code already exists: " + request.getSkuCode());
        }

        Sku sku = skuMapper.toEntity(request, product);
        Sku savedSku = skuRepository.save(sku);

        log.info("SKU created successfully with ID: {}", savedSku.getId());
        return skuMapper.toResponse(savedSku);
    }

    /**
     * Get SKU by ID.
     *
     * @param id the SKU ID
     * @return SKU response
     */
    @Transactional(readOnly = true)
    public SkuResponse getSkuById(Long id) {
        log.debug("Fetching SKU with ID: {}", id);

        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("SKU not found with ID: {}", id);
                    return new ResourceNotFoundException("SKU", "id", id);
                });

        return skuMapper.toResponse(sku);
    }

    /**
     * Get all SKUs for a product.
     *
     * @param productId the product ID
     * @return list of SKU responses
     */
    @Transactional(readOnly = true)
    public List<SkuResponse> getSkusByProductId(Long productId) {
        log.debug("Fetching SKUs for product ID: {}", productId);

        // Validate product exists
        if (!productRepository.existsById(productId)) {
            log.warn("Product not found with ID: {}", productId);
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        List<Sku> skus = skuRepository.findByProductId(productId);
        log.debug("Found {} SKUs for product ID: {}", skus.size(), productId);

        return skus.stream()
                .map(skuMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing SKU.
     *
     * @param id      the SKU ID
     * @param request the update request
     * @return updated SKU response
     */
    public SkuResponse updateSku(Long id, SkuRequest request) {
        log.debug("Updating SKU with ID: {}", id);

        // Fetch existing SKU
        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("SKU not found with ID: {}", id);
                    return new ResourceNotFoundException("SKU", "id", id);
                });

        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", request.getProductId());
                    return new ResourceNotFoundException("Product", "id", request.getProductId());
                });

        // Check for duplicate SKU code (excluding current SKU)
        if (skuRepository.existsBySkuCodeAndIdNot(request.getSkuCode(), id)) {
            log.warn("Attempt to update SKU with duplicate code: {}", request.getSkuCode());
            throw new ValidationException("SKU code already exists: " + request.getSkuCode());
        }

        skuMapper.updateEntity(sku, request, product);
        Sku updatedSku = skuRepository.save(sku);

        log.info("SKU updated successfully with ID: {}", updatedSku.getId());
        return skuMapper.toResponse(updatedSku);
    }

    /**
     * Delete a SKU.
     *
     * @param id the SKU ID
     */
    public void deleteSku(Long id) {
        log.debug("Deleting SKU with ID: {}", id);

        if (!skuRepository.existsById(id)) {
            log.warn("SKU not found with ID: {}", id);
            throw new ResourceNotFoundException("SKU", "id", id);
        }

        skuRepository.deleteById(id);
        log.info("SKU deleted successfully with ID: {}", id);
    }
}
