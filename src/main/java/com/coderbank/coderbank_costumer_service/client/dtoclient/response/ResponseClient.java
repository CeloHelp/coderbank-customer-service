package com.coderbank.coderbank_costumer_service.client.dtoclient.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseClient(
        UUID accountId,
        UUID customerId,
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
