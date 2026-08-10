package com.coderbank.coderbank_costumer_service.controller;

import com.coderbank.coderbank_costumer_service.dto.request.CustomerRequestDTO;
import com.coderbank.coderbank_costumer_service.dto.response.CustomerResponseDTO;
import com.coderbank.coderbank_costumer_service.model.Customer;
import com.coderbank.coderbank_costumer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO create(@RequestBody @Valid CustomerRequestDTO requestDTO) {
        return customerService.createCustomer(requestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();

    }
}
