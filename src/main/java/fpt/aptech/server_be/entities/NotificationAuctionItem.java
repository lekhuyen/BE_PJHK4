package fpt.aptech.server_be.entities;

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
@Table(name = "notificationAuctionItem")
public class NotificationAuctionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    boolean creatorIsRead = false;
    boolean adminIsRead = false;

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    User creator;

    int auctionItemId;

    Date createdAt = new Date();
    Date updatedAt = new Date();

    String type;
//type =
//    P -> thong bao user tao 1 sp moi cho admin bt
//    T -> thong bao admin duyet cho user bt
}
