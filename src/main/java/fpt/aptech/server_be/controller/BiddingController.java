package fpt.aptech.server_be.controller;

import com.cloudinary.Api;
import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.BiddingRequest;
import fpt.aptech.server_be.dto.response.BiddingResponse;
import fpt.aptech.server_be.dto.response.NotificationResponse;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.service.BiddingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bidding")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BiddingController {

    BiddingService biddingService;

//    @PostMapping
//    public BiddingResponse postBidding(@RequestBody BiddingRequest request) {
//        return ApiResponse.<BiddingResponse>builder()
//                .code(0)
//                .message("Bidding successful")
//                .result(biddingService.createBidding(request))
//                .build()
//                .getResult();
//    }

    @MessageMapping("/create")
    @SendTo("/topic/newbidding")
    public BiddingResponse postBidding(@RequestBody BiddingRequest request) {
        try {
            return ApiResponse.<BiddingResponse>builder()
                    .code(0)
                    .message("Bidding successful")
                    .result(biddingService.createBidding(request))
                    .build()
                    .getResult();
        } catch (AppException e) {
            return ApiResponse.<BiddingResponse>builder()
                    .code(1)
                    .message(e.getMessage())
                    .result(null)
                    .build()
                    .getResult();
        }

    }

    @GetMapping("/{id}")
    public BiddingResponse getBidding(@PathVariable("id") int id) {
        return ApiResponse.<BiddingResponse>builder()
                .code(0)
                .message("Get bidding successful")
                .result(biddingService.getBiddingByProductId(id))
                .build()
                .getResult();
    }

    @GetMapping("/notification/{userId}")
    public ApiResponse<List<NotificationResponse>> getBiddingByUserId(@PathVariable("userId") String userId) {
        return ApiResponse.<List<NotificationResponse>>builder()
                .code(0)
                .message("Get notification by user successful")
                .result(biddingService.getAllNotificationsByUserId(userId))
                .build();
    }

    @PutMapping("/notification/status/{id}/{userId}")
    public ApiResponse<String> updateNotificationStatus(@PathVariable("id") int id,@PathVariable("userId") String userId){
        biddingService.updateStatusNotification(id,userId);
        return ApiResponse.<String>builder()
                .code(0)
                .build();
    }

    @PostMapping("success/{productId}/{sellerId}")
    public boolean auctionSuccess(@PathVariable("productId") int productId,@PathVariable("sellerId") String sellerId){
        return biddingService.auctionSuccess(productId,sellerId);
    }

}
