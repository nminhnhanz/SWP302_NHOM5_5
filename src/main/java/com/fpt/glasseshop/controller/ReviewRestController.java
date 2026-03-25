package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.CreateReviewRequest;
import com.fpt.glasseshop.entity.dto.ReviewDTO;
import com.fpt.glasseshop.repository.UserAccountRepository;
import com.fpt.glasseshop.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "ReviewAPI", description = "Operations related to product reviews")
public class ReviewRestController {

    private final ReviewService reviewService;
    private final UserAccountRepository userAccountRepository;

    private UserAccount getAuthenticatedUser(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("User is not authenticated");
        }
        return userAccountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
    }

    @PostMapping
    @Operation(summary = "Create a product review", description = "Submits a review for a specific product from a delivered order. Requires authentication.")
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(
            Principal principal,
            @Valid @RequestBody CreateReviewRequest request) {
        try {
            UserAccount user = getAuthenticatedUser(principal);
            ReviewDTO reviewDTO = reviewService.createReview(user, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Review submitted successfully", reviewDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating review: " + e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews by Product ID", description = "Retrieves a list of reviews for a specific product")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewsByProduct(
            @Parameter(description = "ID of the product", example = "10") @PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by User ID", description = "Retrieves a list of reviews written by a specific user")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewsByUser(
            @Parameter(description = "ID of the user", example = "5") @PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
