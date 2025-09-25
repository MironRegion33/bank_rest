package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.service.CardService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserCardController.class,
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
class UserCardControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    CardService cardService;

    @Test
    void transfer_callsServiceAndReturnsOk() throws Exception {
        var req = new TransferDto(1L, 2L, new BigDecimal("50.00"));

        mvc.perform(post("/api/users/{userId}/cards/transfer", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(cardService).transfer(eq(42L), ArgumentMatchers.any(TransferDto.class));
    }

    @Test
    void getUserCards_withStatusAndPaging_returnsPage() throws Exception {
        var page = new PageImpl<>(List.of(
                new CardDto(1L, 42L,
                        "4111 **** **** 1111",
                        CardStatus.ACTIVE,
                        new BigDecimal("100.00")),
                new CardDto(2L, 42L,
                        "5555 **** **** 4444",
                        CardStatus.ACTIVE,
                        new BigDecimal("200.00"))
        ));
        when(cardService.getUserCards(eq(42L), eq(CardStatus.ACTIVE), ArgumentMatchers.any()))
                .thenReturn(page);

        mvc.perform(get("/api/users/{userId}/cards", 42)
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // структура Page по умолчанию — объект с полем content
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].ownerId").value(42))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));

        verify(cardService).getUserCards(eq(42L), eq(CardStatus.ACTIVE), ArgumentMatchers.any());
    }

    @Test
    void getUserCards_withoutStatus_returnsPage() throws Exception {
        var page = new PageImpl<>(List.of(
                new CardDto(3L, 42L,
                        "4000 **** **** 0002",
                        CardStatus.BLOCKED,
                        new BigDecimal("0.00"))
        ));
        when(cardService.getUserCards(eq(42L),
                eq(null),
                ArgumentMatchers.any())).thenReturn(page);

        mvc.perform(get("/api/users/{userId}/cards", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(3));

        verify(cardService).getUserCards(eq(42L), eq(null), ArgumentMatchers.any());
    }
}
