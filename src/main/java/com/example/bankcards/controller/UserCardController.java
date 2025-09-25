package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/cards")
public class UserCardController {

    private final CardService cardService;

    public UserCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/transfer")
    public void transfer(@PathVariable Long userId, @RequestBody TransferDto dto) {
        cardService.transfer(userId, dto);
    }

    @GetMapping
    public Page<CardDto> getUserCards(
            @PathVariable Long userId,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return cardService.getUserCards(userId, status, PageRequest.of(page, size));
    }
}
