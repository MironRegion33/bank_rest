package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateUserDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/create")
    public UserDto createUser(@RequestBody CreateUserDto dto) {
        return userService.createUser(dto);
    }

    @PatchMapping("/update/{id}")
    public UserDto updateUser(@PathVariable long id, @RequestBody CreateUserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
