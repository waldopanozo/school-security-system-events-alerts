# ADR-004: PostgreSQL as Primary Data Store

## Status
Accepted

## Context
The domain needs transactional consistency and auditable operational records.

## Decision
Use PostgreSQL with Flyway migrations.

## Rejected alternatives
- Document-oriented NoSQL: more flexible schema, weaker relational reporting and integrity.
- In-memory storage: not suitable for persistent MVP behavior.

## Consequences
- Versioned migrations and safer deployment rollouts.
- Stronger schema discipline and compatibility management.
