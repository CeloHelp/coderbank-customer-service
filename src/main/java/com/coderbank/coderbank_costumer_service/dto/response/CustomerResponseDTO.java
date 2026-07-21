package com.coderbank.coderbank_costumer_service.dto.response;

public record CustomerResponseDTO(
        String id,
        String name,
        String cpf,
        String email,
        String address
) {
}
