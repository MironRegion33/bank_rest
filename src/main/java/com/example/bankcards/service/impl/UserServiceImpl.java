package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CreateUserDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidUserDataException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("поиск всех пользователей");
        return userRepository.findAll()
                .stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole()))
                .toList();
    }

    @Override
    public UserDto getUserById(long id) {
        log.debug("поиск пользователя с id: {}", id);
        var u = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        log.debug("найден пользователь с id: {}", id);
        return new UserDto(u.getId(), u.getUsername(), u.getRole());
    }

    @Override
    public UserDto createUser(CreateUserDto dto) {
        log.debug("Админ создаёт пользователя: {}", dto.username());
        if (dto.username() == null || dto.password() == null || dto.role() == null) {
            log.error("введены не корректные данные");
            throw new InvalidUserDataException("Все поля должны быть заполнены");
        }
        var u = new User();
        if (userRepository.existsByUsername(dto.username())) {
            log.error("пользователь уже существует: {}", dto.username());
            throw new UsernameAlreadyExistsException("пользователь с таким именем уже существует");
        }
        u.setUsername(dto.username());
        u.setPassword(passwordEncoder.encode(dto.password()));
        u.setRole(dto.role());
        var user = userRepository.save(u);
        log.debug("Админ создал пользователя: {}", user.getUsername());
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public UserDto updateUser(long userId, CreateUserDto dto) {
        log.debug("Админ обновляет пользователя: {}", userId);
        var u = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (dto.username() != null && !dto.username().equals(u.getUsername())) {
            if (userRepository.existsByUsername(dto.username())) {
                throw new UsernameAlreadyExistsException("пользователь с таким именем уже существует");
            }
            u.setUsername(dto.username());
        }
        if (dto.password() != null) {
            u.setPassword(passwordEncoder.encode(dto.password()));
        }
        if (dto.role() != null) {
            u.setRole(dto.role());
        }
        var savedUser = userRepository.save(u);
        log.debug("Админ обновил пользователя: {}", userId);
        return new UserDto(savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
    }

    @Override
    public void deleteUser(long userId) {
        log.debug("Админ удаляет пользователя: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(userId);
        log.debug("Админ удалил пользователя: {}", userId);
    }
}
