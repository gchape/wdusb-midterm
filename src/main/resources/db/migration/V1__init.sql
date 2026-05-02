CREATE SEQUENCE users_id_seq;

CREATE TABLE users
(
    id            INT PRIMARY KEY       DEFAULT nextval('users_id_seq'),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP             DEFAULT NULL
);

ALTER SEQUENCE users_id_seq OWNED BY users.id;

CREATE SEQUENCE publishers_id_seq;

CREATE TABLE publishers
(
    id         INT PRIMARY KEY       DEFAULT nextval('publishers_id_seq'),
    name       VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP             DEFAULT NULL
);

ALTER SEQUENCE publishers_id_seq OWNED BY publishers.id;

CREATE SEQUENCE authors_id_seq;

CREATE TABLE authors
(
    id         INT PRIMARY KEY      DEFAULT nextval('authors_id_seq'),
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(60) NOT NULL,
    bio        TEXT,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP            DEFAULT NULL
);

ALTER SEQUENCE authors_id_seq OWNED BY authors.id;

CREATE SEQUENCE books_id_seq;

CREATE TABLE books
(
    id               INT PRIMARY KEY       DEFAULT nextval('books_id_seq'),
    title            VARCHAR(255) NOT NULL,
    isbn             VARCHAR(13)  NOT NULL UNIQUE,
    publisher_id     INT REFERENCES publishers (id) ON DELETE NO ACTION,
    publication_date DATE         NOT NULL,
    page_count       SMALLINT     NOT NULL CHECK (page_count > 0),
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP             DEFAULT NULL
);

ALTER SEQUENCE books_id_seq OWNED BY books.id;

CREATE TABLE book_authors
(
    book_id   INT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    author_id INT NOT NULL REFERENCES authors (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

CREATE SEQUENCE genres_id_seq;

CREATE TABLE genres
(
    id   INT PRIMARY KEY DEFAULT nextval('genres_id_seq'),
    name VARCHAR(50) NOT NULL UNIQUE
);

ALTER SEQUENCE genres_id_seq OWNED BY genres.id;

CREATE TABLE book_genres
(
    book_id  INT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

CREATE SEQUENCE reviews_id_seq;

CREATE TABLE reviews
(
    id         INT PRIMARY KEY    DEFAULT nextval('reviews_id_seq'),
    book_id    INT       NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    user_id    INT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    rating     SMALLINT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    body       TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_book_review UNIQUE (user_id, book_id)
);

ALTER SEQUENCE reviews_id_seq OWNED BY reviews.id;

CREATE INDEX idx_books_isbn ON books (isbn);
CREATE INDEX idx_reviews_book_id ON reviews (book_id);