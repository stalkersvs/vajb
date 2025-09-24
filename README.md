# Full-stack Starter (React + Spring Boot + PostgreSQL + Redis + Kafka)

## Prerequisites
- Docker Desktop
- Java 21 (JDK)
- Maven (optional if using IDE with Maven wrapper)
- Node.js 20+

## 1) Infra (Docker)
```powershell
cd .\infra
docker compose --env-file .\env.dev up -d
```
Services:
- Postgres: localhost:5432 (appdb/appuser/apppassword)
- Redis: localhost:6379 (password: redispassword)
- Kafka: localhost:9092

## 2) Backend (Spring Boot)
Update `application.yml` if needed. Start:
```powershell
# Build
powershell -NoProfile -Command "Set-Location -Path '.\backend'; & 'mvn' '-DskipTests' 'package'"

# Run
powershell -NoProfile -Command "Set-Location -Path '.\backend'; & 'mvn' 'spring-boot:run'"
```
Endpoints:
- POST /api/auth/register { email, password }
- POST /api/auth/login { email, password } -> { token }
- GET /api/auth/me (Bearer token)
- POST /api/auth/logout (blacklist token)

## 3) Frontend (Vite React)
```powershell
cd .\frontend
npm install
npm run dev
```
The dev server proxies `/api` to `http://localhost:8080`.

## Notes
- JWT secret and expiration configured in `backend/src/main/resources/application.yml`.
- Token is stored in localStorage in the browser and auto-attached via Axios interceptor.
- Kafka topic `user-events` is provisioned by the backend config.

