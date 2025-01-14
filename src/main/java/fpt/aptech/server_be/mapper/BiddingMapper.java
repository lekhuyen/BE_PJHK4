package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.BiddingRequest;
import fpt.aptech.server_be.dto.request.CategoryRequest;
import fpt.aptech.server_be.dto.response.BiddingResponse;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Bidding;
import fpt.aptech.server_be.entities.User;
import org.mapstruct.Mapper;

@Mapper
public class BiddingMapper {
    public static BiddingResponse toBiddingResponse(Bidding bidding) {

        if (bidding == null) {
            return null;
        }
        return BiddingResponse.builder()
                .id(bidding.getId())
                .price(bidding.getPrice())
                .productId(bidding.getAuction_Items().getItem_id())
                .productName(bidding.getAuction_Items().getItem_name())
                .user(bidding.getUser() != null ? bidding.getUser().getId() : null)
                .build();
    }

    public static Bidding toBidding(BiddingRequest request){
        Bidding bidding = new Bidding();
        bidding.setPrice(request.getPrice());

        Auction_Items auction_Items = new Auction_Items();
        auction_Items.setItem_id(request.getProductId());
        bidding.setAuction_Items(auction_Items);

        User user = new User();
        user.setId(request.getUserId());
        bidding.setUser(user);

        return bidding;
    }
}
