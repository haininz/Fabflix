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
import java.sql.Statement;
import java.util.ArrayList;
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

        HttpSession session = request.getSession();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
//            Statement statement = conn.createStatement();
//            Statement movieToStar = conn.createStatement(); // used for find star info according to the movie

            PreparedStatement preparedStatement = null;
//            PreparedStatement preparedStatement1 = null;
//            PreparedStatement preparedStatement2 = null;



            String title = request.getParameter("title");
            System.out.println("Title--->" + title);
            String genre = request.getParameter("genre");
            String number_page = request.getParameter("number_page");
            String jump = request.getParameter("jump");
            String sortBase = request.getParameter("sort_base");

            String offset = new String("");
            String orderBy = new String("");
            if (sortBase.equals("trascasc")) {
                orderBy = "ORDER BY movies.title ASC, ratings.rating ASC";
            }
            else if (sortBase.equals("trdescdesc")) {
                orderBy = "ORDER BY movies.title DESC, ratings.rating DESC";
            }
            else if (sortBase.equals("rtascasc")) {
                orderBy = "ORDER BY ratings.rating ASC, movies.title ASC";
            }
            else if (sortBase.equals("rtdescdesc")) {
                orderBy = "ORDER BY ratings.rating DESC, movies.title DESC";
            }
            else if (sortBase.equals("trascdesc")) {
                orderBy = "ORDER BY movies.title ASC, ratings.rating DESC";
            }
            else if (sortBase.equals("trdescasc")) {
                orderBy = "ORDER BY movies.title DESC, ratings.rating ASC";
            }
            else if (sortBase.equals("rtascdesc")) {
                orderBy = "ORDER BY ratings.rating ASC, movies.title DESC";
            }
            else if (sortBase.equals("rtdescasc")) {
                orderBy = "ORDER BY ratings.rating DESC, movies.title ASC";
            }




            ArrayList<String> previousBrowseParams = (ArrayList<String>) session.getAttribute("previousBrowseParams");
            // previousBrowseParams: {title, genre, number_of_movies_per_page, current_page_number, sort_requirement}

            ArrayList<String> previousSearchParams = (ArrayList<String>) session.getAttribute("previousSearchParams");
            if (previousSearchParams != null){
                previousSearchParams.set(7, "none");
            }

            if (title != null && genre != null){
                if (previousBrowseParams == null){
                    previousBrowseParams = new ArrayList<>();
                    previousBrowseParams.add(title);
                    previousBrowseParams.add(genre);
                    previousBrowseParams.add(number_page);
                    previousBrowseParams.add(String.valueOf(1));
                    previousBrowseParams.add(orderBy);
                    previousBrowseParams.add("browse");
                    session.setAttribute("previousBrowseParams", previousBrowseParams);
                    System.out.println("previousParams list in if: " + previousBrowseParams.toString());
                }
                else {
                    synchronized (previousBrowseParams){
                        previousBrowseParams.set(5, "browse");
                        System.out.println("previousParams list in else original: " + previousBrowseParams.toString());
                        if (jump.equals("next") || jump.equals("previous")) {
//                            PreparedStatement preparedStatement = null;
//                            Statement tempStatement = conn.createStatement();
                            String tempTitle = previousBrowseParams.get(0);
                            String tempGenre = previousBrowseParams.get(1);
                            String tempQuery = new String("");
                            if (tempTitle.equals("")){
                                tempQuery =  "SELECT count(id)\n" +
                                        "from movies JOIN ratings on movies.id = ratings.movieId JOIN\n" +
                                        "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Genres_List\n" +
                                        "FROM (genres_in_movies as gim JOIN genres on gim.genreId = genres.id)\n" +
                                        "WHERE genres.name = ?\n" +
                                        "GROUP BY movieId) as glist on movies.id = glist.movieId JOIN\n" +
                                        "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Stars_List\n" +
                                        "FROM (stars_in_movies as sim JOIN stars on sim.starId = stars.id)\n" +
                                        "GROUP BY movieId) as slist \n" +
                                        "on movies.id = slist.movieId\n";
                                tempQuery += orderBy;
                                preparedStatement = conn.prepareStatement(tempQuery);
                                preparedStatement.setString(1, tempGenre);
                            }
                            else if (tempGenre.equals("")){
                                if (tempTitle.equals("*")){
                                    tempQuery = "SELECT count(id)\n" +
                                            "from movies JOIN ratings on movies.id = ratings.movieId JOIN\n" +
                                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Genres_List\n" +
                                            "FROM (genres_in_movies as gim JOIN genres on gim.genreId = genres.id)\n" +
                                            "GROUP BY movieId) as glist on movies.id = glist.movieId JOIN\n" +
                                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Stars_List\n" +
                                            "FROM (stars_in_movies as sim JOIN stars on sim.starId = stars.id)\n" +
                                            "GROUP BY movieId) as slist \n" +
                                            "on movies.id = slist.movieId\n" +
                                            "where title not regexp ?\n";
                                    tempQuery += orderBy;
                                    preparedStatement = conn.prepareStatement(tempQuery);
                                    preparedStatement.setString(1, "^[a-zA-Z0-9]");
                                }
                                else {
                                    tempQuery = "SELECT count(id)\n" +
                                            "from movies JOIN ratings on movies.id = ratings.movieId JOIN\n" +
                                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Genres_List\n" +
                                            "FROM (genres_in_movies as gim JOIN genres on gim.genreId = genres.id)\n" +
                                            "GROUP BY movieId) as glist on movies.id = glist.movieId JOIN\n" +
                                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Stars_List\n" +
                                            "FROM (stars_in_movies as sim JOIN stars on sim.starId = stars.id)\n" +
                                            "GROUP BY movieId) as slist \n" +
                                            "on movies.id = slist.movieId\n" +
                                            "where title like ?\n";
                                    tempQuery += orderBy;
                                    preparedStatement = conn.prepareStatement(tempQuery);
                                    preparedStatement.setString(1, tempTitle + "%");
                                }
                            }
                            ResultSet rsTemp = preparedStatement.executeQuery();
                            int num = 0;
                            while (rsTemp.next()) {
                                num = Integer.parseInt(rsTemp.getString("count(id)"));
                                System.out.println("COUNT: " + num);
                            }
//                            preparedStatement.close();
//                            tempStatement.close();
                            rsTemp.close();
                            if (jump.equals("next")){
                                int threshold = num / Integer.parseInt(previousBrowseParams.get(2)) + 1;
                                if (Integer.parseInt(previousBrowseParams.get(3)) < threshold){
                                    offset = String.valueOf(Integer.parseInt(previousBrowseParams.get(3)) * Integer.parseInt(previousBrowseParams.get(2)));
                                    System.out.println("offset: " + offset);
                                    previousBrowseParams.set(3, String.valueOf(Integer.parseInt(previousBrowseParams.get(3)) + 1));
                                }
                                else {
                                    offset = String.valueOf(Integer.parseInt(previousBrowseParams.get(2)) * (threshold - 1));
                                }
                            }
                            else if (jump.equals("previous")){
                                if (Integer.parseInt(previousBrowseParams.get(3)) > 1){
                                    offset = String.valueOf((Integer.parseInt(previousBrowseParams.get(3)) - 2) * Integer.parseInt(previousBrowseParams.get(2)));
                                    System.out.println("offset: " + offset);
                                    previousBrowseParams.set(3, String.valueOf(Integer.parseInt(previousBrowseParams.get(3)) - 1));
                                }
                                else {
                                    offset = "0";
                                }
                            }
                        }
                        else {
                            previousBrowseParams.set(0, title);
                            previousBrowseParams.set(1, genre);
                            previousBrowseParams.set(2, number_page);
                            previousBrowseParams.set(4, orderBy);
                        }
                        System.out.println("previousParams list in else: " + previousBrowseParams.toString());
                    }
                }
            }

            String query = new String("");

            String tempTitle = new String("");
            String tempGenre = new String("");
            String tempNumPage = new String("");
            String tempSort = new String("");
            if (!offset.equals("")){
                tempTitle = previousBrowseParams.get(0);
                tempGenre = previousBrowseParams.get(1);
                tempNumPage = previousBrowseParams.get(2);
                tempSort = previousBrowseParams.get(4);
            }
            else {
                tempTitle = title;
                tempGenre = genre;
                tempNumPage = number_page;
                tempSort = orderBy;
            }
            System.out.println("tempTitle: " + tempTitle);
            System.out.println("tempGenre: " + tempGenre);
            System.out.println("tempNumPage: " + tempNumPage);
            System.out.println("tempSort: " + tempSort);


            if (tempTitle.equals("")){
                query = "SELECT id, title, year, director, Genres_List, Stars_List, rating\n" +
                        "from movies JOIN ratings on movies.id = ratings.movieId JOIN\n" +
                        "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Genres_List\n" +
                        "FROM (genres_in_movies as gim JOIN genres on gim.genreId = genres.id)\n" +
                        "WHERE genres.name = ?\n" +
                        "GROUP BY movieId) as glist on movies.id = glist.movieId JOIN\n" +
                        "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Stars_List\n" +
                        "FROM (stars_in_movies as sim JOIN stars on sim.starId = stars.id)\n" +
                        "GROUP BY movieId) as slist \n" +
                        "on movies.id = slist.movieId\n" + tempSort +
                        "\nlimit ?";
                if (!offset.equals("")){
                    query += " offset " + offset;
                }
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, tempGenre);
//                preparedStatement1.setString(2, tempSort);
                preparedStatement.setInt(2, Integer.parseInt(tempNumPage));
//                preparedStatement1.setString(3, tempNumPage);
            }
            else if (tempGenre.equals("")){
                if (tempTitle.equals("*")){
                    query = "SELECT id, title, year, director, Genres_List, Stars_List, rating\n" +
                            "from movies JOIN ratings on movies.id = ratings.movieId JOIN\n" +
                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Genres_List\n" +
                            "FROM (genres_in_movies as gim JOIN genres on gim.genreId = genres.id)\n" +
                            "GROUP BY movieId) as glist on movies.id = glist.movieId JOIN\n" +
                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Stars_List\n" +
                            "FROM (stars_in_movies as sim JOIN stars on sim.starId = stars.id)\n" +
                            "GROUP BY movieId) as slist \n" +
                            "on movies.id = slist.movieId\n" +
                            "where title not regexp ?\n" + tempSort +
                            "\nlimit ?";
                    if (!offset.equals("")){
                        query += " offset " + offset;
                    }
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, "^[a-zA-Z0-9]");
                    preparedStatement.setInt(2, Integer.parseInt(tempNumPage));
                }
                else {
                    query = "SELECT id, title, year, director, Genres_List, Stars_List, rating\n" +
                            "from movies JOIN ratings on movies.id = ratings.movieId JOIN\n" +
                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Genres_List\n" +
                            "FROM (genres_in_movies as gim JOIN genres on gim.genreId = genres.id)\n" +
                            "GROUP BY movieId) as glist on movies.id = glist.movieId JOIN\n" +
                            "(SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as Stars_List\n" +
                            "FROM (stars_in_movies as sim JOIN stars on sim.starId = stars.id)\n" +
                            "GROUP BY movieId) as slist \n" +
                            "on movies.id = slist.movieId\n" +
                            "where title like ?\n" + tempSort +
                            "\nlimit ?";
                    if (!offset.equals("")){
                        query += " offset " + offset;
                    }
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, tempTitle + "%");
                    preparedStatement.setInt(2, Integer.parseInt(tempNumPage));
                }
            }
            else {
                System.out.println("Get by title/genre------------>Input Error");
            }

//            if (!offset.equals("")){
//                query += " offset " + offset;
//            }
            System.out.println("QUERY from movieservlet: \n" + query);

            System.out.println("Prepared: \n" + preparedStatement.toString());


            // Perform the query
//            ResultSet rs = statement.executeQuery(query);
            ResultSet rs = preparedStatement.executeQuery();

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
                        "where movies.id = ?\n" +
                        "ORDER BY name limit 3";
                preparedStatement = conn.prepareStatement(query1);
                preparedStatement.setString(1, movies_id);

//                ResultSet rs1 = movieToStar.executeQuery(query1);
                ResultSet rs1 = preparedStatement.executeQuery();
                while (rs1.next()){
                    String stars_id = rs1.getString("id");
                    String star_name = rs1.getString("name");
                    movies_stars_id += stars_id + "," + star_name + "\n";
                }


                String[] tempG = movies_genres.split(", ");
                Arrays.sort(tempG);
                String temp_genres = "";
                for (int z = 0; z < tempG.length && z < 3; z++){
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
                for (int z = 0; z < tempS.length && z < 3; z++){
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
            preparedStatement.close();
//            preparedStatement1.close();
//            preparedStatement2.close();
//            statement.close();
//            movieToStar.close();

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


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }
}
