package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // ✅ API Yêu Thích Sản Phẩm
    @PostMapping("/add-favorite-item")
    public ResponseEntity<String> addFavoriteItem(@RequestParam String userId, @RequestParam String itemId) {
        boolean isAdded = favoriteService.addFavoriteItem(userId, itemId);
        return ResponseEntity.ok(isAdded ? "Added to favorites!" : "Already in favorites");
    }

    // ✅ API Follow Nhà Đấu Giá
    // ✅ API Follow Nhà Đấu Giá với xác thực
    @PostMapping("/follow-auctioneer")
    public ResponseEntity<String> followAuctioneer(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String auctioneerId = payload.get("auctioneerId");

        System.out.println("📥 Nhận yêu cầu Follow từ userId: " + userId + ", auctioneerId: " + auctioneerId);
        System.out.println("🔑 Token: " + token);

        if (userId == null || auctioneerId == null) {
            return ResponseEntity.badRequest().body("Missing userId or auctioneerId");
        }

        boolean isFollowed = favoriteService.followAuctioneer(userId, auctioneerId);
        return ResponseEntity.ok(isFollowed ? "Followed successfully!" : "Already followed");
    }




    // ✅ API Lấy danh sách sản phẩm yêu thích
//    @GetMapping("/get-favorite-items/{userId}")
//    public ResponseEntity<List<String>> getFavoriteItems(@PathVariable String userId) {
//        List<String> favoriteItems = favoriteService.getFavoriteItems(userId);
//        return ResponseEntity.ok(favoriteItems);
//    }
// ✅ API Lấy danh sách sản phẩm yêu thích với thông tin đầy đủ
    @GetMapping("/get-favorite-items/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getFavoriteItems(@PathVariable String userId) {
        List<Map<String, Object>> favoriteItems = favoriteService.getFavoriteItemsWithDetails(userId);
        return ResponseEntity.ok(favoriteItems);
    }

    // ✅ API Lấy danh sách Nhà Đấu Giá đã Follow
//    @GetMapping("/get-followed-auctioneers/{userId}")
//    public ResponseEntity<List<String>> getFollowedAuctioneers(@PathVariable String userId) {
//        List<String> followedAuctioneers = favoriteService.getFollowedAuctioneers(userId);
//        return ResponseEntity.ok(followedAuctioneers);
//    }
    // ✅ API Lấy danh sách Nhà Đấu Giá đã Follow với thông tin đầy đủ
    @GetMapping("/get-followed-auctioneers/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getFollowedAuctioneers(@PathVariable String userId) {
        List<Map<String, Object>> followedAuctioneers = favoriteService.getFollowedAuctioneersWithDetails(userId);
        return ResponseEntity.ok(followedAuctioneers);
    }



    // ✅ API Hủy Follow Nhà Đấu Giá
    @DeleteMapping("/unfollow-auctioneer")
    public ResponseEntity<String> unfollowAuctioneer(
            @RequestParam String userId,
            @RequestParam String auctioneerId) {

        System.out.println("📥 Nhận yêu cầu Unfollow từ userId: " + userId + ", auctioneerId: " + auctioneerId);

        boolean isUnfollowed = favoriteService.unfollowAuctioneer(userId, auctioneerId);
        return ResponseEntity.ok(isUnfollowed ? "Unfollowed successfully!" : "Not following this auctioneer");
    }
    // ✅ API Hủy Yêu Thích Sản Phẩm
    @DeleteMapping("/remove-favorite-item")
    public ResponseEntity<String> removeFavoriteItem(
            @RequestParam String userId,
            @RequestParam String itemId) {

        System.out.println("📥 Nhận yêu cầu Hủy Yêu Thích từ userId: " + userId + ", itemId: " + itemId);

        boolean isRemoved = favoriteService.removeFavoriteItem(userId, itemId);
        return ResponseEntity.ok(isRemoved ? "Removed from favorites!" : "Item not in favorites");
    }
    // API để lấy số người theo dõi nhà đấu giá
    @GetMapping("/get-followers-count/{auctioneerId}")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable String auctioneerId) {
        int followersCount = favoriteService.getFollowersCount(auctioneerId);
        return ResponseEntity.ok(followersCount);
    }
    // ✅ API: Thêm đánh giá (rating)
    @PostMapping("/add-comment")
    public ResponseEntity<String> addComment(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String auctioneerId = payload.get("auctioneerId");
        String content = payload.get("content");

        boolean isAdded = favoriteService.addComment(userId, auctioneerId, content);
        return ResponseEntity.ok(isAdded ? "Comment added successfully!" : "Failed to add comment");
    }

//    @GetMapping("/get-comments/{auctioneerId}")
//    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable String auctioneerId) {
//        List<Map<String, Object>> comments = favoriteService.getComments(auctioneerId);
//        return ResponseEntity.ok(comments);
//    }

    @GetMapping("/get-comments/{auctioneerId}")
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable String auctioneerId) {
        List<Map<String, Object>> comments = favoriteService.getComments(auctioneerId);
        return ResponseEntity.ok(comments);
    }
    // ✅ API kiểm tra người dùng có follow người bán không
    @GetMapping("/is-following")
    public ResponseEntity<Boolean> isFollowing(
            @RequestParam String userId,
            @RequestParam String auctioneerId) {

        boolean isFollowing = favoriteService.isFollowing(userId, auctioneerId);
        return ResponseEntity.ok(isFollowing);
    }
    // ✅ API: Kiểm tra sản phẩm đã được yêu thích chưa
    @GetMapping("/is-favorite")
    public ResponseEntity<Boolean> isFavorite(
            @RequestParam String userId,
            @RequestParam String itemId) {

        boolean isFavorite = favoriteService.isFavorite(userId, itemId);
        System.out.println("🔎 Kiểm tra yêu thích: UserId = " + userId + ", ItemId = " + itemId + ", Kết quả = " + isFavorite);

        return ResponseEntity.ok(isFavorite);
    }
}