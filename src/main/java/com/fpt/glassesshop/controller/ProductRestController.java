package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.dto.ApiResponse;
import com.fpt.glassesshop.entity.dto.ProductDTO;
import com.fpt.glassesshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "ProductAPI", description = "Operations related to products")
@Slf4j
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all available products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its unique identifier")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @Parameter(description = "ID of the product to retrieve", example = "1") @PathVariable Long id) {
        ProductDTO product = productService.getProductDTOById(id);
        if (product != null) {
            return ResponseEntity.ok(ApiResponse.success(product));
        }
        return ResponseEntity.status(404)
                .body(ApiResponse.error("Product not found"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search for products based on a query string matching name or brand")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> searchProducts(
            @Parameter(description = "Search query string", example = "Ray-Ban") @RequestParam String query) {
        List<ProductDTO> results = productService.searchProductsDTO(query);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Adds a new product to the catalog")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @RequestBody ProductDTO productDTO) {
        log.info("Received request to create product: {}", productDTO);
        ProductDTO created = productService.createProduct(productDTO);
        log.info("Successfully created product with ID: {}", created.getProductId());
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Product created successfully", created));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Removes a product from the catalog by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "ID of the product to delete", example = "1") @PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity
                    .ok(ApiResponse.success("Product deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}
