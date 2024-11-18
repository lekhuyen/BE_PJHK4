package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.CategoryRequest;

import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Mapper
public class CategoryMapper {

    public static Category toCategory(CategoryRequest request){
        Category category = new Category();
        category.setCategory_id(request.getCategory_id());
        category.setCategoryName(request.getCategory_name());
        category.setDescription(request.getDescription());

        return category;
    }
    public static CategoryResponse toCategoryResponse(Category category){

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategory_id(category.getCategory_id());
        categoryResponse.setCategory_name(category.getCategoryName());
        categoryResponse.setDescription(category.getDescription());
        categoryResponse.setAuction_items(toAuctionItemsResponse(category.getAuctionItems()));

        log.info(toAuctionItemsResponse(category.getAuctionItems()).toString());

        return categoryResponse;
    }

    public static List<Auction_ItemsResponse> toAuctionItemsResponse(List<Auction_Items> auctionItems){

        return auctionItems.stream()
                .map(auctionItem -> new Auction_ItemsResponse(
                    auctionItem.getItem_id(),
                    auctionItem.getItem_name(),
                    auctionItem.getDescription(),
                    auctionItem.getImages(),
                    auctionItem.getStarting_price(),
                    auctionItem.getStart_date(),
                    auctionItem.getEnd_date(),
                    auctionItem.getBid_step(),
                    auctionItem.getStatus()
                )).collect(Collectors.toList());

    }
}
