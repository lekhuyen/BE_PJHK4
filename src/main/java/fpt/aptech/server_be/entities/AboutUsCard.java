package fpt.aptech.server_be.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "aboutuscard")
public class AboutUsCard {

    @Id
    @Column(name = "title", nullable = false)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @Column(nullable = true)
    String aboutCardImage;
}
