# Agent Instructions

## Package Manager

Use the Maven Wrapper: `.\mvnw.cmd` on Windows and `./mvnw` on Unix.

## Commands

| Task | Command |
|---|---|
| Run application | `.\mvnw.cmd spring-boot:run` |
| Compile | `.\mvnw.cmd compile` |
| Focused test | `.\mvnw.cmd "-Dtest=ClassName" test` |
| Full test suite | `.\mvnw.cmd test` |
| Start local database | `docker compose up -d` |

## Task Workflow

- Active tasks live in `docs/tasks/` and follow `task-N-description.md`.
- Read the relevant active task before changing code.
- Keep scope, decisions, tests, and acceptance criteria in the task file.
- Move a task to `docs/tasks-done/` only after its acceptance criteria are verified.
- Record implementation summary, verification commands, and commit references when closing a task.
- Do not mark blocked or partially verified work as done.

## Project Conventions

- Keep controllers thin; business orchestration belongs in services.
- Keep external account integration and resilience in `AccountGateway`.
- Apply retry and circuit breaker only around the remote operation, not customer persistence.
- Use DTOs at API and service boundaries; do not expose JPA entities in new endpoints.
- Never edit an applied Flyway migration; create the next versioned migration.
- Coordinate account contract changes with `coderbank-transaction-service`.
- Do not log CPF, email, address, credentials, or complete customer requests.
- Preserve unrelated working-tree changes.

## Verification

- Add focused tests for changed behavior.
- Run focused tests before the full suite.
- Integration tests using Testcontainers require Docker.
- Report any verification blocked by unavailable infrastructure.

## Commit Attribution

AI commits MUST include:

```text
Co-Authored-By: OpenAI GPT-5.6 <noreply@openai.com>
```
