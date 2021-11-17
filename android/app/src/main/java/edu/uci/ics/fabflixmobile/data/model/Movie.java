package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final short year;
    private final String director;
    private final String three_genres;
    private final String three_stars;


    public Movie(String name, short year, String director, String three_genres, String three_stars) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.three_genres = three_genres;
        this.three_stars = three_stars;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public String getDirector(){return director;}

    public String getThree_genres() {
        return three_genres;
    }

    public String getThree_stars() {
        return three_stars;
    }
}