package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.response.NotificationAuctionItemResponse;
import fpt.aptech.server_be.service.NotificationAuctionItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notification/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationAuctionItemController {
    @Autowired
    NotificationAuctionItemService notificationAuctionItemService;

    @GetMapping
    public ApiResponse<List<NotificationAuctionItemResponse>> getAllByAdmin(){
        return ApiResponse.<List<NotificationAuctionItemResponse>>builder()
                .code(0)
                .result(notificationAuctionItemService.findAllByAdmin())
                .build();
    }
    @GetMapping("/{userId}")
    public ApiResponse<List<NotificationAuctionItemResponse>> getAll(@PathVariable String userId){
        return ApiResponse.<List<NotificationAuctionItemResponse>>builder()
                .code(0)
                .result(notificationAuctionItemService.findAll(userId))
                .build();
    }
}
