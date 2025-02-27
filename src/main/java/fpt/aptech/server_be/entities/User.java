package fpt.aptech.server_be.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    String phone;
//    String firstName;
//    String lastName;
    String email;
    LocalDate dob;
    String ciNumber;
    @Column(name = "address", columnDefinition = "NVARCHAR(255) COLLATE Vietnamese_CI_AS")
    String address;
    Boolean isActive = true;
    Boolean isVerify = false;
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

    @JsonManagedReference // ✅ Quản lý vòng lặp
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>(); // ✅ Danh sách địa chỉ của người dùng

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> follows = new ArrayList<>(); // ✅ Danh sách nhà đấu giá đã follow


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "citizen_id", referencedColumnName = "id")
    private UserCitizen citizen;

}
