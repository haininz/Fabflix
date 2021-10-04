import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String id = request.getParameter("id");
        request.getServletContext().log("getting id: " + id);

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT id, title, year, director, genres, stars, rating \n" +
                    "from movies JOIN ratings on movies.id = ratings.movieId JOIN \n" +
                    "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as genres \n" +
                    "FROM (genres_in_movies JOIN genres on genres_in_movies.genreId = genres.id) \n" +
                    "GROUP BY movieId) as g \n" +
                    "on movies.id = g.movieId JOIN \n" +
                    "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as stars \n" +
                    "FROM (stars_in_movies JOIN stars on stars_in_movies.starId = stars.id) \n" +
                    "GROUP BY movieId) as s \n" +
                    "on movies.id = s.movieId\n" +
                    "where movies.id = ?\n" +
                    "ORDER BY ratings.rating DESC limit 20;";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movies_id = rs.getString("id");
                String movies_title = rs.getString("title");
                String movies_year = rs.getString("year");
                String movies_director = rs.getString("director");
                String movies_genres = rs.getString("genres");
                String movies_stars = rs.getString("stars");
                String movies_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movies_id", movies_id);
                jsonObject.addProperty("movies_title", movies_title);
                jsonObject.addProperty("movies_year", movies_year);
                jsonObject.addProperty("movies_director", movies_director);
                jsonObject.addProperty("movies_genres", movies_genres);
                jsonObject.addProperty("movies_stars", movies_stars);
                jsonObject.addProperty("movies_rating", movies_rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

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

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
