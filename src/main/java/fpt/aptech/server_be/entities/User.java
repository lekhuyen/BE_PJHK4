package fpt.aptech.server_be.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    String firstName;
    String lastName;
    String email;
    LocalDate dob;
    String ciNumber;
    String address;

    @ManyToMany
    Set<Role> roles;

// One-to-many relationship with Auction_Items
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Auction_Items> auctionItems;
}
