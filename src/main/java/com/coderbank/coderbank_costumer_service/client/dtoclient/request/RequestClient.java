package com.coderbank.coderbank_costumer_service.client.dtoclient.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestClient(
        @NotNull(message = "Cliente ID é obrigatório")
        UUID customerId,
        BigDecimal amount,
        String currency,
        String description
) {
}
