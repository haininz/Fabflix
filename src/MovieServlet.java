import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieServlet", urlPatterns = "/movies")
public class MovieServlet extends HttpServlet {
    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();
            Statement movieToStar = conn.createStatement(); // used for find star info according to the movie

            String query;
            query = "SELECT id, title, year, director, Genres_List, Stars_List, rating \n" +
                    "from movies JOIN ratings on movies.id = ratings.movieId JOIN \n" +
                    "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Genres_List \n" +
                    "FROM (genres_in_movies as gim JOIN genres on gim.genreId = genres.id) \n" +
                    "GROUP BY movieId) as glist on movies.id = glist.movieId JOIN \n" +
                    "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Stars_List \n" +
                    "FROM (stars_in_movies as sim JOIN stars on sim.starId = stars.id) \n" +
                    "GROUP BY movieId) as slist on movies.id = slist.movieId \n" +
                    "ORDER BY ratings.rating DESC limit 20";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movies_id = rs.getString("id");
                String movies_title = rs.getString("title");
                String movies_year = rs.getString("year");
                String movies_director = rs.getString("director");
                String movies_genres = rs.getString("Genres_List");
                String movies_stars = rs.getString("Stars_List");
                String movies_rating = rs.getString("rating");

                String movies_stars_id = new String("");

//                String[] temp = movies_stars.split(",");
//                movies_stars = String.join(", ", temp[0], temp[1], temp[2]);

                String query1 = "select stars.id, stars.name from movies \n" +
                        "join stars_in_movies on movies.id = stars_in_movies.movieId \n" +
                        "join stars on stars_in_movies.starId = stars.id\n" +
                        "where movies.id = " + "\"" + movies_id + "\"" + "\n" +
                        "ORDER BY name limit 3";

                ResultSet rs1 = movieToStar.executeQuery(query1);
                while (rs1.next()){
                    String stars_id = rs1.getString("id");
                    String star_name = rs1.getString("name");
                    movies_stars_id += stars_id + "," + star_name + "\n";
                }


                String[] tempG = movies_genres.split(", ");
                Arrays.sort(tempG);
                String temp_genres = "";
                for(int z = 0; z < tempG.length && z < 3; z++){
                    if(z == 2 || tempG.length - 1 == z){
                        temp_genres = temp_genres + tempG[z];
                        break;
                    }
                    else {
                        temp_genres = temp_genres + tempG[z] + ", ";
                    }
                }
                movies_genres = temp_genres;

                String[] tempS = movies_stars.split(", ");
                Arrays.sort(tempS);
                String temp_stars = "";
                for(int z = 0; z < tempS.length && z < 3; z++){
                    if(z == 2 || tempS.length - 1 == z){
                        temp_stars = temp_stars + tempS[z];
                        break;
                    }
                    else {
                        temp_stars = temp_stars + tempS[z] + ", ";
                    }
                }
                movies_stars = temp_stars;


                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movies_id", movies_id);
                jsonObject.addProperty("movies_title", movies_title);
                jsonObject.addProperty("movies_year", movies_year);
                jsonObject.addProperty("movies_director", movies_director);
                jsonObject.addProperty("movies_genres", movies_genres);
                jsonObject.addProperty("movies_stars", movies_stars);
                jsonObject.addProperty("movies_rating", movies_rating);

                jsonObject.addProperty("movies_stars_id", movies_stars_id);

                jsonArray.add(jsonObject);

                rs1.close();
            }
            rs.close();
            statement.close();
            movieToStar.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources
    }
}
