# Threat Model (Phase 1)

## STRIDE-Oriented Threat List

| Category | Example Threat | Mitigation Now | Hardening Later |
|---|---|---|---|
| Spoofing | Stolen credentials used to login | BCrypt password verification, account lock flag support, audit logging | Add MFA and adaptive risk checks |
| Tampering | JWT modified by client | Signed JWT (HMAC), strict validation in JwtTokenService | Key rotation policy and key versioning |
| Repudiation | Admin denies lock/unlock action | Immutable audit records with actor, target, timestamp, result | Signed/append-only audit sink export |
| Information Disclosure | Refresh token theft from frontend JS | HttpOnly cookie transport, token hash only in DB, access token in memory | Secure=true in HTTPS profiles, cookie partitioning review |
| Denial of Service | Brute force login and refresh abuse | Bucket4j rate limits on login/refresh endpoints | Distributed rate limiting and IP reputation |
| Elevation of Privilege | USER calls admin endpoints | SecurityFilterChain + @PreAuthorize hasRole('ADMIN') | Add permission granularity beyond role-only checks |

## Session-Specific Abuse Cases

1. Reuse of old refresh token after rotation.
- Current mitigation: old token marked revoked and replacedByTokenId set.
- Later hardening: detect replay and revoke full token family if replay occurs.

2. Stolen access token during TTL window.
- Current mitigation: short access TTL (15 minutes), role checks each request.
- Later hardening: token binding, device/session fingerprinting.

3. Concurrent refresh race.
- Current mitigation: revoked state check and one-time retry behavior in frontend.
- Later hardening: explicit token family concurrency guard and replay telemetry.

## Residual Risk (Accepted For Lab)

- In-memory rate limit maps can grow with unique IPs.
- Local HTTP profile uses Secure=false cookie for developer convenience.
- H2 in-memory database is not durable and is intended only for training.
