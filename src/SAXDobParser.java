import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.*;

import java.io.IOException;  // Import the IOException class to handle errors


public class SAXDobParser extends DefaultHandler {

    private ArrayList<Movie> movies;

    private String tempVal;

    private String tempRoleName;
    private String tempFirstName;
    private String tempLastName;

    private DataSource dataSource;

    private HashMap<String, Integer> genreInDB_map;   // genres name, id
    private Map<String, Set<Integer>> genreInMovie_map; // id, genre id
    private HashMap<String, Set<String>> movieID_map;       // movie id, movie title, year, director
    private HashMap<String, Set<String>> starBirth_map; // star id, star name, birth
    private HashMap<String, Set<String>> starInMovie_map; // movie id, star ids

    private int max_MovieID;
    private int max_genreID;
    private int max_starID;

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
            sp.parse("./actors63.xml", this);

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


    private void insertData() throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException {
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

        // initials
        genreInDB_map = new HashMap<>();
        genreInMovie_map = new HashMap<>();
        movieID_map = new HashMap<>();
        starBirth_map = new HashMap<>();
        starInMovie_map = new HashMap<>();

        // create text file
        PrintWriter movies_file = new PrintWriter("movies.txt");    // done
        PrintWriter stars_file = new PrintWriter("stars.txt");      // done
        PrintWriter genres_in_movies_file = new PrintWriter("genres_in_movies.txt");    // done   完成
        PrintWriter genres_file = new PrintWriter("genres.txt"); // done  完成
        PrintWriter stars_in_movies_file = new PrintWriter("stars_in_movies.txt");
        PrintWriter rating_file = new PrintWriter("rating.txt");    // done  完成
        PrintWriter cantInsert_file = new PrintWriter("inconsistency_report.md");


        PreparedStatement preparedStatement = null;
        //String query = "CALL parse_xml(?, ?, ?, ?, ?, ?);";
        String find_max_movieID = "select max(substring(id, 3)) as id from movies";
        String find_max_genreID = "SELECT MAX(id) as id FROM genres";
        String find_genre_list = "select * from genres";
        String find_max_starID = "select max(substring(id, 3)) as starID from stars";

        try {
            dbCon.setAutoCommit(false);

            // find max movie id
            preparedStatement = dbCon.prepareStatement(find_max_movieID);
            ResultSet max_movieID_r = preparedStatement.executeQuery();
            max_movieID_r.next();
            max_MovieID = Integer.parseInt(max_movieID_r.getString("id"));

            // find max genre id
            preparedStatement = dbCon.prepareStatement(find_max_genreID);
            ResultSet max_genreID_r = preparedStatement.executeQuery();
            max_genreID_r.next();
            max_genreID = Integer.parseInt(max_genreID_r.getString("id"));

            // make a genre hash map
            preparedStatement = dbCon.prepareStatement(find_genre_list);
            ResultSet rSet = preparedStatement.executeQuery();
            while(rSet.next()){
                int get_genreID = Integer.parseInt(rSet.getString("id"));
                genreInDB_map.put(rSet.getString("name"), get_genreID);
            }

            // find max star id
            preparedStatement = dbCon.prepareStatement(find_max_starID);
            ResultSet max_starID_r = preparedStatement.executeQuery();
            max_starID_r.next();
            max_starID = Integer.parseInt(max_starID_r.getString("starID"));




            for (int i = 0; i < movies.size(); i++) {
                String new_MovieID = "";

                if (movies.get(i).getYear() != 0 && movies.get(i).getTitle() != null
                        && !movies.get(i).getTitle().equals("") && movies.get(i).getDirectors().size() > 0
                        && movies.get(i).getStars().size() > 0) {
                    boolean movieExist = false;
                    for (Map.Entry<String, Set<String>> value : movieID_map.entrySet()) {
                        if (value.getValue().contains(movies.get(i).getTitle()) && value.getValue().contains(String.valueOf(movies.get(i).getYear()))
                                && value.getValue().contains(movies.get(i).getDirectors().get(0))) {
                            movieExist = true;
                            new_MovieID = value.getKey();
                            break;
                        }
                    }
                    if (!movieExist) {
                        max_MovieID++;
                        new_MovieID = String.format("tt%07d", max_MovieID);
                        // insert movie
                        movies_file.printf("%s,%s,%d,%s\n", new_MovieID, movies.get(i).getTitle(),
                                movies.get(i).getYear(), movies.get(i).getDirectors().get(0));
                        rating_file.printf("%s,%d,%d\n", new_MovieID, 0, 0);
                        Set<String> tempMovie = new HashSet<>();
                        tempMovie.add(movies.get(i).getTitle());
                        tempMovie.add(String.valueOf(movies.get(i).getYear()));
                        tempMovie.add(movies.get(i).getDirectors().get(0));
                        movieID_map.put(new_MovieID, tempMovie);
                    }

                    for (int j = 0; j < movies.get(i).getStars().size(); j++) {
                        if (movies.get(i).getStars().get(j).getName().equals("")) {
//                            System.out.printf("Bad data, no insertion: one star has no name (year = %s, genre = %s, " +
//                                            "title = %s, directors = %s, stars = %s)", movies.get(i).getYear(),
//                                    movies.get(i).getGenres().toString(), movies.get(i).getTitle(),
//                                    movies.get(i).getDirectors(), movies.get(i).getStars().toString());
                            cantInsert_file.printf("Bad data, no insertion: one star has no name (year = %s, genre = %s, " +
                                            "title = %s, directors = %s, stars = %s)\n", movies.get(i).getYear(),
                                    movies.get(i).getGenres().toString(), movies.get(i).getTitle(),
                                    movies.get(i).getDirectors(), movies.get(i).getStars().toString());; }
                        else {
                            boolean starExist = false;
                            String new_StarID = "";

                            for (Map.Entry<String, Set<String>> value : starBirth_map.entrySet()) {
                                if (value.getValue().contains(movies.get(i).getStars().get(j).getName())
                                        && value.getValue().contains(movies.get(i).getStars().get(j).getDob())) {
                                    starExist = true;
                                    new_StarID = value.getKey();
                                    break;
                                }
                            }
                            if (!starExist) {
                                // insert star
                                max_starID++;
                                new_StarID = String.format("nm%07d", max_starID);

                                Set<String> tempStar = new HashSet<>();
                                tempStar.add(movies.get(i).getStars().get(j).getName());
                                tempStar.add(movies.get(i).getStars().get(j).getDob());
                                starBirth_map.put(new_StarID, tempStar);

                                if (movies.get(i).getStars().get(j).getDob().equals("")) {
                                    stars_file.printf("%s,%s,%s\n", new_StarID, movies.get(i).getStars().get(j).getName(), "null");
                                } else {
                                    try {
                                        stars_file.printf("%s,%s,%d\n", new_StarID, movies.get(i).getStars().get(j).getName(), Integer.parseInt(movies.get(i).getStars().get(j).getDob()));
                                    } catch (Exception e) {
                                        // System.out.printf("Bad data, no insertion (star birth format is not valid: %s)", movies.get(i).getStars().get(j).getDob());
                                        cantInsert_file.printf("Bad data, no insertion (star birth format is not valid: %s) \n", movies.get(i).getStars().get(j).getDob());
                                    }
                                }
                            }
                            boolean starMovieRelaExist = false;
                            for (Map.Entry<String, Set<String>> value : starInMovie_map.entrySet()) {
                                if (value.getValue().contains(new_StarID)
                                        && value.getKey().equals(new_MovieID)) {
                                    starMovieRelaExist = true;
                                    break;
                                }
                            }
                            if (!starMovieRelaExist) {
                                stars_in_movies_file.printf("%s,%s\n", new_StarID, new_MovieID);
                                boolean keyExist = true;
                                for (Map.Entry<String, Set<String>> value : starInMovie_map.entrySet()) {
                                    if (value.getKey().equals(new_MovieID)) {
                                        Set<String> tempValues = value.getValue();
                                        tempValues.add(new_StarID);
                                        value.setValue(tempValues);
                                        break;
                                    }
                                }
                            }
                        }
                    }



                    String genre_temp = "";
                    boolean isBadData;
                    // System.out.println("======>"+ movies.get(i).getTitle() + " : "+ movies.get(i).getGenres());
                    for (int k = 0; k < movies.get(i).getGenres().size(); k++) {
                        isBadData = false;
                        String[] checkS = movies.get(i).getGenres().get(k).split(" ");
                        if(movies.get(i).getGenres().get(k).replace(" ", "").equals("") || movies.get(i).getGenres().get(k).length() < 2 || checkS.length > 2
                                || movies.get(i).getGenres().get(k).contains("*") || movies.get(i).getGenres().get(k).contains(">") || movies.get(i).getGenres().get(k).contains(".")){
                            // System.out.printf("---->" + movies.get(i).getGenres().get(k));
                            isBadData = true;
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Dram")) {
                            genre_temp="Drama";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Actn") || movies.get(i).getGenres().get(k).equalsIgnoreCase("Act")) {
                            genre_temp="Action";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Advt")) {
                            genre_temp="Adventure";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Bio")) {
                            genre_temp= "Biography";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Comd")) {
                            genre_temp = "Comedy";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Crim")) {
                            genre_temp = "Crime";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Faml")) {
                            genre_temp = "Family";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Fant")) {
                            genre_temp= "Fantasy";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Hist")) {
                            genre_temp = "History";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Horr")) {
                            genre_temp="Horror";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Myst")) {
                            genre_temp= "Mystery";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Romt")) {
                            genre_temp = "Romance";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Musc")) {
                            genre_temp = "Musical";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Scfi")) {
                            genre_temp = "Sci-Fi";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Docu")) {
                            genre_temp = "Documentary";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("West")) {
                            genre_temp = "Western";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("susp") || movies.get(i).getGenres().get(k).equalsIgnoreCase(" Susp")) {
                            genre_temp = "Thriller";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("tv")) {
                            genre_temp = "TV Show";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("biop")) {
                            genre_temp = "Biographical Picture";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Disa")) {
                            genre_temp = "Disaster";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("camp")) {
                            genre_temp = "Camp";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Epic")) {
                            genre_temp = "Epic";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Noir")) {
                            genre_temp = "Dark";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Weird")) {
                            genre_temp = "Weird";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Psych Dram")) {
                            genre_temp = "Psych Drama";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Muscl")) {
                            genre_temp = "Muscl Scheme";
                        }
                        else if (movies.get(i).getGenres().get(k).equalsIgnoreCase("Weird")) {
                            genre_temp = "Weird";
                        }
                        else {
                            genre_temp = movies.get(i).getGenres().get(k);

                        }


                        // check genre if is exist in hashmap, otherwise create a new one
                        if(isBadData == false){
                            if (!genreInDB_map.containsKey(genre_temp)){
                                max_genreID++;
                                genreInDB_map.put(genre_temp, max_genreID);

                                // new genres type, write inside
                                genres_file.printf("%d,%s\n", max_genreID, genre_temp);
                            }
                            // System.out.println("genre_temp --->" + genre_temp);

                            // read genre id according to movie id
                            Set<Integer> temp_Set =  new HashSet<Integer>();
                            if (genreInMovie_map.containsKey(new_MovieID)){
                                temp_Set = genreInMovie_map.get(new_MovieID);
                                temp_Set.add(genreInDB_map.get(genre_temp));
                            }
                            else{
                                temp_Set.add(genreInDB_map.get(genre_temp));
                            }
                            genreInMovie_map.put(new_MovieID, temp_Set);
                        }
                        else{
                            cantInsert_file.printf("Bad data on genres, no insertion (year = %s, genre = %s, title = %s, directors = %s, stars = %s)\n",
                                    movies.get(i).getYear(), movies.get(i).getGenres().toString(), movies.get(i).getTitle(),
                                    movies.get(i).getDirectors(), movies.get(i).getStars().toString());;
                        }

                        preparedStatement.addBatch();
//                        System.out.println(preparedStatement.toString());
                        preparedStatement.executeBatch();
                        dbCon.commit();
                    }


                }
                else {
//                    System.out.printf("Bad data, no insertion (year = %s, genre = %s, title = %s, directors = %s, stars = %s)",
//                            movies.get(i).getYear(), movies.get(i).getGenres().toString(), movies.get(i).getTitle(),
//                            movies.get(i).getDirectors(), movies.get(i).getStars().toString());
//                    System.out.print("\n");
                    // System.out.print("1");
                    cantInsert_file.printf("Bad data, no insertion (year = %s, genre = %s, title = %s, directors = %s, stars = %s)\n",
                            movies.get(i).getYear(), movies.get(i).getGenres().toString(), movies.get(i).getTitle(),
                            movies.get(i).getDirectors(), movies.get(i).getStars().toString());
                }
//                preparedStatement.executeUpdate();
            }


            // write into genres_in_movies_file
            for(String key : genreInMovie_map.keySet()){
                Set<Integer> temp = genreInMovie_map.get(key);
                for(Integer x : temp){
                    genres_in_movies_file.printf("%s,%s\n", x, key);
                    // System.out.println("get key from movie ID" + x + " : " + key);
                }
            }

            // close file
            movies_file.close();
            stars_file.close();
            genres_in_movies_file.close();
            genres_file.close();
            stars_in_movies_file.close();
            rating_file.close();
            cantInsert_file.close();

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
