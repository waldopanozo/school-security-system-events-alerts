# ADR-003: Stateless JWT and RBAC

## Status
Accepted

## Context
The system requires strict role-based access for `principal`, `teacher`, and `parent` personas.

## Decision
Use stateless JWT authentication and role-based authorization through Spring Security.

## Rejected alternatives
- Server-side sessions: lower horizontal scalability.
- Frontend-only access control: insecure because backend remains unprotected.

## Consequences
- API scales cleanly across instances.
- Requires robust secret handling and token expiration policies.
