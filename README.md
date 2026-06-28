# LibSys - Book Library System

## Overview

A Spring Boot application written in Kotlin for managing a book library. Supports browsing and managing books, authors,
publishers, and genres with both a server-side rendered UI using **KTE templates** and a modern **REST API** with
OpenAPI documentation.

---

## Technologies

| Technology               | Usage                                                |
|:-------------------------|:-----------------------------------------------------|
| **Kotlin**               | Primary language                                     |
| **JDK 24**               | Runtime                                              |
| **Spring Boot 4.0.6**    | Application framework                                |
| **Spring Security**      | Authentication, authorization, CSRF protection       |
| **Spring Boot Actuator** | Health, metrics, and monitoring endpoints            |
| **Micrometer**           | Custom metrics and Prometheus integration            |
| **SpringDoc / Swagger**  | OpenAPI 3.0 API Documentation                        |
| **Spring Data JPA**      | Repository layer                                     |
| **Hibernate**            | ORM                                                  |
| **PostgreSQL**           | Database (prod profile)                              |
| **H2**                   | In-memory database (dev/test profile)                |
| **Flyway**               | Schema migrations (separate scripts per DB)          |
| **Caffeine**             | In-process cache provider                            |
| **KTE**                  | Server-side template engine (Kotlin version of JTE)  |
| **SLF4J / Logback**      | Structured logging with Spring profile–driven config |
| **JUnit 5 + Mockito**    | Unit and integration testing                         |
| **JaCoCo**               | Code coverage reporting                              |
| **Docker**               | Containerization                                     |

---

## Running the Application

### Development (local, no external database needed)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:libsysdb`)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Actuator health: `http://localhost:8080/actuator/health`

### Production (Docker)

```bash
docker-compose up --build
```

Builds the app image (multi-stage Dockerfile), starts PostgreSQL, waits for health checks, and runs Flyway migrations.

Required environment variables (set in `docker-compose.yaml` or your environment):

| Variable            | Example                                    |
|:--------------------|:-------------------------------------------|
| `POSTGRES_URL`      | `jdbc:postgresql://database:5432/postgres` |
| `POSTGRES_USERNAME` | `postgres`                                 |
| `POSTGRES_PASSWORD` | `postgres`                                 |

---

## User Credentials and Roles

| Username | Password   | Role    | Description                         |
|:---------|:-----------|:--------|:------------------------------------|
| `admin`  | `admin123` | `ADMIN` | Full access: CRUD, admin dashboard  |
| `user`   | `user123`  | `USER`  | Read-only: browse books and authors |

Passwords are stored as **BCrypt** hashes. Registration is enabled in `dev` profile and disabled in `prod`.

---

## Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage report (output: target/site/jacoco/index.html)
./mvnw verify
```

### Test Structure

```
src/test/kotlin/tech/provokedynamic/wdusbmidterm/
├── WdusbMidtermApplicationTests.kt       # Context loads smoke test
├── service/
│   ├── AuthorServiceTest.kt              # Unit tests (Mockito)
│   ├── BookServiceTest.kt                # Unit tests (Mockito)
│   ├── GenreServiceTest.kt               # Unit tests (Mockito)
│   └── UserServiceTest.kt                # Unit tests (Mockito)
├── controller/
│   ├── AuthorRestControllerTest.kt       # @WebMvcTest — controller layer
│   └── GenreRestControllerTest.kt        # @WebMvcTest — controller layer
├── repository/
│   ├── AuthorRepositoryTest.kt           # @DataJpaTest — H2
│   └── GenreRepositoryTest.kt            # @DataJpaTest — H2
└── integration/
    ├── AuthorApiIntegrationTest.kt        # Full context, seeded H2
    ├── GenreApiIntegrationTest.kt         # Full context, seeded H2
    ├── I18nIntegrationTest.kt             # i18n error messages (en/ka)
    └── ActuatorIntegrationTest.kt         # Health, info, metrics endpoints
```

### Test Types

| Type                   | Annotation        | Scope                                         |
|:-----------------------|:------------------|:----------------------------------------------|
| Unit tests             | (plain JUnit 5)   | Service logic, isolated with Mockito mocks    |
| Controller layer tests | `@WebMvcTest`     | HTTP layer, MockMvc, mocked services          |
| Repository layer tests | `@DataJpaTest`    | JPA queries against H2, no Spring MVC context |
| Integration tests      | `@SpringBootTest` | Full context, H2 + Flyway seed, real security |

All test classes cover both **positive scenarios** (happy path) and **negative scenarios** (not found, conflict,
validation errors, authorization failures).

### Coverage Report

After running `./mvnw verify`, open:

```
target/site/jacoco/index.html
```

Excluded from coverage: generated JTE classes, DTOs, entities, model enums, and the main application bootstrap class.

---

## Monitoring

The application uses **Spring Boot Actuator** for operational monitoring.

### Actuator Endpoints

| Endpoint                | Access     | Description                           |
|:------------------------|:-----------|:--------------------------------------|
| `GET /actuator/health`  | Public     | Application health status             |
| `GET /actuator/info`    | Public     | App metadata and feature flags        |
| `GET /actuator/metrics` | ADMIN only | JVM, HTTP, and custom library metrics |
| `GET /actuator/caches`  | ADMIN only | Caffeine cache names and managers     |
| `GET /actuator/loggers` | ADMIN only | View and change log levels at runtime |

### Health Indicators

| Indicator                | Description                                        |
|:-------------------------|:---------------------------------------------------|
| `db`                     | Spring-managed datasource health check             |
| `diskSpace`              | Available disk space                               |
| `catalogHealthIndicator` | Custom: verifies active book/author counts from DB |

In the `dev` profile, full health details (including component breakdown) are always visible. In `prod`, component
details are only shown to authenticated ADMIN users.

### Custom Metrics (Micrometer)

| Metric name                    | Type  | Description                  |
|:-------------------------------|:------|:-----------------------------|
| `library.books.active.total`   | Gauge | Count of non-deleted books   |
| `library.authors.active.total` | Gauge | Count of non-deleted authors |

Access via: `GET /actuator/metrics/library.books.active.total` (requires ADMIN)

Prometheus scraping is available at `GET /actuator/prometheus` (ADMIN only).

---

## Logging

Configured via `logback-spring.xml`. Logs are written to both the console and a rolling file appender.

**Log file location:** `logs/app.log` (relative to the working directory).

### Rolling Policy

- Rotates **daily** and at **10 MB** per file
- Compressed as `.log.gz`
- Retains **30 days** / **500 MB** total

### Profile-Specific Log Levels

| Profile  | App package (`tech.provokedynamic`) | Root level | Hibernate SQL | File appender |
|:---------|:------------------------------------|:-----------|:--------------|:--------------|
| `dev`    | DEBUG                               | INFO       | DEBUG + TRACE | ✓             |
| `prod`   | WARN                                | WARN       | off           | ✓             |
| `test`   | WARN                                | WARN       | off           | ✗ (console)   |
| _(none)_ | INFO                                | INFO       | off           | ✓             |

All log statements use **SLF4J parameterized placeholders** (no string concatenation).

Loggers are present in: `AuthorService`, `BookService`, `UserService`, `UserDetailsService`, and
`GlobalExceptionHandler`.

---

## Profile Configuration

| Profile | Database              | Swagger  | Log level    | Registration | Actuator details |
|:--------|:----------------------|:---------|:-------------|:-------------|:-----------------|
| `dev`   | H2 in-memory (seeded) | enabled  | DEBUG / INFO | enabled      | always visible   |
| `prod`  | PostgreSQL            | disabled | WARN         | disabled     | ADMIN only       |

**Switch profiles:**

```bash
# Dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Prod (requires PostgreSQL environment variables)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

In IntelliJ IDEA: Run → Edit Configurations → Active profiles → `dev` or `prod`.

---

## Architecture

### Layered Structure

```
Controller Layer  →  Service Layer  →  Repository Layer  →  Database
     ↕                    ↕                   ↕
  DTOs (req/res)     Cache (@Cacheable)    JPA Entities
  Validation         Transactions          Projections
  Exception handling Exception types       EntityGraph (N+1 avoidance)
```

### Key Design Decisions

- **Soft deletes** on `Book`, `Author`, `Publisher` via `deletedAt` timestamp
- **Projection interfaces** as repository return types — no manual mapping except `Book.toDetailResponse()`
- **`@CachePut` on repositories** always writes to cache on DB read; **`@Cacheable` on services** serves from cache; *
  *`@CacheEvict` on writes** invalidates stale entries
- **`@RestControllerAdvice`** in `GlobalExceptionHandler` returns structured `ErrorResponse` for all API errors with
  i18n messages

### Package Structure

```
src/main/kotlin/tech/provokedynamic/wdusbmidterm/
├── config/          AppProperties, I18nConfig, ActuatorConfig
├── controller/      View controllers (MVC) + api/ (REST)
├── dto/             request/ and response/ DTOs + projections
├── entity/          JPA entities + toResponse() extensions
├── exception/       Custom exceptions + GlobalExceptionHandler
├── model/           Role enum, ViewModels
├── repository/      Spring Data JPA repositories
├── security/        SecurityConfig
└── service/         Business logic + transactions
```

---

## Security

### Authentication

- Form-based login at `/login` with username and password
- Passwords hashed with **BCryptPasswordEncoder**
- Session invalidated and `JSESSIONID` cookie deleted on logout

### Authorization Summary

| Role    | Catalog browse | Create/Edit/Delete | Admin dashboard | Actuator metrics |
|:--------|:---------------|:-------------------|:----------------|:-----------------|
| (anon)  | ✓              | ✗                  | ✗               | ✗                |
| `USER`  | ✓              | ✗                  | ✗               | ✗                |
| `ADMIN` | ✓              | ✓                  | ✓               | ✓                |

### CSRF

- **Enabled** for all MVC form endpoints
- **Disabled** for `/api/**` and `/actuator/**` (stateless, no session cookies)

---

## Database Migrations (Flyway)

### PostgreSQL (`src/main/resources/db/migration/`)

| File                                | Description                                |
|:------------------------------------|:-------------------------------------------|
| `V1__init.sql`                      | Schema: publishers, authors, genres, books |
| `V2__seed_data.sql`                 | Seed data: 30 books, 20 authors, 10 genres |
| `V3__add_spring_security_users.sql` | Users table + seeded admin/user accounts   |

### H2 — dev/test profile (`src/main/resources/db/migration/h2/`)

| File                                   | Description                             |
|:---------------------------------------|:----------------------------------------|
| `V1__init_h2.sql`                      | H2-compatible schema (IDENTITY columns) |
| `V2__seed_data_h2.sql`                 | Same seed data                          |
| `V3__add_spring_security_users_h2.sql` | Users table with MERGE INTO             |

---

## Caching

Cache names follow the `entity:concern` convention. TTL: 10 minutes in prod, 1 minute in dev.

| Cache Name       | Key                   | Stores                      |
|:-----------------|:----------------------|:----------------------------|
| `authors:all`    | `'list'`              | `List<AuthorResponse>`      |
| `authors:paged`  | `pageNumber:pageSize` | `Page<AuthorResponse>`      |
| `authors:single` | `authorId`            | `AuthorResponse`            |
| `authors:count`  | `'total'`             | `Long`                      |
| `books:paged`    | `pageNumber:pageSize` | `Page<BookCatalogResponse>` |
| `books:single`   | `bookId`              | `BookDetailResponse`        |
| `books:recent`   | `'list'`              | `List<BookCardResponse>`    |
| `books:count`    | `'total'`             | `Long`                      |
| `genres:all`     | `'list'`              | `List<GenreResponse>`       |
| `publishers:all` | `'list'`              | `List<PublisherResponse>`   |

---

## Author

**Giorgi Chapidze**