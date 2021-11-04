import java.util.ArrayList;

public class Movie {
    private String id;
    private String title;
    private int year;
    private ArrayList<String> directors = new ArrayList<>();
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> stars = new ArrayList<>();


    public Movie(String id, String title, int year, ArrayList<String> director, ArrayList<String> genres) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.directors = director;
        this.genres = genres;
    }

    public Movie(){

    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", directors=" + directors.toString() +
                ", genres=" + genres.toString() +
                ", stars=" + stars.toString() +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<String> getDirectors() {
        return directors;
    }

    public void setDirectors(String director) {
        this.directors.add(director);
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(String genre) {
        this.genres.add(genre);
    }

    public ArrayList<String> getStars() {
        return stars;
    }

    public void setStars(String star) {
        this.stars.add(star);
    }
}
