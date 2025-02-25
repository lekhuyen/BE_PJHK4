package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.AuctionSuccessEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, AuctionSuccessEvent> kafkaTemplate;

    @Value("${kafka.topic.auction.success}")
    private String auctionSuccessTopic;

    public KafkaProducer(KafkaTemplate<String, AuctionSuccessEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuctionSuccessEvent(AuctionSuccessEvent event) {
        kafkaTemplate.send(auctionSuccessTopic, event);
    }
}
