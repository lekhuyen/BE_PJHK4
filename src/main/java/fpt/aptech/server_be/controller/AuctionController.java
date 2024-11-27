package fpt.aptech.server_be.controller;


import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.service.Auction_ItemsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ApiResponse<List<Auction_ItemsResponse>> getAllAuctions() {
        return ApiResponse.<List<Auction_ItemsResponse>>builder()
                .result(auction_ItemsService.getAllAuction_Items())
                .build();
    }

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
}
