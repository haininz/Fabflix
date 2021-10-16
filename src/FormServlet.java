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

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "FormServlet", urlPatterns = "/form")
public class FormServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http GET
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        /*
        // Building page head with title
        out.println("<html><head><title>Movie: Found Records</title></head>");

        // Building page body
        out.println("<body><h1>Movie: Found Records</h1>");
         */


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String name = request.getParameter("star_name");
//            System.out.println(name);


            // Generate a SQL query
            String query = String.format("SELECT id, title, year, director, genres, starList, rating \n" +
                    "from (SELECT m.* from stars s, movies m, stars_in_movies sim\n" +
                    "where s.name like '%s' and s.id = sim.starId and m.id = sim.movieId) as movies\n" +
                    "JOIN ratings on movies.id = ratings.movieId \n" +
                    "JOIN (SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as genres \n" +
                    "FROM (genres_in_movies JOIN genres on genres_in_movies.genreId = genres.id) \n" +
                    "GROUP BY movieId) as g \n" +
                    "on movies.id = g.movieId \n" +
                    "JOIN (SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as starList \n" +
                    "FROM (stars_in_movies JOIN stars on stars_in_movies.starId = stars.id) \n" +
                    "GROUP BY movieId) as s \n" +
                    "on movies.id = s.movieId;", name);


            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            System.out.println("-----lol");
            /*
            // Create a html <table>
            out.println("<table border>");

            // Iterate through each row of rs and create a table row <tr>
            out.println("<tr><td>Title</td><td>Year</td><td>Director</td><td>Genres</td><td>Stars</td><td>Rating</td></tr>");
             */

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movies_title = rs.getString("title");
                String movies_year = rs.getString("year");
                String movies_director = rs.getString("director");
                String movies_genres = rs.getString("genres");
                String movies_stars = rs.getString("starList");
                String movies_rating = rs.getString("rating");

//                String[] tempG = movies_genres.split(", ");
//                Arrays.sort(tempG);
//                String temp_genres = "";
//                for(int z = 0; z < tempG.length && z < 3; z++){
//                    if(z == 2 || tempG.length - 1 == z){
//                        temp_genres = temp_genres + tempG[z];
//                        break;
//                    }
//                    else {
//                        temp_genres = temp_genres + tempG[z] + ", ";
//                    }
//                }
//                movies_genres = temp_genres;

                String[] temp = movies_stars.split(", ");
                Arrays.sort(temp);
                movies_stars = String.join(", ", temp[0], temp[1], temp[2]);

                // out.println(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                //        movies_title, movies_year, movies_director, movies_genres, movies_stars, movies_rating));
                JsonObject jsonObject = new JsonObject();
                // jsonObject.addProperty("movies_id", movies_id);
                jsonObject.addProperty("movies_title", movies_title);
                jsonObject.addProperty("movies_year", movies_year);
                jsonObject.addProperty("movies_director", movies_director);
                jsonObject.addProperty("movies_genres", movies_genres);
                jsonObject.addProperty("movies_stars", movies_stars);
                jsonObject.addProperty("movies_rating", movies_rating);

                // jsonObject.addProperty("movies_stars_id", movies_stars_id);

                jsonArray.add(jsonObject);
            }
            // out.println("</table>");


            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

            request.getServletContext().log("getting " + jsonArray.size() + " results");
            out.write(jsonArray.toString());
            System.out.println(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }

}
