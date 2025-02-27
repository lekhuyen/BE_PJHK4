package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    // ✅ Kiểm tra nếu User đã yêu thích sản phẩm này
    boolean existsByUserIdAndItemId(String userId, String itemId);

    // ✅ Kiểm tra nếu User đã follow Nhà Đấu Giá này
    boolean existsByUserIdAndAuctioneerId(String userId, String auctioneerId);

    // ✅ Lấy danh sách sản phẩm yêu thích của người dùng
    List<Favorite> findByUserIdAndItemIdIsNotNull(String userId);

    // ✅ Lấy danh sách Nhà Đấu Giá đã Follow của người dùng
    List<Favorite> findByUserIdAndAuctioneerIdIsNotNull(String userId);

    // ✅ Tìm bản ghi Follow để hủy
    Favorite findByUserIdAndAuctioneerId(String userId, String auctioneerId);

    // ✅ Tìm bản ghi Yêu Thích để hủy
    Favorite findByUserIdAndItemId(String userId, String itemId);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.auctioneerId = :auctioneerId")
    int countFollowersById(@Param("auctioneerId") String auctioneerId);



}
