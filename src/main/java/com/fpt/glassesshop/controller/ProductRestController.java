package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.dto.ProductDTO;
import com.fpt.glassesshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "ProductAPI", description = "Operations related to products")
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all available products")
    public ResponseEntity<com.fpt.glassesshop.entity.dto.ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(com.fpt.glassesshop.entity.dto.ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its unique identifier")
    public ResponseEntity<com.fpt.glassesshop.entity.dto.ApiResponse<ProductDTO>> getProductById(
            @Parameter(description = "ID of the product to retrieve", example = "1") @PathVariable Long id) {
        ProductDTO product = productService.getProductDTOById(id);
        if (product != null) {
            return ResponseEntity.ok(com.fpt.glassesshop.entity.dto.ApiResponse.success(product));
        }
        return ResponseEntity.status(404)
                .body(com.fpt.glassesshop.entity.dto.ApiResponse.error("Product not found"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search for products based on a query string matching name or brand")
    public ResponseEntity<com.fpt.glassesshop.entity.dto.ApiResponse<List<ProductDTO>>> searchProducts(
            @Parameter(description = "Search query string", example = "Ray-Ban") @RequestParam String query) {
        List<ProductDTO> results = productService.searchProductsDTO(query);
        return ResponseEntity.ok(com.fpt.glassesshop.entity.dto.ApiResponse.success(results));
    }
}
