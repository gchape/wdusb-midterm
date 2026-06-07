# LibSys - Book Library System

## Overview

A Spring Boot application written in Kotlin for managing a book library. Supports browsing and managing books, authors,
publishers, and genres with both a server-side rendered UI using **KTE templates** and a modern **REST API** with
OpenAPI documentation.

---

## Architecture

The application follows a layered architecture:

### Controller Layer

- **REST Controllers (`/api/**`):** Provides JSON endpoints for programmatic access and CRUD operations.
- **View Controllers:** Handles HTTP requests for server-side rendered UI; no business logic; delegates entirely to
  services.

### Service Layer

- Core application logic and validation.
- All write operations are `@Transactional`.
- Read operations use `@Transactional(readOnly = true)`.
- Duplicate checks throw typed domain exceptions before persisting.
- `@Cacheable` on all read methods; `@CacheEvict` via `@Caching` on all mutating methods.

### Repository Layer

- Spring Data JPA with Hibernate.
- Derived query methods for filtered lookups.
- `@EntityGraph` used to avoid N+1 on association fetching.
- Returns Spring Data projection interfaces directly — no manual mapping.
- `@CachePut` on all query methods to populate cache on every DB read.

### Cache Layer

- Spring Cache with **Caffeine** as the cache provider.
- Cache names follow the `entity:concern` convention (e.g. `authors:single`, `books:paged`).
- Repository methods use `@CachePut` to always write through to cache.
- Service methods use `@Cacheable` to serve from cache and `@CacheEvict` to invalidate on writes.

### Exception Layer

- Typed domain exceptions: `EntityNotFoundException`, `EntityAlreadyExistsException`, `EntityDeletedException`.
- `@RestControllerAdvice` in `GlobalExceptionHandler` returns structured JSON `ErrorResponse` objects for all API
  errors, with messages resolved from the active locale.

---

## Security

### Authentication

- Form-based login at `/login` with username and password.
- Logout at `/logout` (POST), redirects to `/login?logout=true`.
- Passwords hashed with **BCryptPasswordEncoder** — never stored in plain text.
- Session invalidated and `JSESSIONID` cookie deleted on logout.

### Roles

| Role    | Description                                      |
|:--------|:-------------------------------------------------|
| `USER`  | Can browse books and authors; cannot mutate data |
| `ADMIN` | Full access including create, edit, and delete   |

### Demo Credentials

| Username | Password   | Role    |
|:---------|:-----------|:--------|
| `admin`  | `admin123` | `ADMIN` |
| `user`   | `user123`  | `USER`  |

### Public Endpoints (no login required)

| Path                | Description             |
|:--------------------|:------------------------|
| `GET /`             | Home page               |
| `GET /books`        | Book catalog            |
| `GET /books/{id}`   | Book detail             |
| `GET /authors`      | Author index            |
| `GET /authors/{id}` | Author detail           |
| `GET /login`        | Login page              |
| `GET /register`     | Registration page       |
| `GET /api/**`       | All REST read endpoints |
| `/swagger-ui/**`    | API docs (dev only)     |

### Authenticated Endpoints (login required)

Any route not listed as public requires an authenticated session.

### ADMIN-Only Endpoints

| Path                             | Description              |
|:---------------------------------|:-------------------------|
| `GET/POST /books/new`            | Add a new book           |
| `GET/POST /books/{id}/edit`      | Edit a book              |
| `POST /books/{id}/delete`        | Delete a book            |
| `GET/POST /authors/add`          | Add a new author         |
| `GET/POST /authors/{id}/edit`    | Edit an author           |
| `POST /authors/{id}/delete`      | Delete an author         |
| `GET /admin`                     | Admin dashboard          |
| `POST /admin/users/{id}/disable` | Disable a user           |
| `POST /admin/users/{id}/enable`  | Enable a user            |
| `POST /admin/users/{id}/promote` | Promote to ADMIN         |
| `POST/PUT/DELETE /api/**`        | All REST write endpoints |

### Method-Level Security

`@EnableMethodSecurity(prePostEnabled = true)` is enabled in `SecurityConfig`.

`@PreAuthorize("hasRole('ADMIN')")` is applied to:

- `AdminController` (class level)
- All mutating methods in `AuthorRestController`, `BookRestController`, `GenreRestController`, `PublisherRestController`

### CSRF

- **Enabled** for all MVC form endpoints — every form includes a hidden CSRF token.
- **Disabled** for `/api/**` — the REST API is stateless and does not use session cookies,
  so CSRF protection is not applicable.

---

## Assignment 2 Features

### Profiles

| Profile | Database              | Swagger  | Log level    | Registration |
|:--------|:----------------------|:---------|:-------------|:-------------|
| `dev`   | H2 in-memory (seeded) | enabled  | DEBUG / INFO | enabled      |
| `prod`  | PostgreSQL            | disabled | WARN         | disabled     |

**Command line:**

```bash
# Development — H2 in-memory, Swagger UI on, DEBUG logging, seeds 30 books / 20 authors / 10 genres
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production — PostgreSQL, Swagger off, WARN logging
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**IntelliJ IDEA:** Run → Edit Configurations → Active profiles → enter `dev` or `prod`.

The `dev` profile requires no external database. Flyway runs H2-compatible migrations automatically and seeds all data
on startup. Data resets on every restart.

---

### Custom Configuration Properties (`app.*`)

Defined in `AppProperties` (`@ConfigurationProperties(prefix = "app")`, `@Validated`). Fields are validated with JSR-303
constraints (`@NotBlank`, `@Email`, `@Min`, `@Positive`). The class is injected into `HomeController`, `HomeService`,
and `InfoRestController`. Active runtime values are exposed at `GET /api/info`.

| Property                            | Dev value                                  | Prod value                        | Role                                          |
|:------------------------------------|:-------------------------------------------|:----------------------------------|:----------------------------------------------|
| `app.title`                         | `Library System [DEV]`                     | `Library System`                  | Application display name on home page and API |
| `app.description`                   | `Development environment — data resets...` | `The university library catalog.` | Subtitle shown on home page and `/api/info`   |
| `app.organization`                  | `WDUSB University Library`                 | same                              | Organization name in API metadata             |
| `app.contact-email`                 | `library@wdusb.edu`                        | same                              | Contact email in API metadata (validated)     |
| `app.default-page-size`             | `6`                                        | `12`                              | Default number of results per page            |
| `app.max-page-size`                 | `50`                                       | `100`                             | Maximum allowed page size                     |
| `app.features.swagger-enabled`      | `true`                                     | `false`                           | Enables/disables Springdoc UI                 |
| `app.features.registration-enabled` | `true`                                     | `false`                           | Enables/disables self-registration            |
| `app.features.catalog-public`       | `true`                                     | `true`                            | Makes catalog browsable without login         |
| `app.maintenance.cache-ttl-minutes` | `1`                                        | `10`                              | Caffeine cache TTL in minutes                 |
| `app.maintenance.show-sql-in-logs`  | `true`                                     | `false`                           | Toggles Hibernate SQL logging                 |

---

### Internationalization (i18n)

The API reads the standard `Accept-Language` HTTP header to return localized messages. Supported locales: **`en`** (
default) and **`ka`** (Georgian).

**What is localized:**

- All REST error responses (404, 409, 410, 422, 500) via `GlobalExceptionHandler`
- All `@Valid` DTO field validation error messages (`BookRequest`, `AuthorRequest`, `GenreRequest`, `PublisherRequest`,
  `RegisterRequest`)

**Resource bundle files:**

| File                                        | Locale             |
|:--------------------------------------------|:-------------------|
| `src/main/resources/messages.properties`    | Fallback (English) |
| `src/main/resources/messages_en.properties` | Explicit English   |
| `src/main/resources/messages_ka.properties` | Georgian           |

> `messages.properties` and `messages_en.properties` are intentionally identical. The former is the Spring fallback when
> no locale-specific file matches.

**Testing with curl:**

```bash
# English (default)
curl http://localhost:8080/api/books/99999

# Georgian
curl -H "Accept-Language: ka" http://localhost:8080/api/books/99999

# Trigger a validation error in Georgian
curl -s -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -H "Accept-Language: ka" \
  -d '{}'
```

---

### Structured Logging

Configured via `logback-spring.xml`. Logs are written to both the console and a rolling file appender.

**Log file location:** `logs/app.log` (relative to the working directory where the app is launched).

Rolling policy: rotates daily and at 10 MB per file, compressed as `.log.gz`, retains 30 days / 500 MB total. The
`logs/` directory is excluded from version control via `.gitignore`.

| Profile        | App package (`tech.provokedynamic`) | Root level | Hibernate SQL |
|:---------------|:------------------------------------|:-----------|:--------------|
| `dev`          | DEBUG                               | INFO       | DEBUG + TRACE |
| `prod`         | WARN                                | WARN       | off           |
| _(no profile)_ | —                                   | INFO       | off           |

Loggers are in place across 5 components: `AuthorService`, `BookService`, `UserService`, `UserDetailsService`, and
`GlobalExceptionHandler`. All log statements use SLF4J parameterized placeholders (no string concatenation).

---

## Project Hierarchy

```text
.
├── src/main/
│   ├── kotlin/tech/provokedynamic/wdusbmidterm/
│   │   ├── config/
│   │   │   ├── AppProperties.kt     # @ConfigurationProperties + JSR-303 validation
│   │   │   └── I18nConfig.kt        # AcceptHeaderLocaleResolver, MessageSource, Validator
│   │   ├── controller/
│   │   │   ├── api/                 # REST Endpoints (JSON)
│   │   │   │   └── InfoRestController.kt  # GET /api/info — exposes active config
│   │   │   └── ...                  # View Controllers (KTE)
│   │   ├── dto/
│   │   │   ├── request/             # Request payloads (all validation keys use {bundle.key})
│   │   │   └── response/            # Response interfaces (projections) + ErrorResponse
│   │   ├── entity/                  # JPA Entities + toResponse() extension functions
│   │   ├── exception/               # Custom Errors & GlobalExceptionHandler (@RestControllerAdvice)
│   │   ├── model/                   # Role enum
│   │   ├── repository/              # Spring Data JPA Repositories
│   │   ├── security/                # SecurityConfig
│   │   └── service/                 # Business Logic & Transactions
│   ├── kte/                         # KTE Templates (.kte)
│   └── resources/
│       ├── db/migration/            # Flyway SQL scripts (PostgreSQL)
│       ├── db/migration/h2/         # Flyway SQL scripts (H2 — dev profile)
│       ├── static/                  # CSS
│       ├── messages.properties      # i18n fallback (English)
│       ├── messages_en.properties   # i18n English
│       ├── messages_ka.properties   # i18n Georgian
│       ├── logback-spring.xml       # Profile-driven logging config
│       └── application.yaml         # App configuration (base + dev + prod profiles)
├── logs/                            # Generated at runtime — gitignored
│   └── app.log
├── docker-compose.yaml              # Orchestration
└── pom.xml
```

---

## Technologies

| Technology              | Usage                                                |
|:------------------------|:-----------------------------------------------------|
| **Kotlin**              | Primary language                                     |
| **JDK 25**              | Runtime                                              |
| **Spring Boot**         | Application framework                                |
| **Spring Security**     | Authentication, authorization, CSRF protection       |
| **SpringDoc / Swagger** | OpenAPI 3.0 API Documentation                        |
| **Spring Data JPA**     | Repository layer                                     |
| **Hibernate**           | ORM                                                  |
| **PostgreSQL**          | Database (prod profile)                              |
| **H2**                  | In-memory database (dev profile)                     |
| **Flyway**              | Schema migrations (separate scripts per DB)          |
| **Caffeine**            | In-process cache provider                            |
| **KTE**                 | Server-side template engine (Kotlin version of JTE)  |
| **SLF4J / Logback**     | Structured logging with Spring profile–driven config |
| **Docker**              | Containerization                                     |

---

## Entities and Relationships

### Book

- `id`, `title`, `isbn`, `publicationDate`, `pageCount`
- Belongs to one `Publisher`
- Soft delete via `deletedAt` timestamp (`null` = active)
- `createdAt`, `updatedAt` managed by Hibernate via DB timestamps

### Author

- `id`, `firstName`, `lastName`, `bio`
- Soft delete via `deletedAt` timestamp

### Publisher

- `id`, `name`
- Soft delete via `deletedAt` timestamp

### Genre

- `id`, `name` (unique)
- No soft delete

### User

- `id`, `username`, `passwordHash`, `role`, `enabled`, `createdAt`
- Role is either `USER` or `ADMIN`
- Passwords stored as BCrypt hashes

### Relationships

- `Book ↔ Author`: Many-to-Many via `book_authors` join table (Book is the owning side)
- `Book ↔ Genre`: Many-to-Many via `book_genres` join table (Book is the owning side)
- `Book → Publisher`: Many-to-One

---

## Routes & Endpoints

### REST API (`/api`)

The API is documented via Swagger UI at `/swagger-ui.html` (enabled only in `dev` profile).

| Method   | Path                      | Description              |
|:---------|:--------------------------|:-------------------------|
| `GET`    | `/api/info`               | Active config & features |
| `GET`    | `/api/books`              | Paginated catalog        |
| `GET`    | `/api/authors`            | Paginated authors list   |
| `GET`    | `/api/authors/{id}/books` | Books by specific author |
| `POST`   | `/api/genres`             | Create new genre         |
| `DELETE` | `/api/publishers/{id}`    | Soft-delete publisher    |

### UI Routes

| Method | Path          | Description                        |
|:-------|:--------------|:-----------------------------------|
| `GET`  | `/`           | Home page — recent books and stats |
| `GET`  | `/books`      | Paginated book catalog             |
| `GET`  | `/books/{id}` | Book detail page                   |
| `POST` | `/books/new`  | Submit new book                    |
| `GET`  | `/authors`    | Paginated author index             |

---

## DTOs and Projections

### Request DTOs (`dto/request`)

- `BookRequest` — `title`, `isbn`, `publisherId`, `publicationDate`, `pageCount`, `authorIds`, `genreIds`
- `AuthorRequest` — `firstName`, `lastName`, `bio`
- `PublisherRequest` — `name`
- `GenreRequest` — `name`
- `RegisterRequest` — `username`, `password`, `confirmPassword`

### Response Types (`dto/response`)

- `BookCatalogResponse` — `id`, `title`, `isbn`, `pageCount`, `publicationDate`, `genres`
- `BookDetailResponse` — full book detail including `publisher`, `authors`, `genres`
- `BookCardResponse` — `id`, `title`, `publicationDate` (home page cards)
- `AuthorResponse` — `id`, `firstName`, `lastName`, `bio`
- `AuthorBookResponse` — `id`, `title`, `publicationDate`, `genres`
- `PublisherResponse` — `id`, `name`
- `GenreResponse` — `id`, `name`
- `AppInfoResponse` — active profile, config metadata, feature flags, pagination settings
- `ErrorResponse` — `status`, `error`, `message`, optional `fields` map (validation errors)

---

## Caching

Cache names follow the `entity:concern` naming convention:

| Cache Name          | Key                   | Stores                      |
|:--------------------|:----------------------|:----------------------------|
| `authors:all`       | `'list'`              | `List<AuthorResponse>`      |
| `authors:paged`     | `pageNumber:pageSize` | `Page<AuthorResponse>`      |
| `authors:single`    | `authorId`            | `AuthorResponse`            |
| `authors:count`     | `'total'`             | `Long`                      |
| `authors:exists`    | `firstName:lastName`  | `Boolean`                   |
| `books:paged`       | `pageNumber:pageSize` | `Page<BookCatalogResponse>` |
| `books:single`      | `bookId`              | `BookDetailResponse`        |
| `books:recent`      | `'list'`              | `List<BookCardResponse>`    |
| `books:count`       | `'total'`             | `Long`                      |
| `books:exists`      | `isbn`                | `Boolean`                   |
| `genres:all`        | `'list'`              | `List<GenreResponse>`       |
| `genres:single`     | `genreId`             | `GenreResponse`             |
| `genres:exists`     | `name`                | `Boolean`                   |
| `publishers:all`    | `'list'`              | `List<PublisherResponse>`   |
| `publishers:single` | `publisherId`         | `PublisherResponse`         |
| `publishers:exists` | `name`                | `Boolean`                   |

---

## Database Migrations (Flyway)

### PostgreSQL (`src/main/resources/db/migration/`)

| File                | Description                                             |
|:--------------------|:--------------------------------------------------------|
| `V1__init.sql`      | Schema: publishers, authors, genres, books, join tables |
| `V2__seed_data.sql` | Seed data: publishers, authors, books, genres           |
| `V3__users.sql`     | Schema: users table, sequence, seeded admin and user    |

### H2 — dev profile (`src/main/resources/db/migration/h2/`)

| File                                   | Description                              |
|:---------------------------------------|:-----------------------------------------|
| `V1__init_h2.sql`                      | H2-compatible schema                     |
| `V2__seed_data_h2.sql`                 | Seed data (30 books, 20 authors, genres) |
| `V3__add_spring_security_users_h2.sql` | Users table + seeded admin/user accounts |

---

## Templates (KTE)

Server-side rendering using JTE with Kotlin (`.kte` files) in `src/main/kte/`.

| Template              | Route                                |
|:----------------------|:-------------------------------------|
| `layout.kte`          | Shared layout                        |
| `index.kte`           | `/`                                  |
| `error.kte`           | Error pages                          |
| `books/index.kte`     | `/books`                             |
| `books/form.kte`      | `/books/new`, `/books/{id}/edit`     |
| `books/show.kte`      | `/books/{id}`                        |
| `authors/index.kte`   | `/authors`                           |
| `authors/form.kte`    | `/authors/add`, `/authors/{id}/edit` |
| `authors/show.kte`    | `/authors/{id}`                      |
| `auth/login.kte`      | `/login`                             |
| `auth/register.kte`   | `/register`                          |
| `admin/dashboard.kte` | `/admin`                             |

---

## Running the Application

### Development (local, no external database needed)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:libsysdb`).

### Production (Docker)

```bash
docker-compose up --build
```

This builds the app image (multi-stage Dockerfile), starts PostgreSQL, waits for health checks, and runs Flyway
migrations automatically.

---

## Potential Future Improvements

- [x] Author & Book CRUD via UI
- [x] REST API Layer with OpenAPI
- [x] Caching with Caffeine
- [x] User authentication with Spring Security
- [x] Externalized configuration with Spring Profiles
- [x] Internationalization (en / ka)
- [x] Structured logging with file rotation

---

## Performance & Optimization

### Lighthouse Scores (Home Page)

| Metric         | Score |
|:---------------|:------|
| Performance    | 97    |
| Accessibility  | 100   |
| Best Practices | 100   |
| SEO            | 100   |

### Optimizations Applied

- **Removed self-hosted Inter font files** — replaced 5 TTF font variants (~1.3 MiB total) with the native system font
  stack (`-apple-system`, `BlinkMacSystemFont`, `Segoe UI`, `system-ui`). This eliminated the font loading chain that
  was causing a 19,000ms LCP element render delay.
- **Fixed `NoResourceFoundException` handling** — Chrome DevTools probe requests (`/.well-known/appspecific/...`) were
  being caught by the generic exception handler and returned as 500 errors. Added a dedicated handler that returns 404
  correctly.
- **Fixed Spring MVC binding errors on book form** — `pageCount` (`Short`) and `publicationDate` (`LocalDate`) fields
  threw `NullPointerException` before validation could run when submitted empty. Made these fields nullable in
  `BookRequest` with `null` defaults so Spring can bind gracefully and `@NotNull` messages display correctly.
- **Fixed multi-select binding** — `authorIds` and `genreIds` (`List<Long>`) defaulted to `emptyList()` to prevent
  `NullPointerException` when no options were selected.
- **Enabled HTTP compression** in `application.yaml` for HTML, CSS, and JS responses.
- **Added Caffeine caching** — all read paths served from in-process cache; write operations evict stale entries via
  `@CacheEvict`.

---

## Author

**Giorgi Chapidze**