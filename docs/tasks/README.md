# Task Workflow

## Directories

- `docs/tasks/`: active, planned, or blocked work.
- `docs/tasks-done/`: completed work with verification evidence.

## Naming

Use `task-N-short-description.md`. Related subtasks may share the same task number when they belong to the same branch or delivery goal.

## Lifecycle

1. Create the task in `docs/tasks/` before implementation.
2. Record context, scope, decisions, files, steps, tests, and acceptance criteria.
3. Keep discoveries and scope changes in the same document.
4. Verify every acceptance criterion.
5. Add the implementation result and verification evidence.
6. Move the document to `docs/tasks-done/`.

## Template

```markdown
# Task N: title

## Status

## Context

## Objective

## Scope

## Decisions

## Files

## Implementation Steps

## Tests

## Acceptance Criteria

## Result
```
