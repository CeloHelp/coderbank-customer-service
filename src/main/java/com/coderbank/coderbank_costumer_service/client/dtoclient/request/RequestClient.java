package com.coderbank.coderbank_costumer_service.client.dtoclient.request;

import com.coderbank.coderbank_costumer_service.client.dtoclient.AccountType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RequestClient(
        @NotNull(message = "Cliente ID é obrigatório")
        UUID customerId,
        AccountType accountType,
        String currency
) {
}
