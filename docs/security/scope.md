# Security Scope

## Goal

Create a web-only fullstack security training lab that is practical, understandable, and close to production architecture without production-only complexity.

## Assets To Protect

- User identities and password hashes.
- Access tokens and refresh token lifecycle state.
- Role assignments (USER, ADMIN).
- Admin security actions.
- Audit records for auth and admin events.

## Actors

- Anonymous user.
- Authenticated USER.
- Authenticated ADMIN.
- Internal maintainer (local environment).

## Trust Boundaries

1. Browser runtime: JS memory vs HttpOnly cookie boundary.
2. Frontend SPA to backend API over HTTP localhost.
3. Backend service layer to H2 persistence.
4. Admin privilege boundary within API and UI routing.

## In Scope

- Login, refresh rotation, logout, revocation.
- RBAC USER/ADMIN route and endpoint protection.
- Admin dashboard actions: users, lock/unlock, revoke sessions, audit logs.
- Basic hardening: rate limiting + security headers.
- Swagger bearer auth for manual testing.

## Acceptance Criteria

- Auth and session lifecycle are explicit and testable.
- Refresh tokens are server-managed and revocable.
- Admin-only operations are enforced server-side.
- Audit events are generated for auth and admin actions.
