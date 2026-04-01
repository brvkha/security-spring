---
description: Build a fullstack Spring Security training lab with React admin dashboard, JWT access token, DB-backed refresh token rotation, H2, and Swagger.
---

## User Input

```text
$ARGUMENTS
```

Treat user input as optional extra constraints. If it conflicts with the fixed decisions below, keep the fixed decisions.

## Goal

Create a web-only security training project that is practical, understandable, and production-like in architecture without production-only complexity.

## Fixed Decisions

- Fullstack project: Spring Boot backend + React frontend.
- Backend storage: H2 database for fast local iteration.
- API docs and testability: Swagger UI enabled.
- Auth model: Access token JWT + Refresh token persisted in database from day one.
- Token policy: Access token TTL 15 minutes, refresh token TTL 7 days.
- Refresh token transport: HttpOnly cookie.
- Authorization model: RBAC with only USER and ADMIN.
- Admin dashboard scope:
  - User list
  - Lock/Unlock user
  - Revoke sessions
  - Audit logs
- Exclusions:
  - No OAuth2/OIDC
  - No mobile-specific flows
  - No email verification
  - No password reset

## Architecture Requirements

1. Backend layers:
- controller, dto, service, repository, entity, config/security, exception.

2. Security building blocks:
- SecurityFilterChain for route rules.
- JWT authentication filter to parse and validate access token.
- JwtTokenService for create/parse/validate token.
- AuthExceptionHandler for consistent 401/403 responses.
- Password hashing with BCrypt.

3. Session lifecycle:
- Login issues access token + refresh token.
- Refresh endpoint rotates refresh token:
  - old refresh token revoked
  - new refresh token issued
  - new access token issued
- Logout revokes current refresh token.
- Admin can revoke all active sessions for a user.

4. Refresh token persistence model:
- Store refresh token records in DB with status fields.
- Include token identifier, user relation, issuedAt, expiresAt, revokedAt, replacedByTokenId or equivalent.
- Prefer storing a token hash instead of raw token when reasonable.

5. Cookie policy:
- HttpOnly true.
- SameSite explicitly configured.
- Secure configurable by profile (false for local http, true for https).

## API Requirements

Public endpoints:
- POST /api/auth/login
- POST /api/auth/refresh
- POST /api/auth/logout

Protected sample endpoint:
- GET /api/me

Admin endpoints:
- GET /api/admin/users
- PATCH /api/admin/users/{id}/lock
- PATCH /api/admin/users/{id}/unlock
- POST /api/admin/users/{id}/sessions/revoke
- GET /api/admin/audit-logs

Swagger:
- Expose OpenAPI and Swagger UI locally.
- Add bearer auth scheme in OpenAPI.

## Frontend Requirements

1. React pages:
- Login page.
- Basic USER page (profile/session status).
- Admin dashboard pages:
  - users table
  - lock/unlock actions
  - revoke sessions action
  - audit logs table

2. Frontend auth behavior:
- Keep access token in memory store.
- Use cookie-based refresh flow.
- Implement automatic refresh on 401 once, then retry original request.
- Route guards for USER/ADMIN.

## Observability and Safety Requirements

- Write audit events for auth and admin security actions.
- Include event type, actor, target, timestamp, result.
- Add basic rate limiting for login and refresh endpoints.
- Add basic security headers suitable for local web app training.

## Testing Requirements (Pragmatic)

Include at least:
- Unit tests for token service and auth service logic.
- Integration tests for login, refresh rotation, logout, role denial, lock/unlock.
- One frontend test for route guard behavior.

Target balance:
- Enough tests to protect core auth/session logic without overengineering.

## Implementation Order (Must Follow)

1. Scaffold backend dependencies and config.
2. Create entities/repositories for User, RefreshToken, AuditLog.
3. Implement JWT token service.
4. Implement login/refresh/logout services with rotation.
5. Implement JWT filter and SecurityFilterChain.
6. Implement admin security APIs.
7. Enable Swagger and verify auth flows manually.
8. Scaffold React app with login + admin pages.
9. Add tests and finalize docs.

## Output Format

Return results in 3 phases:

1. Phase 1 - Design
- architecture summary
- auth/session flow narrative
- schema summary

2. Phase 2 - Build
- file-by-file implementation
- run instructions
- sample curl for login/refresh/logout/admin endpoints

3. Phase 3 - Verify
- test summary
- known limitations
- next-step hardening backlog

Do not introduce OAuth2 or production cloud complexity.
