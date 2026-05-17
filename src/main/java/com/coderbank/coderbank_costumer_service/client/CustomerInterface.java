package com.coderbank.coderbank_costumer_service.client;

import com.coderbank.coderbank_costumer_service.client.dtoclient.request.RequestClient;
import com.coderbank.coderbank_costumer_service.client.dtoclient.response.ResponseClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "transaction-service", url = "${client.post.url}")
public interface CustomerInterface {
    @PostMapping("/api/v1/transactions")
    ResponseClient createTransaction(RequestClient requestClient);

    @PostMapping("/api/v1/accounts")
    ResponseClient createAccount(RequestClient requestClient);
}
