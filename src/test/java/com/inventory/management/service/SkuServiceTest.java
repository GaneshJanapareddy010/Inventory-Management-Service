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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SkuService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SkuService Tests")
class SkuServiceTest {

    @Mock
    private SkuRepository skuRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SkuMapper skuMapper;

    @InjectMocks
    private SkuService skuService;

    private Product product;
    private Sku sku;
    private SkuRequest skuRequest;
    private SkuResponse skuResponse;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .build();

        attributes = new HashMap<>();
        attributes.put("color", "Silver");
        attributes.put("size", "15-inch");

        sku = Sku.builder()
                .id(1L)
                .skuCode("LAP-001")
                .product(product)
                .quantity(100)
                .attributes(attributes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        skuRequest = SkuRequest.builder()
                .skuCode("LAP-001")
                .productId(1L)
                .quantity(100)
                .attributes(attributes)
                .build();

        skuResponse = SkuResponse.builder()
                .id(1L)
                .skuCode("LAP-001")
                .productId(1L)
                .productName("Laptop")
                .quantity(100)
                .attributes(attributes)
                .createdAt(sku.getCreatedAt())
                .updatedAt(sku.getUpdatedAt())
                .build();
    }

    @Test
    @DisplayName("Should create SKU successfully")
    void shouldCreateSkuSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(skuRepository.existsBySkuCode("LAP-001")).thenReturn(false);
        when(skuMapper.toEntity(skuRequest, product)).thenReturn(sku);
        when(skuRepository.save(sku)).thenReturn(sku);
        when(skuMapper.toResponse(sku)).thenReturn(skuResponse);

        // When
        SkuResponse result = skuService.createSku(skuRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSkuCode()).isEqualTo("LAP-001");
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getAttributes()).containsEntry("color", "Silver");

        verify(productRepository, times(1)).findById(1L);
        verify(skuRepository, times(1)).existsBySkuCode("LAP-001");
        verify(skuRepository, times(1)).save(sku);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found during creation")
    void shouldThrowResourceNotFoundExceptionWhenProductNotFoundDuringCreation() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        SkuRequest request = SkuRequest.builder()
                .skuCode("LAP-001")
                .productId(999L)
                .quantity(100)
                .attributes(attributes)
                .build();

        // When & Then
        assertThatThrownBy(() -> skuService.createSku(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: '999'");

        verify(skuRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when SKU code already exists")
    void shouldThrowValidationExceptionWhenSkuCodeAlreadyExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(skuRepository.existsBySkuCode("LAP-001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> skuService.createSku(skuRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("SKU code already exists: LAP-001");

        verify(skuRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get SKU by ID successfully")
    void shouldGetSkuByIdSuccessfully() {
        // Given
        when(skuRepository.findById(1L)).thenReturn(Optional.of(sku));
        when(skuMapper.toResponse(sku)).thenReturn(skuResponse);

        // When
        SkuResponse result = skuService.getSkuById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSkuCode()).isEqualTo("LAP-001");

        verify(skuRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when SKU not found")
    void shouldThrowResourceNotFoundExceptionWhenSkuNotFound() {
        // Given
        when(skuRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> skuService.getSkuById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SKU not found with id: '999'");
    }

    @Test
    @DisplayName("Should get all SKUs by product ID")
    void shouldGetAllSkusByProductId() {
        // Given
        Sku sku2 = Sku.builder()
                .id(2L)
                .skuCode("LAP-002")
                .product(product)
                .attributes(new HashMap<>())
                .build();

        SkuResponse response2 = SkuResponse.builder()
                .id(2L)
                .skuCode("LAP-002")
                .productId(1L)
                .productName("Laptop")
                .build();

        when(productRepository.existsById(1L)).thenReturn(true);
        when(skuRepository.findByProductId(1L)).thenReturn(Arrays.asList(sku, sku2));
        when(skuMapper.toResponse(sku)).thenReturn(skuResponse);
        when(skuMapper.toResponse(sku2)).thenReturn(response2);

        // When
        List<SkuResponse> results = skuService.getSkusByProductId(1L);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getSkuCode()).isEqualTo("LAP-001");
        assertThat(results.get(1).getSkuCode()).isEqualTo("LAP-002");

        verify(productRepository, times(1)).existsById(1L);
        verify(skuRepository, times(1)).findByProductId(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting SKUs for non-existent product")
    void shouldThrowResourceNotFoundExceptionWhenGettingSkusForNonExistentProduct() {
        // Given
        when(productRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> skuService.getSkusByProductId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: '999'");

        verify(skuRepository, never()).findByProductId(any());
    }

    @Test
    @DisplayName("Should return empty list when product has no SKUs")
    void shouldReturnEmptyListWhenProductHasNoSkus() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        when(skuRepository.findByProductId(1L)).thenReturn(Arrays.asList());

        // When
        List<SkuResponse> results = skuService.getSkusByProductId(1L);

        // Then
        assertThat(results).isEmpty();

        verify(skuRepository, times(1)).findByProductId(1L);
    }

    @Test
    @DisplayName("Should update SKU successfully")
    void shouldUpdateSkuSuccessfully() {
        // Given
        SkuRequest updateRequest = SkuRequest.builder()
                .skuCode("LAP-001-UPDATED")
                .productId(1L)
                .quantity(100)
                .attributes(attributes)
                .build();

        when(skuRepository.findById(1L)).thenReturn(Optional.of(sku));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(skuRepository.existsBySkuCodeAndIdNot("LAP-001-UPDATED", 1L)).thenReturn(false);
        when(skuRepository.save(sku)).thenReturn(sku);
        when(skuMapper.toResponse(sku)).thenReturn(skuResponse);

        // When
        SkuResponse result = skuService.updateSku(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();

        verify(skuRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(skuMapper, times(1)).updateEntity(sku, updateRequest, product);
        verify(skuRepository, times(1)).save(sku);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent SKU")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentSku() {
        // Given
        when(skuRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> skuService.updateSku(999L, skuRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SKU not found with id: '999'");

        verify(skuRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating with invalid product")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingWithInvalidProduct() {
        // Given
        SkuRequest updateRequest = SkuRequest.builder()
                .skuCode("LAP-001")
                .productId(999L)
                .quantity(100)
                .attributes(attributes)
                .build();

        when(skuRepository.findById(1L)).thenReturn(Optional.of(sku));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> skuService.updateSku(1L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: '999'");

        verify(skuRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when updating with duplicate SKU code")
    void shouldThrowValidationExceptionWhenUpdatingWithDuplicateSkuCode() {
        // Given
        SkuRequest updateRequest = SkuRequest.builder()
                .skuCode("LAP-002")
                .productId(1L)
                .quantity(100)
                .attributes(attributes)
                .build();

        when(skuRepository.findById(1L)).thenReturn(Optional.of(sku));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(skuRepository.existsBySkuCodeAndIdNot("LAP-002", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> skuService.updateSku(1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("SKU code already exists: LAP-002");

        verify(skuRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete SKU successfully")
    void shouldDeleteSkuSuccessfully() {
        // Given
        when(skuRepository.existsById(1L)).thenReturn(true);

        // When
        skuService.deleteSku(1L);

        // Then
        verify(skuRepository, times(1)).existsById(1L);
        verify(skuRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent SKU")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentSku() {
        // Given
        when(skuRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> skuService.deleteSku(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SKU not found with id: '999'");

        verify(skuRepository, never()).deleteById(any());
    }
}
