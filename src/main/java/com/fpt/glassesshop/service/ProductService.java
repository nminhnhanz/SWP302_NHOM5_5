package com.fpt.glassesshop.service;

import com.fpt.glassesshop.entity.Product;
import com.fpt.glassesshop.entity.ProductVariant;
import com.fpt.glassesshop.repository.ProductRepository;
import com.fpt.glassesshop.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fpt.glassesshop.entity.dto.ProductDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final com.fpt.glassesshop.repository.OrderItemRepository orderItemRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Product> products = productRepository.findAllByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(query,
                query);
        // Manually filter duplicates to avoid SQL DISTINCT error on TEXT column
        return products.stream()
                .filter(java.util.concurrent.ConcurrentHashMap.newKeySet()::add)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProductsDTO(String query) {
        return searchProducts(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public ProductDTO getProductDTOById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public List<ProductVariant> getVariantsByProductId(Long id) {
        return productVariantRepository.findByProduct_ProductId(id);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .brand(productDTO.getBrand())
                .description(productDTO.getDescription())
                .productType(productDTO.getProductType())
                .isPrescriptionSupported(productDTO.isPrescriptionSupported())
                .build();
        Product savedProduct = productRepository.save(product);

        if (productDTO.getVariants() != null && !productDTO.getVariants().isEmpty()) {
            List<ProductVariant> variants = productDTO.getVariants().stream()
                    .map(vDto -> ProductVariant.builder()
                            .product(savedProduct)
                            .price(vDto.getPrice())
                            .stockQuantity(vDto.getStockQuantity())
                            .frameSize(vDto.getFrameSize())
                            .color(vDto.getColor())
                            .material(vDto.getMaterial())
                            .imageUrl(vDto.getImageUrl())
                            .status(vDto.getStatus() != null ? vDto.getStatus() : "AVAILABLE")
                            .build())
                    .collect(Collectors.toList());
            productVariantRepository.saveAll(variants);
            savedProduct.setVariants(variants);
        }

        return convertToDTO(savedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        // Check if any order items reference this product's variants
        long orderItemCount = orderItemRepository.countByProductId(id);
        if (orderItemCount > 0) {
            throw new RuntimeException(
                    "Cannot delete product with id: " + id +
                            ". This product has " + orderItemCount +
                            " order item(s) associated with it. Please remove or reassign these orders first.");
        }

        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productType(product.getProductType())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .isPrescriptionSupported(product.isPrescriptionSupported())
                .createdAt(product.getCreatedAt())
                .variants(product.getVariants() != null ? product.getVariants().stream()
                        .map(this::mapToVariantDTO)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private com.fpt.glassesshop.entity.dto.ProductVariantDTO mapToVariantDTO(ProductVariant variant) {
        return com.fpt.glassesshop.entity.dto.ProductVariantDTO.builder()
                .variantId(variant.getVariantId())
                .productId(variant.getProduct() != null ? variant.getProduct().getProductId() : null)
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .frameSize(variant.getFrameSize())
                .color(variant.getColor())
                .material(variant.getMaterial())
                .imageUrl(variant.getImageUrl())
                .status(variant.getStatus())
                .build();
    }
}
