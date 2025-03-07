package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.*;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.CommentRepository;
import fpt.aptech.server_be.repositories.FavoriteRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private CommentRepository commentRepository; // ✅ Inject CommentRepository

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Auction_ItemsRepository auctionItemsRepository;

    // ✅ Thêm sản phẩm vào danh sách yêu thích
    public boolean addFavoriteItem(String userId, String itemId) {
        if (favoriteRepository.existsByUserIdAndItemId(userId, itemId)) {
            return false; // Đã yêu thích rồi
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new RuntimeException("User không tồn tại!");

        Favorite favorite = new Favorite(user, null, itemId);
        favoriteRepository.save(favorite);
        return true;
    }

    // ✅ Follow Nhà Đấu Giá
    public boolean followAuctioneer(String userId, String auctioneerId) {
        if (favoriteRepository.existsByUserIdAndAuctioneerId(userId, auctioneerId)) {
            return false; // Đã follow rồi
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("🚨 User không tồn tại: " + userId);
        }

        Favorite favorite = new Favorite(user, auctioneerId, null);
        favoriteRepository.save(favorite);
        return true;
    }


    // ✅ Lấy danh sách sản phẩm yêu thích
    public List<String> getFavoriteItems(String userId) {
        return favoriteRepository.findByUserIdAndItemIdIsNotNull(userId)
                .stream()
                .map(Favorite::getItemId)
                .collect(Collectors.toList());
    }

    // ✅ Lấy danh sách Nhà Đấu Giá đã Follow
    public List<String> getFollowedAuctioneers(String userId) {
        return favoriteRepository.findByUserIdAndAuctioneerIdIsNotNull(userId)
                .stream()
                .map(Favorite::getAuctioneerId)
                .collect(Collectors.toList());
    }



    // hủy follow
    public boolean unfollowAuctioneer(String userId, String auctioneerId) {
        Favorite favorite = favoriteRepository.findByUserIdAndAuctioneerId(userId, auctioneerId);
        if (favorite == null) {
            return false; // Không follow nên không cần unfollow
        }

        favoriteRepository.delete(favorite);
        return true;
    }

    // hủy yêu thích
    public boolean removeFavoriteItem(String userId, String itemId) {
        Favorite favorite = favoriteRepository.findByUserIdAndItemId(userId, itemId);
        if (favorite == null) {
            return false; // Sản phẩm không có trong danh sách yêu thích
        }

        favoriteRepository.delete(favorite);
        return true;
    }

    public List<Map<String, Object>> getFollowedAuctioneersWithDetails(String userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdAndAuctioneerIdIsNotNull(userId);

        return favorites.stream().map(favorite -> {
            String auctioneerId = favorite.getAuctioneerId();

            // 🔥 Truy vấn thông tin từ bảng User
            User auctioneer = userRepository.findById(auctioneerId).orElse(null);

            Map<String, Object> auctioneerData = new HashMap<>();
            auctioneerData.put("id", auctioneerId);
            auctioneerData.put("name", auctioneer != null ? auctioneer.getName() : "Unknown");
            auctioneerData.put("phone", auctioneer != null ? auctioneer.getPhone() : "");
            auctioneerData.put("location", auctioneer != null ? auctioneer.getAddress() : "No location available");

            return auctioneerData;
        }).collect(Collectors.toList());
    }
    public List<Map<String, Object>> getFavoriteItemsWithDetails(String userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdAndItemIdIsNotNull(userId);

        return favorites.stream().map(favorite -> {
            int itemId = Integer.parseInt(favorite.getItemId());

            // 🔥 Truy vấn thông tin từ bảng Auction_Items
            Auction_Items auctionItem = auctionItemsRepository.findById(itemId).orElse(null);

            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", String.valueOf(itemId)); // ✅ Ép kiểu thành String
            itemData.put("name", auctionItem != null ? auctionItem.getItem_name() : "Unknown Product");
            itemData.put("imageUrl", (auctionItem != null && auctionItem.getImages() != null && !auctionItem.getImages().isEmpty())
                    ? auctionItem.getImages().get(0)
                    : "https://via.placeholder.com/100");
            itemData.put("startingPrice", String.valueOf(auctionItem.getStarting_price())); // ✅ Ép kiểu
            assert auctionItem != null;
            itemData.put("currentPrice", Optional.ofNullable(auctionItem.getBidding())
                    .map(Bidding::getPrice)
                    .orElse(0.0));
            itemData.put("description", auctionItem != null ? auctionItem.getDescription() : "No description available");

            return itemData;
        }).collect(Collectors.toList());
    }

    public int getFollowersCount(String auctioneerId) {
        return favoriteRepository.countFollowersById(auctioneerId);
    }



    // ✅ Thêm đánh giá (rating)
    // ✅ Thêm Comment vào Nhà Đấu Giá
    public boolean addComment(String userId, String auctioneerId, String content) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User không tồn tại!");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setAuctioneerId(auctioneerId);
        comment.setContent(content);
        commentRepository.save(comment);
        return true;
    }
    public List<Map<String, Object>> getComments(String auctioneerId) {
        List<Comment> comments = commentRepository.findByAuctioneerId(auctioneerId);

        return comments.stream().map(comment -> {
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("userName", comment.getUser().getName());
            commentData.put("content", comment.getContent());
            return commentData;
        }).collect(Collectors.toList());
    }

    // ✅ Kiểm tra xem người dùng đã follow người bán hay chưa
    public boolean isFollowing(String userId, String auctioneerId) {
        return favoriteRepository.existsByUserIdAndAuctioneerId(userId, auctioneerId);
    }

    public boolean isFavorite(String userId, String itemId) {
        boolean result = favoriteRepository.existsByUserIdAndItemId(userId, itemId);
        System.out.println("🛠 Kiểm tra yêu thích: UserId=" + userId + ", ItemId=" + itemId + ", Kết quả=" + result);
        return result;
    }


}