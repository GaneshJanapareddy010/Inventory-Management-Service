package com.inventory.management.mapper;

import com.inventory.management.dto.request.SkuRequest;
import com.inventory.management.dto.response.SkuResponse;
import com.inventory.management.entity.Product;
import com.inventory.management.entity.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SkuMapper.
 * Tests entity-DTO conversion logic.
 */
@DisplayName("SkuMapper Tests")
class SkuMapperTest {

    private SkuMapper skuMapper;
    private Product product;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        skuMapper = new SkuMapper();
        
        product = Product.builder()
                .id(1L)
                .name("MacBook Pro")
                .build();
        
        attributes = new HashMap<>();
        attributes.put("color", "Space Gray");
        attributes.put("storage", "512GB");
        attributes.put("ram", "16GB");
    }

    @Test
    @DisplayName("Should convert SkuRequest to Sku entity")
    void shouldConvertRequestToEntity() {
        // Given
        SkuRequest request = SkuRequest.builder()
                .skuCode("MBP16-SG-512")
                .productId(1L)
                .quantity(100)
                .attributes(attributes)
                .build();

        // When
        Sku entity = skuMapper.toEntity(request, product);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getSkuCode()).isEqualTo("MBP16-SG-512");
        assertThat(entity.getProduct()).isEqualTo(product);
        assertThat(entity.getQuantity()).isEqualTo(100);
        assertThat(entity.getAttributes()).isEqualTo(attributes);
        assertThat(entity.getAttributes().get("color")).isEqualTo("Space Gray");
        assertThat(entity.getAttributes().get("storage")).isEqualTo("512GB");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should return null when converting null request to entity")
    void shouldReturnNullWhenConvertingNullRequestToEntity() {
        // When
        Sku entity = skuMapper.toEntity(null, product);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should convert Sku entity to SkuResponse")
    void shouldConvertEntityToResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Sku sku = Sku.builder()
                .id(1L)
                .skuCode("MBP16-SG-512")
                .product(product)
                .quantity(100)
                .attributes(attributes)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        SkuResponse response = skuMapper.toResponse(sku);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSkuCode()).isEqualTo("MBP16-SG-512");
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("MacBook Pro");
        assertThat(response.getQuantity()).isEqualTo(100);
        assertThat(response.getAttributes()).isEqualTo(attributes);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should return null when converting null entity to response")
    void shouldReturnNullWhenConvertingNullEntityToResponse() {
        // When
        SkuResponse response = skuMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should handle null product when converting to response")
    void shouldHandleNullProductWhenConvertingToResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Sku sku = Sku.builder()
                .id(1L)
                .skuCode("MBP16-SG-512")
                .product(null)
                .quantity(100)
                .attributes(attributes)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        SkuResponse response = skuMapper.toResponse(sku);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isNull();
        assertThat(response.getProductName()).isNull();
    }

    @Test
    @DisplayName("Should update existing Sku entity from SkuRequest")
    void shouldUpdateEntityFromRequest() {
        // Given
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        Map<String, Object> oldAttributes = new HashMap<>();
        oldAttributes.put("color", "Silver");
        
        Sku existingSku = Sku.builder()
                .id(1L)
                .skuCode("OLD-CODE")
                .product(product)
                .quantity(50)
                .attributes(oldAttributes)
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now())
                .build();

        Map<String, Object> newAttributes = new HashMap<>();
        newAttributes.put("color", "Space Gray");
        newAttributes.put("storage", "1TB");
        
        SkuRequest updateRequest = SkuRequest.builder()
                .skuCode("MBP16-SG-1TB")
                .productId(1L)
                .quantity(150)
                .attributes(newAttributes)
                .build();

        Product newProduct = Product.builder()
                .id(2L)
                .name("MacBook Pro 16-inch")
                .build();

        // When
        skuMapper.updateEntity(existingSku, updateRequest, newProduct);

        // Then
        assertThat(existingSku.getId()).isEqualTo(1L);
        assertThat(existingSku.getSkuCode()).isEqualTo("MBP16-SG-1TB");
        assertThat(existingSku.getProduct()).isEqualTo(newProduct);
        assertThat(existingSku.getQuantity()).isEqualTo(150);
        assertThat(existingSku.getAttributes()).isEqualTo(newAttributes);
        assertThat(existingSku.getAttributes().get("color")).isEqualTo("Space Gray");
        assertThat(existingSku.getAttributes().get("storage")).isEqualTo("1TB");
        assertThat(existingSku.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("Should not update when entity is null")
    void shouldNotUpdateWhenEntityIsNull() {
        // Given
        SkuRequest request = SkuRequest.builder()
                .skuCode("MBP16-SG-512")
                .quantity(100)
                .build();

        // When & Then (should not throw exception)
        skuMapper.updateEntity(null, request, product);
    }

    @Test
    @DisplayName("Should not update when request is null")
    void shouldNotUpdateWhenRequestIsNull() {
        // Given
        Sku sku = Sku.builder()
                .id(1L)
                .skuCode("MBP16-SG-512")
                .quantity(100)
                .build();

        // When & Then (should not throw exception)
        skuMapper.updateEntity(sku, null, product);
        
        // SKU should remain unchanged
        assertThat(sku.getSkuCode()).isEqualTo("MBP16-SG-512");
        assertThat(sku.getQuantity()).isEqualTo(100);
    }
}
