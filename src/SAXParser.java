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

    ArrayList<ArrayList<String>> titles;

    private String tempVal;

    private ArrayList<String> tempList;

    //to maintain context

    public SAXParser() {
        titles = new ArrayList<ArrayList<String>>();
    }

    public void runExample() {
        parseDocument();
        printData();
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
    private void printData() {

        System.out.println("No of Movies '" + titles.size() + "'.");

        Iterator<ArrayList<String>> it = titles.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempList = new ArrayList<>();
//            tempEmp = new Employee();
//            tempEmp.setType(attributes.getValue("type"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("t")) {
            tempList.add(tempVal);
            //add it to the list
        }
        else if (qName.equalsIgnoreCase("year")) {
            tempList.add(tempVal);
            //add it to the list
        }
        else if (qName.equalsIgnoreCase("dirn")) {
            tempList.add(tempVal);

            //add it to the list
        }
        else if (qName.equalsIgnoreCase("cat")) {
            tempList.add(tempVal);
            titles.add(tempList);
            //add it to the list
        }

    }

    public static void main(String[] args) {
        SAXParser spe = new SAXParser();
        spe.runExample();
    }

}
