---
name: security-admin-dashboard
description: "Use when building a React admin dashboard for security operations: user list, lock/unlock, session revoke, and audit log views with ADMIN-only protection."
---

# Security Admin Dashboard

## Purpose

Deliver an admin panel that exercises and validates core security controls end-to-end.

## Inputs

- Admin API contract
- Role model and route protection rules
- Audit event schema

## Workflow

1. Build admin API endpoints:
- list users
- lock user
- unlock user
- revoke user sessions
- list audit logs

2. Enforce ADMIN authorization in backend.

3. Create React admin routes:
- /admin/users
- /admin/audit-logs

4. Add route guards:
- reject non-authenticated users
- reject non-admin users

5. Implement user management UI:
- searchable/paged user table
- lock/unlock actions with confirmation
- revoke sessions action with visible result state

6. Implement audit log UI:
- table with actor, action, target, result, timestamp
- filtering by event type and date range (simple)

7. UX safety:
- clear loading and error states
- avoid hidden failures on admin actions

## Output Artifacts

- admin backend controller/service methods
- admin frontend pages and route guards
- audit log views and action feedback states

## Definition of Done

- Non-admin users cannot access admin routes or APIs.
- Admin can complete all core actions.
- Audit events are visible after auth/admin actions.
- Failed admin actions are clearly surfaced in UI.

## Common Pitfalls

- UI-only admin protection without backend enforcement.
- Missing confirmation for destructive admin actions.
- No audit visibility after security actions.
