package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.RoomVideoCall;
import fpt.aptech.server_be.repositories.RoomVideoCallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomVideoCallService {
    @Autowired
    private RoomVideoCallRepository roomVideoCallRepository;

    public RoomVideoCall createRoomVideoCall(RoomVideoCall roomVideoCall) {
        RoomVideoCall newRoomVideoCall = new RoomVideoCall();
        newRoomVideoCall.setUserId(roomVideoCall.getUserId());
        newRoomVideoCall.setUserName(roomVideoCall.getUserName());
        newRoomVideoCall.setRole("streamer");

        return roomVideoCallRepository.save(newRoomVideoCall);
    }

    public boolean deleteRoomVideo(int id) {
        RoomVideoCall roomVideoCall = roomVideoCallRepository.findById(id).orElse(null);
        if(roomVideoCall != null) {
            roomVideoCallRepository.delete(roomVideoCall);
            return true;
        }
        return false;
    }

    public List<RoomVideoCall> getAllRoomVideoCalls() {
        return roomVideoCallRepository.findAll();
    }
}
