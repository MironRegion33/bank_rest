package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    UserDto createUser(CreateUserDto createUserDto);

    UserDto updateUser(long userId, CreateUserDto dto);

    void deleteUser(long userId);
}
