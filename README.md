# LibSys - Book Library System

## Overview

A Spring Boot application written in Kotlin for managing a book library. Supports browsing and managing books,
authors, publishers, and genres with server-side rendered UI using JTE/KTE templates.

---

## Architecture

The application follows a layered architecture:

### Controller Layer

- Handles HTTP requests and responses
- View controllers only — no REST API
- No business logic; delegates entirely to services

### Service Layer

- Core application logic
- All write operations are `@Transactional`
- Read operations use `@Transactional(readOnly = true)`
- Duplicate checks throw typed domain exceptions before persisting

### Repository Layer

- Spring Data JPA with Hibernate
- Derived query methods for filtered lookups
- `@EntityGraph` used to avoid N+1 on association fetching
- Returns Spring Data projection interfaces directly — no manual mapping

### Exception Layer

- Typed domain exceptions: `EntityNotFoundException`, `EntityAlreadyExistsException`, `EntityDeletedException`
- `@ControllerAdvice` in `GlobalExceptionHandler` renders the `error.kte` template with appropriate HTTP status

---

## Technologies

| Technology      | Usage                        |
|-----------------|------------------------------|
| Kotlin          | Primary language             |
| JDK 25          | Runtime                      |
| Spring Boot     | Application framework        |
| Spring Web MVC  | View controllers             |
| Spring Data JPA | Repository layer             |
| Hibernate       | ORM                          |
| PostgreSQL      | Database                     |
| Flyway          | Schema migrations            |
| JTE / KTE       | Server-side template engine  |
| Logback         | Logging with Spring profiles |
| Docker          | Containerisation             |

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

## UI Routes

### Home

| Method | Path | Description                        |
|--------|------|------------------------------------|
| GET    | `/`  | Home page — recent books and stats |

### Books (`/books`)

| Method | Path               | Description            |
|--------|--------------------|------------------------|
| GET    | /books             | Paginated book catalog |
| GET    | /books/{id}        | Book detail page       |
| GET    | /books/new         | Add book form          |
| POST   | /books/new         | Submit new book        |
| GET    | /books/{id}/edit   | Edit book form         |
| POST   | /books/{id}/edit   | Submit book update     |
| POST   | /books/{id}/delete | Soft delete a book     |

### Authors (`/authors`)

| Method | Path                 | Description            |
|--------|----------------------|------------------------|
| GET    | /authors             | Paginated author index |
| GET    | /authors/{id}        | Author detail + books  |
| GET    | /authors/add         | Add author form        |
| POST   | /authors/add         | Submit new author      |
| GET    | /authors/{id}/edit   | Edit author form       |
| POST   | /authors/{id}/edit   | Submit author update   |
| POST   | /authors/{id}/delete | Soft delete an author  |

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

All projections are Spring Data interfaces backed by Hibernate — no manual mapping.

---

## Soft Delete

Entities are never hard deleted. Every query filters by `deletedAt IS NULL`.
Accessing a deleted or missing entity throws `EntityNotFoundException` or `EntityDeletedException`,
handled globally by `GlobalExceptionHandler`.

---

## Database Migrations (Flyway)

Files located in `src/main/resources/db/migration/`:

| File                | Description                                             |
|---------------------|---------------------------------------------------------|
| `V1__init.sql`      | Schema: publishers, authors, genres, books, join tables |
| `V2__seed_data.sql` | Seed data: publishers, authors, books, genres           |

Flyway runs automatically on startup.

---

## Templates (KTE)

Server-side rendering using JTE with Kotlin (`.kte` files) in `src/main/kte/`.

| Template            | Route                                |
|---------------------|--------------------------------------|
| `layout.kte`        | Shared layout                        |
| `index.kte`         | `/`                                  |
| `error.kte`         | Error pages                          |
| `books/index.kte`   | `/books`                             |
| `books/show.kte`    | `/books/{id}`                        |
| `books/form.kte`    | `/books/new`, `/books/{id}/edit`     |
| `authors/index.kte` | `/authors`                           |
| `authors/show.kte`  | `/authors/{id}`                      |
| `authors/form.kte`  | `/authors/add`, `/authors/{id}/edit` |

All pages extend `@template.layout(title, activeNav, content)`.

---

## Static Assets

Inter font is self-hosted under `src/main/resources/static/fonts/` and served via Spring MVC's
`ResourceHandlerRegistry` with a 1-year `Cache-Control: public` header. CSS is served similarly
from `src/main/resources/static/css/`.

---

## Logging

Configured via `logback-spring.xml` with Spring profiles:

| Profile | Root Level | SQL Logging                              |
|---------|------------|------------------------------------------|
| `dev`   | INFO       | DEBUG + TRACE (Hibernate SQL + bindings) |
| `prod`  | WARN       | Off                                      |

Hibernate SQL formatting enabled in dev via `hibernate.format_sql=true`.

---

## Running the Application

### Development (local)

Make sure PostgreSQL is running locally on port `5432`, then:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

App will be available at `http://localhost:8080`.

---

### Production (Docker)

The app and database run as Docker containers via `docker-compose.yaml`.

**Requirements:** Docker and Docker Compose installed.

```bash
docker-compose up --build
```

This will:

1. Build the app image using the multi-stage `Dockerfile` (Maven build → JRE runtime)
2. Start a PostgreSQL container and wait for it to be healthy
3. Start the app container connected to the database
4. Run Flyway migrations automatically on startup

App will be available at `http://localhost:8080`.

**To stop:**

```bash
docker-compose down
```

**To stop and remove the database volume:**

```bash
docker-compose down -v
```

**To run only the database** (useful when running the app locally against a dockerised DB):

```bash
docker-compose up database
```

---

## Potential Future Improvements

- [X] Author CRUD — create, edit, delete authors via UI
- [X] Book edit and soft delete via UI
- [ ] Review system — users can submit 1–5 star ratings and written reviews
- [ ] User authentication with Spring Security
- [X] Input validation with `@Valid` and proper error responses
- [X] Global exception handler with `@ControllerAdvice`
- [X] Swagger / OpenAPI documentation
- [ ] Pagination controls on the home page recent section
- [ ] Book cover image upload
- [ ] Advanced filtering — by date range, page count, multiple genres
- [ ] Unit and integration tests with JUnit and Testcontainers

---

## Author

Giorgi Chapidze