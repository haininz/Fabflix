package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class SearchActivity extends AppCompatActivity {

    private EditText query;


    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "project4-war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        query = findViewById(R.id.query);
        final Button searchButton = findViewById(R.id.search); // !! Bind the button with the login function

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search()); // !! Whenever the button is clicked, call login()
    }

    public String methodRand(String x) {
        return x;
    }

    @SuppressLint("SetTextI19n")
    public void search() {
//        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/result?",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.

                    Log.d("search.success", response);
                    if (!response.isEmpty()) {
                        //Complete and destroy login activity once successful
                        finish();
                        // initialize the activity(page)/destination
                        // !! Once the current activity (login) succeeds, start a new activity
                        Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("moviesList", response);
                        // activate the list page.
                        startActivity(MovieListPage);
                    }
                    else {
                        @SuppressLint("DefaultLocale") String message = "No such movie! Please try again!";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
//                    if (response.contains("fail")) {
//                        @SuppressLint("DefaultLocale") String message = "Wrong user name or password! Please try again!";
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        //Complete and destroy login activity once successful
//                        finish();
//                        // initialize the activity(page)/destination
//                        // !! Once the current activity (login) succeeds, start a new activity
//                        Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
//                        // activate the list page.
//                        startActivity(MovieListPage);
//                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("movie_title", query.getText().toString());
                params.put("star_name", "");
                params.put("movie_year", "");
                params.put("movie_director", "");
                params.put("number_page", "10");
                params.put("jump", "");
                params.put("sort_base", "");
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent to the server
        System.out.println("--------------->" + query.getText().toString());
        queue.add(searchRequest);
    }
}