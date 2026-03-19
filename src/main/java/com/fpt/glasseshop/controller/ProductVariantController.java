package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.ProductVariant;
import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.ProductVariantDTO;
import com.fpt.glasseshop.entity.dto.VariantRequest;
import com.fpt.glasseshop.service.ProductVariantService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/products")
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;
    @PostMapping("/{productId}/variants")
    public ResponseEntity<ApiResponse<ProductVariantDTO>> createProVariant (@Valid @RequestBody VariantRequest proVariant, @PathVariable Long productId) throws BadRequestException {

        ProductVariantDTO createProVariant = productVariantService.createProductVariant(proVariant, productId);

        return ResponseEntity.ok(ApiResponse.success("Product Variant created successfully", createProVariant));
    }

    @PutMapping("/variants/{variantId}")
    public ResponseEntity<ApiResponse<ProductVariantDTO>> updateProductVariant(
            @PathVariable Long variantId,
            @RequestBody VariantRequest request
    ) {
        ProductVariantDTO variant =
                productVariantService.updateProductVariant(request, variantId);

        return ResponseEntity.ok(ApiResponse.success("Product Variant updated successfully", variant));
    }

    @PatchMapping("/variants/{variantId}/status")
    public ResponseEntity<ApiResponse<ProductVariantDTO>> updateVariantStatus(
            @PathVariable Long variantId,
            @RequestParam Boolean active
    ) throws BadRequestException {

        ProductVariantDTO updatedVariant =
                productVariantService.updateVarianStatus(variantId, active);

        return ResponseEntity.ok(ApiResponse.success("Product Variant status updated successfully", updatedVariant));
    }

    @GetMapping("/variants")
    public ResponseEntity<ApiResponse<List<ProductVariantDTO>>> getAllProductVariants(ProductVariant productVariant){
        List<ProductVariantDTO> listProVariant = productVariantService.findAllProductVariants();
        return ResponseEntity.ok(ApiResponse.success("Product Variant retrieved successfully", listProVariant));
    }

    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<ApiResponse<ProductVariant>> deleteProductVariant(@PathVariable Long variantId){
        productVariantService.deleteProductVariant(variantId);
        return ResponseEntity.ok(ApiResponse.success("Product Variant deleted successfully", null));
    }

}
