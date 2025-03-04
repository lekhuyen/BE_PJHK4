package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.ReviewItem;
import fpt.aptech.server_be.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewItemRepository extends JpaRepository<ReviewItem, Integer> {

    // Custom query to find ReviewItem by auctionItemId
    @Query("SELECT r FROM ReviewItem r WHERE r.auctionItem.item_id = :auctionItemId")
    List<ReviewItem> findByAuctionItemId(int auctionItemId);
}


