package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.response.NotificationAuctionItemResponse;
import fpt.aptech.server_be.entities.NotificationAuctionItem;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.mapper.NotificationAuctionItemMapper;
import fpt.aptech.server_be.repositories.NotificationAuctionItemRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationAuctionItemService {
    @Autowired
    private NotificationAuctionItemRepository notificationAuctionItemRepository;
    @Autowired
    UserRepository userRepository;

    public List<NotificationAuctionItemResponse> findAllByAdmin(){
        List<NotificationAuctionItem> notificationAuctionItem = notificationAuctionItemRepository.findByTypeP();

        return notificationAuctionItem.stream().map(NotificationAuctionItemMapper::toNotificationAuctionItemResponse).collect(Collectors.toList());
    }

    public List<NotificationAuctionItemResponse> findAll(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User is not found"));
        List<NotificationAuctionItem> notificationAuctionItem = notificationAuctionItemRepository.findByCreator(user);

        return notificationAuctionItem.stream().map(NotificationAuctionItemMapper::toNotificationAuctionItemResponse).collect(Collectors.toList());
    }



}
