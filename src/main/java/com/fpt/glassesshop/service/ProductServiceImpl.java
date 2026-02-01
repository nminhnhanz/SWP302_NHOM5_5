package com.fpt.glassesshop.service;

import com.fpt.glassesshop.entity.Product;
import com.fpt.glassesshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;

    // GET ALL
    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // CREATE PRODUCT
    @Override
    public Product saveProduct(Product product) {

        Product p = new Product();
        p.setName(product.getName());
        p.setBrand(product.getBrand());
        p.setDescription(product.getDescription());
        p.setProductType(product.getProductType());
        p.setIsPrescriptionSupported(product.getIsPrescriptionSupported());

        return productRepo.save(p);
    }

    // GET BY ID
    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // DELETE
    @Override
    public void deleteProductById(Long id) {

        Product p = getProductById(id);
        productRepo.delete(p);
    }

    // UPDATE PRODUCT
    @Override
    public Product updateProduct(Product product, Long id) {

        Product existing = getProductById(id);

        if (product.getName() != null)
            existing.setName(product.getName());

        if (product.getBrand() != null)
            existing.setBrand(product.getBrand());

        if (product.getDescription() != null)
            existing.setDescription(product.getDescription());

        if (product.getProductType() != null)
            existing.setProductType(product.getProductType());

        if (product.getIsPrescriptionSupported() != null) {
            existing.setIsPrescriptionSupported(product.getIsPrescriptionSupported());
        }

        return productRepo.save(existing);
    }
}
