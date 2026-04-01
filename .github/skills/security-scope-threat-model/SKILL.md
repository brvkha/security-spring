---
name: security-scope-threat-model
description: "Use when starting a new web security project, defining trust boundaries, selecting auth model, and producing a practical threat model before coding."
---

# Security Scope and Threat Model

## Purpose

Turn an idea into a concrete security scope so implementation choices are intentional.

## Inputs

- App type (web only, fullstack or backend-only)
- User roles and critical business actions
- Data sensitivity and compliance constraints
- Non-goals (what not to implement now)

## Workflow

1. Define assets to protect:
- identities, sessions, role assignments, admin actions, audit records.

2. Define actors:
- anonymous user, authenticated user, admin, internal operator.

3. Define trust boundaries:
- browser, frontend app, backend API, database.

4. Pick session strategy:
- access token style, refresh strategy, storage, revocation model.

5. Produce threat list using simple STRIDE framing:
- spoofing, tampering, repudiation, information disclosure, denial of service, privilege escalation.

6. For each threat, pick one mitigation now and one later hardening action.

7. Create acceptance criteria for security scope.

## Output Artifacts

- docs/security/scope.md
- docs/security/threat-model.md
- docs/security/non-goals.md

## Definition of Done

- Auth model is explicit and justified.
- Token/session lifecycle is documented.
- Role model is explicit.
- Top threats and mitigations are recorded.
- Non-goals are listed to prevent scope creep.

## Common Pitfalls

- Starting with framework config before defining threats.
- Mixing authentication and authorization requirements.
- No explicit revocation strategy.
