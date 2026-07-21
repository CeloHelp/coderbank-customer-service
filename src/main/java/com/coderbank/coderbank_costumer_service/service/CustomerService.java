package com.coderbank.coderbank_costumer_service.service;

import com.coderbank.coderbank_costumer_service.client.CustomerInterface;
import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.dto.response.CustomerResponseDTO;
import com.coderbank.coderbank_costumer_service.model.Customer;
import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerInterface customerInterface;

    @Retry(name = "customer-service", fallbackMethod = "createCustomerFallback")
    @CircuitBreaker(name = "customer-service", fallbackMethod = "createCustomerFallback")
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO CustomerRequestDTO) {
        // 1. Cria a entidade a partir do DTO (lógica na entidade via factory method)
        Customer customer = Customer.fromDTO(CustomerRequestDTO);

        // 2. Persiste no banco local
        customer = customerRepository.save(customer);

        // 3. Prepara a chamada para o serviço de transações/contas

        RequestClient requestClient = new RequestClient(
                customer.getId(),
                BigDecimal.ZERO,
                "BRL",
                "Abertura de conta via Customer Service"
        );

        // 4. Chamada síncrona via Feign
        log.info(" Cliente enviado com sucesso: {}", requestClient);
        customerInterface.createAccount(requestClient);

        // 5. Retorna o DTO de resposta
        return new CustomerResponseDTO(
                customer.getId().toString(),
                customer.getName(),
                customer.getCpf(),
                customer.getEmail(),
                customer.getAddress()
        );
    }

    public String createCustomerFallback(CustomerRequestDTO CustomerRequestDTO) {
        log.error("Falha ao criar cliente. Fallback acionado para o cliente: {}", CustomerRequestDTO);
        return "Falha ao criar cliente. Por favor, tente novamente mais tarde.";
    }



}
