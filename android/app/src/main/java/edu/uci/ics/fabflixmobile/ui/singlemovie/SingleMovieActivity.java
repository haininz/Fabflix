package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;

public class SingleMovieActivity extends AppCompatActivity {

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "project4-war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private String single_movie_year = null;

    private TextView mTitle;
    private TextView mStars;
    private TextView mDir;
    private TextView mYear;
    private TextView mGenres;

    String movie_id = null;
    final ArrayList<Movie> singleMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);

        // TODO: this should be retrieved from the backend server
        Intent getMovies_intent = this.getIntent();
        movie_id = getMovies_intent.getStringExtra("movies_id");

        final ArrayList<Movie> movies = new ArrayList<>();

        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest singleMovieSearch = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/single-movie",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.

                    Log.d("search.success", response);

                    try{
                        JSONArray result = new JSONArray(response);
                        for (int i = 0; i < result.length(); i++) {
                            singleMovies.add(new Movie(result.getJSONObject(i).getString("movies_title"),
                                    Short.parseShort(result.getJSONObject(i).getString("movies_year")),
                                    result.getJSONObject(i).getString("movies_director"),
                                    result.getJSONObject(i).getString("movies_genres"),
                                    result.getJSONObject(i).getString("movies_stars"),
                                    result.getJSONObject(i).getString("movies_id"))
                            );
                        }
                         System.out.println("single movie page -> " + result.toString());

                        single_movie_year = String.valueOf(singleMovies.get(0).getYear());

                        String[] starsSplits = singleMovies.get(0).getThree_stars().split(", ");
                        String starslist = "";
                        for(int i = 0; i < starsSplits.length; i++){
                            starslist += starsSplits[i];
                            if( i!= starsSplits.length - 1){
                                starslist += ", ";
                            }
                        }

                        mTitle = findViewById(R.id.single_movie_Title);
                        mTitle.setText(singleMovies.get(0).getName());

                        mYear = findViewById(R.id.single_movie_Year);
                        mYear.setText(single_movie_year);

                        mDir = findViewById(R.id.single_movie_Director);
                        mDir.setText(singleMovies.get(0).getDirector());

                        mGenres = findViewById(R.id.single_movie_Genres);
                        mGenres.setText(singleMovies.get(0).getThree_genres());

                        mStars = findViewById(R.id.single_movie_Stars);
                        mStars.setText(starslist);


                        System.out.println("genres -> " + singleMovies.get(0).getThree_genres());
                        System.out.println("starslist -> " + starslist);
                        System.out.println("single_movie_year    -> " + single_movie_year);

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("single movie.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("id", movie_id);
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent to the server
        // System.out.println("---------------> movie_id " + movie_id);
        queue.add(singleMovieSearch);

    }
}