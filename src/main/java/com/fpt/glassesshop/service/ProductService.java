package com.fpt.glassesshop.service;

import com.fpt.glassesshop.entity.Product;
import com.fpt.glassesshop.entity.ProductVariant;
import com.fpt.glassesshop.repository.ProductRepository;
import com.fpt.glassesshop.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return productRepository.findDistinctByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<ProductVariant> getVariantsByProductId(Long id) {
        return productVariantRepository.findByProduct_ProductId(id);
    }
}
