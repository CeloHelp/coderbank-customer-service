package com.coderbank.coderbank_costumer_service.client;

import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.client.dtoclient.response.ResponseClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "transaction-service", url = "${client.post.url}")
public interface CustomerInterface {

    @PostMapping("/api/v1/accounts")
    ResponseClient createAccount(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @RequestBody RequestClient request
    );
}
