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
- `@ControllerAdvice` in `GlobalExceptionHandler` renders the `error.kte` template or returns JSON error responses for
  the API.

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

## Project Hierarchy

```text
.
├── src/main/
│   ├── kotlin/tech/provokedynamic/wdusbmidterm/
│   │   ├── controller/
│   │   │   ├── api/             # REST Endpoints (JSON)
│   │   │   └── ...              # View Controllers (KTE)
│   │   ├── dto/
│   │   │   ├── request/         # Request payloads
│   │   │   └── response/        # Response interfaces (projections)
│   │   ├── entity/              # JPA Entities + toResponse() extension functions
│   │   ├── exception/           # Custom Errors & Global Handler
│   │   ├── model/
│   │   │   └── view/            # UI-specific ViewModels
│   │   ├── repository/          # Spring Data JPA Repositories
│   │   ├── security/            # SecurityConfig
│   │   └── service/             # Business Logic & Transactions
│   ├── kte/                     # KTE Templates (.kte)
│   └── resources/
│       ├── db/migration/        # Flyway SQL scripts
│       ├── static/              # CSS
│       └── application.yaml     # App configuration
├── docker-compose.yaml          # Orchestration
└── pom.xml                      # KTE source set: src/main/kte
```

---

## Technologies

| Technology              | Usage                                               |
|:------------------------|:----------------------------------------------------|
| **Kotlin**              | Primary language                                    |
| **JDK 25**              | Runtime                                             |
| **Spring Boot**         | Application framework                               |
| **Spring Security**     | Authentication, authorization, CSRF protection      |
| **SpringDoc / Swagger** | OpenAPI 3.0 API Documentation                       |
| **Spring Data JPA**     | Repository layer                                    |
| **Hibernate**           | ORM                                                 |
| **PostgreSQL**          | Database                                            |
| **Flyway**              | Schema migrations                                   |
| **Caffeine**            | In-process cache provider                           |
| **KTE**                 | Server-side template engine (Kotlin version of JTE) |
| **Logback**             | Logging with Spring profiles                        |
| **Docker**              | Containerization                                    |

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

### Response Interfaces (`dto/response`)

- `BookCatalogResponse` — `id`, `title`, `isbn`, `pageCount`, `publicationDate`, `genres`
- `BookDetailResponse` — full book detail including `publisher`, `authors`, `genres`
- `BookCardResponse` — `id`, `title`, `publicationDate` (home page cards)
- `AuthorResponse` — `id`, `firstName`, `lastName`, `bio`
- `AuthorBookResponse` — `id`, `title`, `publicationDate`, `genres`
- `PublisherResponse` — `id`, `name`
- `GenreResponse` — `id`, `name`

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

Files located in `src/main/resources/db/migration/`:

| File                | Description                                             |
|:--------------------|:--------------------------------------------------------|
| `V1__init.sql`      | Schema: publishers, authors, genres, books, join tables |
| `V2__seed_data.sql` | Seed data: publishers, authors, books, genres           |
| `V3__users.sql`     | Schema: users table, sequence, seeded admin and user    |

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

## Logging

Configured via `logback-spring.xml` with Spring profiles:

| Profile | Root Level | SQL Logging                               |
|:--------|:-----------|:------------------------------------------|
| `dev`   | INFO       | DEBUG + TRACE (Hibernate SQL + bindings)  |
| `prod`  | WARN       | Off (Swagger UI & API Docs also disabled) |

---

## Running the Application

### Development (local)

Make sure PostgreSQL is running locally on port `5432`, then:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

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

## Author

**Giorgi Chapidze**