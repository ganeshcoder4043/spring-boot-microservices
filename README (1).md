# 🏨 Hotel Booking & Review — Spring Boot Microservices Platform

A production-style microservices system built with **Spring Boot 3/4**, **Spring Cloud**, and **OAuth2**, demonstrating service discovery, centralized configuration, an API Gateway with multi-provider authentication, inter-service communication via **OpenFeign**, and fault tolerance via **Resilience4j**.

---

## 📐 Architecture Overview

```
                                ┌───────────────────────┐
                                │   Service Registry     │
                                │   (Eureka Server)      │
                                │   port: 8761            │
                                └───────────▲─────────────┘
                                            │ registers
                    ┌───────────────────────┼──────────────────────┐
                    │                       │                      │
        ┌───────────┴───────────┐ ┌─────────┴─────────┐ ┌──────────┴──────────┐
        │      API Gateway       │ │   Config Server    │ │   (all microservices  │
        │  (Spring Cloud Gateway │ │   port: 8085        │ │    register with       │
        │   + OAuth2 / Okta +    │ │   Git-backed config │ │    Eureka)             │
        │   Google login)        │ │                      │ │                        │
        │   port: 8084            │ └──────────────────────┘ └────────────────────────┘
        └───────────┬────────────┘
                     │ routes requests
        ┌────────────┼──────────────────────────┐
        │            │                          │
┌───────┴───────┐ ┌──┴──────────────┐ ┌─────────┴─────────┐
│  User Service  │ │  Hotel Service   │ │   Rating Service    │
│  port: 8081     │ │  port: 8082       │ │   port: 8083          │
│  MySQL          │ │  PostgreSQL       │ │   MongoDB              │
│  (Feign client  │ │  (JPA)            │ │   (JPA/Mongo)           │
│  → Hotel &      │ │                   │ │                         │
│  Rating svc)    │ │                   │ │                         │
└─────────────────┘ └───────────────────┘ └─────────────────────────┘
```

All services register themselves with **Eureka** for discovery, pull shared configuration from the **Config Server** (Git-backed), and are fronted by a single **API Gateway** that terminates OAuth2 login and forwards authenticated traffic downstream.

---

## 🧩 Services

| Service | Port | Database | Responsibility |
|---|---|---|---|
| **ServiceRegistry** | 8761 | — | Eureka server; central service discovery |
| **ConfigServer** | 8085 | — | Serves shared config from a Git repo to every service |
| **ApiGateway** | 8084 | — | Single entry point; Spring Cloud Gateway (WebFlux); OAuth2 login (Okta OIDC + Google); JWT-based resource server; routes to downstream services |
| **UserService** | 8081 | MySQL | User CRUD; aggregates a user's hotel ratings by calling RatingService + HotelService via **OpenFeign**; wraps calls with **Resilience4j** (Retry, Circuit Breaker, Rate Limiter) |
| **HotelService** | 8082 | PostgreSQL | Hotel CRUD; role/scope-protected endpoints (`ADMIN`, `SCOPE_internal`) |
| **RatingService** | 8083 | MongoDB | Rating CRUD; lookups by `userId` / `hotelId`; role/scope-protected endpoints |

---

## 🛠️ Tech Stack

- **Language / Runtime:** Java 17–21
- **Framework:** Spring Boot 3.x / 4.x, Spring Cloud 2023–2025 release train
- **Service Discovery:** Netflix Eureka (`spring-cloud-starter-netflix-eureka-client` / `-server`)
- **Config Management:** Spring Cloud Config Server (Git-backed)
- **API Gateway:** Spring Cloud Gateway (reactive / WebFlux)
- **Security:** Spring Security + OAuth2 (Okta OIDC provider, Google provider), JWT resource server, method-level security (`@PreAuthorize`)
- **Inter-service Communication:** OpenFeign, `RestTemplate` with `@LoadBalanced`
- **Resilience:** Resilience4j — Circuit Breaker, Retry, Rate Limiter
- **Persistence:** MySQL (UserService), PostgreSQL (HotelService), MongoDB (RatingService)
- **Build Tool:** Maven (with Maven Wrapper `mvnw`)
- **Observability:** Spring Boot Actuator (health, metrics, circuit-breaker health indicators)

---

## 🔐 Security Model

- **API Gateway**
  - `oauth2Login` — supports Okta (OIDC) and Google sign-in
  - `oauth2Client` — manages authorized client tokens
  - `oauth2ResourceServer` (JWT) — validates bearer tokens on incoming requests
  - Public routes: `/auth/okta/login`, `/auth/google/login`, `/oauth2/**`, `/login/**`; everything else requires authentication
- **Downstream services** (User / Hotel / Rating)
  - Each is a JWT **resource server** validating tokens issued by Okta
  - Method-level authorization via `@PreAuthorize("hasRole('ADMIN')")` / `@PreAuthorize("hasAuthority('SCOPE_internal')")`
- **Service-to-service calls** (UserService → HotelService/RatingService)
  - Use the OAuth2 **client_credentials** grant (`my-internal-client`) so Feign/RestTemplate calls carry a valid internal `Bearer` token

> ⚠️ **Note:** The current `application.yml` files contain hard-coded Okta/Google client secrets and DB passwords for local development convenience. **Before pushing to a public repo or deploying anywhere**, move these into environment variables or a secrets manager (Vault, AWS Secrets Manager, etc.) and rotate the exposed credentials.

---

## 🧯 Resilience (UserService → Hotel/Rating calls)

Configured per-endpoint in `UserService/application.yml`:

| Pattern | Purpose |
|---|---|
| **Retry** (`ratingHotelRetry`) | Up to 3 attempts, 1s wait, retries on timeouts/`IOException`/`HttpServerErrorException` |
| **Circuit Breaker** (`ratingHotelCircuitBreaker`) | Opens at 50% failure rate (min. 5 calls), stays open 6s, then half-opens for 3 trial calls |
| **Rate Limiter** (`userRateLimiter`) | 10 requests / 10s window per instance, 2s timeout |

Fallback methods return a safe dummy `User` response instead of propagating the failure to the client.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ (Java 21 recommended for newer modules)
- Maven (or use the bundled `mvnw` / `mvnw.cmd`)
- Running instances of: **MySQL**, **PostgreSQL**, **MongoDB**
- An Okta developer org (and/or Google OAuth2 credentials) if you want to exercise the login flow

### Startup order matters
Because everything depends on discovery and config, always start services in this order:

1. **ServiceRegistry** (Eureka) → `http://localhost:8761`
2. **ConfigServer** → `http://localhost:8085`
3. **ApiGateway**, **UserService**, **HotelService**, **RatingService** (any order — they'll register with Eureka once up)

### Run a single service
```bash
cd ServiceRegistry/ServiceRegistry
./mvnw spring-boot:run
```
Repeat for `ConfigServer`, `ApiGateway`, `UserService`, `HotelService`, `RatingService` in their own directories.

### Databases
Create the databases referenced in each service's `application.yml` before starting:
```sql
-- MySQL (UserService)
CREATE DATABASE microservices;

-- PostgreSQL (HotelService)
CREATE DATABASE microservices;
```
```bash
# MongoDB (RatingService) — created automatically on first write
mongodb://localhost:27017/Microservice
```

---

## 📡 Key Endpoints (via API Gateway, port 8084)

| Method | Path | Service | Auth |
|---|---|---|---|
| `POST` | `/users/create-user` | UserService | Authenticated |
| `GET` | `/users/{userId}` | UserService | Authenticated (Retry + Circuit Breaker + Rate Limit) |
| `GET` | `/users/all-user` | UserService | Authenticated |
| `POST` | `/hotels/hotel-create` | HotelService | `ADMIN` |
| `GET` | `/hotels/all-hotels` | HotelService | Authenticated |
| `GET` | `/hotels/{hotelId}` | HotelService | `SCOPE_internal` |
| `POST` | `/ratings/create-rating` | RatingService | `ADMIN` |
| `GET` | `/ratings/getAllRatings` | RatingService | Authenticated |
| `GET` | `/ratings/users/{userId}` | RatingService | `SCOPE_internal` |
| `GET` | `/auth/okta/login` | ApiGateway | Public (starts OIDC flow) |
| `GET` | `/auth/google/login` | ApiGateway | Public (starts OAuth2 flow) |

---

## 📁 Project Structure

```
Microservices Learn/
├── ServiceRegistry/     # Eureka server
├── ConfigServer/        # Centralized Git-backed config
├── ApiGateway/          # Gateway + OAuth2 login (Okta/Google)
├── UserService/         # User CRUD + Feign aggregation + Resilience4j
├── HotelService/        # Hotel CRUD (PostgreSQL)
└── RatingService/       # Rating CRUD (MongoDB)
```

---

## 🗺️ Roadmap / Possible Improvements

- [ ] Move all secrets out of `application.yml` into environment variables / a vault
- [ ] Add a distributed tracing setup (Micrometer Tracing + Zipkin) across the gateway and services
- [ ] Add Dockerfiles + `docker-compose.yml` to spin up all services + DBs together
- [ ] Add integration tests for the Feign clients using WireMock
- [ ] Add centralized logging (ELK/Loki) for correlating requests across services

---

## 👤 Author

**Ganesh Kumar**
Aspiring Full Stack Java Backend Developer
GitHub: [github.com/ganeshcoder4043](https://github.com/ganeshcoder4043) · LinkedIn: [linkedin.com/in/ganesh-kumar-coder](https://linkedin.com/in/ganesh-kumar-coder/)
