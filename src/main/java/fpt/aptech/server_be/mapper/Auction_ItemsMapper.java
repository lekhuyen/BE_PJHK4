package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsRespone;
import fpt.aptech.server_be.entities.Auction_Items;


@Mapper
public class Auction_ItemsMapper {

    // Helper method to set common fields for Auction_Items
    private static void mapCommonFields(Auction_ItemsRequest request, Auction_Items auctionItems) {
        auctionItems.setItem_name(request.getItem_name());
        auctionItems.setDescription(request.getDescription());
        auctionItems.setImages(request.getImages());
        auctionItems.setStarting_price(request.getStarting_price());
        auctionItems.setStart_date(request.getStart_date());
        auctionItems.setEnd_date(request.getEnd_date());
        auctionItems.setBid_step(request.getBid_step());
        auctionItems.setStatus(request.getStatus());
    }

    // Convert Auction_ItemsRequest to Auction_Items
    public static Auction_Items toAuction_Items(Auction_ItemsRequest request) {
        Auction_Items auctionItems = new Auction_Items();
        mapCommonFields(request, auctionItems);  // Set common fields
        return auctionItems;
    }

    // Convert Auction_Items to Auction_ItemsRespone
    public static Auction_ItemsRespone toAuction_ItemsRespone(Auction_Items auctionItems) {
        Auction_ItemsRespone response = new Auction_ItemsRespone();
        response.setItem_id(auctionItems.getItem_id());
        mapCommonFields(auctionItems, response);  // Set common fields
        response.setCategory(auctionItems.getCategory());  // Assuming category is set properly
        return response;
    }

    // Helper method for mapping common fields from Auction_Items to Auction_ItemsRespone
    private static void mapCommonFields(Auction_Items auctionItems, Auction_ItemsRespone response) {
        response.setItem_name(auctionItems.getItem_name());
        response.setDescription(auctionItems.getDescription());
        response.setImages(auctionItems.getImages());
        response.setStarting_price(auctionItems.getStarting_price());
        response.setStart_date(auctionItems.getStart_date());
        response.setEnd_date(auctionItems.getEnd_date());
        response.setBid_step(auctionItems.getBid_step());
        response.setStatus(auctionItems.getStatus());
    }
}

