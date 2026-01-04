package com.inventory.management.mapper;

import com.inventory.management.dto.request.ProductRequest;
import com.inventory.management.dto.response.ProductResponse;
import com.inventory.management.entity.Category;
import com.inventory.management.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProductMapper.
 * Tests entity-DTO conversion logic.
 */
@DisplayName("ProductMapper Tests")
class ProductMapperTest {

    private ProductMapper productMapper;
    private Category category;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
        
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should convert ProductRequest to Product entity")
    void shouldConvertRequestToEntity() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("MacBook Pro")
                .description("High-performance laptop")
                .price(new BigDecimal("2500.00"))
                .categoryId(1L)
                .build();

        // When
        Product entity = productMapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("MacBook Pro");
        assertThat(entity.getDescription()).isEqualTo("High-performance laptop");
        assertThat(entity.getPrice()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(entity.getCategory()).isNull(); // Category is set separately in service layer
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should return null when converting null request to entity")
    void shouldReturnNullWhenConvertingNullRequestToEntity() {
        // When
        Product entity = productMapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should convert Product entity to ProductResponse")
    void shouldConvertEntityToResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder()
                .id(1L)
                .name("MacBook Pro")
                .description("High-performance laptop")
                .price(new BigDecimal("2500.00"))
                .category(category)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        ProductResponse response = productMapper.toResponse(product);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("MacBook Pro");
        assertThat(response.getDescription()).isEqualTo("High-performance laptop");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getCategoryName()).isEqualTo("Electronics");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should return null when converting null entity to response")
    void shouldReturnNullWhenConvertingNullEntityToResponse() {
        // When
        ProductResponse response = productMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should handle null category when converting to response")
    void shouldHandleNullCategoryWhenConvertingToResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder()
                .id(1L)
                .name("MacBook Pro")
                .description("High-performance laptop")
                .price(new BigDecimal("2500.00"))
                .category(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        ProductResponse response = productMapper.toResponse(product);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
    }

    @Test
    @DisplayName("Should update existing Product entity from ProductRequest")
    void shouldUpdateEntityFromRequest() {
        // Given
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Old Name")
                .description("Old Description")
                .price(new BigDecimal("1000.00"))
                .category(category)
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now())
                .build();

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated MacBook Pro")
                .description("Updated high-performance laptop")
                .price(new BigDecimal("2800.00"))
                .categoryId(1L)
                .build();

        // When
        productMapper.updateEntity(existingProduct, updateRequest);

        // Then
        assertThat(existingProduct.getId()).isEqualTo(1L);
        assertThat(existingProduct.getName()).isEqualTo("Updated MacBook Pro");
        assertThat(existingProduct.getDescription()).isEqualTo("Updated high-performance laptop");
        assertThat(existingProduct.getPrice()).isEqualByComparingTo(new BigDecimal("2800.00"));
        assertThat(existingProduct.getCategory()).isEqualTo(category); // Category unchanged - set in service layer
        assertThat(existingProduct.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("Should not update when entity is null")
    void shouldNotUpdateWhenEntityIsNull() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("MacBook Pro")
                .build();

        // When & Then (should not throw exception)
        productMapper.updateEntity(null, request);
    }

    @Test
    @DisplayName("Should not update when request is null")
    void shouldNotUpdateWhenRequestIsNull() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("MacBook Pro")
                .build();

        // When & Then (should not throw exception)
        productMapper.updateEntity(product, null);
        
        // Product should remain unchanged
        assertThat(product.getName()).isEqualTo("MacBook Pro");
    }
}
