package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Role;

public record CreateUserDto(String username, String password, Role role) {
}
