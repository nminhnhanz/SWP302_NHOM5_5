package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.AddToCartRequest;
import com.fpt.glasseshop.entity.dto.CartDTO;
import com.fpt.glasseshop.entity.dto.CartItemDTO;
import com.fpt.glasseshop.repository.UserAccountRepository;
import com.fpt.glasseshop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;
    private final UserAccountRepository userAccountRepository;

    private UserAccount getAuthenticatedUser(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }
        return userAccountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(Principal principal) {
        try {
            UserAccount user = getAuthenticatedUser(principal);
            return ResponseEntity.ok(cartService.getCart(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItemsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartItemsByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            Principal principal,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            UserAccount user = getAuthenticatedUser(principal);
            return ResponseEntity.ok(cartService.addToCart(user, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartDTO> updateQuantity(
            Principal principal,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        try {
            UserAccount user = getAuthenticatedUser(principal);
            return ResponseEntity.ok(cartService.updateQuantity(user, cartItemId, quantity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<CartDTO> removeCartItem(
            Principal principal,
            @PathVariable Long cartItemId) {
        try {
            UserAccount user = getAuthenticatedUser(principal);
            return ResponseEntity.ok(cartService.removeCartItem(user, cartItemId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
