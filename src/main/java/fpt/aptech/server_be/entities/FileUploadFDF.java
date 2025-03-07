package fpt.aptech.server_be.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Base64;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "file_uploads")
public class FileUploadFDF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String fileName;
    String fileType;

    @Lob
    @JsonIgnore // This prevents base64 fileData from cluttering the response
    byte[] fileData;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt = new Date();

    // Relationship with Auction_Items
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    Auction_Items auctionItem;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

}