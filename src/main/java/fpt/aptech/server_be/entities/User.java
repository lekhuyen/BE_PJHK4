package fpt.aptech.server_be.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    String password;
//    String firstName;
//    String lastName;
    String email;
    LocalDate dob;
    String ciNumber;
    String address;
    Boolean isActive = true;
    Date createdAt = new Date();
    Date updatedAt = new Date();

//    @OneToOne(mappedBy = "user")
//    Bidding bidding;


    @OneToMany(mappedBy = "user")
    List<Bidding> biddings;

    @ManyToMany
    Set<Role> roles;

// One-to-many relationship with Auction_Items
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Auction_Items> auctionItems;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    List<Auction_Items> auctionItemsBuyer;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    List<Notification> sellerNotifications; // Notifications where the user is the seller

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    List<Notification> buyerNotifications;  // Notifications where the user is the buyer

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    List<NotificationAuctionItem> creatorNotifications;  // Notifications where the user is the buyer

}
