package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateUserDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AdminUserController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    UserService userService;

    @Test
    void getAllUsers_returnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                new UserDto(1L, "alice", Role.ADMIN),
                new UserDto(2L, "bob", Role.USER)
        ));

        mvc.perform(get("/api/admin/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[1].role").value("USER"));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        when(userService.getUserById(5L)).thenReturn(new UserDto(5L, "john", Role.USER));

        mvc.perform(get("/api/admin/users/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.username").value("john"));

        verify(userService).getUserById(5L);
    }

    @Test
    void createUser_creates() throws Exception {
        var req = new CreateUserDto("neo", "thereisnospoon", Role.ADMIN);
        var resp = new UserDto(100L, "neo", Role.ADMIN);
        when(userService.createUser(ArgumentMatchers.any(CreateUserDto.class))).thenReturn(resp);

        mvc.perform(post("/api/admin/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.username").value("neo"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService).createUser(ArgumentMatchers.any(CreateUserDto.class));
    }

    @Test
    void updateUser_updates() throws Exception {
        var req = new CreateUserDto("trinity", "pwd", Role.USER);
        var resp = new UserDto(7L, "trinity", Role.USER);
        when(userService.updateUser(eq(7L), ArgumentMatchers.any(CreateUserDto.class))).thenReturn(resp);

        mvc.perform(patch("/api/admin/users/update/{id}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.username").value("trinity"));

        verify(userService).updateUser(eq(7L), ArgumentMatchers.any(CreateUserDto.class));
    }

    @Test
    void deleteUser_deletes() throws Exception {
        mvc.perform(delete("/api/admin/users/delete/{id}", 9))
                .andExpect(status().isOk());

        verify(userService).deleteUser(9L);
    }
}
