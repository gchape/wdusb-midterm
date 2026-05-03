# LibSys - Book Library System

## Overview

A Spring Boot application written in Kotlin for managing a book library. Supports browsing and managing books, authors,
publishers, and genres with both a server-side rendered UI (KTE) and a full REST API.

---

## Architecture

The application follows a layered architecture:

### Controller Layer

* **View Controllers:** Handle HTTP requests for server-side rendering using KTE templates.
* **REST Controllers:** Provide a programmatic API under the `/api` prefix, returning JSON and utilizing SpringDoc for
  OpenAPI documentation.
* Delegates all business logic to the service layer.

### Service Layer

* Core application logic and validation.
* All write operations are `@Transactional`.
* Read operations use `@Transactional(readOnly = true)`.
* Duplicate checks throw typed domain exceptions (e.g., `EntityAlreadyExistsException`) before persisting.

### Repository Layer

* Spring Data JPA with Hibernate.
* `@EntityGraph` used to avoid N+1 on association fetching.
* Returns Spring Data projection interfaces directly to optimize database performance.

---

## Technologies

| Technology              | Usage                       |
|:------------------------|:----------------------------|
| **Kotlin**              | Primary language            |
| **JDK 25**              | Runtime                     |
| **Spring Boot**         | Application framework       |
| **Spring Web MVC**      | View and REST controllers   |
| **SpringDoc / Swagger** | OpenAPI 3.0 documentation   |
| **Spring Data JPA**     | Repository layer            |
| **Hibernate**           | ORM                         |
| **PostgreSQL**          | Database                    |
| **Flyway**              | Schema migrations           |
| **KTE**                 | Server-side template engine |
| **Docker**              | Containerization            |

---

## REST API Routes (`/api`)

The API supports full CRUD operations and is documented via Swagger UI at `/swagger-ui.html` (enabled in `dev` profile).

### Books

| Method | Path              | Description                           |
|:-------|:------------------|:--------------------------------------|
| GET    | `/api/books`      | Paginated catalog (sort by date desc) |
| GET    | `/api/books/{id}` | Full book detail                      |
| POST   | `/api/books`      | Create a new book                     |
| PUT    | `/api/books/{id}` | Update existing book                  |
| DELETE | `/api/books/{id}` | Soft-delete book                      |

### Authors

| Method | Path                      | Description                        |
|:-------|:--------------------------|:-----------------------------------|
| GET    | `/api/authors`            | Paginated list (sort by last name) |
| GET    | `/api/authors/{id}`       | Author detail                      |
| GET    | `/api/authors/{id}/books` | List of books by this author       |
| POST   | `/api/authors`            | Create a new author                |
| PUT    | `/api/authors/{id}`       | Update author details              |
| DELETE | `/api/authors/{id}`       | Soft-delete author                 |

### Genres & Publishers

| Method | Path                   | Description                        |
|:-------|:-----------------------|:-----------------------------------|
| GET    | `/api/genres`          | List all genres                    |
| GET    | `/api/publishers`      | List all non-deleted publishers    |
| POST   | `/api/genres`          | Create a genre (unique name check) |
| DELETE | `/api/publishers/{id}` | Soft-delete publisher              |

---

## UI Routes

### Books (`/books`)

| Method | Path                 | Description            |
|:-------|:---------------------|:-----------------------|
| GET    | `/books`             | Paginated book catalog |
| GET    | `/books/{id}`        | Book detail page       |
| GET    | `/books/new`         | Add book form          |
| POST   | `/books/new`         | Submit new book        |
| POST   | `/books/{id}/delete` | Soft delete a book     |

---

## Soft Delete

Entities (Books, Authors, Publishers) are never hard deleted. Every query filters by `deletedAt IS NULL`. Accessing a
deleted or missing entity throws `EntityNotFoundException`, handled globally by `GlobalExceptionHandler`.

---

## Templates (KTE)

Server-side rendering using the Kotlin version of JTE (`.kte` files) located in `src/main/kte/`. The Maven build is
configured to look for source templates specifically in the `src/main/kte` directory.

---

## Logging

Configured via `logback-spring.xml`:

* **dev:** `INFO` root, `DEBUG` SQL + bindings enabled.
* **prod:** `WARN` root, SQL logging disabled, Swagger UI/API-docs disabled.

---

## Potential Future Improvements

* [x] Author CRUD — create, edit, delete authors via UI
* [x] Book edit and soft delete via UI
* [x] Input validation with `@Valid` and proper error responses
* [x] Global exception handler with `@ControllerAdvice`
* [x] Swagger / OpenAPI documentation
* [ ] Review system — users can submit 1–5-star ratings and written reviews
* [ ] User authentication with Spring Security
* [ ] Book cover image upload
* [ ] Advanced filtering — by date range, page count, multiple genres
* [ ] Unit and integration tests with JUnit and Testcontainers