# ADR-001: Modular Monolith for MVP

## Status
Accepted

## Context
The project needs fast delivery, strong domain consistency, and lower operational complexity for a credible MVP demo.

## Decision
Use a modular monolith (Spring Boot) with clear internal domain boundaries.

## Rejected alternatives
- Domain microservices: higher initial cost for deployment, tracing, and contracts.
- Full serverless architecture: premature cloud coupling.

## Consequences
- Simpler local development and debugging.
- Clear migration path to microservices in future phases.
