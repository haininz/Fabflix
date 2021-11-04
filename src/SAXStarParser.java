import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;

public class SAXStarParser extends DefaultHandler {

    private ArrayList<Movie> movies;

    private String tempVal;

    private String tempid;

    private DataSource dataSource;

    //to maintain context

    public SAXStarParser() {
    }

    public void runExample(ArrayList<Movie> movies) {
        this.movies = movies;
        parseDocument();
        // printData();
        // insertData();
        System.out.println(movies.get(30).toString());
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

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

    private void insertData(){
        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//            Connection dbCon = dataSource.getConnection();
            String loginUser = "mytestuser";
            String loginPasswd = "My6$Password";
            String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection dbCon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            String query = "CALL add_movie(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement = dbCon.prepareStatement(query);
            Movie movie = movies.get(30);

            for (int j = 0; j < movie.getStars().size(); j++) {
                preparedStatement.setString(1, movie.getTitle());
                preparedStatement.setInt(2, movie.getYear());
                preparedStatement.setString(3, movie.getDirectors().get(0));
                preparedStatement.setString(4, movie.getStars().get(j));
                if (movie.getGenres().get(0).equalsIgnoreCase("Dram")) {
                    preparedStatement.setString(5, "Drama");
                }
                preparedStatement.executeUpdate();
            }
//            for (int i = 0; i < movies.size(); i++) {
////                preparedStatement.setString(1, movies.get(i).getTitle());
//                if (movies.get(i).getYear() != 0) {
////                    preparedStatement.setInt(2, movies.get(i).getYear());
////                    preparedStatement.setString(3, movies.get(i).getDirectors().get(0));
//                    for (int j = 0; j < movies.get(i).getStars().size(); j++) {
//                        preparedStatement.setString(1, movies.get(i).getTitle());
//                        preparedStatement.setInt(2, movies.get(i).getYear());
//                        preparedStatement.setString(3, movies.get(i).getDirectors().get(0));
//                        preparedStatement.setString(4, movies.get(i).getStars().get(i));
//                        if (movies.get(i).getGenres().get(0).equalsIgnoreCase("Dram")) {
//                            preparedStatement.setString(5, "Drama");
//                        }
//                        preparedStatement.executeUpdate();
//                    }
////                    if (movies.get(i).getGenres().get(0).equalsIgnoreCase("Dram")) {
////                        preparedStatement.setString(5, "Drama");
////                    }
//                }
////                preparedStatement.executeUpdate();
//            }

            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
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
        if (qName.equalsIgnoreCase("m")) {
//            movies.add(tempMovie);
        }
        else if (qName.equalsIgnoreCase("f")) {
            tempid = tempVal;
            //add it to the list
        }
        else if (qName.equalsIgnoreCase("a")) {
            for (int i = 0; i < movies.size(); i++) {
                if (movies.get(i).getId().equals(tempid)) {
                    movies.get(i).setStars(tempVal);
                    break;
                }
            }
        }

    }


    public static void main(String[] args) {
        SAXParser spe = new SAXParser();
        spe.runExample();
        ArrayList<Movie> movieList = spe.getMoviesList();
        SAXStarParser saxStarParser = new SAXStarParser();
        saxStarParser.runExample(movieList);
    }

}
