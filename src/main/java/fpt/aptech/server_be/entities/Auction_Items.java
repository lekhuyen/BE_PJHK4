package fpt.aptech.server_be.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    String images;
    Double starting_price;
    LocalDate start_date;
    LocalDate end_date;
    String bid_step;
    String status;


    @ManyToOne
    @JoinColumn(name = "seller_Id", referencedColumnName = "id")
    User user;

    // Many-to-one relationship with Category
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    Category category;
}
