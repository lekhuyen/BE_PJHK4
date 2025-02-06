package fpt.aptech.server_be.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false, columnDefinition = "TEXT")
    String title;        // The title of the blog post
    @Column(nullable = false, columnDefinition = "TEXT")
    String  author;       // The author of the post
    @Column(nullable = false, columnDefinition = "TEXT")
    String content;      // The main content of the blog post
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date blogDate;
    @Column(nullable = true)
    String blogImage;

}
