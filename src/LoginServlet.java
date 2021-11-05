import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Please verify reCAPTCHA first!");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            String customer_query = "SELECT * FROM customers WHERE email = ?";
            String employee_query = "SELECT * FROM employees WHERE email = ?";

            PreparedStatement preparedStatement_employee = conn.prepareStatement(employee_query);
            PreparedStatement preparedStatement_customer = conn.prepareStatement(customer_query);

            preparedStatement_employee.setString(1, username);
            preparedStatement_customer.setString(1, username);
//            preparedStatement.setString(2, password);

//            Statement statement = conn.createStatement();
//            String query = "SELECT * FROM customers WHERE email = " + "\"" + username + "\""
//                    + "and password = " + "\"" + password + "\"";
//            ResultSet rs = statement.executeQuery(query);
            ResultSet rs_employee = preparedStatement_employee.executeQuery();
            ResultSet rs_customer = preparedStatement_customer.executeQuery();

            boolean success = false;
            boolean isExist = false;

            // set this user into the session
            String encryptedPassword = null;
            String login_person = null;

            if(rs_employee.next()){
                // System.out.println("lol ----> ");
                isExist = true;
                encryptedPassword = rs_employee.getString("password");
                login_person = "employee";
            }
            else if(rs_customer.next()){
                // System.out.println("lol <---- ");
                isExist = true;
                encryptedPassword = rs_customer.getString("password");
                login_person = "customer";
            }

            if (isExist) {

                // use the same encryptor to compare the user input password with encrypted password stored in DB
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                System.out.println("password ---> " + password );
                System.out.println("encryptedPassword ---> " + encryptedPassword);
                System.out.println("success ---> " + success);
                System.out.println("username ---> " + username);

                if (success){
                    request.getSession().setAttribute("user", new User(username));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    responseJsonObject.addProperty("login_person", login_person);
                }
                else {
                    System.out.println("no");
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.

                    responseJsonObject.addProperty("message", "Login failed: incorrect user name or password");
                }


            }
            response.getWriter().write(responseJsonObject.toString());


            rs_employee.close();
            preparedStatement_employee.close();

        } catch (Exception e) {
            System.out.println("exception");
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
