# MIDTERM - Book Library System

## Overview

This project is a Spring Boot application written in Kotlin. It manages books and authors using a layered architecture
and exposes RESTful APIs along with a simple server-side UI built with JTE.

The system supports CRUD operations, DTO usage, validation, database migrations with Flyway, and entity relationships.

---

## Architecture

The application follows a layered architecture:

### Controller Layer

* Handles HTTP requests and responses
* Exposes REST endpoints and UI routes
* Does not contain business logic

### Service Layer

* Contains core application logic
* Coordinates operations between layers

### Repository Layer

* Handles database interaction
* Uses Spring Data JPA (Hibernate)

---

## Technologies Used

* Kotlin (latest stable)
* JDK 25
* Spring Boot
* Spring Web
* Spring Data JPA (Hibernate)
* PostgreSQL
* Flyway (database migrations)
* JTE (server-side rendering)
* Swagger (OpenAPI)
* JUnit (testing)

---

## Entities and Relationships

### Author

* id: Long
* name: String
* email: String

### Book

* id: Long
* title: String
* description: String

### Relationship

* One Author → Many Books
* Each Book belongs to one Author (Many-to-One)

---

## API Endpoints

### Author APIs

| Method | Endpoint      | Description      |
|--------|---------------|------------------|
| POST   | /authors      | Create author    |
| GET    | /authors      | Get all authors  |
| GET    | /authors/{id} | Get author by ID |
| PUT    | /authors/{id} | Update author    |
| DELETE | /authors/{id} | Delete author    |

### Book APIs

| Method | Endpoint    | Description    |
|--------|-------------|----------------|
| POST   | /books      | Create book    |
| GET    | /books      | Get all books  |
| GET    | /books/{id} | Get book by ID |
| PUT    | /books/{id} | Update book    |
| DELETE | /books/{id} | Delete book    |

---

## DTO Usage

The application uses DTOs to separate internal models from API responses.

### Request DTOs

* AuthorRequestDTO
* BookRequestDTO

### Response DTOs

* AuthorResponseDTO
* BookResponseDTO

Entities are not returned directly from controllers.

---

## Validation

Validation is implemented using annotations:

* @NotNull
* @Size
* @Email

Invalid requests return appropriate error responses.

---

## Exception Handling

* Uses Spring Boot default exception handling
* Prevents application crashes on invalid input
* Returns appropriate HTTP status codes

---

## Database Configuration (PostgreSQL)

```properties id="db01"
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

Make sure PostgreSQL is running and the database `library_db` is created.

---

## Flyway Migrations

Database schema is managed using Flyway.

Migration files are located in:

```id="flyway01"
src/main/resources/db/migration
```

Example migration:

```sql id="flyway02"
CREATE TABLE books
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    isbn         VARCHAR(20)  NOT NULL UNIQUE,
    published_at DATE,
    genre        VARCHAR(50),
    page_count   INT CHECK (page_count > 0),
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW(),
    deleted      BOOLEAN   DEFAULT false
);
```

Flyway runs automatically on application startup.

---

## Swagger Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui.html

All endpoints are documented and testable.

---

## JTE UI Pages

### Author Pages

* /authors → list authors
* /authors/create → create author form

### Book Pages

* /books → list books
* /books/create → create book form
* /books/{id} → book details

---

## Running the Application

```bash id="run02"
./mvnw spring-boot:run
```

---

## Testing

Testing is implemented using JUnit.

You can run tests with:

```bash id="test01"
./mvnw test
```

---

## Features

* Layered architecture (Controller / Service / Repository)
* RESTful APIs
* CRUD operations
* One-to-Many relationship
* DTO pattern
* Validation
* Flyway migrations
* Swagger integration
* Server-side UI with JTE
* Unit and integration testing with JUnit

---

## Future Improvements

* Pagination and sorting
* Search by title or author
* Authentication (Spring Security)
* File upload for book covers
* Advanced filtering

---

## Author

Giorgi Chapidze