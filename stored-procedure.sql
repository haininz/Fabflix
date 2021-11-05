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





DELIMITER $$
create procedure parse_xml (IN title varchar(100), year int, director varchar(100),
                            star_name varchar(100), birth_year int, genre_name varchar(32))
BEGIN
    set @decision = 1;
    set @count_movie := (select count(*) from movies where movies.title = title and movies.year = year and movies.director = director);
    IF @count_movie = 0 THEN
        set @new_movie_id := (select concat('tt0', cast(cast(substring(max(id) from 3) as unsigned)+1 as char(10))) from movies);
        insert into movies values (@new_movie_id, title, year, director);
        insert into ratings values (@new_movie_id, 0.0, 0);
    ELSE
        set @decision = 0;
        set @new_movie_id := (select id from movies where movies.title = title and movies.year = year and movies.director = director);
    END IF;

    set @count_star := (select count(*) from stars where stars.name = star_name and stars.birthYear = birth_year);
    IF @count_star = 0 THEN
        set @new_star_id := (select concat('nm', cast(cast(substring(max(id) from 3) as unsigned)+1 as char(10))) from stars);
        insert into stars values (@new_star_id, star_name, birth_year);
        insert into stars_in_movies values (@new_star_id, @new_movie_id);
    ELSE
        set @new_star_id := (select id from stars where stars.name = star_name and stars.birthYear = birth_year);
        IF @decision = 1 THEN
            insert into stars_in_movies values (@new_star_id, @new_movie_id);
        END IF;
    END IF;



    set @count_genre := (select count(*) from genres where genres.name = genre_name);
    IF @count_genre = 0 THEN
        set @new_genre_id := (select max(id)+1 from genres);
        insert into genres values (@new_genre_id, genre_name);
        insert into genres_in_movies values (@new_genre_id, @new_movie_id);
    ELSE
        set @new_genre_id := (select id from genres where genres.name = genre_name);
        IF @decision = 1 THEN
            insert into genres_in_movies values (@new_genre_id, @new_movie_id);
        END IF;
    END IF;



END
$$
DELIMITER ;