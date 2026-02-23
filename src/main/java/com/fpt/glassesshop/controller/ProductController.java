package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.Product;
import com.fpt.glassesshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        model.addAttribute("products", productService.searchProducts(query));
        model.addAttribute("query", query);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("variants", productService.getVariantsByProductId(id));
        return "product-detail";
    }
}
