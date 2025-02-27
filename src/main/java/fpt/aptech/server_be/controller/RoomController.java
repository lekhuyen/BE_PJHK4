package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.entities.Room;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.service.AgoraTokenService;
import fpt.aptech.server_be.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video-call")
public class RoomController {
    @Autowired
    private AgoraTokenService agoraTokenService;

    @GetMapping("/join")
    public Map<String, String> joinRoom(@RequestParam String channelName, @RequestParam String uid) throws Exception {
        String token = agoraTokenService.generateToken(channelName, uid);

        Map<String, String> response = new HashMap<>();
        response.put("channelName", channelName);
        response.put("token", token);

        return response;
    }

}
