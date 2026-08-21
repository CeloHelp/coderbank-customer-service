package com.coderbank.coderbank_costumer_service.service;

import com.coderbank.coderbank_costumer_service.client.AccountGateway;
import com.coderbank.coderbank_costumer_service.client.dtoclient.AccountType;
import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.dto.response.CustomerResponseDTO;
import com.coderbank.coderbank_costumer_service.model.Customer;
import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountGateway accountGateway;

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = Customer.fromDTO(customerRequestDTO);
        customer = customerRepository.save(customer);

        RequestClient requestClient = new RequestClient(
                customer.getId(),
                AccountType.CHECKING,
                "BRL"
        );
        UUID idempotencyKey = UUID.randomUUID();

        accountGateway.createAccount(idempotencyKey, requestClient);
        log.info("Conta solicitada com sucesso para o cliente {}.", customer.getId());

        return new CustomerResponseDTO(
                customer.getId().toString(),
                customer.getName(),
                customer.getCpf(),
                customer.getEmail(),
                customer.getAddress()
        );
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        log.info("Listando {} clientes.", customers.size());
        return customers;
    }
}
