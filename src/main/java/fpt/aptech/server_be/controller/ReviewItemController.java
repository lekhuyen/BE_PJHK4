package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ReviewItemRequest;
import fpt.aptech.server_be.dto.response.ReviewItemResponse;
import fpt.aptech.server_be.entities.ReviewItem;
import fpt.aptech.server_be.service.ReviewItemService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reviewitem")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewItemController {

    // Inject ReviewItemService to handle business logic
    private final ReviewItemService reviewItemService;

    // Get all reviews
    @GetMapping
    public ResponseEntity<List<ReviewItemResponse>> getAllReviews() {
        List<ReviewItemResponse> reviews = reviewItemService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // Get a specific review by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewItemResponse> getReviewById(@PathVariable int id) {
        ReviewItemResponse reviewItemResponse = reviewItemService.getReviewById(id);
        return ResponseEntity.ok(reviewItemResponse);
    }

    // Get reviews by auction item ID
    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<?> getReviewsByAuctionId(@PathVariable int auctionId) {
        List<ReviewItemResponse> reviews = reviewItemService.getReviewsByAuctionItemId(auctionId);

        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();  // No reviews found
        }

        return ResponseEntity.ok(reviews);  // Return reviews if found
    }

    // Create Review
    @PostMapping
    public ResponseEntity<ReviewItemResponse> createReview(@RequestBody @Valid ReviewItemRequest reviewItemRequest) {
        // Extract userId from the request body
        String userId = reviewItemRequest.getUserId();  // Assuming userId is part of the ReviewItemRequest

        // Ensure that the userId is not null or empty
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        // Create the review by passing the userId and reviewItemRequest to the service
        ReviewItemResponse response = reviewItemService.createReview(userId, reviewItemRequest);

        // Return the response with HTTP status CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Edit (Update) Review
    @PutMapping("/{id}")
    public ResponseEntity<ReviewItemResponse> editReview(
            @PathVariable int id,
            @RequestBody @Valid ReviewItemRequest reviewItemRequest) {
        log.info("Received request to edit review with ID: {} and request: {}", id, reviewItemRequest);
        ReviewItemResponse reviewItemResponse = reviewItemService.editReview(id, reviewItemRequest);
        return ResponseEntity.ok(reviewItemResponse);
    }

    // Delete Review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable int reviewId) {
        log.info("Received request to delete review with ID: {}", reviewId);
        reviewItemService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

}

