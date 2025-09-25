package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCardDto(Long ownerId, String number, LocalDate expiryDate, BigDecimal balance) {
}
