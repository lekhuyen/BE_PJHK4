package fpt.aptech.server_be.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_citizen")
public class UserCitizen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String ciCode;
    String fullName;
    String address;
    String birthDate;
    String startDate;

    @OneToOne(mappedBy = "citizen")
    private User user;
}
