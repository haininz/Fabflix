import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet("/autocompleteSearch")
public class AutoCompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public AutoCompleteServlet() {
        super();
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection conn = dataSource.getConnection();
            Statement statement = conn.createStatement();


            int offsetNum = 10;

            // get the query string from parameter
            String requiredQuery = request.getParameter("query");
            requiredQuery = "+" + requiredQuery;
            requiredQuery = requiredQuery.replaceAll(" ","* +") + "*";

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match

            String search_query = "SELECT id, title from movies WHERE MATCH (title) AGAINST ('" +requiredQuery+ "' in boolean mode) ORDER BY title ASC LIMIT 10";
            System.out.println("search_query: " + search_query);
            ResultSet search_rs = statement.executeQuery(search_query);

            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // return the empty json array if query is null or empty
            if (requiredQuery == null || requiredQuery.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            while(search_rs.next()){
                String movies_title = search_rs.getString("title");
                String movies_id = search_rs.getString("id");
                jsonArray.add(generateJsonObject(movies_id, movies_title));
            }


            response.getWriter().write(jsonArray.toString());
            statement.close();
            conn.close();
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movies_id", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}