package com.inventory.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SKU (Stock Keeping Unit) entity representing a sellable variant of a product.
 * Each SKU has a unique code and represents a specific configuration of a product.
 * 
 * Example: "iPhone 15 - 128GB - Black" would be one SKU of the "iPhone 15" product.
 * 
 * Relationships:
 * - N:1 with Product (many SKUs can belong to one product)
 */
@Entity
@Table(name = "skus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique SKU code identifier.
     * Format example: "PROD-001-BLK-128GB" or "LAP-DELL-XPS-15-I7"
     */
    @NotBlank(message = "SKU code is required")
    @Size(min = 3, max = 50, message = "SKU code must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9\\-_]+$", message = "SKU code must contain only uppercase letters, numbers, hyphens, and underscores")
    @Column(name = "sku_code", nullable = false, unique = true, length = 50)
    private String skuCode;

    /**
     * Quantity in stock. Must be non-negative.
     * Using Integer instead of int to allow null checks if needed.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Unidirectional Many-to-One relationship with Product.
     * Using LAZY fetch to optimize performance.
     * Foreign key constraint is enforced at the database level.
     */
    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @javax.persistence.ForeignKey(name = "fk_sku_product"))
    private Product product;

    /**
     * Additional attributes for the SKU variant stored as JSON.
     * Examples: {"color": "Black", "size": "128GB", "material": "Aluminum"}
     * 
     * Using @Type annotation for PostgreSQL JSON support.
     * For PostgreSQL, this will be stored as JSONB for better performance.
     * For H2 (testing), it will be stored as VARCHAR.
     */
    @Type(type = "json")
    @Column(name = "attributes", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Override equals and hashCode based on business key (skuCode).
     * SKU code is unique and serves as a natural identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sku sku = (Sku) o;
        return Objects.equals(skuCode, sku.skuCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuCode);
    }

    /**
     * Custom toString to avoid lazy loading issues and circular references.
     * Does not include the product to prevent triggering lazy initialization.
     */
    @Override
    public String toString() {
        return "Sku{" +
                "id=" + id +
                ", skuCode='" + skuCode + '\'' +
                ", quantity=" + quantity +
                ", productId=" + (product != null ? product.getId() : null) +
                ", attributes=" + attributes +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
