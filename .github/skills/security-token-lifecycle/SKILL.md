---
name: security-token-lifecycle
description: "Use when implementing login, refresh rotation, logout revocation, and HttpOnly-cookie refresh workflows with DB-backed token state."
---

# Token Lifecycle and Session Control

## Purpose

Implement reliable session management that avoids access-token-only weaknesses.

## Inputs

- Access TTL and refresh TTL
- Cookie policy
- Session revocation requirements

## Workflow

1. Model refresh token persistence:
- token id
- user id
- token hash or token reference
- issuedAt, expiresAt, revokedAt
- parent/replacedBy reference for rotation chain

2. Login flow:
- validate credentials
- issue short-lived access JWT
- issue refresh token and persist record
- set refresh token in HttpOnly cookie

3. Refresh flow with rotation:
- validate incoming refresh token against DB
- deny if revoked/expired/not found
- revoke old refresh token
- mint and persist new refresh token
- mint new access token
- update cookie with new refresh token

4. Logout flow:
- revoke active refresh token from cookie
- clear refresh cookie

5. Admin revoke flow:
- revoke all active refresh tokens for selected user

6. Safety controls:
- add lockout for repeated login failure
- add simple endpoint rate limits for login/refresh

7. Failure contracts:
- stable response for invalid, expired, revoked token cases

## Output Artifacts

- refresh token entity/repository/service
- login, refresh, logout endpoints
- admin session revocation endpoint
- cookie utility/config

## Definition of Done

- Refresh rotation works and old token cannot be reused.
- Logout invalidates refresh token server-side.
- Admin can force session revoke for a user.
- Access token TTL and refresh token TTL are configurable.

## Common Pitfalls

- Not rotating refresh token.
- Storing refresh token in localStorage.
- Not clearing cookie on logout.
- No strategy for concurrent refresh attempts.
