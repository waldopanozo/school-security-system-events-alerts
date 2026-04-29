# School Safety System with Events and Alerts

MVP product in the `wellbeing-children` domain using the `senior-java-spring-react` stack.

## System goal (plain language)

This system helps a school react quickly and with full traceability during safety-related situations.

It allows teams to:
- register incidents or evacuation drills with clear severity,
- trigger simulated multi-channel alerts for families,
- coordinate actions by role (`principal`, `teacher`, `parent`),
- close events with a postmortem template for operational learning.

## Current MVP scope

Implemented:
- Spring Boot backend with versioned API (`/api/v1`), JWT + RBAC, and idempotency.
- PostgreSQL persistence with Flyway migrations.
- Kafka event publishing (`school.security.event.v1`).
- Simulated notifications by channel (`EMAIL`, `SMS`, `PUSH`).
- React + TypeScript frontend for role-based demo flows.
- Dockerfiles and `docker-compose.yml` for reproducible local setup.
- ADR set in `infra/adrs/`.
- CI workflow in `.github/workflows/ci.yml`.

## Logical architecture (summary)

```text
React Web (role views)
   -> Spring Boot API (auth, events, postmortem, templates)
      -> PostgreSQL (transactional state)
      -> Kafka topic school.security.event.v1 (versioned domain events)
```

ADRs:
- `infra/adrs/ADR-001-modular-monolith.md`
- `infra/adrs/ADR-002-kafka-events.md`
- `infra/adrs/ADR-003-jwt-rbac.md`
- `infra/adrs/ADR-004-postgresql.md`

## Repository structure

- `backend/`: Java 21 + Spring Boot 3.x API
- `frontend/`: React + TypeScript web app (Vite)
- `infra/adrs/`: architecture decisions
- `requirements/requirements.md`: full MVP requirements
- `docker-compose.yml`: full local stack

## Local run tutorial (under 30 minutes)

Prerequisites:
- Docker + Docker Compose

Start everything:

```bash
docker compose up --build
```

Service endpoints:
- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- Healthcheck: `http://localhost:8080/actuator/health`

Stop everything:

```bash
docker compose down -v
```

## Guided usage flow

1) Get a token for a role:

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"principal"}'
```

2) Create an event:

```bash
curl -s -X POST http://localhost:8080/api/v1/events \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Idempotency-Key: create-evt-001" \
  -H "Content-Type: application/json" \
  -d '{"type":"INCIDENT","incidentType":"FIGHT","severity":"HIGH","location":"Central courtyard"}'
```

3) Activate alert:

```bash
curl -s -X POST http://localhost:8080/api/v1/events/<EVENT_ID>/activate-alert \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Idempotency-Key: activate-evt-001"
```

4) Register postmortem:

```bash
curl -s -X POST http://localhost:8080/api/v1/postmortems \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"eventId":"<EVENT_ID>","summary":"Orderly evacuation","rootCause":"Scheduled drill","correctiveActions":"Reinforce classroom checklist"}'
```

5) Close event:

```bash
curl -s -X POST http://localhost:8080/api/v1/events/<EVENT_ID>/close \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Idempotency-Key: close-evt-001"
```

## Security model

- AuthN: stateless JWT.
- AuthZ: RBAC by endpoint:
  - `PRINCIPAL`: create/activate/close events + postmortem.
  - `TEACHER`: create/list/view events.
  - `PARENT`: read-only allowed routes.
- Idempotency for critical commands through `Idempotency-Key`.

## Observability and operations

- Actuator health endpoint enabled.
- Prometheus metrics endpoint enabled.
- Versioned domain events emitted to Kafka.

Initial SLI/SLO targets:
- P95 from alert activation to first simulated notification <= 10s.
- Simulated notification success rate >= 99%.

## Tests

Backend tests (executed):

```bash
docker run --rm -v "$PWD/backend":/app -w /app maven:3.9.9-eclipse-temurin-21 mvn test
```

Result: `BUILD SUCCESS`.

Frontend build (executed):

```bash
cd frontend && npm install && npm run build
```

Result: production build completed (`dist/` generated).

## CI/CD and quality gates

Workflow: `.github/workflows/ci.yml`
- backend job: `mvn test`
- frontend job: `npm ci && npm run build`

Recommended next quality gates:
- coverage thresholds,
- SCA policy enforcement,
- API/event contract checks.

## Production deployment guide

This repository is container-ready for production deployment:

1. Build images from:
   - `backend/Dockerfile`
   - `frontend/Dockerfile`
2. Push images to a registry (GHCR/ECR/GCR).
3. Configure secrets:
   - `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `KAFKA_BOOTSTRAP`
4. Run Flyway migrations at backend startup.
5. Connect metrics/logs to your observability platform.

Kubernetes baseline:
- `Deployment` + `Service` for frontend/backend,
- managed or stateful workloads for PostgreSQL and Kafka,
- readiness/liveness probes via `/actuator/health`.

## Explicit assumptions

- Multi-channel communications are simulated for MVP.
- Single-tenant logical deployment.
- Event closure requires a postmortem.

## Internal references

- Full requirements: `requirements/requirements.md`
- Architecture decisions: `infra/adrs/`
