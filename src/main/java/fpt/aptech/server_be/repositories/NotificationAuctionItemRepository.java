package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.NotificationAuctionItem;
import fpt.aptech.server_be.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationAuctionItemRepository extends JpaRepository<NotificationAuctionItem, Integer> {
    @Query("select n from NotificationAuctionItem n where n.type='P' ORDER BY n.createdAt DESC")
    public List<NotificationAuctionItem> findByTypeP();


    @Query("select n from NotificationAuctionItem n where n.creator =:user ORDER BY n.createdAt DESC")
    public List<NotificationAuctionItem> findByCreator(@Param("user") User user);
}
