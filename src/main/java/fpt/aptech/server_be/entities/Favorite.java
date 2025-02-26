package fpt.aptech.server_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorites")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;



    @Column(nullable = true)
    private String auctioneerId; // Lưu ID Nhà Đấu Giá nếu Follow

    @Column(nullable = true)
    private String itemId; // Lưu ID Sản Phẩm nếu yêu thích

    public Favorite(User user, String auctioneerId, String itemId) {
        this.user = user;
        this.auctioneerId = auctioneerId;
        this.itemId = itemId;
    }
}
