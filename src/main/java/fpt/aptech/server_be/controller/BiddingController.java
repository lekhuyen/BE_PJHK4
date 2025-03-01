package fpt.aptech.server_be.controller;

import com.cloudinary.Api;
import fpt.aptech.server_be.configuration.RabbitConfig;
import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.AuctionSuccessDTO;
import fpt.aptech.server_be.dto.request.BiddingRequest;
import fpt.aptech.server_be.dto.response.BiddingResponse;
import fpt.aptech.server_be.dto.response.NotificationResponse;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.service.BiddingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/bidding")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BiddingController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

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

//    @PostMapping("success/{productId}/{sellerId}")
//    public boolean auctionSuccesss(@PathVariable("productId") int productId,@PathVariable("sellerId") String sellerId){
//        return biddingService.auctionSuccesss(productId,sellerId);
//    }


    @PostMapping("success/{productId}/{sellerId}")
    public boolean   auctionSuccess(@PathVariable("productId") int productId,@PathVariable("sellerId") String sellerId){
        String correlationId = UUID.randomUUID().toString();

        MessageProperties props = new MessageProperties();
        props.setCorrelationId(correlationId);
        props.setReplyTo(RabbitConfig.REPLY_QUEUE);

        AuctionSuccessDTO auctionMessage = new AuctionSuccessDTO(productId, sellerId, correlationId);

        Message message = rabbitTemplate.getMessageConverter().toMessage(auctionMessage, props);

        rabbitTemplate.send(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, message);

        Message response = rabbitTemplate.receive(RabbitConfig.REPLY_QUEUE, 5000);
        if (response != null) {
            return (Boolean) rabbitTemplate.getMessageConverter().fromMessage(response);
        } else {
            return false;
        }
    }

}
