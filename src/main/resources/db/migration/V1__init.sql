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

CREATE SEQUENCE genres_id_seq;
CREATE TABLE genres
(
    id   INT PRIMARY KEY DEFAULT nextval('genres_id_seq'),
    name VARCHAR(50) NOT NULL UNIQUE
);
ALTER SEQUENCE genres_id_seq OWNED BY genres.id;

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

CREATE TABLE book_genres
(
    book_id  INT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

CREATE INDEX idx_books_isbn ON books (isbn);