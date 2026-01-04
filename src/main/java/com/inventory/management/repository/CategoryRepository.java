package com.inventory.management.repository;

import com.inventory.management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Category entity.
 * Provides CRUD operations and custom queries for categories.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by its name.
     * Used to check for duplicate category names.
     *
     * @param name the category name
     * @return Optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Check if a category with the given name exists.
     *
     * @param name the category name
     * @return true if category exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a category with the given name exists, excluding a specific ID.
     * Useful for update operations to allow same name for the entity being updated.
     *
     * @param name the category name
     * @param id the category ID to exclude
     * @return true if another category with the same name exists
     */
    boolean existsByNameAndIdNot(String name, Long id);
}
