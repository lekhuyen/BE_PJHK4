package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.ReviewItemRequest;
import fpt.aptech.server_be.dto.response.ReviewItemResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.ReviewItem;
import fpt.aptech.server_be.entities.User;
import org.springframework.stereotype.Component;

@Component
public class ReviewItemMapper {

    // Convert ReviewItemRequest to ReviewItem entity
    public ReviewItem toEntity(ReviewItemRequest reviewItemRequest, User user, Auction_Items auctionItem) {
        if (reviewItemRequest == null) {
            return null;
        }

        ReviewItem reviewItem = new ReviewItem();

        // Map the fields from ReviewItemRequest to ReviewItem
        reviewItem.setRating(reviewItemRequest.getRating()); // Set the rating provided in the request
        reviewItem.setComment(reviewItemRequest.getComment() != null ? reviewItemRequest.getComment() : ""); // Ensure comment is not null
        reviewItem.setVerified(reviewItemRequest.isVerified()); // Set verified status from request

        // Handle the 'markAsRated' field from the request. It determines if the review is rated or not
        // If no rating is provided, set markAsRated to false
        reviewItem.setMarkAsRated(reviewItemRequest.getRating() != 0); // Set markAsRated to true if rating is non-zero

        // Set the passed User and Auction_Items objects
        reviewItem.setUser(user);
        reviewItem.setAuctionItem(auctionItem);

        return reviewItem;
    }

    // Convert ReviewItem entity to ReviewItemResponse DTO
    public ReviewItemResponse toResponse(ReviewItem reviewItem) {
        if (reviewItem == null) {
            return null;
        }

        ReviewItemResponse reviewItemResponse = new ReviewItemResponse();

        // Map the fields from ReviewItem to ReviewItemResponse
        reviewItemResponse.setId(reviewItem.getId());
        reviewItemResponse.setRating(reviewItem.getRating()); // Include rating as int
        reviewItemResponse.setComment(reviewItem.getComment());
        reviewItemResponse.setCreatedAt(reviewItem.getCreatedAt());
        reviewItemResponse.setVerified(reviewItem.isVerified());
        reviewItemResponse.setMarkAsRated(reviewItem.isMarkAsRated()); // Use isMarkAsRated to check if the rating is valid

        // Map the User's name and AuctionItem's name if available
        reviewItemResponse.setUserName(reviewItem.getUser() != null ? reviewItem.getUser().getName() : null);
        reviewItemResponse.setAuctionItemName(reviewItem.getAuctionItem() != null ? reviewItem.getAuctionItem().getItem_name() : null);

        return reviewItemResponse;
    }
}
