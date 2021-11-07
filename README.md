# cs122b-fall21-team-25

1. Project 3 Demo Url: https://youtu.be/tFKI3vBU_sk
   <br><br>

2. How to deploy application on Tomcat
    - install maven
    - create a new directory and git clone
    - mvn clean and mvn package
    - copy war file under tomcat web apps file
    - run war file on tomcat manager page
      <br><br>

3. Prepared Statement Usage:
   <br>
   AddStarServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/AddStarServlet.java
   <br>
   DashboardServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/DashboardServlet.java
   <br>
   LoginServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/LoginServlet.java
   <br>
   MovieServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/MovieServlet.java
   <br>
   PlaceOrderServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/PlaceOrderServlet.java
   <br>
   SAXDobParser.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/SAXDobParser.java
   <br>
   SearchResultServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/SearchResultServlet.java
   <br>
   SingleMovieServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/SingleMovieServlet.java
   <br>
   SingleStarServlet.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/SingleStarServlet.java
   <br>
   UpdateSecurePassword.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/UpdateSecurePassword.java
   <br>
   UpdateSecurePasswordCustomer.java: https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/UpdateSecurePasswordCustomer.java
<br>
   
4. Parsing time optimization strategies:
   <br>
    - Used stored procedures
    - Used addBatch() and executeBatch() for PreparedStatement
    - Used HashMaps for checking duplicates
    - Wrote insert statements into files and used load data to speed up insertion
    
5. Inconsistency Report:
   <br>
   We wrote each piece of report information into the file "inconsistency_report.md" on AWS; We also showed the content of it at the end of the demo

6. Each member's contribution:
   <br>

    - Haoxin Lin:
        - Implemented HTTPS
        - Implemented Password Encryption
        - Implemented Employee Dashboard
    <br><br>

    - Haining Zhou:
        - Implemented reCAPTCHA
        - Implemented Employee Dashboard
        - Implemented XML Parsing