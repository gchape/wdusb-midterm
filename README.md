# Bibliotheca — Book Library System

## Overview

A Spring Boot application written in Kotlin for managing a book library. Supports browsing, searching, and adding
books with full author relationships, server-side rendered UI using JTE/KTE templates, and a REST API.

---

## Architecture

The application follows a layered architecture:

### Controller Layer

- Handles HTTP requests and responses
- Split into REST controllers (`/api/**`) and view controllers (`/`, `/books/**`)
- No business logic

### Service Layer

- Core application logic
- All write operations are `@Transactional`
- Read operations use `@Transactional(readOnly = true)`

### Repository Layer

- Spring Data JPA with Hibernate
- Derived query methods and JPQL `@Query` for search
- `@EntityGraph` used to avoid N+1 on author fetching

---

## Technologies

| Technology      | Usage                        |
|-----------------|------------------------------|
| Kotlin          | Primary language             |
| JDK 25          | Runtime                      |
| Spring Boot     | Application framework        |
| Spring Web MVC  | REST + view controllers      |
| Spring Data JPA | Repository layer             |
| Hibernate       | ORM                          |
| PostgreSQL      | Database                     |
| Flyway          | Schema migrations            |
| JTE / KTE       | Server-side template engine  |
| Logback         | Logging with Spring profiles |

---

## Entities and Relationships

### Book

- `id`, `title`, `isbn`, `publishedAt`, `pageCount`, `genre`
- Soft delete via `deleted` flag
- `createdAt`, `updatedAt` managed by Hibernate

### Author

- `id`, `firstname`, `lastname`, `email`, `dob`, `bio`
- Soft delete via `deleted` flag

### Review

- `id`, `rating` (1–5), `body`
- Belongs to `Book` and `User`

### User

- `id`, `username`

### Relationships

- `Book ↔ Author`: Many-to-Many via `authors_books` join table (Book is the owning side)
- `Book → Review`: One-to-Many
- `User → Review`: One-to-Many

---

## API Endpoints

### Book REST API (`/api/books`)

| Method | Endpoint          | Description                              |
|--------|-------------------|------------------------------------------|
| GET    | /api/books        | Get all books (paginated, filter/search) |
| GET    | /api/books/{isbn} | Get book by ISBN                         |
| GET    | /api/books/recent | Get recently added books                 |

Query parameters for `GET /api/books`:

- `page` (default: 0)
- `pageSize` (default: 10)
- `query` — searches title, ISBN, genre
- `genre` — filters by genre (case-insensitive)

### Book UI Routes

| Method | Path          | Description      |
|--------|---------------|------------------|
| GET    | /             | Home page        |
| GET    | /books        | Browse all books |
| GET    | /books/{isbn} | Book detail page |
| GET    | /books/form   | Add book form    |
| POST   | /books/form   | Submit new book  |

---

## DTOs

### Request

- `BookRequestDto` — `title`, `isbn`, `publishedAt`, `pageCount`, `genre`, `authorIds`

### Response

- `BookResponseDto` — `id`, `title`, `isbn`, `genre`, `pageCount`, `publishedAt`, `authors`
- `AuthorResponseDto` — `id`, `firstname`, `lastname`, `email`, `dob`

---

## Soft Delete

Entities are never hard deleted. Every query filters by `deleted = false`.
The `deleted` column defaults to `false` at the database level via Hibernate `@Generated`.

---

## Database Migrations (Flyway)

Files located in `src/main/resources/db/migration/`:

| File                | Description               |
|---------------------|---------------------------|
| `V1__init.sql`      | Schema creation           |
| `V2__seed_data.sql` | Initial authors and books |

Flyway runs automatically on startup.

---

## Templates (KTE)

Server-side rendering using JTE with Kotlin (`.kte` files) in `src/main/jte/`.

| Template         | Route           |
|------------------|-----------------|
| `layout.kte`     | Shared layout   |
| `home.kte`       | `/`             |
| `books.kte`      | `/books`        |
| `book.kte`       | `/books/{isbn}` |
| `books-form.kte` | `/books/form`   |

All pages use `@template.layout(title, content)` to avoid repeating nav/footer.

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

Make sure PostgreSQL is running, then:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Potential Future Improvements

- [ ] Author CRUD — create, edit, delete authors via UI and REST API
- [ ] Book edit and soft delete via UI
- [ ] Review system — users can submit 1–5 star ratings and written reviews
- [ ] User authentication with Spring Security
- [ ] Input validation with `@Valid` and proper error responses
- [ ] Global exception handler with `@ControllerAdvice`
- [ ] Swagger / OpenAPI documentation
- [ ] Pagination controls on the home page recent section
- [ ] Book cover image upload
- [ ] Advanced filtering — by date range, page count, multiple genres
- [ ] Unit and integration tests with JUnit and Testcontainers

---

## Author

Giorgi Chapidze