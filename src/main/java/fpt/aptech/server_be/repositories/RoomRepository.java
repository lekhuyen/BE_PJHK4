package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
