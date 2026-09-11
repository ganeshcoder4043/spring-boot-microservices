# Microservices Learn — Hotel Booking Platform

A Spring Boot microservices project simulating a hotel booking system with service discovery, centralized configuration, API gateway routing, OAuth2 security (Okta + Google), Feign-based inter-service communication, and Resilience4j fault tolerance.

## Architecture Overview

```
                              ┌───────────────────────┐
                              │   ServiceRegistry      │
                              │   (Eureka Server)      │
                              │   Port: 8761           │
                              └───────────▲────────────┘
                                          │ register/discover
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
          ┌─────────▼─────────┐ ┌─────────▼─────────┐ ┌─────────▼─────────┐
          │   ApiGateway       │ │   ConfigServer     │ │  Downstream       │
          │   Port: 8084       │ │   Port: 8085       │ │  Services         │
          │   Spring Cloud     │ │   Git-backed config │ │  (below)          │
          │   Gateway (WebFlux)│ └────────────────────┘ └────────────────────┘
          └─────────┬──────────┘
                    │ routes /users/**, /hotels/**, /staffs/**, /ratings/**
        ┌───────────┼────────────────┬───────────────────┐
        ▼                            ▼                    ▼
┌───────────────┐          ┌──────────────────┐   ┌──────────────────┐
│  UserService   │──Feign──▶│  HotelService    │   │  RatingService    │
│  Port: 8081    │──Feign──▶│  Port: 8082      │   │  Port: 8083       │
│  MySQL         │          │  PostgreSQL      │   │  MongoDB          │
└───────────────┘          └──────────────────┘   └──────────────────┘
```

## Services

| Service | Port | Database | Responsibility |
|---|---|---|---|
| **ServiceRegistry** | 8761 | — | Eureka service discovery server |
| **ConfigServer** | 8085 | — | Centralized config, backed by a Git repo |
| **ApiGateway** | 8084 | — | Single entry point; routes requests, handles OAuth2 login (Okta + Google) |
| **UserService** | 8081 | MySQL | User CRUD; aggregates ratings + hotel data via Feign clients; Resilience4j (Retry, Circuit Breaker, Rate Limiter) |
| **HotelService** | 8082 | PostgreSQL | Hotel CRUD; staff listing |
| **RatingService** | 8083 | MongoDB | Rating CRUD, keyed by userId / hotelId |

## Tech Stack

- **Java 17 / 21**, **Spring Boot 3.x**, **Spring Cloud 2023.x**
- **Spring Cloud Gateway** (reactive, WebFlux) — API Gateway
- **Netflix Eureka** — service discovery
- **Spring Cloud Config** — centralized configuration (Git-backed)
- **Spring Cloud OpenFeign** — declarative REST clients between services
- **Spring Security OAuth2** (Client + Resource Server) — Okta (OIDC) + Google login
- **Resilience4j** — Circuit Breaker, Retry, Rate Limiter (used in UserService)
- **Spring Data JPA** (MySQL, PostgreSQL), **Spring Data MongoDB**
- **Lombok**, **Maven**

## Prerequisites

- JDK 17+ (some modules use 21)
- Maven (or use the included `mvnw` wrapper)
- MySQL running locally with a `microservices` database (UserService)
- PostgreSQL running locally with a `microservices` database (HotelService)
- MongoDB running locally (`Microservice` database, RatingService)
- An Okta developer account (OIDC app) and a Google OAuth2 client, if you want to test login end-to-end

## Configuration & Secrets

⚠️ **Do not commit real credentials.** The `application.yml` files in this repo currently contain plaintext client secrets, DB passwords, and Okta issuer/client IDs — treat those as placeholders and move them to environment variables or a private Config Server Git repo before pushing anywhere public. Example pattern:

```yaml
okta:
  oauth2:
    issuer: ${OKTA_ISSUER_URI}
    client-id: ${OKTA_CLIENT_ID}
    client-secret: ${OKTA_CLIENT_SECRET}
```

## Startup Order

Services must be started in this order so discovery/config/routing work correctly:

1. **ServiceRegistry** (Eureka) — `8761`
2. **ConfigServer** — `8085`
3. **HotelService**, **RatingService**, **UserService** — `8082`, `8083`, `8081`
4. **ApiGateway** — `8084` (last, since it routes to the others)

### Run each service

```bash
cd ServiceRegistry/ServiceRegistry && ./mvnw spring-boot:run
cd ConfigServer/ConfigServer && ./mvnw spring-boot:run
cd HotelService/HotelService && ./mvnw spring-boot:run
cd RatingService/RatingService && ./mvnw spring-boot:run
cd UserService/UserService && ./mvnw spring-boot:run
cd ApiGateway/ApiGateway && ./mvnw spring-boot:run
```

Eureka dashboard: http://localhost:8761

## API Endpoints (via Gateway, port 8084)

### UserService (`/users/**`)
| Method | Path | Notes |
|---|---|---|
| POST | `/users/create-user` | Create a user |
| GET | `/users/{userId}` | Fetch user + aggregated ratings/hotel data (Retry + Circuit Breaker + Rate Limiter) |
| GET | `/users/all-user` | List all users |
| PUT | `/users/{userId}` | Update user |
| DELETE | `/users/{userId}` | Delete user |

### HotelService (`/hotels/**`, `/staffs/**`)
| Method | Path | Notes |
|---|---|---|
| POST | `/hotels/hotel-create` | Requires `ROLE_ADMIN` |
| GET | `/hotels/all-hotels` | Public (authenticated) |
| GET | `/hotels/{hotelId}` | Requires `SCOPE_internal` (service-to-service) |
| PUT | `/hotels/{hotelId}` | Requires `ROLE_ADMIN` |
| DELETE | `/hotels/{hotelId}` | Requires `ROLE_ADMIN` |
| GET | `/staffs` | List staff |

### RatingService (`/ratings/**`)
| Method | Path | Notes |
|---|---|---|
| POST | `/ratings/create-rating` | Requires `ROLE_ADMIN` |
| GET | `/ratings/getAllRatings` | Public (authenticated) |
| GET | `/ratings/users/{userId}` | Requires `SCOPE_internal` |
| GET | `/ratings/hotels/{hotelId}` | Requires `ROLE_ADMIN` |

### Auth (ApiGateway)
| Method | Path | Notes |
|---|---|---|
| GET | `/auth/okta/login` | Returns JWT + refresh token after Okta OIDC login |
| GET | `/auth/google/login` | Returns access token after Google login |

## Security Model

- **ApiGateway**: `oauth2Login` (Okta OIDC + Google) for user-facing login, plus `oauth2ResourceServer` (JWT) to validate tokens on downstream calls.
- **HotelService / RatingService / UserService**: pure `oauth2ResourceServer` — validate the JWT issued by Okta, and use `@PreAuthorize` for role/scope-based method security (`ROLE_ADMIN`, `SCOPE_internal`).
- **Service-to-service calls** (UserService → HotelService/RatingService via Feign): authenticated using an OAuth2 **client-credentials** grant (`my-internal-client`), injected via a Feign `RequestInterceptor` / RestTemplate interceptor.

## Resilience (UserService → Hotel/Rating calls)

Configured via Resilience4j in `UserService/src/main/resources/application.yml`:

- **Circuit Breaker** (`ratingHotelCircuitBreaker`) — opens after 50% failure rate over the last 10 calls, returns a dummy fallback `User`.
- **Retry** (`ratingHotelRetry`) — up to 3 attempts, 1s wait, ignores `UserNotFoundException`.
- **Rate Limiter** (`userRateLimiter`) — 10 requests / 10s window, returns `429 TOO_MANY_REQUESTS` fallback.

Actuator health (with circuit breaker status) is exposed at `/actuator/health` on UserService.

## Notes / Known Issues

- Some modules mix Spring Boot 3.2.x and 4.0.x / Spring Cloud versions across services (visible in the `pom.xml` files and surefire reports) — align these before deploying together, since version drift is what caused the `ConfigDataLocationResolverContext` / `JwtDecoder` bean errors seen in the test reports.
- RatingService and HotelService currently fail their `contextLoads` test unless a `JwtDecoder` bean (i.e. a valid `issuer-uri`) is reachable — mock or profile-isolate this for CI.

## Project Structure

```
Microservices Learn/
├── ServiceRegistry/ServiceRegistry/
├── ConfigServer/ConfigServer/
├── ApiGateway/ApiGateway/
├── UserService/UserService/
├── HotelService/HotelService/
└── RatingService/RatingService/
```

Each folder is an independent Maven project with its own `pom.xml`, `mvnw` wrapper, and `application.yml`.
