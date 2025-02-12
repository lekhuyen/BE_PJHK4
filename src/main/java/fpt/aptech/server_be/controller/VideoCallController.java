package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.TokenRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/video-call")
public class VideoCallController {

    // Cấu hình thông tin Agora
    @Value("${agora.appId}")
    private String appId;

    @Value("${agora.appCertificate}")
    private String appCertificate;

    // URL của Agora Token Service (REST API)
    private static final String AGORA_API_URL = "https://agora.io/api/v1/token";

    // Tạo token cho người dùng tham gia vào phòng
    @PostMapping("/generate-token")
    public String generateToken(@RequestBody TokenRequest request) {
        return getAgoraToken(request.getChannelName(), request.getUid());
    }

    private String getAgoraToken(String channelName, int uid) {
        String token = "";  // Token sẽ được tạo ở đây

        // Tạo thông tin gửi đi (trong trường hợp bạn tự xây dựng mã nguồn tạo token)
        String url = AGORA_API_URL + "?appId=" + appId + "&appCertificate=" + appCertificate +
                "&channelName=" + channelName + "&uid=" + uid + "&role=1"; // role=1: publisher (người phát video)

        // Gửi yêu cầu tới Agora API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // Nhận phản hồi từ Agora API
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            token = response.getBody();
        }

        return token;
    }
}
