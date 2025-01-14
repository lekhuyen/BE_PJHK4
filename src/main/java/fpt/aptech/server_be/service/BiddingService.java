package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.BiddingRequest;
import fpt.aptech.server_be.dto.response.BiddingResponse;
import fpt.aptech.server_be.dto.response.NotificationResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Bidding;
import fpt.aptech.server_be.entities.Notification;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.exception.ErrorCode;
import fpt.aptech.server_be.mapper.BiddingMapper;
import fpt.aptech.server_be.mapper.NotificationMapper;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.BiddingRepository;
import fpt.aptech.server_be.repositories.NotificationRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BiddingService {
    @Autowired
    private BiddingRepository biddingRepository;

    @Autowired
    private Auction_ItemsRepository auctionItemsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    final JavaMailSender mailSender;


    public BiddingResponse createBidding(BiddingRequest request) {

        Auction_Items auctionItem = auctionItemsRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User seller = userRepository.findById(request.getSeller())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Bidding existingBidding = biddingRepository.findByUserAndAuction_Items(user, auctionItem);

        // user da dau gia
        if (existingBidding != null) {
            if (request.getPrice() <= existingBidding.getPrice()) {
                throw new AppException(ErrorCode.PRICE_HIGHER_CURRENT_PRICE);
            }

            existingBidding.setPrice(request.getPrice());
            biddingRepository.save(existingBidding);

//            gui thong bao
            Notification notification = Notification.builder()
                    .sellerIsRead(false)
                    .buyerIsRead(false)
                    .bidding(existingBidding)
                    .price(request.getPrice())
                    .seller(seller)
                    .buyer(user)
                    .auction(false)
                    .date(new Date())
                    .build();


            notificationRepository.save(notification);
            NotificationResponse notificationResponse = NotificationMapper.toNotificationResponse(notification);

            messagingTemplate.convertAndSend("/topic/notification", notificationResponse);

            return BiddingMapper.toBiddingResponse(existingBidding);
        }

        //user chua dau gia
        Bidding highestBidding = biddingRepository.findBiddingByAuction_Items(auctionItem);

        // Nếu chưa có ai đấu giá trước đó
        if (highestBidding == null) {
            Bidding newBidding = BiddingMapper.toBidding(request);
            biddingRepository.save(newBidding);

            Notification notification = Notification.builder()
                    .sellerIsRead(false)
                    .buyerIsRead(false)
                    .bidding(newBidding)
                    .seller(seller)
                    .price(request.getPrice())
                    .buyer(user)
                    .date(new Date())
                    .auction(false)
                    .build();

            notificationRepository.save(notification);
            NotificationResponse notificationResponse = NotificationMapper.toNotificationResponse(notification);
            messagingTemplate.convertAndSend("/topic/notification", notificationResponse);


            return BiddingMapper.toBiddingResponse(newBidding);
        }

        // da co nguoi dau gia
        if (request.getPrice() <= highestBidding.getPrice()) {
            throw new AppException(ErrorCode.PRICE_HIGHER_CURRENT_PRICE);
        }

        // da co nguoi dau gia,update đấu giá va userId
        highestBidding.setPrice(request.getPrice());

        User user1 = new User();
        user1.setId(request.getUserId());
        highestBidding.setUser(user1);

        biddingRepository.save(highestBidding);

        Notification notification = Notification.builder()
                .sellerIsRead(false)
                .buyerIsRead(false)
                .bidding(highestBidding)
                .price(request.getPrice())
                .seller(seller)
                .buyer(user)
                .date(new Date())
                .auction(false)
                .build();

        notificationRepository.save(notification);
        NotificationResponse notificationResponse = NotificationMapper.toNotificationResponse(notification);

        messagingTemplate.convertAndSend("/topic/notification", notificationResponse);


        return BiddingMapper.toBiddingResponse(highestBidding);
    }

    public BiddingResponse getBiddingByProductId(int productId) {
        Auction_Items auctionItems = new Auction_Items();
        auctionItems.setItem_id(productId);
        Bidding bidding = biddingRepository.findBiddingByAuction_Items(auctionItems);
        return BiddingMapper.toBiddingResponse(bidding);
    }

    public List<NotificationResponse> getAllNotificationsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<Notification> notification = notificationRepository.findNotificationBySellerAndBuyer(user);
        return notification.stream().map(NotificationMapper::toNotificationResponse).collect(Collectors.toList());
    }

    public void updateStatusNotification(int id,String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Notification notification = notificationRepository.findNotificationById(id,user);
        if(notification != null && Objects.equals(notification.getBuyer().getId(), userId)){
            if(!notification.isBuyerIsRead()){
                notification.setBuyerIsRead(true);
                notificationRepository.save(notification);
            }
        }
        if(notification != null && Objects.equals(notification.getSeller().getId(), userId)){
            if(!notification.isSellerIsRead()){
                notification.setSellerIsRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    //dau gia thanh cong
    public boolean auctionSuccess(int productId,String sellerId){
        Auction_Items auctionItem = auctionItemsRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));
        if(auctionItem!= null){
            auctionItem.setSoldout(true);
            auctionItemsRepository.save(auctionItem);
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Bidding bidding = biddingRepository.findBiddingByAuction_Items(auctionItem);

        User buyer = userRepository.findById(bidding.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));



        Notification notification = Notification.builder()
                .sellerIsRead(false)
                .buyerIsRead(false)
                .bidding(bidding)
                .price(bidding.getPrice())
                .seller(seller)
                .buyer(buyer)
                .date(new Date())
                .auction(true)
                .build();

        notificationRepository.save(notification);
        NotificationResponse notificationResponse = NotificationMapper.toNotificationResponse(notification);
        messagingTemplate.convertAndSend("/topic/notification", notificationResponse);



        // Message for the buyer
        SimpleMailMessage buyerMessage = new SimpleMailMessage();
        buyerMessage.setTo(buyer.getEmail());
        buyerMessage.setSubject("Đấu giá thành công");
        buyerMessage.setText("Chúc mừng bạn đã đấu giá thành công sản phẩm " + auctionItem.getItem_name() + " với giá " + bidding.getPrice());
        buyerMessage.setSentDate(new Date());
        mailSender.send(buyerMessage);

// Message for the seller
        SimpleMailMessage sellerMessage = new SimpleMailMessage();
        sellerMessage.setTo(seller.getEmail());
        sellerMessage.setSubject("Chúc mừng! Sản phẩm " + auctionItem.getItem_name() + " của bạn đã được đấu giá thành công");
        sellerMessage.setText("Sản phẩm " + auctionItem.getItem_name() + " của bạn đã được khách hàng " + buyer.getName() + " đấu giá thành công với mức giá " + bidding.getPrice());
        sellerMessage.setSentDate(new Date());
        mailSender.send(sellerMessage);


        return true;

    }

}
