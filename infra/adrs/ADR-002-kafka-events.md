# ADR-002: Kafka for Domain Events

## Status
Accepted

## Context
The alerting flow needs asynchronous decoupling between command handling and channel delivery simulation.

## Decision
Use Kafka with a versioned topic: `school.security.event.v1`.

## Rejected alternatives
- RabbitMQ: valid option but less aligned with replay and stream evolution goals.
- REST polling: lower resilience and higher latency.

## Consequences
- Clear, versioned async contracts.
- Increased ops complexity, mitigated through Docker Compose for local development.
