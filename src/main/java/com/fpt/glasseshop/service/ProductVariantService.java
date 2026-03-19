package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.Product;
import com.fpt.glasseshop.entity.ProductVariant;
import com.fpt.glasseshop.entity.dto.ProductVariantDTO;
import com.fpt.glasseshop.entity.dto.VariantRequest;
import com.fpt.glasseshop.exception.ResourceNotFoundException;
import com.fpt.glasseshop.repository.ProductRepository;
import com.fpt.glasseshop.repository.ProductVariantRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductVariantService{

    @Autowired
    private ProductVariantRepository productVariantRepo;

    @Autowired
    private ProductRepository productRepo;


    public ProductVariant getProductVariantById(Long productVariantId) {
        ProductVariant proVariantExists = productVariantRepo.findById(productVariantId).orElseThrow(() -> new ResourceNotFoundException("Product Variant not found with id " + productVariantId));
        return proVariantExists;
    }


    public ProductVariantDTO createProductVariant(VariantRequest request, Long productId) throws BadRequestException {
        Product pro = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));

        boolean existed = productVariantRepo.existsVariant(
                productId,
                request.getFrameSize().trim().toLowerCase(),
                request.getColor().trim().toLowerCase(),
                request.getMaterial().trim().toLowerCase());

        if(existed){
            throw new BadRequestException("Variant with same color, frame size and matterial already exists");
        }
        ProductVariant proVariant = new ProductVariant();
        proVariant.setProduct(pro);
        proVariant.setPrice(request.getPrice());
        proVariant.setStockQuantity(request.getStockQuantity());
        proVariant.setFrameSize(request.getFrameSize());
        proVariant.setColor(request.getColor());
        proVariant.setMaterial(request.getMaterial());
        proVariant.setImageUrl(request.getImageUrl());
        proVariant.setStatus(request.getStatus());

        ProductVariant saved = productVariantRepo.save(proVariant);
        return mapToDTO(saved);
    }


    public List<ProductVariantDTO> findAllProductVariants() {
        return productVariantRepo.findByDeletedFalse()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    //List for USER
//    public List<ProductVariantDTO> findAllProductVariantsForUser() {
//        List<ProductVariant> variants = productVariantRepo.findByActiveTrue();
//
//        return variants.stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }


    public ProductVariantDTO updateProductVariant(VariantRequest productVariant, Long productVariantId) {
        ProductVariant proVariantExists = getProductVariantById(productVariantId);
        if(proVariantExists != null){
            proVariantExists.setColor(productVariant.getColor().trim());
            proVariantExists.setPrice(productVariant.getPrice());
            proVariantExists.setFrameSize(productVariant.getFrameSize().trim());
            proVariantExists.setStatus(productVariant.getStatus());
            proVariantExists.setMaterial(productVariant.getMaterial().trim());
            proVariantExists.setStockQuantity(productVariant.getStockQuantity());
            proVariantExists.setImageUrl(productVariant.getImageUrl());
        }
        if (productVariant.getActive() != null) {
            proVariantExists.setActive(productVariant.getActive());
        }
        ProductVariant saved = productVariantRepo.save(proVariantExists);
        return mapToDTO(saved);
    }
    //Update riêng stock của variant (hết hàng, nhập kho, sai số)

    public ProductVariant updateStockProductVariant(Long productVariantId, Integer stockQty) {
        ProductVariant proVariantExists = getProductVariantById(productVariantId);
        if(proVariantExists != null){
            proVariantExists.setStockQuantity(stockQty);
        }
        return productVariantRepo.save(proVariantExists);
    }
    //Hết kho ẩn variant khỏi người dùng

    public ProductVariantDTO updateVarianStatus(Long productVariantId, Boolean active) throws BadRequestException {
        ProductVariant proVariantExists = getProductVariantById(productVariantId);
        if(active == null){
            throw new BadRequestException("active is required");
        }
        proVariantExists.setActive(active);
        ProductVariant saved = productVariantRepo.save(proVariantExists);
        return mapToDTO(saved);

    }


    public void deleteProductVariant(Long productVariantId) {
        ProductVariant variant = getProductVariantById(productVariantId);
        variant.setDeleted(true);
        variant.setActive(false);
        productVariantRepo.save(variant);
    }
    private ProductVariantDTO mapToDTO(ProductVariant variant) {
        return ProductVariantDTO.builder()
                .variantId(variant.getVariantId())
                .productId(variant.getProduct().getProductId())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .frameSize(variant.getFrameSize())
                .color(variant.getColor())
                .material(variant.getMaterial())
                .imageUrl(variant.getImageUrl())
                .status(variant.getStatus())
                .active(variant.getActive())
                .build();
    }

}
