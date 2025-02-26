package fpt.aptech.server_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String userId;  // ✅ ID của người dùng
    private String address; // ✅ Địa chỉ đầy đủ
    private String zip;     // ✅ Mã bưu điện (ZIP code)
    private String phone;
}