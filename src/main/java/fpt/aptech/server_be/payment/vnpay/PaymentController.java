package fpt.aptech.server_be.payment.vnpay;

import fpt.aptech.server_be.core.response.ResponseObject;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
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

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(@RequestParam String productId,
                                                        @RequestParam String amount,
                                                        @RequestParam String orderId,HttpServletRequest request) {
        log.info("✅ Nhận productId: {}, amount: {}, orderId: {}", productId, amount, orderId);

        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request,productId));
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        String orderInfo = request.getParameter("vnp_OrderInfo"); // Chứa productId
        String productId = orderInfo.replace("Thanh toán cho sản phẩm ID: ", ""); // Lọc productId

        log.info("✅ Thanh toán thành công - productId: {}", productId);

        Auction_Items auctionItems = auction_ItemsRepository.findById(Integer.parseInt(productId)).get();
        auctionItems.setSoldout(true);
        auctionItems.setPaid(true);
        auction_ItemsRepository.save(auctionItems);


        String redirectUrl = String.format("http://localhost:3000/manager-post");

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/vn-pay-callback-mobile")
    public void payCallbackHandlerMobile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String productId = orderInfo.replace("Thanh toán cho sản phẩm ID: ", ""); // Lọc productId

        log.info("✅ Thanh toán thành công - productId: {}", productId);

        Auction_Items auctionItems = auction_ItemsRepository.findById(Integer.parseInt(productId)).get();
        auctionItems.setSoldout(true);
        auctionItems.setPaid(true);
        auction_ItemsRepository.save(auctionItems);


        String redirectUrl = String.format("http://localhost:3000/manager-post");

        response.sendRedirect(redirectUrl);
    }

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
