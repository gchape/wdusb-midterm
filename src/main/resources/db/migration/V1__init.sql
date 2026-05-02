CREATE TABLE books
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    isbn         VARCHAR(20)  NOT NULL UNIQUE,
    published_at DATE         NOT NULL,
    genre        VARCHAR(50),
    page_count   INT          NOT NULL CHECK (page_count > 0),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted      BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE authors
(
    id         SERIAL PRIMARY KEY,
    firstname  VARCHAR(50)  NOT NULL,
    lastname   VARCHAR(60)  NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    dob        DATE         NOT NULL,
    bio        TEXT,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted    BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE authors_books
(
    author_id  INT       NOT NULL,
    book_id    INT       NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (author_id, book_id),
    CONSTRAINT fk_author
        FOREIGN KEY (author_id)
            REFERENCES authors (id)
            ON DELETE RESTRICT,
    CONSTRAINT fk_book
        FOREIGN KEY (book_id)
            REFERENCES books (id)
            ON DELETE RESTRICT
);

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE reviews
(
    id         SERIAL PRIMARY KEY,
    book_id    INT       NOT NULL,
    user_id    INT       NOT NULL,
    rating     SMALLINT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    body       TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);