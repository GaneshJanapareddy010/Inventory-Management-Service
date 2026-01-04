package com.inventory.management.repository.specification;

import com.inventory.management.entity.Category;
import com.inventory.management.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.math.BigDecimal;

/**
 * JPA Specifications for Product entity.
 * Provides dynamic query building for search and filter operations.
 */
public class ProductSpecification {

    /**
     * Search products by name (case-insensitive, partial match).
     *
     * @param name the search term
     * @return Specification for name search
     */
    public static Specification<Product> hasNameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    /**
     * Filter products by category ID.
     *
     * @param categoryId the category ID
     * @return Specification for category filter
     */
    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, Category> categoryJoin = root.join("category");
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    /**
     * Filter products with price greater than or equal to minimum price.
     *
     * @param minPrice the minimum price
     * @return Specification for minimum price filter
     */
    public static Specification<Product> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    /**
     * Filter products with price less than or equal to maximum price.
     *
     * @param maxPrice the maximum price
     * @return Specification for maximum price filter
     */
    public static Specification<Product> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }
}
