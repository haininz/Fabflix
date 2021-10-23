import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession();

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        if (id.equals("goback")){
            ArrayList<String> previousBrowseParams = (ArrayList<String>) session.getAttribute("previousBrowseParams");
            // previousBrowseParams: {title, genre, number_of_movies_per_page, current_page_number, sort_requirement}
            ArrayList<String> previousSearchParams = (ArrayList<String>) session.getAttribute("previousSearchParams");
            // previousSearchParams: {name, title, year, director, number_of_movies_per_page, current_page_number, sort_requirement}
            if (previousBrowseParams != null) {
                if (previousBrowseParams.get(5).equals("browse")) {
                    System.out.println("Returning to previous browse history (5): " + previousBrowseParams.get(5));
                    String tempTitle = previousBrowseParams.get(0);
                    String tempGenre = previousBrowseParams.get(1);
                    String tempNumPage = previousBrowseParams.get(2);
                    String sortBase = previousBrowseParams.get(4);
                    response.sendRedirect("browse.html?title=" + tempTitle + "&genre=" + tempGenre
                            + "&number_page=" + tempNumPage + "&jump=&sort_base=");
//                    System.out.println("browse.html?title=" + tempTitle + "&genre=" + tempGenre
//                            + "&number_page=" + tempNumPage + "&jump=&sort_base=");
                }
            }
            if (previousSearchParams != null) {
                if (previousSearchParams.get(7).equals("search")) {
                    System.out.println("Returning to previous search history (7): " + previousSearchParams.get(7));
                    String tempName = previousSearchParams.get(0);
                    String tempTitle = previousSearchParams.get(1);
                    String tempYear = previousSearchParams.get(2);
                    String tempDirector = previousSearchParams.get(3);
                    String tempNumPage = previousSearchParams.get(4);
                    String sortBase = previousSearchParams.get(6);
                    response.sendRedirect("searchResult.html?movie_title=" + tempTitle + "&movie_year=" + tempYear
                            + "&movie_director=" + tempDirector + "&star_name=" + tempName
                            + "&jump=&number_page=" + tempNumPage + "&sort_base=");
//                    System.out.println("searchResult.html?movie_title=" + tempTitle + "&movie_year=" + tempYear
//                            + "&movie_director=" + tempDirector + "&star_name=" + tempName
//                            + "&jump=&number_page=" + tempNumPage + "&sort_base=");
                }
            }
        }
        else {
            // The log message can be found in localhost log
            request.getServletContext().log("getting id: " + id);

            // Output stream to STDOUT
            PrintWriter out = response.getWriter();

            // Get a connection from dataSource and let resource manager close the connection after usage.
            try (Connection conn = dataSource.getConnection()) {
                // Get a connection from dataSource

                // Construct a query with parameter represented by "?"
                String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                        "where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, id);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();
                String movieInfo = "";

                // Iterate through each row of rs
                while (rs.next()) {

                    String starId = rs.getString("starId");
                    String starName = rs.getString("name");  //
                    String starBirth = rs.getString("birthYear"); //

                    // when birth is null, set to N/A
                    if(starBirth == null){
                        starBirth = "N/A";
                    }

                    String movieId = rs.getString("movieId");
                    String movieTitle = rs.getString("title");

                    movieInfo += movieTitle + "," + movieId + "\n";
                    // System.out.println("movieInfo-->" + movieInfo);

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("star_id", starId);
                    jsonObject.addProperty("star_name", starName);
                    jsonObject.addProperty("star_birth", starBirth);
                    jsonObject.addProperty("movie_id", movieId);
                    jsonObject.addProperty("movie_title", movieTitle);
                    jsonObject.addProperty("movie_info", movieInfo);

                    jsonArray.add(jsonObject);
                }
                rs.close();
                statement.close();

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



        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
