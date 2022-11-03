- # General
    - #### Team#: 25

    - #### Members: Haining Zhou; Haoxin Lin
    
    - #### Instruction of deployment:
        - Setup AWS service and Google cloud computing services with Tomcat and Apache2
        - Install JAVA 11
        - Install Mysql 8.0 and inserted the original database and xml data
        - Build the replication relationship between master and slave instance on Mysql
        - Maven-package the code and upload the .war file into master and slave instance
        - Setup the port between servicers such as 22, 80, 3306, 8080 and 8443
        - Download Apache Jmeter and make the configuration based on the url
        - Use python command to run the codebase of Time measurement
        - For test log in, use "a@email.com" as username and "a2" as password
      
- # Overall Structure:
  ![](images/structure.png)
      
- # Stage 1: Setup AWS, MySQL, JDBC, Tomcat, Start Fabflix
   - #### Collaborations and Work Distribution:
     Haoxin Lin:
        - Set up the environment
        - Worked on index page and MovieServlet
        - Worked on single star page and SingleStarServlet
        - Developed the link from single star to movies list
        - Developed the link from single star to single movie
        - Developed the link to go back moves list page
          <br><br>

     Haining Zhou:
        - Created mysql table
        - Worked on index page and MovieServlet
        - Worked on single movie page and SingleMovieServlet
        - Developed the link from movies list to single movie
        - Developed the link from single movie to single stars
        - Developed the link to go back moves list page

   - #### Demo URL: https://youtu.be/YwcTtcIKyZw

- # Stage 2: Developing Fabflix Website
    - #### Substring matching design:
      Basically, we use % to match an arbitrary number of characters for MySQL in our substring match function.
      For searching the movie title, director, and star name, we use two % to include the substring that users input in
      our search query.  It will search the data to find which contains the substring part and return. But for the
      searching the year, we don't use this method because it must be a specific number.
  
    - #### Collaborations and Work Distribution:
      Haoxin Lin:
        - Implemented login page
        - Implemented main page
        - Implemented shopping cart page
        - Implemented payment page
        - Implemented place order action
          <br><br>

      Haining Zhou:
        - Implemented searching
        - Implemented browsing
        - Implemented movie list page
        - Implemented single pages
        - Implemented jump functionality

    - #### Demo URL: https://youtu.be/4LG-mj_MNEU
  
- # Stage 3: reCAPTCHA, HTTPS, PreparedStatement, Stored Procedure, XML Parsing
    - #### Parsing time optimization strategies:
      - Used stored procedures
      - Used addBatch() and executeBatch() for PreparedStatement
      - Used HashMaps for checking duplicates
      - Wrote insert statements into files and used load data to speed up insertion

    - #### Collaborations and Work Distribution:
      Haoxin Lin:
        - Implemented HTTPS
        - Implemented Password Encryption
        - Implemented Employee Dashboard
          <br><br>

      Haining Zhou:
        - Implemented reCAPTCHA
        - Implemented Employee Dashboard
        - Implemented XML Parsing

    - #### Demo URL: https://youtu.be/-yPmK0sfq_s

- # Stage 4: Full-Text Search, Autocomplete, Android Application, FuzzySearch
    - #### Collaborations and Work Distribution:
      Haoxin Lin:
        - Implemented Autocomplete
          <br><br>

      Haining Zhou:
        - Implemented Android

    - #### Demo URL: https://youtu.be/i1zzBd0H-Fo

- # Stage 5: Scaling Fabflix and Performance Tuning
      
    - #### How Connection Pooling is utilized in the Fabflix code.
      Connection Pooling is used to increase the performance of Fabflix via reusing connections. In order to add the 
      functionality, we modify the context.xml file under /WebContent/META-INF/. When there is a request to get 
      connection, a connection is taken from the "pool" (previous connections) instead of being newly generated; when
      the connection is closed, it is returned to the "pool" rather than being "killed".
    
    - #### How Connection Pooling works with two backend SQL.
      We enable Connection Pooling on both the Master instance and the Slave instance. When there is a connection request in either of the instances,
      it will behave like what is stated in the above question.
  
    - #### Master/Slave  
      - #### How read/write requests were routed to Master/Slave SQL?
        A reading request can be sent to either the Master instance or the Slave instance, while a writing request should
        be sent to only the Master instance because the things being written will be automatically sync to the Slave
        instance. To do this, we set up 2 resources in the context.xml file called jdbc/moviedb (localhost) and 
        jdbc/master (master instance ip) separately. For the servlets that have the write functionality (AddStarServlet 
        and DashboardServlet), we use the jdbc/master; for all other servlets with read functionality, we use jdbc/moviedb.

    - #### JMeter TS/TJ Time Logs
      - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
        log_processing.py [file_name]

      - #### JMeter TS/TJ Time Measurement Report
| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 320                        | 200.72845609732622                  | 200.52109728877005        | Since there is only 1 thread, the time is relatively low compared with 10 threads. Ts and Tj do not differ much because the main tasks in doGet are all related to JDBC            |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 1583                       | 1506.4742903073661                  | 1506.2769840885           | With the number of threads increase to 10, the time significantly increases. Ts and Tj again do not differ much because the main tasks in doGet are all related to JDBC            |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | 2113                       | 2108.566413405948                   | 2078.058532541264         | A big reason why the number could be very big is we switch from t2 micro to t3 small after this. Ts and Tj again do not differ much because the main tasks in doGet are all related to JDBC           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 1619                       | 1693.411808524398                   | 1514.8124157983084        | Without connection pooling, each time a connection is newly created instead of picked from the pool. Therefore, it generally takes longer time than case 2           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 313                        | 230.622125                          | 224.8265865               | Since there is only 1 thread, the time is relatively low compared with 10 threads. The average is lower than single instance case 1 because of load balancing.  Ts and Tj do not differ much because the main tasks in doGet are all related to JDBC           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 1561                       | 1483.8507295420366                  | 1483.4765393671019        | With the number of threads increase to 10, the time significantly increases. With load balancing, the time in general is less than single instance case 3. Ts and Tj again do not differ much because the main tasks in doGet are all related to JDBC 
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 1569                       | 1630.3162123865031                  | 1438.0681238006134        | With the number of threads increase to 10, the time significantly increases. With load balancing, the time in general is less than single instance case 4. Ts and Tj again do not differ much because the main tasks in doGet are all related to JDBC           |
    
  - #### Collaborations and Work Distribution:
    Haoxin Lin:
      - Implement MySQL master-Slave Replication
      - GCP environment Setup
      - Built Load Banlancer
      - Collect the performance results
      - Optimization  
        <br>

    Haining Zhou:
      - Implement Connection Pooling
      - Developed the Tomcat service
      - Built Apache JMeter
      - Design the codebase of Time measurement
      - Optimization

  - #### Demo URL: https://youtu.be/iyj1H6eEymc
