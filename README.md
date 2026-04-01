# Security Spring — Fullstack Training Lab

A practical Spring Security training lab with JWT authentication, refresh token rotation, and a React admin dashboard.

## Architecture

```
backend/   — Spring Boot 3 / Java 17 REST API
frontend/  — React + Vite SPA
```

## Features

- **JWT access tokens** (15-minute TTL, stored in memory on client)
- **Refresh tokens** persisted in DB as SHA-256 hashes with rotation (7-day TTL, HttpOnly cookie)
- **RBAC** with USER and ADMIN roles
- **Admin dashboard**: user list, lock/unlock, revoke sessions, audit logs
- **Audit trail** for all auth and admin events
- **Rate limiting** on login/refresh (Bucket4j)
- **Swagger UI** at `http://localhost:8080/swagger-ui.html`
- **H2 console** at `http://localhost:8080/h2-console`

## Quick Start

### Backend
```bash
cd backend
mvn spring-boot:run
```
Starts on `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Starts on `http://localhost:5173`

## Demo Credentials

| Username | Password | Role  |
|----------|----------|-------|
| admin    | admin123 | ADMIN |
| user     | user123  | USER  |

## API Endpoints

### Public
- `POST /api/auth/login` — Login, returns access token + sets refresh cookie
- `POST /api/auth/refresh` — Rotate refresh token
- `POST /api/auth/logout` — Revoke refresh token

### Protected (requires Bearer token)
- `GET /api/me` — Current user info

### Admin (requires ADMIN role)
- `GET /api/admin/users` — List all users
- `PATCH /api/admin/users/{id}/lock` — Lock user
- `PATCH /api/admin/users/{id}/unlock` — Unlock user
- `POST /api/admin/users/{id}/sessions/revoke` — Revoke all sessions
- `GET /api/admin/audit-logs` — Paginated audit log

## Sample cURL

```bash
# Login
curl -c cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'

# Use access token (replace TOKEN)
curl -H 'Authorization: Bearer TOKEN' http://localhost:8080/api/me

# Refresh
curl -b cookies.txt -c cookies.txt -X POST http://localhost:8080/api/auth/refresh

# Logout
curl -b cookies.txt -X POST http://localhost:8080/api/auth/logout

# Admin - list users
curl -H 'Authorization: Bearer TOKEN' http://localhost:8080/api/admin/users
```

## Testing

```bash
# Backend (15 tests)
cd backend && mvn test

# Frontend (4 tests)
cd frontend && npx vitest run
```

## Security Design Notes

- Access token is stored **in memory** (not localStorage) on the client
- Refresh token is stored as **SHA-256 hash** in the database, never raw
- Refresh token rotation: old token revoked, new token issued on every refresh
- Cookie policy: `HttpOnly`, `SameSite=Strict`, `Secure=false` for local dev
- CSRF protection disabled intentionally (stateless JWT API + SameSite cookie)
- Security headers: `X-Content-Type-Options`, `X-Frame-Options`, `X-XSS-Protection`, `Cache-Control`
