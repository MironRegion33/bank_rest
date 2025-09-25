package com.example.bankcards.config;


import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      CardRepository cardRepository,
                                      BCryptPasswordEncoder passwordEncoder) {
        return args -> {

            userRepository.findByUsername("admin").orElseGet(() -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("adminpass"));
                admin.setRole(Role.ADMIN);
                User savedAdmin = userRepository.save(admin);

                Card adminCard = new Card();
                adminCard.setOwner(savedAdmin);
                adminCard.setCardNumber("4111111111110001");
                adminCard.setExpirationDate(LocalDate.of(2030, 12, 31));
                adminCard.setBalance(new BigDecimal("10000.00"));
                adminCard.setStatus(CardStatus.ACTIVE);
                cardRepository.save(adminCard);

                return savedAdmin;
            });

            userRepository.findByUsername("user").orElseGet(() -> {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("userpass"));
                user.setRole(Role.USER);
                User savedUser = userRepository.save(user);

                Card userCard = new Card();
                userCard.setOwner(savedUser);
                userCard.setCardNumber("5555444433331111");
                userCard.setExpirationDate(LocalDate.of(2028, 6, 30));
                userCard.setBalance(new BigDecimal("1500.50"));
                userCard.setStatus(CardStatus.ACTIVE);
                cardRepository.save(userCard);

                return savedUser;
            });
        };
    }
}
