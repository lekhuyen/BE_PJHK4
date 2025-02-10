package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import fpt.aptech.server_be.entities.User;
import org.mapstruct.Mapper;


@Mapper
public class Auction_ItemsMapper {

    // Helper method to set common fields for Auction_Items
    private static void mapCommonFields(Auction_ItemsRequest request, Auction_Items auctionItems) {

        User user = new User();
        user.setId(request.getUserId());

        auctionItems.setUser(user);
        auctionItems.setItem_name(request.getItem_name());
        auctionItems.setDescription(request.getDescription());
//        auctionItems.setImages(request.getImages());
        auctionItems.setStarting_price(request.getStarting_price());
        auctionItems.setStart_date(request.getStart_date());
        auctionItems.setEnd_date(request.getEnd_date());
        auctionItems.setBid_step(request.getBid_step());
//        auctionItems.setStatus(request.getStatus());
    }

    // Convert Auction_ItemsRequest to Auction_Items
    public static Auction_Items toAuction_Items(Auction_ItemsRequest request) {
        Auction_Items auctionItems = new Auction_Items();
        mapCommonFields(request, auctionItems);
        return auctionItems;
    }

    // Convert Auction_Items to Auction_ItemsResponse
    public static Auction_ItemsResponse toAuction_ItemsResponse(Auction_Items auctionItems) {

        Auction_ItemsResponse response = new Auction_ItemsResponse();

        response.setItem_id(auctionItems.getItem_id());
        response.setItem_name(auctionItems.getItem_name());
        response.setDescription(auctionItems.getDescription());
        response.setImages(auctionItems.getImages());
        response.setStarting_price(auctionItems.getStarting_price());
        response.setCurrent_price(auctionItems.getCurrent_price());
        response.setStart_date(auctionItems.getStart_date());
        response.setEnd_date(auctionItems.getEnd_date());
        response.setBid_step(auctionItems.getBid_step());
        response.setStatus(auctionItems.isStatus());
        response.setSell(auctionItems.isSell());
        response.setPaid(auctionItems.isPaid());
        response.setSoldout(auctionItems.isSoldout());
        response.setUser(UserMapper.toUserResponse(auctionItems.getUser()));
        response.setBuyer(UserMapper.toUserResponse(auctionItems.getBuyer()));
//        mapCommonFields(auctionItems, response);
        response.setCategory(toCategoryResponse(auctionItems.getCategory()));

        response.setBidding(
                auctionItems.getBidding() != null
                        ? BiddingMapper.toBiddingResponse(auctionItems.getBidding())
                        : null
        );

        return response;
    }

//    private static UserResponse toUserResponse(User user) {
//        UserResponse userResponse = new UserResponse();
//
//        userResponse.setId(user.getId());
//        userResponse.setName(user.getName());
//        userResponse.setEmail(user.getEmail());
//        userResponse.setCiNumber(String.valueOf(user.getCiNumber()));
//
//        return userResponse;
//    }

    private static CategoryResponse toCategoryResponse(Category category){
        return CategoryMapper.toCategoryResponse(category);
    }

    // Helper method for mapping common fields from Auction_Items to Auction_ItemsRespone
    private static void mapCommonFields(Auction_Items auctionItems, Auction_ItemsResponse response) {
        response.setItem_name(auctionItems.getItem_name());
        response.setDescription(auctionItems.getDescription());
//        response.setImages(auctionItems.getImages());
        response.setStarting_price(auctionItems.getStarting_price());
        response.setStart_date(auctionItems.getStart_date());
        response.setEnd_date(auctionItems.getEnd_date());
        response.setBid_step(auctionItems.getBid_step());
//        response.setStatus(auctionItems.getStatus());
    }
}

