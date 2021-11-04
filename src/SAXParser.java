import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SAXParser extends DefaultHandler {

    public  ArrayList<Movie> movies;

    private String tempVal;

    private Movie tempMovie;

    //to maintain context

    public SAXParser() {
        movies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
//        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

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
//    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMovie = new Movie();
//            tempEmp = new Employee();
//            tempEmp.setType(attributes.getValue("type"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            movies.add(tempMovie);
        }
        else if (qName.equalsIgnoreCase("fid") || qName.equalsIgnoreCase("filmed")) {
            tempMovie.setId(tempVal);
            //add it to the list
        }
        else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
            //add it to the list
        }
        else if (qName.equalsIgnoreCase("year")) {
            try {
                tempMovie.setYear(Integer.parseInt(tempVal));
            }
            catch (Exception e){
                tempMovie.setYear(0);
            }
            //add it to the list
        }
        else if (qName.equalsIgnoreCase("dirn")) {
            tempMovie.setDirectors(tempVal);
            //add it to the list
        }
        else if (qName.equalsIgnoreCase("cat")) {
            tempMovie.setGenres(tempVal);
            //add it to the list
        }

    }

    public ArrayList<Movie> getMoviesList(){
        return movies;
    }

//    public static void main(String[] args) {
//        SAXParser spe = new SAXParser();
//        spe.runExample();
//    }

}
