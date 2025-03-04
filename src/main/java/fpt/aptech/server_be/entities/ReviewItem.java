package fpt.aptech.server_be.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "review_items")
public class ReviewItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id; // Unique ID for the review

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user; // The user who wrote the review

    @ManyToOne
    @JoinColumn(name = "auction_item_id", referencedColumnName = "item_id")
    Auction_Items auctionItem; // The item being reviewed

    int rating; // Rating from 1 to 5

    boolean markAsRated = false;

    @Column(length = 1000)
    String comment; // Review text or comment

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date createdAt = new Date(); // Timestamp when the review was created

    boolean isVerified; // Flag to indicate if the review is from a verified buyer

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }


}


