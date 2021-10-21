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
@WebServlet(name = "PlaceOrderServlet", urlPatterns = "/placeorder")
public class PlaceOrderServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PrintWriter out = response.getWriter();

        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String first_name = request.getParameter("first_name");
            String last_name = request.getParameter("last_name");
            String card_number = request.getParameter("card_number");
            String exp_data = request.getParameter("exp_data");

            System.out.println("expiration data: " + exp_data);

            String query = "SELECT COUNT(*) AS person "
                    + "FROM creditcards AS c "
                    + "WHERE c.id = " + "\"" + card_number + "\"" + "AND c.firstName = " + "\"" + first_name + "\""
                    + " AND c.lastName = " + "\"" + last_name + "\"" + " AND c.expiration = " + "\"" + exp_data + "\"" + ";";

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String num_person = rs.getString("person");


            HttpSession session = request.getSession();
            ArrayList<ArrayList<String>> previousItems = (ArrayList<ArrayList<String>>) session.getAttribute("previousItems");
            System.out.println("-------- Array List ----------"  + previousItems);


            JsonObject jsonObject = new JsonObject();
            if(Integer.parseInt(num_person) == 0){
                System.out.println("no such person -> payment fail");
                jsonObject.addProperty("findPerson","failure");
            }
            else{
                System.out.println("payment success");
                jsonObject.addProperty("findPerson","success");
                previousItems.clear(); // clear all information once purchased
            }


//            System.out.println("searchResult.html?movie_title="+movie_title+"&movie_year="+movie_year
//                    +"&movie_director="+movie_director+"&star_name="+star_name);

            out.write(jsonObject.toString());
            // response.sendRedirect("result.html??first_name=" +first_name +"&last_name=" +last_name +"&card_number="+card_number +"&exp_data=" +exp_data);
            // response.sendRedirect("payment.html");
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject jsonObject = new JsonObject();
        out.write(jsonObject.toString());
        out.close();
    }

}