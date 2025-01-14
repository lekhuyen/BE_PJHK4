package fpt.aptech.server_be.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "bidding3")
public class Bidding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    double price;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    Auction_Items auction_Items;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @OneToMany(mappedBy = "bidding", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notification> notifications;
}
