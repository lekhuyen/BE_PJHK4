package fpt.aptech.server_be.payment.vnpay;

import fpt.aptech.server_be.core.response.ResponseObject;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Bidding;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment") // Đổi trực tiếp thành đường dẫn cố định
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    @Autowired
    public Auction_ItemsRepository auction_ItemsRepository;

    @Autowired
    public UserRepository userRepository;

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(@RequestParam String productId,
                                                        @RequestParam String amount,
                                                        @RequestParam String orderId,HttpServletRequest request) {
        log.info("✅ Nhận productId: {}, amount: {}, orderId: {}", productId, amount, orderId);

        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request,productId));
    }

//    @GetMapping("/vn-pay-callback")
//    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String status = request.getParameter("vnp_ResponseCode");
//        String orderInfo = request.getParameter("vnp_OrderInfo");
//
//        if (status == null || orderInfo == null) {
//            log.error("🚨 Lỗi callback: Thiếu thông tin thanh toán!");
//            response.sendRedirect("http://192.168.1.30:8080/payment-failed");
//            return;
//        }
//
//        try {
//            String productId = orderInfo.replace("Thanh toán cho sản phẩm ID: ", "").trim();
//            log.info("✅ Thanh toán thành công - productId: {}", productId);
//
//            // 🔥 Tìm sản phẩm theo ID
//            Auction_Items auctionItems = auction_ItemsRepository.findById(Integer.parseInt(productId))
//                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm!"));
//
//            auctionItems.setSoldout(true);
//            auctionItems.setPaid(true);
//            auction_ItemsRepository.save(auctionItems);
//
//            // ✅ Lấy thông tin người bán, người mua, và admin
//            User seller = auctionItems.getUser();
//            User buyer = auctionItems.getBuyer();
//            User admin = userRepository.findByEmail("admin@gmail.com")
//                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản admin!"));
//
//            if (seller == null || buyer == null) {
//                log.error("🚨 Lỗi: Không tìm thấy người bán hoặc người mua!");
//                response.sendRedirect("http://192.168.1.30:8080/payment-failed");
//                return;
//            }
//
//            // ✅ Lấy giá thắng cuộc từ `Bidding`
//            Bidding bidding = auctionItems.getBidding();
//            double finalPrice = bidding.getPrice();
//
//            // ✅ Kiểm tra nếu `money` của buyer hoặc seller là null thì đặt về 0.0
//            seller.setMoney((seller.getMoney() != null ? seller.getMoney() : 0.0));
//            buyer.setMoney((buyer.getMoney() != null ? buyer.getMoney() : 0.0));
//            admin.setMoney((admin.getMoney() != null ? admin.getMoney() : 0.0));
//
//            // 🔥 Kiểm tra nếu số dư của buyer có đủ không
//            if (buyer.getMoney() < finalPrice) {
//                log.error("🚨 Lỗi: Người mua {} không đủ tiền để thanh toán! Số dư: ${}", buyer.getName(), buyer.getMoney());
//                response.sendRedirect("http://192.168.1.30:8080/payment-failed");
//                return;
//            }
//
//            // ✅ Tính toán số tiền người bán nhận được sau khi trừ phí 2%
//            double fee = finalPrice * 0.02; // 🔥 Tính phí 2%
//            double amountAfterFee = finalPrice - fee; // 🔥 Số tiền thực nhận của người bán
//
//            // ✅ Cập nhật tiền
//            seller.setMoney(seller.getMoney() + amountAfterFee); // Người bán nhận tiền sau khi trừ phí
//            buyer.setMoney(buyer.getMoney() - finalPrice); // Người mua bị trừ toàn bộ tiền
//            admin.setMoney(admin.getMoney() + fee); // Admin nhận 2% phí giao dịch
//
//            // ✅ Lưu cập nhật vào database
//            userRepository.save(seller);
//            userRepository.save(buyer);
//            userRepository.save(admin);
//
//            log.info("✅ Cập nhật số tiền thành công: Người bán {} nhận +${}, Người mua {} bị trừ -${}, Admin nhận +${}",
//                    seller.getName(), amountAfterFee, buyer.getName(), finalPrice, fee);
//
//            response.sendRedirect("http://localhost:3000/profile-page");
////            response.sendRedirect("myapp://mybids");
//
//        } catch (Exception e) {
//            log.error("🚨 Lỗi xử lý callback: {}", e.getMessage());
//            response.sendRedirect("http://192.168.1.30:8080/payment-failed");
//        }
//    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        String orderInfo = request.getParameter("vnp_OrderInfo");

        if (status == null || orderInfo == null) {
            log.error("🚨 Lỗi callback: Thiếu thông tin thanh toán!");
            response.sendRedirect("http://localhost:3000/payment-failed"); // 🔥 Đổi thành React URL
            return;
        }

        try {
            String productId = orderInfo.replace("Thanh toán cho sản phẩm ID: ", "").trim();
            log.info("✅ Thanh toán thành công - productId: {}", productId);

            // 🔥 Tìm sản phẩm theo ID
            Auction_Items auctionItems = auction_ItemsRepository.findById(Integer.parseInt(productId))
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm!"));

            auctionItems.setSoldout(true);
            auctionItems.setPaid(true);
            auction_ItemsRepository.save(auctionItems);

            // ✅ Lấy thông tin người bán, người mua, và admin
            User seller = auctionItems.getUser();
            User buyer = auctionItems.getBuyer();
            User admin = userRepository.findByEmail("admin@gmail.com")
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản admin!"));

            if (seller == null || buyer == null) {
                log.error("🚨 Lỗi: Không tìm thấy người bán hoặc người mua!");
                response.sendRedirect("http://localhost:3000/payment-failed");
                return;
            }

            // ✅ Lấy giá thắng cuộc từ `Bidding`
            Bidding bidding = auctionItems.getBidding();
            double finalPrice = bidding.getPrice();

            // ✅ Kiểm tra nếu `money` của buyer hoặc seller là null thì đặt về 0.0
            seller.setMoney((seller.getMoney() != null ? seller.getMoney() : 0.0));
            buyer.setMoney((buyer.getMoney() != null ? buyer.getMoney() : 0.0));
            admin.setMoney((admin.getMoney() != null ? admin.getMoney() : 0.0));

            // 🔥 Kiểm tra nếu số dư của buyer có đủ không
            if (buyer.getMoney() < finalPrice) {
                log.error("🚨 Lỗi: Người mua {} không đủ tiền để thanh toán! Số dư: ${}", buyer.getName(), buyer.getMoney());
                response.sendRedirect("http://localhost:3000/payment-failed");
                return;
            }

            // ✅ Tính toán số tiền người bán nhận được sau khi trừ phí 2%
            double fee = finalPrice * 0.02; // 🔥 Tính phí 2%
            double amountAfterFee = finalPrice - fee; // 🔥 Số tiền thực nhận của người bán

            // ✅ Cập nhật tiền
            seller.setMoney(seller.getMoney() + amountAfterFee); // Người bán nhận tiền sau khi trừ phí
            buyer.setMoney(buyer.getMoney() - finalPrice); // Người mua bị trừ toàn bộ tiền
            admin.setMoney(admin.getMoney() + fee); // Admin nhận 2% phí giao dịch

            // ✅ Lưu cập nhật vào database
            userRepository.save(seller);
            userRepository.save(buyer);
            userRepository.save(admin);

            log.info("✅ Cập nhật số tiền thành công: Người bán {} nhận +${}, Người mua {} bị trừ -${}, Admin nhận +${}",
                    seller.getName(), amountAfterFee, buyer.getName(), finalPrice, fee);

            // ✅ Kiểm tra User-Agent để phân biệt Mobile và React Web
            String userAgent = request.getHeader("User-Agent");
            log.info("📢 User-Agent: {}", userAgent);

            if (userAgent != null && userAgent.contains("Mobile")) {
                response.sendRedirect("myapp://mybids"); // 🔥 Mobile chuyển hướng về app
            } else {
                response.sendRedirect("http://localhost:3000/profile-page"); // 🔥 React chuyển hướng về web
            }

        } catch (Exception e) {
            log.error("🚨 Lỗi xử lý callback: {}", e.getMessage());
            response.sendRedirect("http://localhost:3000/payment-failed");
        }
    }

//    @GetMapping("/vn-pay-callback-mobile")
//    public void payCallbackHandlerMobile(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String status = request.getParameter("vnp_ResponseCode");
//        String orderInfo = request.getParameter("vnp_OrderInfo");
//        String productId = orderInfo.replace("Thanh toán cho sản phẩm ID: ", ""); // Lọc productId
//
//        log.info("✅ Thanh toán thành công - productId: {}", productId);
//
//        Auction_Items auctionItems = auction_ItemsRepository.findById(Integer.parseInt(productId)).get();
//        auctionItems.setSoldout(true);
//        auctionItems.setPaid(true);
//        auction_ItemsRepository.save(auctionItems);
//
//
//        String redirectUrl = String.format("http://localhost:3000/profile-page");
//
//        response.sendRedirect(redirectUrl);
//    }

//    mobile
@GetMapping("/bids/{userId}")
public Map<String, List<Auction_Items>> getBidsByUser(@PathVariable String userId) {
    log.info("📢 Truy vấn danh sách đấu giá cho userId: {}", userId);
    List<Auction_Items> paidItems = auction_ItemsRepository.findPaidItemsByUserId(userId);
    List<Auction_Items> unpaidItems = auction_ItemsRepository.findUnpaidItemsByUserId(userId);

    Map<String, List<Auction_Items>> result = new HashMap<>();
    result.put("paid", paidItems);
    result.put("unpaid", unpaidItems);
    log.info("✅ Đã tìm thấy {} sản phẩm đã thanh toán", paidItems.size());
    log.info("✅ Đã tìm thấy {} sản phẩm chưa thanh toán", unpaidItems.size());
    return Map.of("paid", paidItems, "unpaid", unpaidItems);
}

    @GetMapping("/won-items/{userId}")
    public ResponseObject<List<Auction_Items>> getWonItemsByUser(@PathVariable String userId) {
        log.info("✅ Lấy danh sách sản phẩm user đã thanh toán - userId: {}", userId);

        List<Auction_Items> wonItems = auction_ItemsRepository.findWonItemsByUserId(userId);

        wonItems.forEach(item -> {
            if (item.getUser() != null) {
                log.info("📌 Người bán của item {}: {}", item.getItem_id(), item.getUser().getName());
                item.getUser().setAuctionItems(null);
            } else {
                log.info("🚨 Item {} không có thông tin người bán!", item.getItem_id());
            }
        });

        return new ResponseObject<>(HttpStatus.OK, "Success", wonItems);
    }
    @GetMapping("/unwon-items/{userId}")
    public ResponseObject<List<Auction_Items>> getUnwonItemsByUser(@PathVariable String userId) {
        log.info("✅ Lấy danh sách sản phẩm chưa thanh toán - userId: {}", userId);

        List<Auction_Items> unwonItems = auction_ItemsRepository.findUnWonItemsByUserId(userId);

        unwonItems.forEach(item -> {
            if (item.getUser() != null) {
                log.info("📌 Người bán của item {}: {}", item.getItem_id(), item.getUser().getName());
                item.getUser().setAuctionItems(null); // Xóa dữ liệu vòng lặp tránh lỗi JSON
            } else {
                log.info("🚨 Item {} không có thông tin người bán!", item.getItem_id());
            }
        });

        return new ResponseObject<>(HttpStatus.OK, "Success", unwonItems);
    }

}
