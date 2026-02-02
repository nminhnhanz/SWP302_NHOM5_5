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

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productType(product.getProductType())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .isPrescriptionSupported(product.isPrescriptionSupported())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
