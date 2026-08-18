package com.coderbank.coderbank_costumer_service.client;

import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.service.CustomerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class ResilienceBoundaryTest {

    @Test
    void shouldApplyResilienceOnlyToAccountGateway() throws NoSuchMethodException {
        var createAccount = AccountGateway.class.getMethod("createAccount", RequestClient.class);
        var createCustomer = CustomerService.class.getMethod("createCustomer", CustomerRequestDTO.class);

        Retry retry = createAccount.getAnnotation(Retry.class);
        CircuitBreaker circuitBreaker = createAccount.getAnnotation(CircuitBreaker.class);

        assertThat(retry).isNotNull();
        assertThat(retry.fallbackMethod()).isEqualTo("createAccountFallback");
        assertThat(circuitBreaker).isNotNull();
        assertThat(circuitBreaker.fallbackMethod()).isEmpty();

        assertThat(createCustomer.getAnnotation(Retry.class)).isNull();
        assertThat(createCustomer.getAnnotation(CircuitBreaker.class)).isNull();
        assertThat(createCustomer.getAnnotation(Transactional.class)).isNotNull();
    }
}
