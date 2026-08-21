package com.coderbank.coderbank_costumer_service.client;

import com.coderbank.coderbank_costumer_service.client.dtoclient.AccountType;
import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.exceptions.TransactionServiceUnavailableException;
import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
        "spring.docker.compose.enabled=false"
})
class AccountGatewayResilienceTest {

    @Autowired
    private AccountGateway accountGateway;

    @MockitoBean
    private CustomerInterface customerInterface;

    @MockitoBean
    private CustomerRepository customerRepository;

    @Test
    void shouldRetryOnlyTheFeignCallBeforeExecutingFallback() {
        UUID idempotencyKey = UUID.fromString("0d64e618-c20a-4694-b426-19e9f3701154");
        RequestClient request = new RequestClient(
                UUID.fromString("6f61d75d-a7f1-495d-a52f-6170d080ee88"),
                AccountType.CHECKING,
                "BRL"
        );
        RuntimeException remoteFailure = new RuntimeException("Serviço remoto indisponível");
        when(customerInterface.createAccount(idempotencyKey, request)).thenThrow(remoteFailure);

        assertThatThrownBy(() -> accountGateway.createAccount(idempotencyKey, request))
                .isInstanceOf(TransactionServiceUnavailableException.class)
                .hasCause(remoteFailure);

        verify(customerInterface, times(2)).createAccount(idempotencyKey, request);
        verifyNoInteractions(customerRepository);
    }
}
