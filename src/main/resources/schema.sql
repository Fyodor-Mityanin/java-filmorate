DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS subscribes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS mpa;
DROP TABLE IF EXISTS rating;

CREATE TABLE IF NOT EXISTS films (
    id           int PRIMARY KEY AUTO_INCREMENT,
    name         varchar(255),
    mpa          int,
    rate         int DEFAULT 0,
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

CREATE TABLE IF NOT EXISTS subscribes (
    author  int,
    subscriber  int
);

CREATE TABLE IF NOT EXISTS genre (
    id           int PRIMARY KEY AUTO_INCREMENT,
    name         varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS mpa (
    id           int PRIMARY KEY AUTO_INCREMENT,
    name         varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id    int,
    genre_id   int
);

ALTER TABLE films ADD CONSTRAINT IF NOT EXISTS films_mpa_id_fk FOREIGN KEY (mpa) REFERENCES mpa (id);

ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS films_id_fk FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS genre_id_fk FOREIGN KEY (genre_id) REFERENCES genre (id);
ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS unique_id_fk UNIQUE (film_id, genre_id);

ALTER TABLE subscribes ADD CONSTRAINT IF NOT EXISTS author_id_fk FOREIGN KEY (author) REFERENCES users (id);
ALTER TABLE subscribes ADD CONSTRAINT IF NOT EXISTS subscriber_id_fk FOREIGN KEY (subscriber) REFERENCES users (id);
ALTER TABLE subscribes ADD CONSTRAINT IF NOT EXISTS subscribe_id_fk UNIQUE (author, subscriber);