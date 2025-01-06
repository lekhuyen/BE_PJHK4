package fpt.aptech.server_be.entities;

import fpt.aptech.server_be.enums.CountryCode;
import fpt.aptech.server_be.enums.InterestedIn;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NotBlank
    @Column(name = "name", nullable = false)
    String name;

    @NotBlank
    @Column(name = "email", nullable = false)
    String email;

    @NotBlank
    @Column(name = "phone", nullable = false)
    String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code", nullable = false)
    CountryCode countryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "interested_in", nullable = false)
    InterestedIn interestedIn;

    @NotBlank
    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "reply_message", nullable = true)
    String replyMessage;

    @PrePersist
    public void setDefaultReplyMessage() {
        if (this.replyMessage == null) {
            this.replyMessage = "";
        }
    }


}
