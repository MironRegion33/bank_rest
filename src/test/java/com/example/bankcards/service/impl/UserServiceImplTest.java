package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CreateUserDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.InvalidUserDataException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void getAllUsers() {
        var user1 = new User();
        user1.setId(1L);
        user1.setUsername("username1");
        user1.setPassword("password1");
        var user2 = new User();
        user2.setId(2L);
        user2.setUsername("username2");
        user2.setPassword("password2");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> users = userService.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).username()).isEqualTo("username1");
        assertThat(users.get(1).username()).isEqualTo("username2");
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById() {
        var user = new User();
        user.setId(10L);
        user.setUsername("Alina");
        user.setRole(Role.USER);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        UserDto dto = userService.getUserById(10L);
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.username()).isEqualTo("Alina");
        assertThat(dto.role()).isEqualTo(Role.USER);
        verify(userRepository).findById(10L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByIdWithUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(100L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
        verify(userRepository).findById(100L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUser() {
        var dto = new CreateUserDto("Anton", "password", Role.USER);
        when(userRepository.existsByUsername("Anton")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });
        UserDto result = userService.createUser(dto);
        assertThat(result.username()).isEqualTo("Anton");
        assertThat(result.role()).isEqualTo(Role.USER);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void createUserWithDuplicateUsername() {
        var dto = new CreateUserDto("Anton", "password", Role.USER);
        when(userRepository.existsByUsername("Anton")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("пользователь с таким именем уже существует");
        verify(userRepository).existsByUsername("Anton");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserWithInvalidUsername() {
        var dto = new CreateUserDto(null, "password", Role.USER);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessageContaining("Все поля должны быть заполнены");
        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser() {
        var user = new User();
        user.setId(10L);
        user.setUsername("Anton");
        user.setRole(Role.USER);
        user.setPassword("password");

        var dto = new CreateUserDto("Alina", "newpass", Role.ADMIN);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("Alina")).thenReturn(false);
        when(passwordEncoder.encode("newpass")).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        UserDto result = userService.updateUser(10L, dto);
        assertThat(result.username()).isEqualTo("Alina");
        assertThat(result.role()).isEqualTo(Role.ADMIN);
        assertThat(result.id()).isEqualTo(10L);
        verify(userRepository).findById(10L);
        verify(userRepository).existsByUsername("Alina");
        verify(passwordEncoder).encode("newpass");
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserWithInvalidUsername() {
        var user = new User();
        user.setId(10L);
        user.setUsername("Anton");
        user.setRole(Role.USER);
        user.setPassword("password");

        var dto = new CreateUserDto("Alina", "newpass", Role.ADMIN);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("Alina")).thenReturn(true);
        assertThatThrownBy(() -> userService.updateUser(10L, dto))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("пользователь с таким именем уже существует");
        verify(userRepository).findById(10L);
        verify(userRepository).existsByUsername("Alina");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUserWithOnlyRole() {
        var user = new User();
        user.setId(10L);
        user.setUsername("Anton");
        user.setRole(Role.USER);
        user.setPassword("password");
        var dto = new CreateUserDto(null, null, Role.ADMIN);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        UserDto result = userService.updateUser(10L, dto);
        assertThat(result.username()).isEqualTo("Anton");
        assertThat(result.role()).isEqualTo(Role.ADMIN);
        assertThat(result.id()).isEqualTo(10L);
        assertThat(user.getUsername()).isEqualTo("Anton");
        assertThat(user.getPassword()).isEqualTo("password");
        verify(userRepository).findById(10L);
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserWithUserNotFound() {
        var dto = new CreateUserDto("Anton", "password", Role.USER);
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(10L, dto))
        .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
        verify(userRepository).findById(10L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(10L)).thenReturn(true);
        userService.deleteUser(10L);
        verify(userRepository).existsById(10L);
        verify(userRepository).deleteById(10L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserWithUserNotFound() {
        when(userRepository.existsById(10L)).thenReturn(false);
        assertThatThrownBy(() -> userService.deleteUser(10L))
        .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
        verify(userRepository).existsById(10L);
        verifyNoMoreInteractions(userRepository);
    }
}