package com.coderbank.coderbank_costumer_service.service;

import com.coderbank.coderbank_costumer_service.client.AccountGateway;
import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.dto.response.CustomerResponseDTO;
import com.coderbank.coderbank_costumer_service.model.Customer;
import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountGateway accountGateway;

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
        accountGateway.createAccount(requestClient);
        log.info("Conta solicitada com sucesso para o cliente {}.", customer.getId());

        // 5. Retorna o DTO de resposta
        return new CustomerResponseDTO(
                customer.getId().toString(),
                customer.getName(),
                customer.getCpf(),
                customer.getEmail(),
                customer.getAddress()
        );






    }

    public List<Customer> getAllCustomers() {

        log.info("Listando clientes: {}", customerRepository.findAll());

        return customerRepository.findAll();
    }












}
