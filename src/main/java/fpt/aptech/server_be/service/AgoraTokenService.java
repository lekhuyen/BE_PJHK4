package fpt.aptech.server_be.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class AgoraTokenService {
    @Value("${agora.appId}")
    private String appId;

    @Value("${agora.appCertificate}")
    private String appCertificate;

    private static final String APP_ID = "9b1d7859b01d4f7fbb071c2e16681b0f";
    private static final String APP_CERTIFICATE = "b3ac1b87350c4b3b99bfe2dbb7663550";

    public String generateToken(String channelName, String uid) throws Exception {
        // Tạo URL API của Agora
        String apiUrl = "https://api.agora.io/v1/apps/" + APP_ID + "/tokens";
        URL url = new URL(apiUrl);

        // Tạo kết nối
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic " + APP_CERTIFICATE);
        connection.setDoOutput(true);

        // Tạo payload JSON
        String jsonInputString = "{"
                + "\"channelName\": \"" + channelName + "\","
                + "\"uid\": \"" + uid + "\","
                + "\"role\": \"publisher\","
                + "\"expire\": 3600"
                + "}";

        // Gửi request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Đọc phản hồi từ server
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return "Token generated successfully";
        } else {
            return "Failed to generate token, HTTP response code: " + responseCode;
        }
    }
}
