package com.chargersolde.config;

import com.chargersolde.entity.Role;
import com.chargersolde.entity.User;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {

        String email = "admin@chargersolde.com";

        if (!userRepository.existsByEmail(email)) {

            User admin = User.builder()
                    .nom("Hatem")
                    .prenom("Messoudi")
                    .email(email)
                    .numTel("20230214")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .role(Role.ROLE_ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);

            log.info("=================================================");
            log.info("Compte ADMIN créé avec succès !");
            log.info("Email    : {}", email);
            log.info("Password : Admin@1234");
            log.info("=================================================");
        } else {
            log.info("Compte admin déjà existant, initialisation ignorée.");
        }
    }
}
