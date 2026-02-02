package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.dto.ApiResponse;
import com.fpt.glassesshop.entity.dto.ProductDTO;
import com.fpt.glassesshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Operations related to products")
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all available products")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts(); // Assuming this method exists in ProductService or
                                                                     // I'll implement it
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its unique identifier")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @Parameter(description = "ID of the product to retrieve", example = "1") @PathVariable Long id) {
        ProductDTO product = productService.getProductDTOById(id); // Assuming this method exists
        if (product != null) {
            return ResponseEntity.ok(ApiResponse.success(product));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Product not found"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search for products based on a query string matching name or brand")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> searchProducts(
            @Parameter(description = "Search query string", example = "Ray-Ban") @RequestParam String query) {
        List<ProductDTO> results = productService.searchProductsDTO(query); // Assuming this method exists
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
