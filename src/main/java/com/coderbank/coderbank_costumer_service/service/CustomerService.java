package com.coderbank.coderbank_costumer_service.service;

import com.coderbank.coderbank_costumer_service.client.CustomerInterface;
import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.client.dtoclient.response.ResponseClient;
import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.dto.response.CustomerRespondeDTO;
import com.coderbank.coderbank_costumer_service.model.Customer;
import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerInterface customerInterface;

    @Transactional
    public CustomerRespondeDTO createCustomer(CustomerRequestDTO dto) {
        // 1. Cria a entidade a partir do DTO (lógica na entidade via factory method)
        Customer customer = Customer.fromDTO(dto);

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
        customerInterface.createAccount(requestClient);

        // 5. Retorna o DTO de resposta
        return new CustomerRespondeDTO(
                customer.getId().toString(),
                customer.getName(),
                customer.getCpf(),
                customer.getEmail(),
                customer.getAddress()
        );
    }

    public ResponseClient sendTransaction(RequestClient request) {
        return customerInterface.createTransaction(request);
    }

}
