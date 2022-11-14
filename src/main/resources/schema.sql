DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS connection;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS rating;

CREATE TABLE IF NOT EXISTS films (
    id           int PRIMARY KEY AUTO_INCREMENT,
    name         varchar(255),
    rating       int,
    description  varchar(255),
    release_date date,
    duration     int,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id       int PRIMARY KEY AUTO_INCREMENT,
    email    varchar(255),
    login    varchar(255),
    name     varchar(255),
    birthday date,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS connection (
    id         int PRIMARY KEY AUTO_INCREMENT,
    status     ENUM ('NO_RESPONSE', 'ACCEPTED', 'REJECTED'),
    createdAt  timestamp DEFAULT (now()),
    lastUpdate timestamp DEFAULT (now()),
    requester  int,
    requestee  int,
    specifier  int,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS genre (
    id           int PRIMARY KEY AUTO_INCREMENT,
    name         varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS rating (
    id           int PRIMARY KEY AUTO_INCREMENT,
    name         varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id    int,
    genre_id   int
);

ALTER TABLE connection ADD CONSTRAINT IF NOT EXISTS requester_user_id_fk FOREIGN KEY (requester) REFERENCES users (id);
ALTER TABLE connection ADD CONSTRAINT IF NOT EXISTS requestee_user_id_fk FOREIGN KEY (requestee) REFERENCES users (id);
ALTER TABLE connection ADD CONSTRAINT IF NOT EXISTS specifier_user_id_fk FOREIGN KEY (specifier) REFERENCES users (id);

ALTER TABLE films ADD CONSTRAINT IF NOT EXISTS films_rating_id_fk FOREIGN KEY (rating) REFERENCES rating (id);

ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS films_id_fk FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS genre_id_fk FOREIGN KEY (genre_id) REFERENCES genre (id);
ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS unique_id_fk UNIQUE (film_id, genre_id);
