package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AdminCardController.class,
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
class AdminCardControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    CardService cardService;

    @Test
    void getAll_returnsCards() throws Exception {
        var dto1 = new CardDto(1L, 10L,
                "4111 **** **** 1111",
                CardStatus.ACTIVE,
                new BigDecimal("100.00"));
        var dto2 = new CardDto(2L, 11L,
                "5555 **** **** 4444",
                CardStatus.BLOCKED,
                new BigDecimal("0.00"));
        when(cardService.getAllCards()).thenReturn(List.of(dto1, dto2));

        mvc.perform(get("/api/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ownerId").value(10))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("BLOCKED"));

        verify(cardService).getAllCards();
    }

    @Test
    void create_createsCard() throws Exception {
        var request = new CreateCardDto(10L,
                "4111111111111111",
                LocalDate.of(2030, 12, 31),
                new BigDecimal("500.00"));
        var response = new CardDto(100L,
                10L,
                "4111 **** **** 1111",
                CardStatus.ACTIVE,
                new BigDecimal("500.00"));
        when(cardService.createCard(ArgumentMatchers.any(CreateCardDto.class))).thenReturn(response);

        mvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.ownerId").value(10))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(cardService).createCard(ArgumentMatchers.any(CreateCardDto.class));
    }

    @Test
    void block_blocksCard() throws Exception {
        var dto = new CardDto(1L, 10L,
                "4111 **** **** 1111",
                CardStatus.BLOCKED,
                new BigDecimal("100.00"));
        when(cardService.blockCard(1L)).thenReturn(dto);

        mvc.perform(patch("/api/admin/cards/{id}/block", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        verify(cardService).blockCard(1L);
    }

    @Test
    void activate_activatesCard() throws Exception {
        var dto = new CardDto(1L, 10L,
                "4111 **** **** 1111",
                CardStatus.ACTIVE,
                new BigDecimal("100.00"));
        when(cardService.activateCard(1L)).thenReturn(dto);

        mvc.perform(patch("/api/admin/cards/{id}/activate", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(cardService).activateCard(1L);
    }

    @Test
    void delete_deletesCard() throws Exception {
        mvc.perform(delete("/api/admin/cards/{id}/delete", 1))
                .andExpect(status().isOk());

        verify(cardService).deleteCard(1L);
    }

    @Test
    void requestBlock_blocksByUser() throws Exception {
        var result = new CardDto(1L, 10L,
                "4111 **** **** 1111",
                CardStatus.BLOCKED,
                new BigDecimal("100.00"));
        when(cardService.requestBlock(10L, 1L)).thenReturn(result);

        mvc.perform(patch("/api/admin/cards/{userId}/request-block", 10)
                        .param("cardId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        verify(cardService).requestBlock(10L, 1L);
    }
}
