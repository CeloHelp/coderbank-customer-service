package com.coderbank.coderbank_costumer_service.client.dtoclient.response;

import com.coderbank.coderbank_costumer_service.client.dtoclient.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseClient(
        UUID accountId,
        UUID customerId,
        AccountType accountType,
        BigDecimal balance,
        String currency,
        LocalDateTime createdAt
) {
}
