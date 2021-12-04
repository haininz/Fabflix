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

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "DashboardServlet", urlPatterns = "/dashboard")
public class DashboardServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PrintWriter out = response.getWriter();

        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            PreparedStatement preparedStatement = null;

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String movie_title = request.getParameter("movie_title");
            String movie_year = request.getParameter("movie_year");
            String movie_director = request.getParameter("movie_director");
            String star_name = request.getParameter("star_name");
            String movie_genre = request.getParameter("movie_genre");


            System.out.println("insert movie_title: " + movie_title );
            System.out.println("insert movie_year: " + movie_year);
            System.out.println("insert movie_director: " + movie_director);
            System.out.println("insert star_name: " + star_name);
            System.out.println("insert movie_genre: " + movie_genre);

            String checkMovieNum_query = "select count(*) as num from movies";
            preparedStatement = dbCon.prepareStatement(checkMovieNum_query);
            ResultSet checkMovieNum_rs = preparedStatement.executeQuery();
            checkMovieNum_rs.next();
            int checkMovieNumBefore = Integer.parseInt(checkMovieNum_rs.getString("num"));

            String query = "CALL add_movie(?, ?, ?, ?, ?)";
            preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, movie_title);
            preparedStatement.setInt(2, Integer.parseInt(movie_year));
            preparedStatement.setString(3, movie_director);
            preparedStatement.setString(4, star_name);
            preparedStatement.setString(5, movie_genre);
            System.out.println("preparedStatement for insert movies: \n" + preparedStatement);
            preparedStatement.executeUpdate();


            preparedStatement = dbCon.prepareStatement(checkMovieNum_query);
            checkMovieNum_rs = preparedStatement.executeQuery();
            checkMovieNum_rs.next();
            int checkMovieNumAfter = Integer.parseInt(checkMovieNum_rs.getString("num"));


            String checkMovieID_query = "select * from movies where title = ? ";
            preparedStatement = dbCon.prepareStatement(checkMovieID_query);
            preparedStatement.setString(1, movie_title);
            System.out.println("preparedStatement for check movie id: " + preparedStatement.toString());
            ResultSet checkMovieID_rs = preparedStatement.executeQuery();
            checkMovieID_rs.next();
            String checkMovieID = checkMovieID_rs.getString("id");
            System.out.println("checkMovieID: " + checkMovieID);


            String checkStarID_query = "select * from stars_in_movies where movieId = ?";
            preparedStatement = dbCon.prepareStatement(checkStarID_query);
            preparedStatement.setString(1, checkMovieID);
            ResultSet checkStarID_rs = preparedStatement.executeQuery();
            checkStarID_rs.next();
            String checkStarID = checkStarID_rs.getString("starId");


            String checkGenreID_query = "select * from genres_in_movies where movieId = ?";
            preparedStatement = dbCon.prepareStatement(checkGenreID_query);
            preparedStatement.setString(1, checkMovieID);
            ResultSet checkGenreID_rs = preparedStatement.executeQuery();
            checkGenreID_rs.next();
            String checkGenreID = checkGenreID_rs.getString("genreId");

            preparedStatement.close();


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("insert_status", "success");
            if(checkMovieNumBefore == checkMovieNumAfter){
                jsonObject.addProperty("check_insert", "same");
            }
            else{
                jsonObject.addProperty("check_insert", "diff");
            }
            jsonObject.addProperty("insert_MovieID", checkMovieID);
            jsonObject.addProperty("insert_StarID", checkStarID);
            jsonObject.addProperty("insert_GenreID", checkGenreID);

            out.write(jsonObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
            // e.printStackTrace();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject jsonObject = new JsonObject();
        out.write(jsonObject.toString());
        out.close();
    }

}