# MVP Requirements

## 1) Executive Summary

Product name (must remain exact): **Sistema de seguridad escolar con eventos y alertas**.

This MVP addresses school safety coordination by combining incident/drill management, simulated parent communication, and operational postmortems. The scope is intentionally vertical: one complete end-to-end flow with senior-level engineering quality.

Core delivery:
1. Incident/drill registration with severity.
2. Simulated multi-channel communication flow.
3. Parent communication via versioned templates.
4. Event closure with mandatory postmortem.

## 2) Architecture and Logical Diagrams

### 2.1 High-level architecture
- React + TypeScript frontend by role.
- Spring Boot backend as modular monolith.
- PostgreSQL as transactional source of truth.
- Kafka for versioned domain event propagation.
- Simulated notification processing for channels.
- Observability with logs, metrics, and traces.

### 2.2 Logical components
- `web-app`:
  - Principal console.
  - Teacher console.
  - Parent portal.
- `security-events-api`:
  - Event management.
  - Drill handling.
  - Template management.
  - Postmortem management.
  - AuthN/AuthZ.
- `event-bus`:
  - Versioned event topics.
- `notification-simulator`:
  - Channel delivery simulation and status tracking.
- `postgres`:
  - Events, notifications, timeline, and postmortems.

### 2.3 ADR minimum set
- ADR-001: Modular monolith vs microservices.
- ADR-002: Kafka async communication vs queue/polling alternatives.
- ADR-003: JWT + RBAC vs server sessions.
- ADR-004: PostgreSQL relational model vs NoSQL.

## 3) Data Model and Main Flows

### 3.1 Main entities
- `security_event`: type (`INCIDENT|DRILL`), severity (`LOW|MEDIUM|HIGH|CRITICAL`), status (`CREATED|ALERT_ACTIVATED|CLOSED`), location, actor, timestamps.
- `event_notification`: channel, recipient, template, delivery status.
- `postmortem_report`: summary, root cause, corrective actions.
- `idempotency_record`: command deduplication key.

### 3.2 Versioned API contracts (v1)
- `POST /events`
- `GET /events/{id}`
- `POST /events/{id}/activate-alert`
- `POST /events/{id}/close`
- `POST /postmortems`
- `GET /templates`

### 3.3 Versioned event contracts
Topic: `school.security.event.v1`
- `SecurityEventCreated.v1`
- `SecurityAlertActivated.v1`
- `SecurityNotificationDispatched.v1`
- `SecurityEventClosed.v1`

### 3.4 Main E2E flow
1. Principal creates incident/drill.
2. Event is persisted and `SecurityEventCreated.v1` is emitted.
3. Principal activates alert.
4. Notification simulation runs per channel and emits delivery events.
5. Team updates operations.
6. Principal closes event only after postmortem exists.

### 3.5 Idempotency and failure handling
- `Idempotency-Key` required for critical commands.
- Duplicate command protection in database.
- Retry and dead-letter strategy for async processing (next increment).

## 4) Testing and Security Plan

### 4.1 Test strategy
- Unit tests: domain transitions, role-based access rules.
- Integration tests: repository and migration behavior.
- Contract tests: API/event schema compatibility (next increment).
- E2E flow test: create -> alert -> postmortem -> close.

### 4.2 Security baseline
Threat model focus:
- Unauthorized access to sensitive events.
- Role spoofing.
- Command replay.

Controls:
- JWT with short expiration.
- RBAC at backend endpoint level.
- Secret management via environment variables.
- Dependency scanning in CI.
- Auditability through event timeline and notification records.

## 5) Observability and Operations Plan

- Structured logs with correlation context.
- Prometheus metrics endpoint.
- Health/readiness checks.
- Initial SLI/SLO:
  - P95 alert activation to first notification <= 10s.
  - Simulated notification success >= 99%.

## 6) Prioritized Backlog with Measurable Acceptance

1. Foundation stack (Docker + DB + Kafka + services running).
2. AuthN/AuthZ with JWT + RBAC.
3. Safe schema migrations.
4. Event create/view/activate/close flow.
5. Template-based simulated notifications.
6. Postmortem-required closure logic.
7. Observability baseline and CI quality gates.

## Explicit assumptions

1. Multi-channel delivery is simulated in MVP.
2. Single-tenant logical model for first release.
3. Parent role is read-only.
4. Demo runs fully via Docker Compose.
