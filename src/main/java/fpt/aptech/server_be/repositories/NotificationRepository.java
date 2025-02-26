package fpt.aptech.server_be.repositories;


import fpt.aptech.server_be.entities.Notification;
import fpt.aptech.server_be.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    @Query("SELECT b FROM Notification b WHERE b.seller = :user OR b.buyer = :user order by b.date DESC ")
    List<Notification> findNotificationBySellerAndBuyer(@Param("user") User user);

    @Query("SELECT b FROM Notification b WHERE b.id = :id and (b.buyer = :user or b.seller = : user)")
    Notification findNotificationById(@PathVariable("id") int id, @PathVariable("user") User user);
}
