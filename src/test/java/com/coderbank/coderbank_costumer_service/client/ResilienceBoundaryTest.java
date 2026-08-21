package com.coderbank.coderbank_costumer_service.client;

import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.client.dtoclient.response.ResponseClient;
import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.service.CustomerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResilienceBoundaryTest {

    @Test
    void shouldApplyResilienceOnlyToAccountGateway() throws NoSuchMethodException {
        var createAccount = AccountGateway.class.getMethod("createAccount", UUID.class, RequestClient.class);
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

    @Test
    void shouldDeclareIdempotencyHeaderAndRequestBodyOnFeignContract() throws NoSuchMethodException {
        var createAccount = CustomerInterface.class.getMethod("createAccount", UUID.class, RequestClient.class);
        var parameters = createAccount.getParameters();

        RequestHeader requestHeader = parameters[0].getAnnotation(RequestHeader.class);
        assertThat(requestHeader).isNotNull();
        assertThat(requestHeader.value()).isEqualTo("Idempotency-Key");
        assertThat(parameters[1].getAnnotation(RequestBody.class)).isNotNull();
    }

    @Test
    void shouldUseOnlyTheMinimalAccountOpeningPayload() {
        assertThat(Arrays.stream(RequestClient.class.getRecordComponents())
                .map(component -> component.getName()))
                .containsExactly("customerId", "accountType", "currency");
    }

    @Test
    void shouldRepresentTheCompleteAccountOpeningResponse() {
        assertThat(Arrays.stream(ResponseClient.class.getRecordComponents())
                .map(component -> component.getName()))
                .containsExactly("accountId", "customerId", "accountType", "balance", "currency", "createdAt");
    }
}
