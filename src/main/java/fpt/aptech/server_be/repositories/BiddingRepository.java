package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Bidding;
import fpt.aptech.server_be.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BiddingRepository extends JpaRepository<Bidding, Integer> {
    @Query("SELECT b FROM Bidding b WHERE b.user = :user AND b.auction_Items = :auctionItems")
    Bidding findByUserAndAuction_Items(@Param("user") User user, @Param("auctionItems") Auction_Items auctionItems);

    @Query("SELECT b FROM Bidding b WHERE b.auction_Items = :auctionItems")
    Bidding findBiddingByAuction_Items(@Param("auctionItems") Auction_Items auctionItems);

    @Query("SELECT b.auction_Items.item_name, COUNT(b.id), AVG(b.price) FROM Bidding b GROUP BY b.auction_Items.item_name")
    List<Object[]> getBiddingStatistics();

    @Query("SELECT b.auction_Items.item_name, SUM(b.price), SUM(b.price) * 0.02 " +
            "FROM Bidding b WHERE b.auction_Items.isPaid = true GROUP BY b.auction_Items.item_name")
    List<Object[]> getTotalBiddingAmount();

    @Query("SELECT SUM(b.price) * 0.02 FROM Bidding b WHERE b.auction_Items.isPaid = true")
    Double getTotalAdminEarnings();

}
