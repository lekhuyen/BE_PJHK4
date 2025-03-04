package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.ReviewItemRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.dto.response.ReviewItemResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.ReviewItem;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.mapper.Auction_ItemsMapper;
import fpt.aptech.server_be.mapper.ReviewItemMapper;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.ReviewItemRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewItemService {

    private final UserRepository userRepository;
    private final Auction_ItemsRepository auctionItemsRepository;
    private final ReviewItemMapper reviewItemMapper;
    private final ReviewItemRepository reviewItemRepository;

    @Autowired
    public ReviewItemService(UserRepository userRepository,
                             Auction_ItemsRepository auctionItemsRepository,
                             ReviewItemMapper reviewItemMapper,
                             ReviewItemRepository reviewItemRepository) {
        this.userRepository = userRepository;
        this.auctionItemsRepository = auctionItemsRepository;
        this.reviewItemMapper = reviewItemMapper;
        this.reviewItemRepository = reviewItemRepository;
    }

    // Get all reviews
    public List<ReviewItemResponse> getAllReviews() {
        List<ReviewItem> reviews = reviewItemRepository.findAll();
        return reviews.stream()
                .map(reviewItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get a specific review by ID
    public ReviewItemResponse getReviewById(int reviewId) {
        ReviewItem reviewItem = reviewItemRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return reviewItemMapper.toResponse(reviewItem);
    }

    public List<ReviewItemResponse> getReviewsByAuctionItemId(int auctionItemId) {
        // Fetch reviews using the repository method
        List<ReviewItem> reviews = reviewItemRepository.findByAuctionItemId(auctionItemId);

        // Convert ReviewItem entities to ReviewItemResponse DTOs using method reference
        return reviews.stream()
                .map(reviewItemMapper::toResponse)  // Using method reference here
                .collect(Collectors.toList());
    }

    // Create Review
    public ReviewItemResponse createReview(String userId, ReviewItemRequest reviewItemRequest) {

        // Fetch the User by userId (passed as parameter)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        // Fetch the Auction Item by auctionItemId
        Auction_Items auctionItem = auctionItemsRepository.findById(reviewItemRequest.getAuctionItemId())
                .orElseThrow(() -> new IllegalArgumentException("Auction Item not found with ID: " + reviewItemRequest.getAuctionItemId()));

        // Convert ReviewItemRequest to ReviewItem entity
        ReviewItem reviewItem = reviewItemMapper.toEntity(reviewItemRequest, user, auctionItem);

        // Save the review
        reviewItemRepository.save(reviewItem);

        // Map ReviewItem entity to ReviewItemResponse DTO
        return reviewItemMapper.toResponse(reviewItem);
    }

    // Edit (Update) Review
    public ReviewItemResponse editReview(int reviewId, ReviewItemRequest reviewItemRequest) {
        // Fetch the existing review by ID
        ReviewItem existingReview = reviewItemRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Update rating and comment with helper methods
        existingReview.setRating(getRating(reviewItemRequest.getRating()));
        existingReview.setComment(getComment(reviewItemRequest.getComment()));

        // Optionally, update 'isVerified' if it's part of the request
        existingReview.setVerified(reviewItemRequest.isVerified());

        // Update User if part of the request
        if (reviewItemRequest.getUserId() != null) {
            User user = userRepository.findById(reviewItemRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existingReview.setUser(user);
        }

        // Ensure auctionItemId is provided and is valid
        Auction_Items auctionItem = auctionItemsRepository.findById(reviewItemRequest.getAuctionItemId())
                .orElseThrow(() -> new RuntimeException("Auction item not found"));
        existingReview.setAuctionItem(auctionItem);

        // Save the updated review item entity
        ReviewItem updatedReviewItem = reviewItemRepository.save(existingReview);

        // Return the updated response DTO
        return reviewItemMapper.toResponse(updatedReviewItem);
    }

    // Delete Review
    public void deleteReview(int reviewId) {
        // Fetch the existing review by ID
        ReviewItem existingReview = reviewItemRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Delete the review
        reviewItemRepository.delete(existingReview);
    }

    // Helper method to handle rating logic
    private int getRating(int rating) {
        return (rating != 0) ? rating : 0;  // Default to 0 if rating is not provided
    }

    // Helper method to handle comment logic
    private String getComment(String comment) {
        return (comment != null && !comment.isEmpty()) ? comment : "";  // Default to empty string if comment is not provided
    }
}
