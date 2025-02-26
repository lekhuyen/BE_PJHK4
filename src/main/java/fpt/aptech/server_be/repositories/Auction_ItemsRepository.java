package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.dto.response.Auction_ItemsResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import fpt.aptech.server_be.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Auction_ItemsRepository extends JpaRepository<Auction_Items, Integer> {
    @Query("SELECT a FROM Auction_Items a WHERE a.status = true AND a.category = :category")
    List<Auction_Items> getAuction_ItemsByCategoryId(@Param("category") Category category);

    @Query("SELECT a FROM Auction_Items a WHERE a.status = true AND a.category = :category")
    Page<Auction_Items> findByCategory(@Param("category") Category category, Pageable pageable);


    @Query("select a from Auction_Items a where a.item_name like %:name% and a.status = true")
    Page<Auction_Items> findAllByItem_name(@Param("name") String name, Pageable pageable);


    @Query("select a from Auction_Items a where a.status = true")
    Page<Auction_Items> findAllS(Pageable pageable);

    @Query("select a from Auction_Items a where a.user= :user")
    List<Auction_Items> findAllByUser(@Param("user") User user);

//    @Query("select a from Auction_Items a where a.user= :user and a.status = true")
//    List<Auction_Items> findAllByCreator(@Param("user") User user);

    @Query("select a from Auction_Items a where a.buyer= :buyer")
    List<Auction_Items> findAllBuyer(@Param("buyer") User buyer);

    // Lấy danh sách sản phẩm đã bán (Featured)
    List<Auction_Items> findByIsSellTrue();

    // Lấy danh sách sản phẩm chưa bán (Upcoming)
    List<Auction_Items> findByIsSellFalse();

    @Query("select a from Auction_Items a where a.isSoldout = false ")
    List<Auction_Items> findAllProductBidding();


    @Query("SELECT a FROM Auction_Items a WHERE a.user.id = :userId AND a.isPaid = true")
    List<Auction_Items> findPaidItemsByUserId(@Param("userId") String userId);

    @Query("SELECT a FROM Auction_Items a WHERE a.user.id = :userId AND a.isPaid = false")
    List<Auction_Items> findUnpaidItemsByUserId(@Param("userId") String userId);

    @Query("SELECT a FROM Auction_Items a WHERE a.buyer.id = :userId AND a.isPaid = true")
    List<Auction_Items> findWonItemsByUserId(@Param("userId") String userId);


    @Query("SELECT a FROM Auction_Items a WHERE a.user.id = :userId AND a.isSell = false ")
    List<Auction_Items> findByUserIdAndIsSellFalse(@Param("userId") String userId);

    @Query("SELECT a FROM Auction_Items a WHERE a.user.id = :userId AND a.isSell = true")
    List<Auction_Items> findByUserIdAndIsSellTrue(@Param("userId") String userId);


    @Query("SELECT a FROM Auction_Items a WHERE a.buyer.id = :userId AND a.isPaid = false")
    List<Auction_Items> findUnWonItemsByUserId(@Param("userId") String userId);

}
