INSERT INTO publishers (id, name)
VALUES (7, 'Secker & Warburg'),
       (8, 'Chatto & Windus'),
       (9, 'Gnome Press'),
       (10, 'Ace Books'),
       (11, 'Bantam Books'),
       (12, 'DAW Books'),
       (13, 'Orbit Books');

INSERT INTO authors (id, first_name, last_name, bio)
VALUES (8, 'George', 'Orwell', 'English novelist, essayist, and critic known for his dystopian literature.'),
       (9, 'Aldous', 'Huxley', 'English writer and philosopher.'),
       (10, 'Isaac', 'Asimov',
        'American writer and professor of biochemistry, considered a master of hard science fiction.'),
       (11, 'Ursula K.', 'Le Guin', 'American author best known for her works of speculative fiction.'),
       (12, 'William', 'Gibson', 'American-Canadian speculative fiction writer, pioneer of the cyberpunk subgenre.'),
       (13, 'Neal', 'Stephenson', 'American writer known for his works of speculative fiction and cyberpunk.'),
       (14, 'Patrick', 'Rothfuss', 'American epic fantasy writer.'),
       (15, 'George R.R.', 'Martin', 'American novelist and television writer, author of A Song of Ice and Fire.'),
       (16, 'N.K.', 'Jemisin',
        'American sci-fi and fantasy writer, first author to win three consecutive Hugo Awards.'),
       (17, 'Daniel', 'Abraham',
        'American science fiction and fantasy author, one half of the pen name James S.A. Corey.'),
       (18, 'Ty', 'Franck', 'American novelist and screenwriter, one half of the pen name James S.A. Corey.');

INSERT INTO books (id, title, isbn, publisher_id, publication_date, page_count)
VALUES (8, '1984', '9780451524935', 7, '1949-06-08', 328),
       (9, 'Brave New World', '9780060850524', 8, '1932-01-01', 268),
       (10, 'Foundation', '9780553293357', 9, '1951-06-01', 244),
       (11, 'The Left Hand of Darkness', '9780441478125', 10, '1969-03-01', 304),
       (12, 'Neuromancer', '9780441569595', 10, '1984-07-01', 271),
       (13, 'Snow Crash', '9780553380958', 11, '1992-06-01', 480),
       (14, 'The Name of the Wind', '9780756404741', 12, '2007-03-27', 662),
       (15, 'A Game of Thrones', '9780553103540', 11, '1996-08-01', 694),
       (16, 'The Fifth Season', '9780316229296', 13, '2015-08-04', 468),
       (17, 'Leviathan Wakes', '9780316129084', 13, '2011-06-15', 561);

INSERT INTO book_authors (book_id, author_id)
VALUES (8, 8),
       (9, 9),
       (10, 10),
       (11, 11),
       (12, 12),
       (13, 13),
       (14, 14),
       (15, 15),
       (16, 16),
       (17, 17),
       (17, 18);

INSERT INTO genres (id, name)
VALUES (1, 'Science Fiction'),
       (2, 'Fantasy'),
       (3, 'Epic Fantasy'),
       (9, 'Dystopian'),
       (10, 'Cyberpunk'),
       (11, 'Space Opera');

INSERT INTO book_genres (book_id, genre_id)
VALUES (8, 1),
       (8, 9),
       (9, 1),
       (9, 9),
       (10, 1),
       (10, 11),
       (11, 1),
       (12, 1),
       (12, 10),
       (13, 1),
       (13, 10),
       (14, 2),
       (14, 3),
       (15, 2),
       (15, 3),
       (16, 2),
       (16, 1),
       (17, 1),
       (17, 11);

SELECT setval('publishers_id_seq', (SELECT MAX(id) FROM publishers));
SELECT setval('authors_id_seq', (SELECT MAX(id) FROM authors));
SELECT setval('books_id_seq', (SELECT MAX(id) FROM books));
SELECT setval('genres_id_seq', (SELECT MAX(id) FROM genres));