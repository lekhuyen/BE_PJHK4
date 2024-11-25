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
@Table(name = "Category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int category_id;
    String categoryName;
    String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    List<Auction_Items> auctionItems;


}
