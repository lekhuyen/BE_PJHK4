package fpt.aptech.server_be.payment.vnpay;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final fpt.aptech.server_be.core.config.payment.VNPAYConfig vnPayConfig;

    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request,String productId) {
        try {
            log.info("✅ Bắt đầu tạo thanh toán VNPay cho productId: {}", productId);

            // 1️⃣ Kiểm tra productId có hợp lệ không
            if (productId == null || productId.isEmpty()) {
                throw new IllegalArgumentException("🚨 Lỗi: productId không hợp lệ!");
            }

            // 2️⃣ Kiểm tra và lấy amount từ request
            String amountStr = request.getParameter("amount");
            if (amountStr == null || amountStr.isEmpty()) {
                throw new IllegalArgumentException("🚨 Lỗi: Amount is required.");
            }

            // ✅ Chuyển đổi `amount` từ String thành `double` để tránh lỗi parse
            double amountDouble = Double.parseDouble(amountStr);
            long amount = (long) (amountDouble * 100); // Chuyển về VNĐ (VNPay yêu cầu nhân 100)

            // 3️⃣ Kiểm tra bankCode
            String bankCode = request.getParameter("bankCode");
            if (bankCode != null && bankCode.isEmpty()) {
                bankCode = null;
            }

            // 4️⃣ Chuẩn bị dữ liệu để gửi đến VNPay
            Map<String, String> vnpParamsMap = new HashMap<>(vnPayConfig.getVNPayConfig());
            vnpParamsMap.put("vnp_Amount", String.valueOf(amount)); // ✅ Lưu amount dưới dạng String hợp lệ
            if (bankCode != null) {
                vnpParamsMap.put("vnp_BankCode", bankCode);
            }
            vnpParamsMap.put("vnp_IpAddr", fpt.aptech.server_be.util.VNPayUtil.getIpAddress(request));
            vnpParamsMap.put("vnp_OrderInfo", "Thanh toán cho sản phẩm ID: " + productId);

            log.info("📢 VNPay Request Parameters: {}", vnpParamsMap);

            // 5️⃣ Tạo URL thanh toán VNPay
            String queryUrl = fpt.aptech.server_be.util.VNPayUtil.getPaymentURL(vnpParamsMap, true);
            String hashData = fpt.aptech.server_be.util.VNPayUtil.getPaymentURL(vnpParamsMap, false);
            String vnpSecureHash = fpt.aptech.server_be.util.VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

            String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

            log.info("✅ Generated Payment URL: {}", paymentUrl);

            return PaymentDTO.VNPayResponse.builder()
                    .code("ok")
                    .message("success")
                    .paymentUrl(paymentUrl)
                    .build();
        } catch (NumberFormatException e) {
            log.error("🚨 Lỗi: Amount không đúng định dạng! Giá trị nhận được: {}", request.getParameter("amount"));
            throw new RuntimeException("Lỗi: Amount không hợp lệ! Vui lòng nhập số hợp lệ.");
        } catch (Exception e) {
            log.error("🚨 Lỗi khi tạo thanh toán VNPay: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi tạo thanh toán VNPay: " + e.getMessage());
        }
    }

}
