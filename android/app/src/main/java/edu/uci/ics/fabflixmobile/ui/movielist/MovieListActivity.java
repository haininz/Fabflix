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

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    Button previous, next;

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
            System.out.println("result -> " + result.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        final ArrayList<Movie> movies = new ArrayList<>();
        movies.add(new Movie("The Terminal", (short) 2004, "a", "a", "a"));
        movies.add(new Movie("The Final Season", (short) 2007, "a", "a", "a"));
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list); // !! Reference to list defined in activity_movielist.xml
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}