package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.CategoryRequest;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.entities.RoomVideoCall;
import fpt.aptech.server_be.service.RoomVideoCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-video-call")
public class RoomVideoCallController {

    @Autowired
    private RoomVideoCallService roomVideoCallService;

    @PostMapping
    ApiResponse<RoomVideoCall> create(@RequestBody RoomVideoCall request) {
        return  ApiResponse.<RoomVideoCall>builder()
                .message("Create RoomVideoCall successful")
                .result(roomVideoCallService.createRoomVideoCall(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoomVideoCall>> getAll() {
        return  ApiResponse.<List<RoomVideoCall>>builder()
                .message("Get RoomVideoCall successful")
                .result(roomVideoCallService.getAllRoomVideoCalls())
                .build();
    }

    @DeleteMapping("/{roomId}")
    ApiResponse<Boolean> delete(@RequestBody int roomId) {
        return  ApiResponse.<Boolean>builder()
                .message("Delete RoomVideoCall successful")
                .result(roomVideoCallService.deleteRoomVideo(roomId))
                .build();
    }
}
