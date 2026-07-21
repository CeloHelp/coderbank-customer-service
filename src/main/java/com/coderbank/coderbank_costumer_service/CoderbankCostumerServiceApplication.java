package com.coderbank.coderbank_costumer_service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class CoderbankCostumerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoderbankCostumerServiceApplication.class, args);
	}

}
