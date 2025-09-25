package com.example.bankcards.dto;


import com.example.bankcards.entity.enums.Role;

public record UserDto(long id, String username, Role role) {
}
