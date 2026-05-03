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

### Repository Layer

- Spring Data JPA with Hibernate.
- Derived query methods for filtered lookups.
- `@EntityGraph` used to avoid N+1 on association fetching.
- Returns Spring Data projection interfaces directly — no manual mapping.

### Exception Layer

- Typed domain exceptions: `EntityNotFoundException`, `EntityAlreadyExistsException`, `EntityDeletedException`.
- `@ControllerAdvice` in `GlobalExceptionHandler` renders the `error.kte` template or returns JSON error responses for
  the API.

---

## Project Hierarchy

```text
.
├── src/main/
│   ├── kotlin/tech/provokedynamic/wdusbmidterm/
│   │   ├── controller/
│   │   │   ├── api/             # REST Endpoints (JSON)
│   │   │   └── ...              # View Controllers (KTE)
│   │   ├── entity/              # JPA Entities (Book, Author, etc.)
│   │   ├── exception/           # Custom Errors & Global Handler
│   │   ├── model/
│   │   │   ├── dto/             # Request payloads
│   │   │   ├── projection/      # Read-only API/UI interfaces
│   │   │   └── view/            # UI-specific ViewModels
│   │   ├── repository/          # Spring Data JPA Repositories
│   │   └── service/             # Business Logic & Transactions
│   ├── kte/                     # KTE Templates (.kte)
│   └── resources/
│       ├── db/migration/        # Flyway SQL scripts
│       ├── static/              # CSS & Fonts (Inter)
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
| **SpringDoc / Swagger** | OpenAPI 3.0 API Documentation                       |
| **Spring Data JPA**     | Repository layer                                    |
| **Hibernate**           | ORM                                                 |
| **PostgreSQL**          | Database                                            |
| **Flyway**              | Schema migrations                                   |
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

### Request DTOs

- `BookCreateRequest` — `title`, `isbn`, `publisherId`, `publicationDate`, `pageCount`, `authorIds`, `genreIds`
- `AuthorRequest` — `firstName`, `lastName`, `bio`
- `PublisherRequest` — `name`
- `GenreRequest` — `name`

### Projection Interfaces (read side)

- `BookCatalogItem` — `id`, `title`, `isbn`, `pageCount`, `publicationDate`, `genres`
- `BookDetailProjection` — full book detail including `publisher`, `authors`, `genres`
- `BookCardProjection` — `id`, `title`, `publicationDate` (home page cards)
- `AuthorResponse` — `id`, `firstName`, `lastName`, `bio`
- `AuthorBookItem` — `id`, `title`, `publicationDate`, `genres`
- `PublisherResponse` — `id`, `name`
- `GenreResponse` — `id`, `name`

---

## Database Migrations (Flyway)

Files located in `src/main/resources/db/migration/`:

| File                | Description                                             |
|:--------------------|:--------------------------------------------------------|
| `V1__init.sql`      | Schema: publishers, authors, genres, books, join tables |
| `V2__seed_data.sql` | Seed data: publishers, authors, books, genres           |

---

## Templates (KTE)

Server-side rendering using JTE with Kotlin (`.kte` files) in `src/main/kte/`.

| Template           | Route                            |
|:-------------------|:---------------------------------|
| `layout.kte`       | Shared layout                    |
| `books/index.kte`  | `/books`                         |
| `books/form.kte`   | `/books/new`, `/books/{id}/edit` |
| `authors/show.kte` | `/authors/{id}`                  |

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
- [ ] Review system — ratings and written reviews
- [ ] User authentication with Spring Security
- [ ] Book cover image upload
- [ ] Unit and integration tests with Testcontainers

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
  `BookCreateRequest` with `null` defaults so Spring can bind gracefully and `@NotNull` messages display correctly.
- **Fixed multi-select binding** — `authorIds` and `genreIds` (`List<Long>`) defaulted to `emptyList()` to prevent
  `NullPointerException` when no options were selected.
- **Enabled HTTP compression** in `application.yaml` for HTML, CSS, and JS responses.

## Author

**Giorgi Chapidze**