package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;

import java.math.BigDecimal;

public record CardDto(long id, long ownerId, String number, CardStatus status, BigDecimal balance) {
}
