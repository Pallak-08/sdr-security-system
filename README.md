# SDR Security System

A web-based security system for authenticating Software Defined Radio (SDR) devices. Built as an internship project at DRDO.

Devices present a Device ID and API Key for verification. All attempts are logged, monitored via an admin dashboard, and suspicious activity triggers live threat alerts.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular 17 (standalone components) |
| Backend | Spring Boot 3.2 (Java 17) |
| Database | H2 (file-based, persists on disk) |
| Deployment | Docker + Docker Compose |

---

## Features

### Device Verification
- POST-based verification (credentials never exposed in URL)
- Returns APPROVED / REJECTED / LOCKED status with reason
- All attempts logged to database with timestamp

### Brute-Force Lockout
- 3 consecutive failed attempts locks a device for 5 minutes
- Locked devices return LOCKED status in logs
- Counter resets automatically after lockout expires or on successful login

### Admin Dashboard
- Session token-based login (UUID token, stored in localStorage)
- All admin endpoints require `X-Admin-Token` header — returns 401 otherwise
- Live threat alert banner flashes red when a REJECTED/LOCKED attempt is detected
- Logs auto-refresh every 8 seconds
- Add and remove devices
- View full verification history per device in a modal

### Security
- CORS restricted to `http://localhost:4200`
- H2 console restricted to localhost only
- API keys masked in Device List UI
- Angular AuthGuard protects `/admin-dashboard` route
- Input validation on all endpoints

---

## Project Structure

```
sdr-security-system/
├── sdr-backend/                  # Spring Boot backend
│   ├── src/main/java/com/example/server/
│   │   ├── ServerApplication.java
│   │   ├── HelloController.java      # /verify, /logs, /logs/{deviceId}
│   │   ├── AdminController.java      # /admin/* endpoints
│   │   ├── VerificationService.java  # business logic + lockout
│   │   ├── VerificationLog.java      # JPA entity
│   │   └── VerificationLogRepository.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── devices.properties        # seed devices
│   └── devices.properties            # runtime device registry
│
├── sdr-frontend/                 # Angular frontend
│   └── src/app/
│       ├── pages/landing/            # home page
│       ├── pages/verification/       # device verify page
│       ├── pages/admin-login/        # admin login
│       ├── pages/admin-dashboard/    # dashboard + device list
│       ├── auth.guard.ts             # route guard
│       └── app.routes.ts
│
└── docker-compose.yml
```

---

## How to Run Locally

### Prerequisites
- Java 17+
- Node.js 18+ and Angular CLI (`npm install -g @angular/cli`)
- Maven (or use the included `./mvnw` wrapper)

### Terminal 1 — Backend
```bash
cd sdr-backend
./mvnw spring-boot:run
```
Backend runs at **http://localhost:8080**

### Terminal 2 — Frontend
```bash
cd sdr-frontend
npm install
ng serve
```
Frontend runs at **http://localhost:4200**

---

## Demo Credentials

### Admin Login
| Field | Value |
|---|---|
| Username | `admin` |
| Password | `drdo@2024` |

### Pre-registered Test Devices
| Device ID | API Key |
|---|---|
| SDR001 | 12345 |
| SDR002 | ABCDE |
| SDR003 | 99999 |

---

## API Endpoints

### Public
| Method | Endpoint | Description |
|---|---|---|
| POST | `/verify` | Verify a device (body: `deviceId`, `apiKey`) |

### Admin (requires `X-Admin-Token` header)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/admin/login` | Login, returns session token |
| POST | `/admin/logout` | Invalidate token |
| POST | `/admin/add-device` | Register a new device |
| DELETE | `/admin/delete-device/{deviceId}` | Remove a device |
| GET | `/admin/devices` | List all registered devices |
| GET | `/logs` | All verification logs |
| GET | `/logs/{deviceId}` | Logs for a specific device |

---

## Docker Deployment

```bash
docker-compose up --build
```

- Backend: http://localhost:8080
- Frontend: http://localhost:4200

---

## Future Work (for next interns)

- [ ] Replace H2 with PostgreSQL for production-grade persistence
- [ ] Hash API keys with SHA-256 (never store plaintext secrets)
- [ ] Add HTTPS / TLS (mandatory for any real deployment)
- [ ] Rate limiting on `/verify` (Spring `@RateLimiter` or Bucket4j)
- [ ] WebSocket-based real-time alerts instead of polling
- [ ] Role-based access control (super-admin vs read-only admin)
- [ ] Pagination on verification logs for large datasets
- [ ] Email/SMS alert on repeated failed attempts

---

## Author

Pallak Khullar — DRDO Internship 2026
