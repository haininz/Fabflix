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
import java.util.Map;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "CheckOutServlet", urlPatterns = "/checkout")
public class CheckOutServlet extends HttpServlet {
    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public boolean movieExists(ArrayList<ArrayList<String>> previous, String id){
        for (ArrayList<String> movie : previous){
            if (movie.get(0).equals(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession();

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String id = request.getParameter("id");
            String title = request.getParameter("title");
            String show = request.getParameter("show");

            boolean modify = true;
            if (show.equals("true")){
                modify = false;
            }

            ArrayList<ArrayList<String>> previousItems = (ArrayList<ArrayList<String>>) session.getAttribute("previousItems");
            ArrayList<String> temp = new ArrayList<>();
            if (previousItems == null) {
                previousItems = new ArrayList<>();
                if (modify){
                    temp.add(id);
                    temp.add(title);
                    temp.add(String.valueOf(1));
                    temp.add(String.valueOf(15));
                    previousItems.add(temp);
                }
                session.setAttribute("previousItems", previousItems);
            }
            else {
                synchronized (previousItems) {
                    if (modify){
                        if (!movieExists(previousItems, id)){
                            temp.add(id);
                            temp.add(title);
                            temp.add(String.valueOf(1));
                            temp.add(String.valueOf(15));
                            previousItems.add(temp);
                        }
                        else {
                            for (ArrayList<String> movie : previousItems){
                                if (movie.get(0).equals(id)){
                                    int quantity = Integer.parseInt(movie.get(2)) + 1;
                                    movie.set(2, String.valueOf(quantity));
                                    movie.set(3, String.valueOf(quantity * 15));
                                }
                            }
                        }
                    }
                }
            }


            System.out.println("previous items: " + previousItems.toString());

            if (show.equals("true")){
                JsonObject responseJsonObject = new JsonObject();

                JsonArray previousItemsJsonArray = new JsonArray();
//            previousItems.forEach(previousItemsJsonArray::add);
                int totalPrice = 0;
                for (ArrayList<String> movie : previousItems){
                    responseJsonObject.addProperty("id", movie.get(0));
                    responseJsonObject.addProperty("title", movie.get(1));
                    responseJsonObject.addProperty("quantity", movie.get(2));
                    responseJsonObject.addProperty("price", movie.get(3));
                    previousItemsJsonArray.add(responseJsonObject);
                    responseJsonObject = new JsonObject();
                    totalPrice += Integer.parseInt(movie.get(3));
                }
                responseJsonObject.addProperty("title", "Total");
                responseJsonObject.addProperty("quantity", "-");
                responseJsonObject.addProperty("price", String.valueOf(totalPrice));
                previousItemsJsonArray.add(responseJsonObject);
                System.out.println("Json array: " + previousItemsJsonArray.toString());
                out.write(previousItemsJsonArray.toString());
            }


            response.setStatus(200);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}