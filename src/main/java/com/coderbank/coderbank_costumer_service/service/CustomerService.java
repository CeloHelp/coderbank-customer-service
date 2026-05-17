package com.coderbank.coderbank_costumer_service.service;

import com.coderbank.coderbank_costumer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

    }


}
