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
//    private void printData() {
//
//        System.out.println("No of Movies '" + movies.size() + "'.");
//
//        Iterator<Movie> it = movies.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
//
//    }


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
                    movies.get(i).setStars(new Star(tempVal, ""));
                    break;
                }
            }
        }

    }

    public ArrayList<Movie> getMovieList() {
        return this.movies;
    }
}
