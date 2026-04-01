---
name: security-testing-hardening
description: "Use when finishing a security-focused web project to add pragmatic tests, hardening controls, and a repeatable validation checklist before declaring done."
---

# Security Testing and Hardening

## Purpose

Prevent regressions in auth/session logic and raise baseline safety without enterprise-level overhead.

## Inputs

- Implemented auth/session flows
- Admin security actions
- Current test setup

## Workflow

1. Add backend unit tests for:
- JWT create/parse validation
- auth decision logic (role, lock status, token status)

2. Add backend integration tests for:
- login success/failure
- refresh rotation correctness
- logout revocation
- admin role denial and allow paths

3. Add frontend tests for:
- route guard behavior (unauthenticated and non-admin)
- admin action UI state on success/failure

4. Add API abuse protections:
- basic rate limiting on login and refresh
- clear error payload for throttling

5. Add baseline hardening:
- security headers appropriate for web app
- strict CORS policy for local dev setup

6. Add verification checklist document:
- auth happy path
- token invalid/expired paths
- role denial path
- lock/unlock path
- session revoke path

7. Produce final validation notes with known limitations.

## Output Artifacts

- test classes/spec files
- security hardening configuration updates
- docs/security/verification-checklist.md
- docs/security/limitations.md

## Definition of Done

- Core auth/session flows have automated test coverage.
- Regressions in refresh rotation are detectable.
- Admin-only boundaries are verified by tests.
- Basic hardening controls are enabled and documented.

## Common Pitfalls

- Testing only happy paths.
- Skipping invalid/revoked/expired token cases.
- No explicit verification checklist after implementation.
