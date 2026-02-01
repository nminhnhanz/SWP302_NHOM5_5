package com.fpt.glassesshop.service;

import com.fpt.glassesshop.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    Product saveProduct(Product product);

    Product getProductById(Long id);

    void deleteProductById(Long id);

    Product updateProduct(Product product, Long id);
}

