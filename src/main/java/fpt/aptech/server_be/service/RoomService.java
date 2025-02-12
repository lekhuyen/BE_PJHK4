package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.Room;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    // Tạo phòng mới
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }


    public Room addUserToRoom(int roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        return roomRepository.save(room);
    }


}
