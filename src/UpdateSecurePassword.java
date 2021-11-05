import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.sql.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;



public class UpdateSecurePassword {

    /*
     * 
     * This program updates your existing moviedb customers table to change the
     * plain text passwords to encrypted passwords.
     * 
     * You should only run this program **once**, because this program uses the
     * existing passwords as real passwords, then replace them. If you run it more
     * than once, it will treat the encrypted passwords as real passwords and
     * generate wrong values.
     * 
     */
    public static void main(String[] args) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        // Statement statement = connection.createStatement();
        // change the customers table password column from VARCHAR(20) to VARCHAR(128)
        // String alterQuery = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
        String alterQuery = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
        PreparedStatement ps1 = connection.prepareStatement(alterQuery);

        int alterResult = ps1.executeUpdate(alterQuery);
        System.out.println("altering customers table schema completed, " + alterResult + " rows affected");
        // get the ID and password for each customer
        // String query = "SELECT id, password from customers";
        String query = "SELECT email, password from employees";

        ps1 = connection.prepareStatement(query);

        ResultSet rs = ps1.executeQuery();

        // we use the StrongPasswordEncryptor from jasypt library (Java Simplified Encryption) 
        //  it internally use SHA-256 algorithm and 10,000 iterations to calculate the encrypted password
        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

        ArrayList<String> updateQueryList = new ArrayList<>();

        System.out.println("encrypting password (this might take a while)");
        while (rs.next()) {
            // get the ID and plain text password from current table
            // String id = rs.getString("id");
            String email = rs.getString("email");
            String password = rs.getString("password");
            
            // encrypt the password using StrongPasswordEncryptor
            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            // generate the update query
            // String updateQuery = String.format("UPDATE customers SET password='%s' WHERE id=%s;", encryptedPassword, id);
            String updateQuery = String.format("UPDATE employees SET password='%s' WHERE email='%s';", encryptedPassword, email);
            updateQueryList.add(updateQuery);
        }
        rs.close();

        // execute the update queries to update the password
        System.out.println("updating password");
        int count = 0;
        for (String updateQuery : updateQueryList) {
            ps1 = connection.prepareStatement(updateQuery);
            int updateResult = ps1.executeUpdate();
            count += updateResult;
        }
        System.out.println("updating password completed, " + count + " rows affected");

        ps1.close();
        connection.close();

        System.out.println("finished");

    }

}
