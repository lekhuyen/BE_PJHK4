package fpt.aptech.server_be.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Auction_Items")
public class Auction_Items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int item_id;
    String item_name;
    String description;
    Double starting_price;
    Double current_price;
    LocalDate start_date;
    LocalDate end_date;
    String bid_step;

    @OneToOne(mappedBy = "auction_Items", cascade = CascadeType.ALL)
    Bidding bidding;

    boolean isSell;
    boolean status ;
    boolean isSoldout;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    boolean isPaid = false;
    double width = 12.1;
    double height = 12.1;

    Date createdAt = new Date();
    Date updatedAt = new Date();


    //who bought it?
    @OneToOne(cascade = CascadeType.ALL)
    User solTo;

    @ElementCollection
    @CollectionTable(name = "auction_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "images_name")
    List<String> images;

//    its creator
    @ManyToOne
    @JoinColumn(name = "seller_Id", referencedColumnName = "id")
    User user;

    @ManyToOne
    @JoinColumn(name = "buyer_Id", referencedColumnName = "id")
    User buyer;

    // Many-to-one relationship with Category
    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "category_id")
    Category category;

    @OneToMany(mappedBy = "acAuctionItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms;

}
