package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.entities.UserCitizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCitizenRepository extends JpaRepository<UserCitizen, Integer> {
    @Query("SELECT a FROM UserCitizen a WHERE a.ciCode = :ciCode")
    UserCitizen findByAndCiCode(@Param("ciCode") String ciCode);

    @Query("SELECT a FROM UserCitizen a WHERE a.user = :user")
    UserCitizen findByAndOrderByUser(@Param("user") User user);
}
