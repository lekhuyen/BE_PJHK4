package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
}
