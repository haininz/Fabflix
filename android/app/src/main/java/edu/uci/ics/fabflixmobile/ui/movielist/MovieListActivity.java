package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    Button previous, next;
    final ArrayList<Movie> movies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // TODO: this should be retrieved from the backend server
        Intent getMovies_intent = this.getIntent();
        previous = findViewById(R.id.prevButton);
        next = findViewById(R.id.nextButton);

        try{
            JSONArray result = new JSONArray(getMovies_intent.getStringExtra("moviesList"));
            for (int i = 0; i < result.length(); i++) {
                movies.add(new Movie(result.getJSONObject(i).getString("movies_title"),
                        Short.parseShort(result.getJSONObject(i).getString("movies_year")),
                        result.getJSONObject(i).getString("movies_director"),
                        result.getJSONObject(i).getString("movies_genres"),
                        result.getJSONObject(i).getString("movies_stars"),
                        result.getJSONObject(i).getString("movies_id"))
                );
            }
            System.out.println("result -> " + result.toString());
            System.out.println("movies -> " + movies.get(0).toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }


//        movies.add(new Movie("The Terminal", (short) 2004, "a", "a", "a"));
//        movies.add(new Movie("The Final Season", (short) 2007, "a", "a", "a"));
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list); // !! Reference to list defined in activity_movielist.xml
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
//            Movie movie = movies.get(position);
//            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d, id: %s", position, movie.getName(), movie.getYear(), movies.get(position).getMovid_id());
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Intent sing_movie_page = new Intent(MovieListActivity.this, SingleMovieActivity.class);
            sing_movie_page.putExtra("movies_id", movies.get(position).getMovid_id());
            startActivity(sing_movie_page);
        });
    }
}