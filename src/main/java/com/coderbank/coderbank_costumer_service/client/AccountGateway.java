package com.coderbank.coderbank_costumer_service.client;

import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.client.dtoclient.response.ResponseClient;
import com.coderbank.coderbank_costumer_service.exceptions.TransactionServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountGateway {

    private final CustomerInterface customerInterface;

    @Retry(name = "transactionServiceRetry", fallbackMethod = "createAccountFallback")
    @CircuitBreaker(name = "transactionServiceCircuitBreaker")
    public ResponseClient createAccount(RequestClient request) {
        return customerInterface.createAccount(request);
    }

    private ResponseClient createAccountFallback(RequestClient request, Throwable cause) {
        log.error("Falha ao criar conta para o cliente {}.", request.customerId(), cause);
        throw new TransactionServiceUnavailableException(
                "Serviço de transações indisponível. Tente novamente mais tarde.",
                cause
        );
    }
}
