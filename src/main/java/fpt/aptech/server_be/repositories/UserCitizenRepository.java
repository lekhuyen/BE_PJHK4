package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.UserCitizen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCitizenRepository extends JpaRepository<UserCitizen, Integer> {
}
