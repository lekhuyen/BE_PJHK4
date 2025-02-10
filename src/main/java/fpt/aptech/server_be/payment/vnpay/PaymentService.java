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
        String amountStr = request.getParameter("amount");
        if (amountStr == null || amountStr.isEmpty()) {
            throw new IllegalArgumentException("Amount is required.");
        }
        long amount = Long.parseLong(amountStr) * 100L;

        String bankCode = request.getParameter("bankCode");
        if (bankCode != null && bankCode.isEmpty()) {
            bankCode = null;
        }

        Map<String, String> vnpParamsMap = new HashMap<>(vnPayConfig.getVNPayConfig());
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", fpt.aptech.server_be.util.VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_OrderInfo",productId);

        // Debug log để theo dõi request
        System.out.println("VNPAY Request Parameters: " + vnpParamsMap);

        // Xây dựng query URL
        String queryUrl = fpt.aptech.server_be.util.VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = fpt.aptech.server_be.util.VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = fpt.aptech.server_be.util.VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        System.out.println("Generated Payment URL: " + paymentUrl);

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }

}
