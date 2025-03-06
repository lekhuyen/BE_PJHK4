package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.BiddingRequest;
import fpt.aptech.server_be.dto.response.BiddingResponse;
import fpt.aptech.server_be.dto.response.NotificationResponse;
import fpt.aptech.server_be.entities.*;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.exception.ErrorCode;
import fpt.aptech.server_be.mapper.BiddingMapper;
import fpt.aptech.server_be.mapper.NotificationMapper;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.BiddingRepository;
import fpt.aptech.server_be.repositories.NotificationRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
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
    private EmailService emailService;

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
// Lấy giá hiện tại và bidStep để kiểm tra
//        double currentPrice = auctionItem.getBidding().getPrice();
//        double bidStep = Double.parseDouble(auctionItem.getBid_step()); // Giả sử bid_step là chuỗi
//
//        // Kiểm tra giá đặt phải lớn hơn hoặc bằng giá hiện tại cộng với bidStep
//        if (request.getPrice() < currentPrice + bidStep) {
//            throw new AppException(ErrorCode.PRICE_TOO_LOW); // Nếu không thỏa mãn, báo lỗi
//        }
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

//    dau gia thanh cong
//    public boolean auctionSuccesss(int productId,String sellerId){
//        Auction_Items auctionItem = auctionItemsRepository.findById(productId)
//                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));
//
//        Bidding bidding = biddingRepository.findBiddingByAuction_Items(auctionItem);
//
//
//        User buyer = userRepository.findById(bidding.getUser().getId())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        if(auctionItem!= null){
//            auctionItem.setSoldout(true);
//            auctionItem.setBuyer(buyer);
//            auctionItemsRepository.save(auctionItem);
//        }
//
//        User seller = userRepository.findById(sellerId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        Notification notification = Notification.builder()
//                .sellerIsRead(false)
//                .buyerIsRead(false)
//                .bidding(bidding)
//                .price(bidding.getPrice())
//                .seller(seller)
//                .buyer(buyer)
//                .date(new Date())
//                .auction(true)
//                .build();
//
//        notificationRepository.save(notification);
//        NotificationResponse notificationResponse = NotificationMapper.toNotificationResponse(notification);
//        messagingTemplate.convertAndSend("/topic/notification", notificationResponse);
//
//
//
//        // Message for the buyer
//        SimpleMailMessage buyerMessage = new SimpleMailMessage();
//        buyerMessage.setTo(buyer.getEmail());
//        buyerMessage.setSubject("Đấu giá thành công");
//        buyerMessage.setText("Chúc mừng bạn đã đấu giá thành công sản phẩm " + auctionItem.getItem_name() + " với giá " + bidding.getPrice());
//        buyerMessage.setSentDate(new Date());
//        mailSender.send(buyerMessage);
//
//// Message for the seller
//        SimpleMailMessage sellerMessage = new SimpleMailMessage();
//        sellerMessage.setTo(seller.getEmail());
//        sellerMessage.setSubject("Chúc mừng! Sản phẩm " + auctionItem.getItem_name() + " của bạn đã được đấu giá thành công");
//        sellerMessage.setText("Sản phẩm " + auctionItem.getItem_name() + " của bạn đã được khách hàng " + buyer.getName() + " đấu giá thành công với mức giá " + bidding.getPrice());
//        sellerMessage.setSentDate(new Date());
//        mailSender.send(sellerMessage);
//
//
//        return true;
//
//    }

    @Transactional
    public boolean auctionSuccess(int productId,String sellerId){
        Auction_Items auctionItem = auctionItemsRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));


        Bidding bidding = biddingRepository.findBiddingByAuction_Items(auctionItem);
        User buyer = null;
        if(bidding != null){
            buyer = userRepository.findById(bidding.getUser().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }


        if(auctionItem!= null){
            auctionItem.setSoldout(true);
            auctionItem.setBuyer(buyer);
            auctionItemsRepository.save(auctionItem);
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Notification notification = null;
        if(bidding != null && bidding.getPrice() > 0){
             notification = Notification.builder()
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
            assert notificationResponse != null;
            messagingTemplate.convertAndSend("/topic/notification", notificationResponse);

            sendAuctionSuccessEmails(buyer, seller, auctionItem, bidding);
        }

        return true;

    }

    @Async
    protected void sendAuctionSuccessEmails(User buyer, User seller, Auction_Items auctionItem, Bidding bidding) {
        try {
            MimeMessage buyerMessage = mailSender.createMimeMessage();
            MimeMessageHelper buyerHelper = new MimeMessageHelper(buyerMessage, true, "UTF-8");
            buyerHelper.setTo(buyer.getEmail());
            buyerHelper.setSubject("🎉 Congratulations! You've won the auction! 🎉");
            buyerHelper.setSentDate(new Date());

//// Fetch buyer and seller names
            String buyerName = (buyer != null) ? buyer.getName() : "Anonymous Buyer";
            String sellerName = (auctionItem.getUser() != null) ? auctionItem.getUser().getName() : "Anonymous Seller";

// Get image list from product
            List<String> imageUrls = auctionItem.getImages();

// Get current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String auctionDate = dateFormat.format(new Date());

// Build HTML Content
            StringBuilder contentBuyer = new StringBuilder("<html><body style='font-family: Arial, sans-serif; background-color: #ffffff;'>");

// Centered container with slight shadow
            contentBuyer.append("<div style='max-width: 700px; margin: auto; padding: 20px; "
                    + "border: 5px solid #28a745; border-radius: 15px; background-color: white; "
                    + "box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.1); text-align: center;'>");

// **Display Product Images, centered in rows**
            // **Display Product Images, 3 per row**
            if (imageUrls != null && !imageUrls.isEmpty()) {
                contentBuyer.append("<div style='display: flex; flex-wrap: wrap; justify-content: center; gap: 10px;'>");
                for (int i = 0; i < imageUrls.size(); i++) {
                    // Open a new row every 3 images
                    if (i % 3 == 0) {
                        contentBuyer.append("<div style='display: flex; justify-content: center; width: 100%;'>");
                    }

                    contentBuyer.append("<div style='width: 30%; text-align: center; padding: 5px;'>"
                            + "<img src='" + imageUrls.get(i) + "' width='100%' height='auto' "
                            + "style='max-width: 200px; border-radius: 10px; box-shadow: 2px 2px 10px rgba(0,0,0,0.1);'/>"
                            + "</div>");

                    // Close the row after 3 images
                    if (i % 3 == 2 || i == imageUrls.size() - 1) {
                        contentBuyer.append("</div>");
                    }
                }
                contentBuyer.append("</div>"); // Close main image container
            }

// **Congratulatory Title**
            contentBuyer.append("<div style='padding: 20px;'>"
                    + "<h1 style='color: #28a745;'>🎉 CONGRATULATIONS! 🎉</h1>"
                    + "<h3 style='color: #555;'>You've won the auction on <strong>" + auctionDate + "</strong></h3>"
                    + "</div>");

// **Auction Details**
            contentBuyer.append("<p style='font-size: 16px;'>"
                    + "Congratulations <strong>" + buyerName + "</strong>! You've successfully won the auction for <strong>"
                    + auctionItem.getItem_name() + "</strong> from seller <strong>" + sellerName + "</strong> at a price of <strong>$"
                    + bidding.getPrice() + "</strong>.</p>");

// **Thank You Note**
            contentBuyer.append("<p style='font-size: 14px; color: #888;'>"
                    + "Thank you for participating in the auction on <strong>Biddora</strong>!</p>");

            // **Biddora Icon**
            contentBuyer.append("<div style='margin-bottom: 20px;'>"
                    + "<img src='cid:biddoraIcon' alt='Biddora Logo' width='100' "
                    + "style='border-radius: 10px; box-shadow: 2px 2px 10px rgba(0,0,0,0.1);'/>"
                    + "</div>");
// **Close container**
            contentBuyer.append("</div></body></html>");

// Attach icon image (ensure the path is accessible)
            buyerHelper.setText(contentBuyer.toString(), true);
            buyerHelper.addInline("biddoraIcon", new FileSystemResource(new File("images/BiddoraIcon.png")));

// Send email
            mailSender.send(buyerMessage);

            // **Email cho người bán**
            MimeMessage sellerMessage = mailSender.createMimeMessage();
            MimeMessageHelper sellerHelper = new MimeMessageHelper(sellerMessage, true, "UTF-8");
            sellerHelper.setTo(seller.getEmail());
            sellerHelper.setSubject("🎉 Your item has been sold! 🎉");
            sellerHelper.setSentDate(new Date());

// Fetch item details
            String sellerName1 = (seller != null) ? seller.getName() : "Anonymous Seller";
            String buyerName1 = (buyer != null) ? buyer.getName() : "Anonymous Buyer";
            String itemName = auctionItem.getItem_name();
            String itemDescription = auctionItem.getDescription();
            double finalPrice = bidding.getPrice();

// Build HTML content
            StringBuilder contentSeller = new StringBuilder("<html><body style='font-family: Arial, sans-serif;'>");

// **Centered Container**
            contentSeller.append("<div style='max-width: 700px; margin: auto; padding: 20px; border: 5px solid #007bff; "
                    + "border-radius: 15px; background-color: white; box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.1); text-align: center;'>");

// **Sold Item Banner**
            if (imageUrls != null && !imageUrls.isEmpty()) {
                contentSeller.append("<div style='display: flex; justify-content: center; flex-wrap: wrap; gap: 10px;'>");
                int count = 0;
                for (String imageUrl : imageUrls) {
                    if (count % 3 == 0) {
                        contentSeller.append("<div style='width: 100%; display: flex; justify-content: center;'>"); // New row
                    }
                    contentSeller.append("<div style='margin: 5px; text-align: center;'>"
                            + "<img src='" + imageUrl + "' width='200' height='200' "
                            + "style='border-radius: 10px; box-shadow: 2px 2px 10px rgba(0,0,0,0.1);'/>"
                            + "</div>");
                    count++;
                    if (count % 3 == 0) {
                        contentSeller.append("</div>"); // Close row
                    }
                }
                if (count % 3 != 0) {
                    contentSeller.append("</div>"); // Close last row if not a multiple of 3
                }
                contentSeller.append("</div>");
            }

// **Title & Congratulations Message**
            contentSeller.append("<h1 style='color: #007bff;'>🎉 Congratulations, " + sellerName1 + "! 🎉</h1>"
                    + "<h3 style='color: #555;'>Your item has been successfully sold!</h3>");

// **Product Details**
            contentSeller.append("<div style='text-align: left; margin-top: 20px;'>"
                    + "<p style='font-size: 16px;'><strong>Item:</strong> " + itemName + "</p>"
                    + "<p style='font-size: 16px;'><strong>Description:</strong> " + itemDescription + "</p>"
                    + "<p style='font-size: 16px;'><strong>Final Price:</strong> <span style='color: #28a745; font-weight: bold;'>$" + finalPrice + "</span></p>"
                    + "<p style='font-size: 16px;'><strong>Buyer:</strong> " + buyerName1 + " (" + buyer.getEmail() + ")</p>"
                    + "</div>");

// **Next Steps**
            contentSeller.append("<div style='margin-top: 20px; text-align: left;'>"
                    + "<p style='font-size: 14px; color: #333;'>Please contact the buyer to arrange payment and delivery.</p>"
                    + "<p style='font-size: 14px; color: #333;'>Thank you for using <strong>Biddora</strong>!</p>"
                    + "</div>");

// **Footer**
            contentSeller.append("<p style='font-size: 14px; color: #888; text-align: center;'>"
                    + "Best regards,<br><strong>Biddora Auction Team</strong></p>");

            // **Biddora Icon**
            contentSeller.append("<div style='margin-bottom: 20px;'>"
                    + "<img src='cid:biddoraIconSeller' alt='Biddora Logo Seller' width='100' "
                    + "style='border-radius: 10px; box-shadow: 2px 2px 10px rgba(0,0,0,0.1);'/>"
                    + "</div>");

            // Attach icon image (ensure the path is accessible)
            buyerHelper.setText(contentBuyer.toString(), true);
            buyerHelper.addInline("biddoraIconSeller", new FileSystemResource(new File("images/BiddoraIconSeller.png")));

// **Close Container**
            contentSeller.append("</div></body></html>");

// Set email content
            sellerHelper.setText(contentSeller.toString(), true);

// Send email
            mailSender.send(sellerMessage);


        } catch (MessagingException e) {
            e.printStackTrace(); // Log lỗi nếu có vấn đề trong quá trình gửi email
        }
    }
}
