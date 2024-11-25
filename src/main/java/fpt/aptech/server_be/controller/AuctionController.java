package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.Auction_ItemsRequest;
import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.service.Auction_ItemsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionController {

    Auction_ItemsService auction_ItemsService;


    @GetMapping("/")
    public ApiResponse<List<Auction_ItemsResponse>> getAllAuctions() {
        return ApiResponse.<List<Auction_ItemsResponse>>builder()
                .result(auction_ItemsService.getAllAuction_Items())
                .build();
    }

    @PostMapping("/")
    public ApiResponse<String> createAuction(@RequestBody Auction_ItemsRequest request) {
        auction_ItemsService.addAuction_Items(request);

        return ApiResponse.<String>builder()
                .message("Add auction item successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAuction(@PathVariable int id) {
        auction_ItemsService.deleteAuction_Items(id);
        return ApiResponse.<String>builder()
                .message("Delete auction item successfully")
                .build();
    }

    @PutMapping("/")
    public ApiResponse<String> updateAuction(@RequestBody Auction_ItemsRequest request) {
        auction_ItemsService.updateAuction_Items(request);
        return ApiResponse.<String>builder()
                .message("Update auction item successfully")
                .build();
    }
}
