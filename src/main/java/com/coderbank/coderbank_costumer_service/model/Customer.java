package com.coderbank.coderbank_costumer_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

@Entity
@Table (name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(nullable = false, unique = true, length = 11)
    @CPF
    private String cpf;

    @Column(nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String address;

    // Construtores
    public Customer() {}

    public Customer(String name, String cpf, String email, String address) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.address = address;
    }
}
