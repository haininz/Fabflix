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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "SearchResultServlet", urlPatterns = "/result")
public class SearchResultServlet extends HttpServlet {

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
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        try {
            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();
            Statement movieToStar = dbCon.createStatement();

            String name = request.getParameter("star_name");
            String title = request.getParameter("movie_title");
            String year = request.getParameter("movie_year");
            String director = request.getParameter("movie_director");
            String number_page = request.getParameter("number_page");
            String jump = request.getParameter("jump");
            String sortBase = request.getParameter("sort_base");

            String offset = new String("");
            String orderBy = new String("");
            if (sortBase.equals("trasc")) {
                orderBy = "ORDER BY movies.title ASC, ratings.rating ASC";
            }
            else if (sortBase.equals("trdesc")) {
                orderBy = "ORDER BY movies.title DESC, ratings.rating DESC";
            }
            else if (sortBase.equals("rtasc")) {
                orderBy = "ORDER BY ratings.rating ASC, movies.title ASC";
            }
            else if (sortBase.equals("rtdesc")) {
                orderBy = "ORDER BY ratings.rating DESC, movies.title DESC";
            }

            System.out.println("jump: " + jump);

            ArrayList<String> previousSearchParams = (ArrayList<String>) session.getAttribute("previousSearchParams");
            // previousSearchParams: {name, title, year, director, number_of_movies_per_page, current_page_number, sort_requirement}
            if (title != null && name != null && year != null && director != null){
                if (previousSearchParams == null){
                    previousSearchParams = new ArrayList<>();
                    previousSearchParams.add(name);
                    previousSearchParams.add(title);
                    previousSearchParams.add(year);
                    previousSearchParams.add(director);
                    previousSearchParams.add(number_page);
                    previousSearchParams.add(String.valueOf(1));
                    previousSearchParams.add(orderBy);
                    session.setAttribute("previousSearchParams", previousSearchParams);
                    System.out.println("previousParams list in if: " + previousSearchParams.toString());
                }
                else {
                    synchronized (previousSearchParams){
                        System.out.println("previousParams list in else original: " + previousSearchParams.toString());
                        if (jump.equals("next")){
//                            System.out.println("page number: " + String.valueOf(Integer.parseInt(previousSearchParams.get(3)) + 1));
//                            Integer.parseInt("#pages: " + String.valueOf(Integer.parseInt(number_page)));
                            offset = String.valueOf(Integer.parseInt(previousSearchParams.get(5)) * Integer.parseInt(previousSearchParams.get(4)));
                            System.out.println("offset: " + offset);
                            previousSearchParams.set(5, String.valueOf(Integer.parseInt(previousSearchParams.get(5)) + 1));
                        }
                        else if (jump.equals("previous")){
                            if (Integer.parseInt(previousSearchParams.get(5)) > 1){
                                offset = String.valueOf((Integer.parseInt(previousSearchParams.get(5)) - 2) * Integer.parseInt(previousSearchParams.get(4)));
                                System.out.println("offset: " + offset);
                                previousSearchParams.set(5, String.valueOf(Integer.parseInt(previousSearchParams.get(5)) - 1));
                            }
                            else {
                                offset = "0";
                            }
                        }
                        else {
                            previousSearchParams.set(0, name);
                            previousSearchParams.set(1, title);
                            previousSearchParams.set(2, year);
                            previousSearchParams.set(3, director);
                            previousSearchParams.set(4, number_page);
                            previousSearchParams.set(6, orderBy);
                        }
                        System.out.println("previousParams list in else: " + previousSearchParams.toString());
                    }
                }
            }

            String tempName = new String("");
            String tempTitle = new String("");
            String tempYear = new String("");
            String tempDirector = new String("");
            String tempNumPage = new String("");
            String tempSort = new String("");

            if (!offset.equals("")){
                tempName = previousSearchParams.get(0);
                tempTitle = previousSearchParams.get(1);
                tempYear= previousSearchParams.get(2);
                tempDirector = previousSearchParams.get(3);
                tempNumPage = previousSearchParams.get(4);
                tempSort = previousSearchParams.get(6);
            }
            else {
                tempName = name;
                tempTitle = title;
                tempYear = year;
                tempDirector = director;
                tempNumPage = number_page;
                tempSort = orderBy;
            }

            System.out.println("tempName: " + tempName);
            System.out.println("tempTitle: " + tempTitle);
            System.out.println("tempYear: " + tempYear);
            System.out.println("tempDirector: " + tempDirector);
            System.out.println("tempNumPage: " + tempNumPage);
            System.out.println("tempSort: " + tempSort);

            String whereClause = "where ";
            boolean hasPrevious = false;

            if (!tempName.equals("")){
                hasPrevious = true;
                whereClause += "s.name like " + "\"%" + tempName + "%\" ";
            }
            if (!tempYear.equals("")){
                if (hasPrevious){
                    whereClause += "and ";
                }
                hasPrevious = true;
                whereClause += "m.year = " + tempYear + " ";
            }
            if (!tempTitle.equals("")){
                if (hasPrevious){
                    whereClause += "and ";
                }
                hasPrevious = true;
                whereClause += "m.title like " + "\"%" + tempTitle + "%\" ";
            }
            if (!tempDirector.equals("")){
                if (hasPrevious){
                    whereClause += "and ";
                }
                whereClause += "m.director like " + "\"%" + tempDirector + "%\" ";
            }

            String query = String.format("SELECT DISTINCT id, title, year, director, genres, starList, rating\n" +
                    "from (SELECT m.* from stars s, movies m, stars_in_movies sim\n%s\n" +
                    "and s.id = sim.starId and m.id = sim.movieId) as movies\n" +
                    "JOIN ratings on movies.id = ratings.movieId\n" +
                    "JOIN (SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as genres\n" +
                    "FROM (genres_in_movies JOIN genres on genres_in_movies.genreId = genres.id)\n" +
                    "GROUP BY movieId) as g\n" +
                    "on movies.id = g.movieId\n" +
                    "JOIN (SELECT DISTINCT movieId, GROUP_CONCAT(name SEPARATOR ', ') as starList\n" +
                    "FROM (stars_in_movies JOIN stars on stars_in_movies.starId = stars.id)\n" +
                    "GROUP BY movieId) as s\n" +
                    "on movies.id = s.movieId\n%s\nlimit %s", whereClause, tempSort, tempNumPage);

            if (!offset.equals("")){
                query += " offset " + offset;
            }
            System.out.println("QUERY from movieservlet: " + query);


            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movies_id = rs.getString("id");
                String movies_title = rs.getString("title");
                String movies_year = rs.getString("year");
                String movies_director = rs.getString("director");
                String movies_genres = rs.getString("genres");
                String movies_stars = rs.getString("starList");
                String movies_rating = rs.getString("rating");

                String movies_stars_id = new String("");
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
            // out.println("</table>");


            // Close all structures
            rs.close();
            statement.close();
            movieToStar.close();
            dbCon.close();

            request.getServletContext().log("getting " + jsonArray.size() + " results");
            out.write(jsonArray.toString());
            System.out.println("jsonArray.toString() --->" + jsonArray.toString());
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
