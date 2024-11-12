package fpt.aptech.server_be.repositories;


import fpt.aptech.server_be.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
