package fpt.aptech.server_be.controller;


import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.dto.response.PageResponse;
import fpt.aptech.server_be.service.Auction_ItemsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionController {

    Auction_ItemsService auction_ItemsService;



    @GetMapping
    public ApiResponse<PageResponse<Auction_ItemsResponse>> getAllAuctions(
            @RequestParam(value = "page", required = false,defaultValue = "1") int page,
            @RequestParam(value = "size", required = false,defaultValue = "3") int size,
            @RequestParam(value = "name", required = false) String name
    ) {
        return ApiResponse.<PageResponse<Auction_ItemsResponse>>builder()
                .result(auction_ItemsService.getAllAuction_Items( page, size,name))
                .build();
    }

    @GetMapping("category/{id}")
    public ApiResponse<PageResponse<Auction_ItemsResponse>> getAuctionByCategory(@PathVariable int id,
                                 @RequestParam(value = "page", required = false,defaultValue = "1") int page,
                                  @RequestParam(value = "size", required = false,defaultValue = "3") int size) {
        PageResponse<Auction_ItemsResponse> response = auction_ItemsService.getAuctionItemByCategory(id, page, size);
        return ApiResponse.<PageResponse<Auction_ItemsResponse>> builder()
                .result(response)
                .build();
    }
//    @GetMapping
//    public ApiResponse<List<Auction_ItemsResponse>> getAllAuctions() {
//        return ApiResponse.<List<Auction_ItemsResponse>>builder()
//                .result(auction_ItemsService.getAllAuction_Items())
//                .build();
//    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> createAuction(@ModelAttribute Auction_ItemsRequest request) {
        auction_ItemsService.addAuction_Items(request);

        return ApiResponse.<String>builder()
                .message("Add auction item successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAuction(@PathVariable int id) {
        auction_ItemsService.deleteAuction_Items(id);
        return ApiResponse.<String>builder()
                .code(0)
                .message("Delete auction item successfully")
                .build();
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateAuction(@ModelAttribute Auction_ItemsRequest request) throws IOException {
        auction_ItemsService.updateAuction_Items(request);
        return ApiResponse.<String>builder()
                .code(0)
                .message("Update auction item successfully")
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<Auction_ItemsResponse> getAuctionById(@PathVariable int id) {
        Auction_ItemsResponse auctionItemsResponses = auction_ItemsService.getAuction_ItemsById(id);
        return ApiResponse.<Auction_ItemsResponse> builder()
                .code(0)
                .message("Get auction item successfully")
                .result(auctionItemsResponses)
                .build();
    }

    @GetMapping("/creator/{id}")
    public ApiResponse<List<Auction_ItemsResponse>> getAuctionByCreator(@PathVariable String id) {
        List<Auction_ItemsResponse> auctionItemsResponses = auction_ItemsService.getAllByCreator(id);
        return ApiResponse.<List<Auction_ItemsResponse>> builder()
                .code(0)
                .message("Get auction item successfully")
                .result(auctionItemsResponses)
                .build();
    }

    @GetMapping("/buyer/{id}")
    public ApiResponse<List<Auction_ItemsResponse>> getAuctionByBuyer(@PathVariable String id) {
        List<Auction_ItemsResponse> auctionItemsResponses = auction_ItemsService.getAllByBuyer(id);
        return ApiResponse.<List<Auction_ItemsResponse>> builder()
                .code(0)
                .message("Get auction item successfully")
                .result(auctionItemsResponses)
                .build();
    }

    @PutMapping("/status/{id}")
    public ApiResponse<Boolean> updateAuctionStatus(@PathVariable int id) {
      boolean isUpdate =  auction_ItemsService.updateStatus(id);
        if(isUpdate) {
            return ApiResponse.<Boolean> builder()
                    .code(0)
                    .message("Update auction item successfully")
                    .build();
        }
        return ApiResponse.<Boolean> builder()
                .code(1)
                .message("Update auction item failed")
                .build();
    }

    @PutMapping("issell/{id}")
    public Boolean updateAuctionIsSell(@PathVariable int id) {
        return auction_ItemsService.updateIsSell(id);
    }

    @GetMapping("get-onhome")
    public ApiResponse<List<Auction_ItemsResponse>> getAuctionOnHome() {
        List<Auction_ItemsResponse> list = auction_ItemsService.getAuctionsOnHome();

        return ApiResponse.<List<Auction_ItemsResponse>>builder()
                .result(list)
                .build();
    }


    @GetMapping("/featured")
    public ApiResponse<List<Auction_ItemsResponse>> getFeaturedAuctions() {
        return ApiResponse.<List<Auction_ItemsResponse>>builder()
                .result(auction_ItemsService.getFeaturedAuctions())
                .code(0).build();
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<Auction_ItemsResponse>> getUpcomingAuctions() {
        return ApiResponse.<List<Auction_ItemsResponse>>builder()
                .result(auction_ItemsService.getUpcomingAuctions())
                .code(0).build();
    }
}
