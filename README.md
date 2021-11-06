# cs122b-fall21-team-25

1. Project 3 Demo Url: https://youtu.be/b2XCOj5Nqlg
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
    - Use stored procedures
    - Use addBatch() and executeBatch() for PreparedStatement
    - Tried HashMap for checking duplicates

<br>
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