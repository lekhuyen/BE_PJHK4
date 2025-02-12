package fpt.aptech.server_be.payment.vnpay;

import lombok.Builder;

public abstract class PaymentDTO {
    @Builder
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
        public  String urlok;
    }
}
