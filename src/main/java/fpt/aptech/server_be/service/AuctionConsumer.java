package fpt.aptech.server_be.service;

import fpt.aptech.server_be.configuration.RabbitConfig;
import fpt.aptech.server_be.dto.request.AuctionSuccessDTO;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Bidding;
import fpt.aptech.server_be.entities.Notification;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.BiddingRepository;
import fpt.aptech.server_be.repositories.NotificationRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class AuctionConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BiddingService biddingService;

    @RabbitListener(queues = RabbitConfig.REQUEST_QUEUE, concurrency = "10-20")
    public void consume(Message message) {
        try {
            AuctionSuccessDTO auctionMessage = (AuctionSuccessDTO) rabbitTemplate.getMessageConverter().fromMessage(message);
            System.out.println("ddddddddddddddd"+auctionMessage);
            boolean result = biddingService.auctionSuccess(auctionMessage.getProductId(), auctionMessage.getSellerId());

            String replyTo = message.getMessageProperties().getReplyTo();
            String correlationId = message.getMessageProperties().getCorrelationId();

            MessageProperties responseProps = new MessageProperties();
            responseProps.setCorrelationId(correlationId);
            Message responseMessage = rabbitTemplate.getMessageConverter().toMessage(result, responseProps);
            rabbitTemplate.send(replyTo, responseMessage);
        } catch (Exception e) {
            log.error("Error processing message: ", e);
        }
    }


}
