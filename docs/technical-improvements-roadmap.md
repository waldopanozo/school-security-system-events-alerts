# Technical Improvements Roadmap

This document tracks the agreed technical improvements for the MVP so they can be implemented incrementally, one by one, with clear acceptance criteria.

## How to use this document

- Pick one item at a time.
- Move status from `pending` -> `in_progress` -> `done`.
- Add links to PRs/commits once completed.
- Do not start the next item until acceptance criteria are verified.

---

## 1) Transactional outbox pattern

- **Status:** `pending`
- **Priority:** `P0`
- **Goal:** Ensure reliable DB + event publication consistency.
- **Scope:**
  - Add `outbox_event` table.
  - Persist outbox records in same transaction as domain write.
  - Add relay/publisher job to push pending outbox events to Kafka.
  - Mark published records and track retries/failures.
- **Acceptance criteria:**
  - Event creation and outbox write are atomic.
  - No direct domain event publish from service transaction.
  - Integration test proves no lost events on simulated failure.

## 2) Stronger authentication and authorization

- **Status:** `pending`
- **Priority:** `P0`
- **Goal:** Harden auth flows and reduce token abuse risk.
- **Scope:**
  - Add JWT issuer/audience validation.
  - Add short-lived access token + refresh flow.
  - Add secret rotation strategy (env/versioned key support).
  - Extend authorization from role-only to role + scopes where needed.
- **Acceptance criteria:**
  - Invalid issuer/audience tokens are rejected.
  - Refresh flow works and old refresh token is invalidated/rotated.
  - Security tests cover role and scope restrictions.

## 3) Richer operational data model

- **Status:** `pending`
- **Priority:** `P1`
- **Goal:** Improve traceability and domain consistency.
- **Scope:**
  - Persist `incident_type` catalog as first-class entity.
  - Add `event_timeline` table with actor/action metadata.
  - Enforce state transitions with explicit domain rules.
- **Acceptance criteria:**
  - Incident types are managed via persistence (not free text only).
  - Every critical command appends timeline entry.
  - Invalid transitions are rejected and tested.

## 4) Enterprise-grade idempotency

- **Status:** `pending`
- **Priority:** `P0`
- **Goal:** Make idempotency deterministic for retries and clients.
- **Scope:**
  - Store request hash per `Idempotency-Key`.
  - Store operation response snapshot (or reference).
  - Return previous response for exact repeated request.
  - Add TTL cleanup process.
- **Acceptance criteria:**
  - Duplicate exact requests return same response safely.
  - Same key with different payload is rejected.
  - TTL cleanup job works without affecting active keys.

## 5) Versioned event contract validation

- **Status:** `pending`
- **Priority:** `P1`
- **Goal:** Prevent contract drift between producers/consumers.
- **Scope:**
  - Define JSON Schema or Avro for all `SecurityEvent*.v1`.
  - Validate outgoing events against schema.
  - Add CI checks for contract compatibility.
- **Acceptance criteria:**
  - Event payloads validated in tests.
  - CI fails on incompatible contract changes.
  - Versioning strategy documented (`v1`, backward compatibility rules).

## 6) More realistic notification simulation

- **Status:** `pending`
- **Priority:** `P1`
- **Goal:** Simulate production-like delivery behavior.
- **Scope:**
  - Add retry with backoff.
  - Add dead-letter handling for unrecoverable failures.
  - Track per-channel delivery metrics.
  - Make failure ratio configurable by environment.
- **Acceptance criteria:**
  - Retries and DLQ can be observed in logs/metrics.
  - Channel metrics expose success/failure/latency.
  - Functional test covers success + failure paths.

## 7) Full observability baseline

- **Status:** `pending`
- **Priority:** `P0`
- **Goal:** Provide actionable insight for operations and incidents.
- **Scope:**
  - JSON structured logs.
  - Correlation ID propagation across request -> DB -> Kafka.
  - OpenTelemetry traces.
  - Basic dashboards for latency/error rates/notification success.
- **Acceptance criteria:**
  - Trace and correlation IDs visible end-to-end.
  - Dashboards show core SLI trends.
  - Alerting thresholds defined for key failure signals.

## 8) CI quality and security gates

- **Status:** `pending`
- **Priority:** `P0`
- **Goal:** Block regressions before merge.
- **Scope:**
  - Backend static analysis (`spotbugs`/`checkstyle`).
  - Frontend linting (`eslint`).
  - Dependency scanning (SCA).
  - Coverage thresholds for backend/frontend.
- **Acceptance criteria:**
  - Pipeline fails on lint/static-analysis/coverage/SCA threshold breaches.
  - Required checks documented and reproducible locally.

## 9) Boundary and behavior tests

- **Status:** `pending`
- **Priority:** `P1`
- **Goal:** Strengthen confidence on critical flows.
- **Scope:**
  - Authorization tests by role.
  - Idempotency duplicate behavior tests.
  - E2E flow tests for happy and error paths.
- **Acceptance criteria:**
  - Tests cover principal/teacher/parent boundary rules.
  - Idempotency edge cases validated.
  - E2E suite runs in CI profile reliably.

## 10) Docker and runtime hardening

- **Status:** `pending`
- **Priority:** `P1`
- **Goal:** Improve production readiness and security posture.
- **Scope:**
  - Run containers as non-root.
  - Add resource constraints and health checks per service.
  - Validate required env vars at startup.
  - Add migration rollback/runbook guidance.
- **Acceptance criteria:**
  - Images run as non-root by default.
  - Runtime checks fail fast on missing critical configuration.
  - Deployment docs include rollback and operational runbook.

---

## Suggested execution order

1. Outbox pattern
2. CI quality/security gates
3. Auth hardening
4. Idempotency upgrade
5. Observability baseline
6. Contract validation
7. Operational data model
8. Notification simulation hardening
9. Boundary/E2E expansion
10. Docker/runtime hardening

---

## Work log

- Add entries here while implementing each improvement:
  - Date
  - Owner
  - Item number
  - PR/commit reference
  - Notes/risks
