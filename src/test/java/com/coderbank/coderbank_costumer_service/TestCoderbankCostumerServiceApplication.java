package com.coderbank.coderbank_costumer_service;

import org.springframework.boot.SpringApplication;

public class TestCoderbankCostumerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(CoderbankCostumerServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
