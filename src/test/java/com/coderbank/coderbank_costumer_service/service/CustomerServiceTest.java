package com.coderbank.coderbank_costumer_service.service;

import com.coderbank.coderbank_costumer_service.client.AccountGateway;
import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.exceptions.TransactionServiceUnavailableException;
import com.coderbank.coderbank_costumer_service.model.Customer;
import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("6f61d75d-a7f1-495d-a52f-6170d080ee88");

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountGateway accountGateway;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new CustomerRequestDTO(
                "Maria Silva",
                "52998224725",
                "maria@example.com",
                "Rua Principal, 100"
        );

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            ReflectionTestUtils.setField(customer, "id", CUSTOMER_ID);
            return customer;
        });
    }

    @Test
    void shouldPersistCustomerAndRequestAccountCreation() {
        var response = customerService.createCustomer(request);

        ArgumentCaptor<RequestClient> accountRequest = ArgumentCaptor.forClass(RequestClient.class);
        verify(customerRepository).save(any(Customer.class));
        verify(accountGateway).createAccount(accountRequest.capture());

        assertThat(response.id()).isEqualTo(CUSTOMER_ID.toString());
        assertThat(accountRequest.getValue().customerId()).isEqualTo(CUSTOMER_ID);
    }

    @Test
    void shouldNotRepeatCustomerPersistenceWhenAccountGatewayFails() {
        var failure = new TransactionServiceUnavailableException("Serviço indisponível");
        doThrow(failure).when(accountGateway).createAccount(any(RequestClient.class));

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isSameAs(failure);

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(accountGateway, times(1)).createAccount(any(RequestClient.class));
    }
}
