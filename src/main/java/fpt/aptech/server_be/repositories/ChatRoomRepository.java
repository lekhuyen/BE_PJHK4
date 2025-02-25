package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.acAuctionItem = :acAuctionItem AND cr.buyer = :buyer AND cr.seller = :seller")
    ChatRoom findByAcAuctionItemAndBuyerAndSeller(
            @Param("acAuctionItem") Auction_Items acAuctionItem,
            @Param("buyer") User buyer,
            @Param("seller") User seller);

//    @Query("SELECT cr FROM ChatRoom cr WHERE cr.buyer = :user or cr.seller = :user ORDER BY cr.date DESC")
    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN FETCH cr.message cm WHERE cr.buyer = :user OR cr.seller = :user ORDER BY cm.timestamp desc ")
    List<ChatRoom> findAllChatByBuyer(@Param("user") User user);
}
