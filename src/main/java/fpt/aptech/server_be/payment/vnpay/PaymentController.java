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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

}
