package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {

    List<CardDto> getAllCards();
    CardDto createCard(CreateCardDto cardDto);
    CardDto blockCard(Long cardId);
    CardDto activateCard(Long cardId);
    void deleteCard(Long cardId);
    CardDto requestBlock(Long userId, Long cardId);
    void transfer(Long userId, TransferDto dto);
    Page<CardDto> getUserCards(Long ownerId, CardStatus status, Pageable pageable);
}
