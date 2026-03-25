package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.Cart;
import com.fpt.glasseshop.entity.CartItem;
import com.fpt.glasseshop.entity.LensOption;
import com.fpt.glasseshop.entity.ProductVariant;
import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.AddToCartRequest;
import com.fpt.glasseshop.entity.dto.CartDTO;
import com.fpt.glasseshop.entity.dto.CartItemDTO;
import com.fpt.glasseshop.exception.ResourceNotFoundException;
import com.fpt.glasseshop.repository.CartItemRepository;
import com.fpt.glasseshop.repository.CartRepository;
import com.fpt.glasseshop.repository.LensOptionRepository;
import com.fpt.glasseshop.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final LensOptionRepository lensOptionRepository;

    @Transactional
    public CartDTO getCart(UserAccount user) {
        Cart cart = getOrCreateCart(user);
        return convertToDTO(cart);
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        return cartRepository.findByUserUserId(userId)
                .map(cart -> convertToDTO(cart).getItems())
                .orElse(new ArrayList<>());
    }

    @Transactional
    public CartDTO addToCart(UserAccount user, AddToCartRequest request) {
        Cart cart = getOrCreateCart(user);

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product Variant not found with id: " + request.getVariantId()));

        LensOption lensOption = null;
        if (request.getLensOptionId() != null) {
            lensOption = lensOptionRepository.findById(request.getLensOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Lens Option not found with id: " + request.getLensOptionId()));
        }

        // Check if item already exists in cart with same variant
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getVariant().getVariantId().equals(variant.getVariantId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            if (request.getIsLens() != null) existingItem.setIsLens(request.getIsLens());
            if (request.getIsPreorder() != null) existingItem.setIsPreorder(request.getIsPreorder());
            if (lensOption != null) existingItem.setLensOption(lensOption);
            cartItemRepository.save(existingItem);
        } else {
            java.math.BigDecimal variantPriceVal = variant.getProduct() != null && variant.getProduct().getPrice() != null ? variant.getProduct().getPrice() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal lensPriceVal = lensOption != null && lensOption.getPrice() != null ? lensOption.getPrice() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal price = variantPriceVal.add(lensPriceVal);
            
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .lensOption(lensOption)
                    .quantity(request.getQuantity())
                    .productId(variant.getProduct() != null ? variant.getProduct().getProductId() : null)
                    .productName(variant.getProduct() != null ? variant.getProduct().getName() : null)
                    .price(price)
                    .isLens(request.getIsLens() != null ? request.getIsLens() : false)
                    .isPreorder(request.getIsPreorder() != null ? request.getIsPreorder() : false)
                    .build();

            if (Boolean.TRUE.equals(request.getIsLens())) {
                com.fpt.glasseshop.entity.Prescription prescription = com.fpt.glasseshop.entity.Prescription.builder()
                        .sphLeft(request.getSphLeft())
                        .sphRight(request.getSphRight())
                        .cylLeft(request.getCylLeft())
                        .cylRight(request.getCylRight())
                        .axisLeft(request.getAxisLeft())
                        .axisRight(request.getAxisRight())
                        .pd(request.getPd())
                        .status(false) // false = pending
                        .cartItem(newItem) // linking to cartItem
                        .build();
                newItem.setPrescription(prescription);
            }

            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return convertToDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartDTO updateQuantity(UserAccount user, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + cartItemId));

        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new IllegalArgumentException("CartItem does not belong to the user's cart");
        }

        if (quantity <= 0) {
            return removeCartItem(user, cartItemId);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return convertToDTO(cartRepository.findById(cart.getCartId()).orElse(cart));
    }

    @Transactional
    public CartDTO removeCartItem(UserAccount user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + cartItemId));

        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new IllegalArgumentException("CartItem does not belong to the user's cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return convertToDTO(cartRepository.save(cart));
    }

    private Cart getOrCreateCart(UserAccount user) {
        return cartRepository.findByUserUserId(user.getUserId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void clearCart(UserAccount user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalItems = 0;

        List<CartItemDTO> itemDTOs = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                BigDecimal variantPrice = (item.getVariant().getProduct() != null && item.getVariant().getProduct().getPrice() != null) ? item.getVariant().getProduct().getPrice()
                        : BigDecimal.ZERO;
                BigDecimal lensPrice = (item.getLensOption() != null && item.getLensOption().getPrice() != null)
                        ? item.getLensOption().getPrice()
                        : BigDecimal.ZERO;

                BigDecimal unitPrice = variantPrice.add(lensPrice);
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                totalPrice = totalPrice.add(subtotal);
                totalItems += item.getQuantity();

                CartItemDTO itemDTO = CartItemDTO.builder()
                        .cartItemId(item.getCartItemId())
                        .variantId(item.getVariant().getVariantId())
                        .productId(item.getProductId() != null ? item.getProductId() : (item.getVariant().getProduct() != null ? item.getVariant().getProduct().getProductId() : null))
                        .isLens(item.getIsLens())
                        .isPreorder(item.getIsPreorder())
                        .productName(item.getProductName() != null ? item.getProductName() : (item.getVariant().getProduct() != null ? item.getVariant().getProduct().getName() : ""))
                        .variantColor(item.getVariant().getColor())
                        .variantSize(item.getVariant().getFrameSize())
                        .imageUrl(item.getVariant().getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(unitPrice)
                        .subtotal(subtotal)
                        .build();

                itemDTOs.add(itemDTO);
            }
        }

        return CartDTO.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUser().getUserId())
                .items(itemDTOs)
                .totalPrice(totalPrice)
                .totalItems(totalItems)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
