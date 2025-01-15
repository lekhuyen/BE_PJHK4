package fpt.aptech.server_be.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import fpt.aptech.server_be.enums.CountryCode;
import fpt.aptech.server_be.enums.InterestedIn;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
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

    @Column(name = "country_phone_code", nullable = true)
    String countryPhoneCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "interested_in", nullable = false)
    InterestedIn interestedIn;

    @NotBlank
    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "reply_message", nullable = true)
    String replyMessage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "rece_time", nullable = false, updatable = false)
    LocalDateTime receivetime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "reply_time", nullable = false)
    LocalDateTime replyTime;

    @PrePersist
    public void setDefaultValues() {
        if (this.replyMessage == null) {
            this.replyMessage = "";
        }
        if (this.receivetime == null) {
            this.receivetime = LocalDateTime.now();  // Set current timestamp
        }
        if (this.replyTime == null) {
            this.replyTime = LocalDateTime.now();  // Default for replyTime (could also be null if not needed)
        }
    }

    @PostLoad
    public void setCountryPhoneCode() {
        if (this.countryCode != null) {
            this.countryPhoneCode = countryCode.getCode(); // Get the phone code from the enum
        }
    }

    public String getFormattedCountryCode() {
        if (this.countryCode != null && this.countryPhoneCode != null) {
            return this.countryCode.name() + "(" + this.countryPhoneCode + ")";
        }
        return null;
    }
}
