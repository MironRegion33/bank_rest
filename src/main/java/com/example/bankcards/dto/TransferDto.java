package com.example.bankcards.dto;

import java.math.BigDecimal;

public record TransferDto(Long fromCardId,
                          Long toCardId,
                          BigDecimal amount) {
}
