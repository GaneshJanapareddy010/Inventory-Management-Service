package com.inventory.management.repository;

import com.inventory.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity.
 * Extends JpaSpecificationExecutor for dynamic query support.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    /**
     * Check if a product with the given name exists.
     *
     * @param name the product name
     * @return true if product exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a product with the given name exists, excluding a specific ID.
     * Useful for update operations.
     *
     * @param name the product name
     * @param id the product ID to exclude
     * @return true if another product with the same name exists
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Check if any products exist for a given category.
     *
     * @param categoryId the category ID
     * @return true if products exist for the category
     */
    boolean existsByCategoryId(Long categoryId);
}
