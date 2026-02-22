package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.Product;
import com.fpt.glasseshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products") // ⭐ Base path
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ⭐ LIST ALL PRODUCTS
    @GetMapping("")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    // ⭐ SEARCH PRODUCTS
    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        model.addAttribute("products", productService.searchProducts(query));
        model.addAttribute("query", query);
        return "products";
    }

    // ⭐ PRODUCT DETAIL
    @GetMapping("/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("variants", productService.getVariantsByProductId(id));
        return "product-detail";
    }
}