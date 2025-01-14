package fpt.aptech.server_be.configuration;


import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.enums.Role;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {


    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByEmail("admin@gmail.com").isEmpty()){

                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());

                User user = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
//                        .roles(roles.add())
                        .createdAt(new Date())
                        .updatedAt(new Date())
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created password admin, ");
            }
        };
    }

}
