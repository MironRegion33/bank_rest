package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardDto;
import com.example.bankcards.service.CardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
public class AdminCardController {

    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardDto> getAll() {
        return cardService.getAllCards();
    }

    @PostMapping
    public CardDto create(@RequestBody CreateCardDto cardDto) {
        return cardService.createCard(cardDto);
    }

    @PatchMapping("/{id}/block")
    public CardDto block(@PathVariable Long id) {
        return cardService.blockCard(id);
    }

    @PatchMapping("/{id}/activate")
    public CardDto activate(@PathVariable Long id) {
        return cardService.activateCard(id);
    }

    @DeleteMapping("/{id}/delete")
    public void delete(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @PatchMapping("/{userId}/request-block")
    public CardDto requestBlock(@PathVariable Long userId, @RequestParam Long cardId) {
        return cardService.requestBlock(userId, cardId);
    }
}
