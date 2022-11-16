DROP TABLE if exists USERS, FRIENDSHIP, FILMS, MPA_RATINGS, GENRES, LIKES, FILM_GENRE, FILM_MPA_RATING  CASCADE;

CREATE TABLE IF NOT EXISTS "USERS"
(
    "ID"       INTEGER AUTO_INCREMENT,
    "LOGIN"    VARCHAR(20) NOT NULL,
    "EMAIL"    VARCHAR(50) NOT NULL,
    "NAME"     VARCHAR(50),
    "BIRTHDAY" DATE,
    CONSTRAINT USERS_PK
        PRIMARY KEY (ID)
);
CREATE TABLE IF NOT EXISTS "FRIENDSHIP"
(
    "FROM_USER_ID" INTEGER NOT NULL,
    "TO_USER_ID"   INTEGER NOT NULL,
    "STATUS"       BOOLEAN NOT NULL,
    CONSTRAINT FRIENDSHIP_PK
        PRIMARY KEY (FROM_USER_ID, TO_USER_ID)
);
CREATE TABLE IF NOT EXISTS "FILMS"
(
    "ID"          INT     NOT NULL AUTO_INCREMENT,
    "NAME"        VARCHAR NOT NULL,
    "DESCRIPTION" VARCHAR(200),
    "DURATION"    INT,
    "RELEASEDATE" DATE,
    "ID_MPA"      INT,
    CONSTRAINT "FILMS_PK"
        PRIMARY KEY (ID)
);
CREATE TABLE IF NOT EXISTS "MPA_RATINGS"
(
    "MPA_RATING_ID" INTEGER NOT NULL,
    "NAME"          VARCHAR NOT NULL UNIQUE,
    CONSTRAINT MPA_RATINGS_PK
        PRIMARY KEY (MPA_RATING_ID)
);
CREATE TABLE IF NOT EXISTS "GENRES"
(
    "GENRE_ID" INT                   NOT NULL,
    "NAME"     CHARACTER VARYING(50) NOT NULL,
    CONSTRAINT "GENRE_PK"
        PRIMARY KEY (GENRE_ID)
);
CREATE TABLE IF NOT EXISTS "LIKES"
(
    "FILM_ID" INT NOT NULL,
    "USER_ID" INT NOT NULL,
    CONSTRAINT "LIKES_PK"
        PRIMARY KEY (FILM_ID, USER_ID)
);
CREATE TABLE IF NOT EXISTS "FILM_GENRE" (
"FILM_ID" INTEGER NOT NULL REFERENCES films (id) ON DELETE CASCADE,
"GENRE_ID" INTEGER NOT NULL REFERENCES genres (genre_id) ON DELETE RESTRICT,
PRIMARY KEY (FILM_ID, genre_id)
);
CREATE TABLE IF NOT EXISTS "FILM_MPA_RATING" (
"FILM_ID" INTEGER NOT NULL REFERENCES films (id) ON DELETE CASCADE,
"MPA_RATING_ID" INTEGER NOT NULL REFERENCES MPA_RATINGS (MPA_RATING_ID) ON DELETE RESTRICT,
PRIMARY KEY (FILM_ID, MPA_RATING_ID)
);
