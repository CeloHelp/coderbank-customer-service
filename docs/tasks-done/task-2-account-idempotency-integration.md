# Task 2: integrate account opening idempotency

## Status

Done.

## Context

The `coderbank-transaction-service` account endpoint now requires an idempotency key and a minimal account-opening payload. The current Customer Service still sends the legacy payload and cannot complete customer onboarding against the new contract.

Upstream dependency:

```text
Repository: coderbank-transaction-service
Branch: task-2-apply-idempotency
Minimum commit: 3e16905 fix(accounts): Harden account opening idempotency
Local URL: http://localhost:8082
```

Transaction Service contract:

```http
POST /api/v1/accounts
Idempotency-Key: <UUID>
Content-Type: application/json
```

```json
{
  "customerId": "<UUID>",
  "accountType": "CHECKING",
  "currency": "BRL"
}
```

Successful response:

```json
{
  "accountId": "<UUID>",
  "customerId": "<UUID>",
  "accountType": "CHECKING",
  "balance": 0,
  "currency": "BRL",
  "createdAt": "<local-date-time>"
}
```

The Transaction Service rejects unknown properties. Continuing to send `amount` or `description` results in HTTP 400.

## Objective

Update the Customer Service integration so every new customer requests one idempotent `CHECKING` account while all retry attempts reuse the same key.

## Scope

### Included

- Align Feign request and response contracts.
- Send `Idempotency-Key` as an HTTP header.
- Generate the key once before entering the resilient gateway operation.
- Send `CHECKING` for customer onboarding.
- Preserve the existing resilience boundary in `AccountGateway`.
- Update unit, AOP, and integration tests.

### Excluded

- Savings-account opening endpoint.
- Customer-request idempotency at the public API boundary.
- Retry timeout and exception-policy tuning.
- Distributed transactions, outbox, or saga.
- Account movement rules.

## Decisions

- The backend selects `CHECKING`; the customer does not choose an account type during onboarding.
- The account-opening request no longer includes `amount` or `description`.
- The Customer Service generates a UUID idempotency key before calling `AccountGateway`.
- Resilience4j retries only the gateway method, so every attempt receives the same method arguments and key.
- The generated key protects retries inside one `createCustomer` execution; it does not make the public customer endpoint idempotent.
- The Transaction Service remains the authority for replay, duplicate account type, and account-opening validation.
- HTTP 4xx retry behavior will be addressed in the next resilience-policy task.

## Files

### Modify

- `src/main/java/com/coderbank/coderbank_costumer_service/client/dtoclient/request/RequestClient.java`
- `src/main/java/com/coderbank/coderbank_costumer_service/client/dtoclient/response/ResponseClient.java`
- `src/main/java/com/coderbank/coderbank_costumer_service/client/CustomerInterface.java`
- `src/main/java/com/coderbank/coderbank_costumer_service/client/AccountGateway.java`
- `src/main/java/com/coderbank/coderbank_costumer_service/service/CustomerService.java`
- `src/test/java/com/coderbank/coderbank_costumer_service/client/AccountGatewayResilienceTest.java`
- `src/test/java/com/coderbank/coderbank_costumer_service/client/ResilienceBoundaryTest.java`
- `src/test/java/com/coderbank/coderbank_costumer_service/service/CustomerServiceTest.java`

### Create if needed

- `src/main/java/com/coderbank/coderbank_costumer_service/client/dtoclient/AccountType.java`
- Contract or integration test covering both services.

## Implementation Steps

1. Add a client-side `AccountType` enum containing `CHECKING` and `SAVINGS`.
2. Change `RequestClient` to `customerId`, `accountType`, and `currency`.
3. Align `ResponseClient` with `accountId`, `customerId`, `accountType`, `balance`, `currency`, and `createdAt`.
4. Add `@RequestHeader("Idempotency-Key") UUID idempotencyKey` to the Feign method.
5. Change `AccountGateway.createAccount` to receive the key and request.
6. Keep fallback only on `@Retry` and add both original arguments before `Throwable` in its signature.
7. Generate the UUID once in `CustomerService`, before calling the gateway.
8. Send `AccountType.CHECKING` and `BRL` during customer onboarding.
9. Update mocks, captors, and structural annotation tests.
10. Run an integrated onboarding request against the Transaction Service.

Expected Feign signature:

```java
@PostMapping("/api/v1/accounts")
ResponseClient createAccount(
        @RequestHeader("Idempotency-Key") UUID idempotencyKey,
        @RequestBody RequestClient request
);
```

Expected gateway boundary:

```java
@Retry(name = "transactionServiceRetry", fallbackMethod = "createAccountFallback")
@CircuitBreaker(name = "transactionServiceCircuitBreaker")
public ResponseClient createAccount(UUID idempotencyKey, RequestClient request)
```

Expected fallback signature:

```java
private ResponseClient createAccountFallback(
        UUID idempotencyKey,
        RequestClient request,
        Throwable cause
)
```

## Tests

- Customer persistence occurs once when account creation fails.
- Gateway retry calls Feign twice with the same idempotency key.
- Gateway fallback executes after retry exhaustion.
- Successful onboarding sends `CHECKING`, `BRL`, and no legacy fields.
- Transaction Service returns HTTP 201 and the created account.
- Replaying the same key returns the same `accountId` without a second account.
- Missing or malformed contract data is surfaced without exposing internal details.

## Verification Commands

```powershell
.\mvnw.cmd "-Dtest=CustomerServiceTest,ResilienceBoundaryTest,AccountGatewayResilienceTest" test
.\mvnw.cmd test
```

Integrated verification requires:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Run the Transaction Service from `task-2-apply-idempotency` on port 8082 before submitting a customer onboarding request.

## Rollout Order

1. Keep the hardened Transaction Service contract available locally or in the target environment.
2. Deploy or start the updated Customer Service immediately after the Transaction Service contract is available.
3. Do not run the old Customer Service against the strict Transaction Service contract.
4. Verify one customer onboarding and confirm exactly one `CHECKING` account.
5. Configure retryable exceptions and Feign timeouts in the next task.

## Acceptance Criteria

- The Customer Service no longer sends `amount` or `description`.
- Every onboarding account request sends `accountType = CHECKING`.
- Every Feign request sends a UUID `Idempotency-Key` header.
- All retries for one operation use the same key.
- Customer persistence is not included in the retry boundary.
- Focused tests pass.
- Full tests pass with Docker/Testcontainers available.
- Manual or automated onboarding succeeds against `task-2-apply-idempotency` of the Transaction Service.
- The Transaction Service receives no unknown legacy properties.
- The account response can be deserialized without ignored or missing contract fields.
- No CPF, email, address, or complete customer DTO is logged.

## Result

Implementation completed in the Customer Service:

- Added the shared client-side `AccountType` contract.
- Replaced the legacy account-opening payload with `customerId`, `accountType`, and `currency`.
- Added the complete account-opening response fields.
- Added the UUID `Idempotency-Key` Feign header.
- Generated one key per `createCustomer` execution and propagated it through `AccountGateway` retries.
- Preserved customer persistence outside the retry and circuit-breaker boundary.
- Removed logging of complete customer records from customer listing.
- Updated service, resilience, Feign annotation, and DTO contract tests.

Verification performed:

```powershell
.\mvnw.cmd "-Dtest=CustomerServiceTest,ResilienceBoundaryTest,AccountGatewayResilienceTest" test
```

Result: passed with 8 tests, 0 failures, and 0 errors.

```powershell
.\mvnw.cmd test
```

Result: passed with 9 tests, 0 failures, and 0 errors using Docker/Testcontainers.

Integrated verification performed:

- The Transaction Service was available on port 8082.
- Replaying one account request with the same idempotency key returned the same `accountId`.
- The replayed account returned `accountType = CHECKING` and `currency = BRL`.
- Customer onboarding through `POST http://localhost:8085/api/v1/customers` returned successfully against the hardened Transaction Service contract.
