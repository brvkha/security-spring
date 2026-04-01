# Phase 2 - Build

## Purpose

Translate the approved Phase 1 design into an implementation checklist that can be executed in order, with runnable local commands and API smoke scripts.

## Checklist Convention

- [ ] not started
- [x] completed
- Keep the implementation order unchanged.

## 1. File-by-File Build Checklist (Must Follow Order)

### Step 1 - Scaffold backend dependencies and config

- [ ] backend/pom.xml
  - Add Spring Security, Validation, JPA, H2, JWT, OpenAPI, Bucket4j, test dependencies.
- [ ] backend/src/main/resources/application.yml
  - Add JWT TTL config, refresh TTL config, cookie policy, rate-limit settings, H2 and springdoc paths.
- [ ] backend/src/main/resources/application-test.yml
  - Add test profile DB and ddl settings.
- [ ] backend/src/main/java/com/example/security/config/SecurityConfig.java
  - Stateless SecurityFilterChain, route authorization rules, CORS, BCrypt bean.
- [ ] backend/src/main/java/com/example/security/config/OpenApiConfig.java
  - Add bearerAuth schema and global security requirement.
- [ ] backend/src/main/java/com/example/security/config/RateLimitConfig.java
  - Add login and refresh token-bucket resolvers.
- [ ] backend/src/main/java/com/example/security/config/SecurityHeadersFilter.java
  - Add baseline browser security headers for local lab.
- [ ] backend/src/main/java/com/example/security/exception/AuthExceptionHandler.java
  - Add consistent 401 and 403 response contract.

### Step 2 - Create entities and repositories

- [ ] backend/src/main/java/com/example/security/entity/Role.java
  - USER and ADMIN enum only.
- [ ] backend/src/main/java/com/example/security/entity/User.java
  - username, password hash, role, locked/enabled status.
- [ ] backend/src/main/java/com/example/security/entity/RefreshToken.java
  - token hash, user FK, issued/expires/revoked timestamps, replacement chain marker.
- [ ] backend/src/main/java/com/example/security/entity/AuditLog.java
  - event type, actor, target, result, timestamp, details.
- [ ] backend/src/main/java/com/example/security/repository/UserRepository.java
- [ ] backend/src/main/java/com/example/security/repository/RefreshTokenRepository.java
- [ ] backend/src/main/java/com/example/security/repository/AuditLogRepository.java
- [ ] backend/src/main/java/com/example/security/service/DataSeeder.java
  - Seed admin and user demo accounts.
- [ ] backend/src/main/resources/data.sql
  - Keep compatible with JPA/bootstrap strategy.

### Step 3 - Implement JWT token service

- [ ] backend/src/main/java/com/example/security/service/JwtTokenService.java
  - generateAccessToken, parseToken, validateToken, extractUsername.

### Step 4 - Implement login/refresh/logout with rotation

- [ ] backend/src/main/java/com/example/security/dto/LoginRequest.java
- [ ] backend/src/main/java/com/example/security/dto/AuthResponse.java
- [ ] backend/src/main/java/com/example/security/exception/AuthException.java
- [ ] backend/src/main/java/com/example/security/service/RefreshTokenService.java
  - secure token generation, SHA-256 hashing, revoke and revoke-all logic.
- [ ] backend/src/main/java/com/example/security/service/AuthService.java
  - login, refresh rotation, logout, refresh cookie set/clear.
- [ ] backend/src/main/java/com/example/security/controller/AuthController.java
  - /login, /refresh, /logout with rate-limit checks.

### Step 5 - Implement JWT filter and SecurityFilterChain wiring

- [ ] backend/src/main/java/com/example/security/security/JwtAuthenticationFilter.java
  - Parse Bearer token and set SecurityContext.
- [ ] backend/src/main/java/com/example/security/config/SecurityConfig.java
  - Register JWT filter before UsernamePasswordAuthenticationFilter.
- [ ] backend/src/main/java/com/example/security/dto/ErrorResponse.java
  - Stable error payload for auth and access errors.
- [ ] backend/src/main/java/com/example/security/exception/GlobalExceptionHandler.java
  - Consistent non-auth exception responses.

### Step 6 - Implement admin security APIs

- [ ] backend/src/main/java/com/example/security/dto/UserDto.java
- [ ] backend/src/main/java/com/example/security/dto/AuditLogDto.java
- [ ] backend/src/main/java/com/example/security/service/AuditLogService.java
  - Record auth/admin actions and query logs.
- [ ] backend/src/main/java/com/example/security/service/UserAdminService.java
  - lock/unlock, session revoke, user list.
- [ ] backend/src/main/java/com/example/security/controller/AdminController.java
  - /api/admin/users, lock/unlock, revoke, audit-logs.
- [ ] backend/src/main/java/com/example/security/controller/MeController.java
  - Protected sample endpoint /api/me.

### Step 7 - Enable Swagger and manually verify flows

- [ ] backend/src/main/java/com/example/security/config/OpenApiConfig.java
  - Confirm bearer auth appears in Swagger UI.
- [ ] backend/src/main/java/com/example/security/controller/AuthController.java
  - Confirm public auth endpoints are visible.
- [ ] backend/src/main/java/com/example/security/controller/AdminController.java
  - Confirm admin endpoints are bearer-protected.

### Step 8 - Scaffold React login + user + admin pages

- [ ] frontend/package.json
  - React router, axios, test deps.
- [ ] frontend/src/api/axios.js
  - attach access token, one-time refresh retry queue.
- [ ] frontend/src/store/authStore.js
  - in-memory auth state only (no localStorage).
- [ ] frontend/src/contexts/AuthContext.jsx
  - login/logout actions and role state.
- [ ] frontend/src/components/RequireAuth.jsx
  - auth guard and role guard.
- [ ] frontend/src/pages/LoginPage.jsx
- [ ] frontend/src/pages/MePage.jsx
- [ ] frontend/src/pages/UnauthorizedPage.jsx
- [ ] frontend/src/pages/admin/UsersPage.jsx
  - list, lock/unlock, revoke sessions action.
- [ ] frontend/src/pages/admin/AuditLogsPage.jsx
  - paged table of security events.
- [ ] frontend/src/App.jsx
  - route map with USER and ADMIN restrictions.
- [ ] frontend/src/main.jsx
  - app bootstrap.

### Step 9 - Add tests and finalize docs

- [ ] backend/src/test/java/com/example/security/service/JwtTokenServiceTest.java
- [ ] backend/src/test/java/com/example/security/service/RefreshTokenServiceTest.java
- [ ] backend/src/test/java/com/example/security/integration/AuthIntegrationTest.java
  - login, refresh rotation, role denial, lock behavior.
- [ ] frontend/src/test/RequireAuth.test.jsx
  - route guard behavior.
- [ ] docs/security/phase-1-design.md
- [ ] docs/security/phase-2-build.md

## 2. Run Instructions

## 2.1 Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+
- npm 9+

## 2.2 Start backend

    cd backend
    mvn clean spring-boot:run

Backend runs at: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui.html

H2 Console: http://localhost:8080/h2-console

## 2.3 Start frontend

    cd frontend
    npm install
    npm run dev

Frontend runs at: http://localhost:5173

## 2.4 Run tests

Backend tests:

    cd backend
    mvn test

Frontend tests:

    cd frontend
    npx vitest run

## 3. Sample cURL Scripts (Same Endpoint Set as Design)

Demo users (seeded):

- admin / admin123
- user / user123

## 3.1 Login (store cookie)

    curl -i -c cookies.txt -X POST http://localhost:8080/api/auth/login \
      -H "Content-Type: application/json" \
      -d '{"username":"admin","password":"admin123"}'

Copy accessToken from response body into TOKEN.

## 3.2 Call protected endpoint

    curl -i -H "Authorization: Bearer TOKEN" http://localhost:8080/api/me

## 3.3 Refresh (rotate refresh token)

    curl -i -b cookies.txt -c cookies.txt -X POST http://localhost:8080/api/auth/refresh

Copy new accessToken from response body and replace TOKEN.

## 3.4 Admin: list users

    curl -i -H "Authorization: Bearer TOKEN" http://localhost:8080/api/admin/users

## 3.5 Admin: lock a user

    curl -i -X PATCH -H "Authorization: Bearer TOKEN" http://localhost:8080/api/admin/users/2/lock

## 3.6 Admin: unlock a user

    curl -i -X PATCH -H "Authorization: Bearer TOKEN" http://localhost:8080/api/admin/users/2/unlock

## 3.7 Admin: revoke all sessions for a user

    curl -i -X POST -H "Authorization: Bearer TOKEN" http://localhost:8080/api/admin/users/2/sessions/revoke

## 3.8 Admin: read audit logs

    curl -i -H "Authorization: Bearer TOKEN" "http://localhost:8080/api/admin/audit-logs?page=0&size=20"

## 3.9 Logout

    curl -i -b cookies.txt -X POST http://localhost:8080/api/auth/logout

## 4. Manual Verification Checklist

- [ ] Login returns accessToken and sets HttpOnly refresh cookie.
- [ ] Access to /api/me fails without bearer and succeeds with bearer.
- [ ] Refresh rotates cookie token value and returns new accessToken.
- [ ] USER role gets 403 on /api/admin/users.
- [ ] ADMIN can lock/unlock and revoke user sessions.
- [ ] Audit logs contain LOGIN, REFRESH, LOGOUT, LOCK_USER, UNLOCK_USER, REVOKE_SESSIONS events.
