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
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
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
//            String star_birth = request.getParameter("star_birth");
//            String movie_StarName = request.getParameter("movie_StarName");
            String insert_type = request.getParameter("insertType");
//            String insert_type = null;
//            if(movie_title == null){
//                insert_type = "insertMovie";
//            }else{
//                insert_type = "insertStar";
//            }

            System.out.println("insert movie_title: " + movie_title );
            System.out.println("insert movie_year: " + movie_year);
            System.out.println("insert movie_director: " + movie_director);
            System.out.println("insert star_name: " + star_name);
//            System.out.println("insert movie_StarName: " + movie_StarName);
            System.out.println("insert movie_genre: " + movie_genre);
//            System.out.println("insert star_birth: " + star_birth);

            // insert a star
//            if (insert_type.equals("insertStar")){
//                String dropStar_query= "delete from stars where name = ?";
//                preparedStatement = dbCon.prepareStatement(dropStar_query);
//                preparedStatement.setString(1, star_name);
//                System.out.println("dropStar_query: " + preparedStatement);
//                preparedStatement.executeUpdate();
//
//                String lastStarID_query = "select * from stars ORDER BY id DESC LIMIT 1";
//                preparedStatement = dbCon.prepareStatement(lastStarID_query);
//                ResultSet lastStarID_rs = preparedStatement.executeQuery();
//                lastStarID_rs.next();
//                String lastStarRecord_id = lastStarID_rs.getString("id");
//                // System.out.println("lastStarRecord_id : " + preparedStatement);
//                String nm = lastStarRecord_id.substring(0,2);
//                int starID = Integer.parseInt(lastStarRecord_id.substring(2));
//                System.out.println("starID : " + starID);
//                String newStarID = nm + String.valueOf(starID + 1);
//                System.out.println("newStarID : " + newStarID);
//
//
//
//                String insertStar_query = "INSERT INTO stars VALUES('" + newStarID + "', '" + star_name
//                        + "', " + star_birth + ")";
//                System.out.println("insert star query:" + insertStar_query);
//                preparedStatement = dbCon.prepareStatement(insertStar_query);
//                preparedStatement.executeUpdate();
//            }
             if (insert_type.equals("insertMovie")){
                String query = "CALL add_movie(?, ?, ?, ?, ?)";
                preparedStatement = dbCon.prepareStatement(query);
                preparedStatement.setString(1, movie_title);
                preparedStatement.setInt(2, Integer.parseInt(movie_year));
                preparedStatement.setString(3, movie_director);
                preparedStatement.setString(4, star_name);
                preparedStatement.setString(5, movie_genre);
                System.out.println("preparedStatement for insert movies: \n" + preparedStatement);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("insert_status", "success");


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