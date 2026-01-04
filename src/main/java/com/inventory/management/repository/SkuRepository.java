package com.inventory.management.repository;

import com.inventory.management.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sku entity.
 */
@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {

    /**
     * Find all SKUs for a given product.
     *
     * @param productId the product ID
     * @return list of SKUs
     */
    @Query("SELECT s FROM Sku s WHERE s.product.id = :productId")
    List<Sku> findByProductId(@Param("productId") Long productId);

    /**
     * Find SKU by SKU code.
     *
     * @param skuCode the SKU code
     * @return Optional containing the SKU if found
     */
    Optional<Sku> findBySkuCode(String skuCode);

    /**
     * Check if a SKU code already exists (excluding a specific ID).
     *
     * @param skuCode the SKU code to check
     * @param id      the ID to exclude
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sku s WHERE s.skuCode = :skuCode AND s.id != :id")
    boolean existsBySkuCodeAndIdNot(@Param("skuCode") String skuCode, @Param("id") Long id);

    /**
     * Check if a SKU code already exists.
     *
     * @param skuCode the SKU code to check
     * @return true if exists, false otherwise
     */
    boolean existsBySkuCode(String skuCode);
}
