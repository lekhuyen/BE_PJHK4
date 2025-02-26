package fpt.aptech.server_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference // ✅ Ngăn vòng lặp vô tận

    private User user; // ✅ Một người dùng có thể có nhiều địa chỉ

    private String address;
    private String zip;
    private String phone;


    // ✅ Getters & Setters
}
