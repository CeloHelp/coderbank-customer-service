package com.coderbank.coderbank_costumer_service.client;

import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.exceptions.TransactionServiceUnavailableException;
import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
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
        RequestClient request = new RequestClient(
                UUID.fromString("6f61d75d-a7f1-495d-a52f-6170d080ee88"),
                BigDecimal.ZERO,
                "BRL",
                "Abertura de conta via Customer Service"
        );
        RuntimeException remoteFailure = new RuntimeException("Serviço remoto indisponível");
        when(customerInterface.createAccount(request)).thenThrow(remoteFailure);

        assertThatThrownBy(() -> accountGateway.createAccount(request))
                .isInstanceOf(TransactionServiceUnavailableException.class)
                .hasCause(remoteFailure);

        verify(customerInterface, times(2)).createAccount(request);
        verifyNoInteractions(customerRepository);
    }
}
