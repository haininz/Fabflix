import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class SAXDobParser extends DefaultHandler {

    private ArrayList<Movie> movies;

    private String tempVal;

    private String tempRoleName;
    private String tempFirstName;
    private String tempLastName;

    private DataSource dataSource;

    //to maintain context

    public SAXDobParser() {
    }

    public void runExample(ArrayList<Movie> movies) {
        this.movies = movies;
        parseDocument();
//         printData();
//        System.out.println(movies.get(29).toString());
        try {
            insertData();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("./xml/actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies '" + movies.size() + "'.");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }

    }


    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
//        if (qName.equalsIgnoreCase("film")) {
//            //create a new instance of employee
//            tempMovie = new Movie();
////            tempEmp = new Employee();
////            tempEmp.setType(attributes.getValue("type"));
//        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("actor")) {
//            movies.add(tempMovie);
        }
        else if (qName.equalsIgnoreCase("stagename")) {
            tempRoleName = tempVal;
            //add it to the list
        }
//        else if (qName.equalsIgnoreCase("familyname")) {
//            tempLastName = tempVal;
//            //add it to the list
//        }
//        else if (qName.equalsIgnoreCase("firstname")) {
//            tempFirstName = tempVal;
//            //add it to the list
//        }
        else if (qName.equalsIgnoreCase("dob")) {
            for (int i = 0; i < movies.size(); i++) {
                for (int j = 0; j < movies.get(i).getStars().size(); j++) {
                    if (movies.get(i).getStars().get(j).getName().equalsIgnoreCase(tempRoleName)) {
                        movies.get(i).getStars().get(j).setDob(tempVal);
//                        movies.get(i).getStars().get(j).setRealName(tempFirstName + " " + tempLastName);
                    }
                }
            }
        }

    }


    private void insertData() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        Connection dbCon = null;
        try {
            dbCon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement = null;
        String query = "CALL parse_xml(?, ?, ?, ?, ?, ?);";

        try {
            dbCon.setAutoCommit(false);
            preparedStatement = dbCon.prepareStatement(query);

            for (int i = 0; i < movies.size(); i++) {
                if (movies.get(i).getYear() != 0 && movies.get(i).getTitle() != null
                        && !movies.get(i).getTitle().equals("") && movies.get(i).getDirectors().size() > 0
                        && movies.get(i).getStars().size() > 0) {
                    for (int j = 0; j < movies.get(i).getStars().size(); j++) {
                        preparedStatement.setString(1, movies.get(i).getTitle());
                        preparedStatement.setInt(2, movies.get(i).getYear());
                        preparedStatement.setString(3, movies.get(i).getDirectors().get(0));
                        preparedStatement.setString(4, movies.get(i).getStars().get(j).getName());
                        if (movies.get(i).getStars().get(j).getName().equals("")) {
                            System.out.printf("Bad data, no insertion: one star has no name (year = %s, genre = %s, " +
                                            "title = %s, directors = %s, stars = %s)", movies.get(i).getYear(),
                                    movies.get(i).getGenres().toString(), movies.get(i).getTitle(),
                                    movies.get(i).getDirectors(), movies.get(i).getStars().toString());
                            System.out.println();
                        }
                        else {
                            if (movies.get(i).getStars().get(j).getDob().equals("")) {
                                preparedStatement.setInt(5, 0);
                            }
                            else {
                                try {
                                    preparedStatement.setInt(5, Integer.parseInt(movies.get(i).getStars().get(j).getDob().replaceAll("\\s", "")));
                                }
                                catch (Exception e) {
                                    System.out.printf("Bad data, no insertion (star birth format is not valid: %s)", movies.get(i).getStars().get(j).getDob());
                                    System.out.println();
                                }
                                if (movies.get(i).getGenres().size() == 0) {
                                    preparedStatement.setString(6, "");
                                }
                                for (int k = 0; k < movies.get(i).getGenres().size(); k++) {
                                    if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Dram")) {
                                        preparedStatement.setString(6, "Drama");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Actn")) {
                                        preparedStatement.setString(6, "Action");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Advt")) {
                                        preparedStatement.setString(6, "Adventure");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Bio")) {
                                        preparedStatement.setString(6, "Biography");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Comd")) {
                                        preparedStatement.setString(6, "Comedy");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Crim")) {
                                        preparedStatement.setString(6, "Crime");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Faml")) {
                                        preparedStatement.setString(6, "Family");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Fant")) {
                                        preparedStatement.setString(6, "Fantasy");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Hist")) {
                                        preparedStatement.setString(6, "History");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Horr")) {
                                        preparedStatement.setString(6, "Horror");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Myst")) {
                                        preparedStatement.setString(6, "Mystery");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Romt")) {
                                        preparedStatement.setString(6, "Romance");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Musc")) {
                                        preparedStatement.setString(6, "Musical");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Scfi")) {
                                        preparedStatement.setString(6, "Sci-Fi");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Docu")) {
                                        preparedStatement.setString(6, "Documentary");
                                    }
                                    else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("West")) {
                                        preparedStatement.setString(6, "Western");
                                    }
                                    else {
                                        preparedStatement.setString(6, movies.get(i).getGenres().get(k));
                                    }
                                    preparedStatement.addBatch();
                                    // System.out.println(preparedStatement.toString());
                                    preparedStatement.executeBatch();
                                    dbCon.commit();
                                }
                            }
                        }
                    }
//                    if (movies.get(i).getGenres().get(0).equalsIgnoreCase("Dram")) {
//                        preparedStatement.setString(5, "Drama");
//                    }
                }
                else {
                    System.out.printf("Bad data, no insertion (year = %s, genre = %s, title = %s, directors = %s, stars = %s)",
                            movies.get(i).getYear(), movies.get(i).getGenres().toString(), movies.get(i).getTitle(),
                            movies.get(i).getDirectors(), movies.get(i).getStars().toString());
                    System.out.println();
                }
//                preparedStatement.executeUpdate();
            }

            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SAXParser spe = new SAXParser();
        spe.runExample();
        ArrayList<Movie> movieList = spe.getMoviesList();
        SAXStarParser saxStarParser = new SAXStarParser();
        saxStarParser.runExample(movieList);
        movieList = saxStarParser.getMovieList();
        SAXDobParser saxDobParser = new SAXDobParser();
        saxDobParser.runExample(movieList);
    }

}
