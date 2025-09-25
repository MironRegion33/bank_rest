package com.example.bankcards.security;

import com.example.bankcards.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().name())
                .build();
    }
}
