package fpt.aptech.server_be.controller;

import io.media.media.RtcTokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/agora")
public class AgoraController {
    private final String appId = "c37acd12fafb4d4494fd8516342a7a10";
    private final String appCertificate = "2b1e344b1d7242f7b42377ace20f54e0";

    @GetMapping("/token")
    public String generateToken(
            @RequestParam String channelName,
            @RequestParam String uid
    ) {
        int expirationTimeInSeconds = 36000; // Token expires in 1 hour
        int currentTimestamp = (int) (System.currentTimeMillis() / 1000); // Get current Unix timestamp in seconds
        int privilegeExpiredTs = currentTimestamp + expirationTimeInSeconds; // Expiration timestamp
        RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
        String token = tokenBuilder.buildTokenWithUid(
                appId,
                appCertificate,
                channelName,
                Integer.parseInt(uid),
                RtcTokenBuilder.Role.Role_Publisher,
                privilegeExpiredTs
        );
        log.info(String.valueOf(privilegeExpiredTs));
        return token;
    }
}
