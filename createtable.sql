CREATE DATABASE IF NOT EXISTS moviedb;

USE moviedb;

CREATE TABLE IF NOT EXISTS movies (
    id VARCHAR(10) NOT NULL,
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stars (
    id VARCHAR(10) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthYear INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars (id),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE TABLE IF NOT EXISTS genres (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(32),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS genres_in_movies (
    genreId INTEGER NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres (id),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE TABLE IF NOT EXISTS creditcards (
    id VARCHAR(20) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    expiration DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS customers (
    id INTEGER NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    ccId VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (ccId) REFERENCES creditcards (id)
);

CREATE TABLE IF NOT EXISTS sales (
    id INTEGER NOT NULL AUTO_INCREMENT,
    customerId INTEGER NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    saleDate DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customerId) REFERENCES customers (id),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE TABLE IF NOT EXISTS ratings (
    movieId VARCHAR(10) NOT NULL,
    rating FLOAT NOT NULL,
    numVotes INTEGER NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies (id)
);


CREATE TABLE IF NOT EXISTS employees(
    email VARCHAR(50) PRIMARY KEY,
    password VARCHAR(20) NOT NULL,
    fullname VARCHAR(100) NOT NULL
);

DELIMITER $$
create procedure add_movie (IN title varchar(100), year int, director varchar(100),
                            star_name varchar(100), genre_name varchar(32))
BEGIN
    set @count_movie := (select count(*) from movies where movies.title = title and movies.year = year and movies.director = director);
    IF @count_movie = 0 THEN
        set @new_movie_id := (select concat('tt0', cast(cast(substring(max(id) from 3) as unsigned)+1 as char(10))) from movies);
        insert into movies values (@new_movie_id, title, year, director);

        set @count_star := (select count(*) from stars where stars.name = star_name);
        IF @count_star = 0 THEN
            set @new_star_id := (select concat('nm', cast(cast(substring(max(id) from 3) as unsigned)+1 as char(10))) from stars);
            insert into stars values (@new_star_id, star_name, null);
        ELSE
            set @new_star_id := (select id from stars where stars.name = star_name);
        END IF;
        insert into stars_in_movies values (@new_star_id, @new_movie_id);

        set @count_genre := (select count(*) from genres where genres.name = genre_name);
        IF @count_genre = 0 THEN
            set @new_genre_id := (select max(id)+1 from genres);
            insert into genres values (@new_genre_id, genre_name);
        ELSE
            set @new_genre_id := (select id from genres where genres.name = genre_name);
        END IF;
        insert into genres_in_movies values (@new_genre_id, @new_movie_id);

    END IF;
END
$$
DELIMITER ;