---
name: security-spring-foundation
description: "Use when building or refactoring baseline Spring Security architecture: SecurityFilterChain, JWT filter, exception handling, RBAC USER/ADMIN, and Swagger-ready API security."
---

# Spring Security Foundation

## Purpose

Create a clean, reusable backend security baseline that is easy to reason about and test.

## Inputs

- Java and Spring Boot version
- Token TTL and issuer settings
- Role model (USER/ADMIN)
- Public vs protected route list

## Workflow

1. Add dependencies:
- spring-boot-starter-security
- spring-boot-starter-validation
- jjwt-api + jjwt-impl + jjwt-jackson
- springdoc-openapi starter

2. Define configuration properties:
- jwt issuer, secret, access ttl
- refresh ttl
- cookie settings (httpOnly, sameSite, secure)
- lockout and rate-limit basics

3. Create security core components:
- JwtTokenService
- JwtAuthenticationFilter (OncePerRequestFilter)
- SecurityFilterChain
- AuthExceptionHandler for 401/403

4. Define route security policy:
- permit auth endpoints and swagger docs
- protect app endpoints
- enforce admin-only routes

5. Set authorization style:
- URL rules in chain + method-level checks where needed

6. Wire Swagger security schema:
- bearer auth for protected endpoints

7. Validate manually:
- request with no token -> 401 on protected routes
- request with wrong role -> 403
- request with valid token -> 200

## Output Artifacts

- config/security classes
- auth token service classes
- consistent error payload contract
- openapi security definition

## Definition of Done

- SecurityContext is set only by validated token.
- Public/protected/admin routes behave as expected.
- 401 and 403 responses are stable and documented.
- Swagger can test bearer-protected endpoints.

## Common Pitfalls

- Missing stateless session policy.
- Incorrect authority naming (ROLE_ prefix mismatch).
- Catching JWT exceptions but returning inconsistent auth state.
