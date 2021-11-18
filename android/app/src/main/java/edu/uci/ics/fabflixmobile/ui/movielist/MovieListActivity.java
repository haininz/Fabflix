package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

    Button previous, next;
    final ArrayList<Movie> movies = new ArrayList<>();

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "project4-war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

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
            sing_movie_page.putExtra("movies_title", movies.get(position).getName());
            sing_movie_page.putExtra("movies_id", movies.get(position).getMovid_id());
            sing_movie_page.putExtra("movies_director", movies.get(position).getDirector());
            // sing_movie_page.putExtra("movies_stars", movies.get(position));
            sing_movie_page.putExtra("movies_id", movies.get(position).getMovid_id());

            startActivity(sing_movie_page);
        });

        next.setOnClickListener(view -> next());
        previous.setOnClickListener(view -> previous());
    }

    @SuppressLint("SetTextI18n")
    public void next(){
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest nextRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/result",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.

                    Log.d("search.success", response);
                    if (!response.isEmpty()) {
                        //Complete and destroy login activity once successful
                        finish();
                        // initialize the activity(page)/destination
                        // !! Once the current activity (login) succeeds, start a new activity
                        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("moviesList", response);
                        // activate the list page.
                        startActivity(MovieListPage);
                    }
                    else {
                        @SuppressLint("DefaultLocale") String message = "No such movie! Please try again!";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("movie_title", "");
                params.put("star_name", "");
                params.put("movie_year", "");
                params.put("movie_director", "");
                params.put("number_page", "20");
                params.put("jump", "next");
                params.put("sort_base", "");
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent to the server
        queue.add(nextRequest);
    }

    @SuppressLint("SetTextI18n")
    public void previous(){
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest nextRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/result",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.

                    Log.d("search.success", response);
                    if (!response.isEmpty()) {
                        //Complete and destroy login activity once successful
                        finish();
                        // initialize the activity(page)/destination
                        // !! Once the current activity (login) succeeds, start a new activity
                        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("moviesList", response);
                        // activate the list page.
                        startActivity(MovieListPage);
                    }
                    else {
                        @SuppressLint("DefaultLocale") String message = "No such movie! Please try again!";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("movie_title", "");
                params.put("star_name", "");
                params.put("movie_year", "");
                params.put("movie_director", "");
                params.put("number_page", "20");
                params.put("jump", "previous");
                params.put("sort_base", "");
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent to the server
        queue.add(nextRequest);
    }
}