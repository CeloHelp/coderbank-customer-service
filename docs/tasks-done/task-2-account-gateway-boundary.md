# Task 2: isolate the account resilience boundary

## Status

Completed.

## Context

Retry, circuit breaker, fallback, customer persistence, and remote account creation were originally applied to the same `CustomerService.createCustomer` method. A retry could therefore repeat local customer-creation work instead of repeating only the transient remote operation.

## Objective

Move remote-account resilience into a dedicated Spring bean and keep customer persistence outside the retry boundary.

## Implementation

- Created `AccountGateway` as the account-integration boundary.
- Moved `@Retry` and `@CircuitBreaker` to `AccountGateway.createAccount`.
- Kept the fallback only on the outer retry aspect.
- Kept `@Transactional` on `CustomerService.createCustomer`.
- Replaced direct Feign usage in `CustomerService` with `AccountGateway`.
- Moved failure translation and safe account-request logging to the gateway.
- Logged account-request success only after the remote call returned.
- Added behavioral, structural, and Spring AOP tests.

## Files

- `src/main/java/com/coderbank/coderbank_costumer_service/client/AccountGateway.java`
- `src/main/java/com/coderbank/coderbank_costumer_service/service/CustomerService.java`
- `src/test/java/com/coderbank/coderbank_costumer_service/client/AccountGatewayResilienceTest.java`
- `src/test/java/com/coderbank/coderbank_costumer_service/client/ResilienceBoundaryTest.java`
- `src/test/java/com/coderbank/coderbank_costumer_service/service/CustomerServiceTest.java`

## Verification

Focused command:

```powershell
.\mvnw.cmd "-Dtest=CustomerServiceTest,ResilienceBoundaryTest,AccountGatewayResilienceTest" test
```

Result:

```text
Tests run: 4
Failures: 0
Errors: 0
BUILD SUCCESS
```

The runtime test confirmed that the proxied gateway performs two Feign attempts and executes one final fallback, while the Customer Service invokes customer persistence once.

## Commit

```text
64a7106 feat: adicionando a classe AccountGateway para lidar com o retry e fallback + testes unitarios iniciais
```

## Acceptance Criteria

- Retry and circuit breaker apply only to the remote account call: verified.
- Customer persistence remains outside the retry boundary: verified.
- Fallback is configured once: verified.
- Self-invocation is avoided through a separate Spring bean: verified.
- Focused tests pass: verified.

## Result

The resilience boundary is isolated. Contract idempotency integration remains active in `docs/tasks/task-2-account-idempotency-integration.md`.
