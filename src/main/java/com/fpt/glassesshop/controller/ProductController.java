package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.Product;
import com.fpt.glassesshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductController {

    private final ProductService productService;
    // GET ALL PRODUCTS
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
    // CREATE PRODUCT
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }
    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public Product updateProduct(
            @RequestBody Product product,
            @PathVariable Long id
    ) {
        return productService.updateProduct(product, id);
    }
    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
    }
}
