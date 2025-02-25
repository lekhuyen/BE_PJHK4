//package fpt.aptech.server_be.service;
//
//import fpt.aptech.server_be.entities.AuctionSuccessEvent;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaConsumer {
//    private final BiddingService biddingService;  // Service gửi thông báo, email
//
//    public KafkaConsumer( BiddingService biddingService) {
//        this.biddingService = biddingService;
//    }
//
//    @KafkaListener(topics = "${kafka.topic.auction.success}", groupId = "auction-consumer-group")
//    public void listenAuctionSuccess(AuctionSuccessEvent event) {
//        // Xử lý thông báo và gửi email cho người bán và người mua
//        biddingService.sendAuctionSuccessNotification(event);
//    }
//}
