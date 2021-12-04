- # General
    - #### Team#: 25

    - #### Names: Haining Zhou; Haoxin Lin

    - #### Project 5 Video Demo Link: https://youtu.be/M5OtdeYzMFk

    - #### Instruction of deployment:
        - Setup AWS service and Google cloud computing services with Tomcat and Apache2
        - Install JAVA 11
        - Install Mysql 8.0 and inserted the original database and xml data
        - Build the replication relationship between master and slave instance on Mysql
        - Maven package the code and upload the .war file into master and slave instance
        - Setup the port between servicers such as 22, 80, 3306, 8080 and 8443
        - Download the Jmeter and make the configuration based on the url
        - Use python command to run the codebase of Time measurement

    - #### Collaborations and Work Distribution:
      Haoxin Lin
      <br>
        - Implement MySQL master-Slave Replication
        - GCP environment Setup
        - Built Load Banlancer
        - Collect the performance results
        - Optimization  
          <br>
          Haining Zhou
        - Implement Connection Pooling
        - Developed the Tomcat service
        - Built Apache JMeter
        - Design the codebase of Time measurement
        - Optimization


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/AddStarServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/AutoCompleteServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/CheckOutServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/DashboardServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/FormServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/IndexServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/LoginServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/MovieServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/PlaceOrderServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/SearchResultServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/SingleMovieServlet.java
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/src/SingleStarServlet.java
      
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
      Connection Pooling is used to increase the performance of Fabflix via reusing connections. In order to add the 
      functionality, we modify the context.xml file under /WebContent/META-INF/. When there is a request to get 
      connection, a connection is taken from the "pool" (previous connections) instead of being newly generated; when
      the connection is closed, it is returned to the "pool" rather than being "killed".
    
    - #### Explain how Connection Pooling works with two backend SQL.
      I assume the two backend SQL refers to the Master instance and the Slave instance. We enable Connection Pooling
      on both the Master instance and the Slave instance. When there is a connection request in either of the instances,
      it will behave like what is stated in the above question.
  
- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
      https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-25/blob/main/WebContent/META-INF/context.xml
  
    - #### How read/write requests were routed to Master/Slave SQL?
      A reading request can be sent to either the Master instance or the Slave instance, while a writing request should
      be sent to only the Master instance because the things being written will be automatically sync to the Slave
      instance. To do this, we set up 2 resources in the context.xml file called jdbc/moviedb (localhost) and 
      jdbc/master (master instance ip) separately. For the servlets that have the write functionality (AddStarServlet 
      and DashboardServlet), we use the jdbc/master; for all of the other servlets with read functionality, we use jdbc/moviedb.
        

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
      log_processing.py [file_name]

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 320                        | 200.72845609732622                  | 200.52109728877005        | Since there is only 1 thread, the time is relatively low compared with 10 threads. Ts and Tj do not differ much because the main tasks in doGet are all related to JDBC            |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 1583                       | 1506.4742903073661                  | 1506.2769840885           | With the number of threads increase to 10, the time significantly increases. Ts and Tj again do not differ much because the main tasks in doGet are all related to JDBC            |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | 2113                       | 2108.566413405948                   | 2078.058532541264         | A big reason why the number could be very big is we switch from t2 micro to t3 small after this. Ts and Tj again do not differ much because the main tasks in doGet are all related to JDBC           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 1619                       | 1693.411808524398                   | 1514.8124157983084        | Without connection pooling, each time a connection is newly created instead of picked from the pool. Therefore, it generally takes longer time than case 2           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 313                        | 230.622125                          | 224.8265865               | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 1056                       |                           |                | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 1569                       | 1630.3162123865031                  | 1438.0681238006134        | With the number of threads increase to 10, the time significantly increases. With load balancingTs and Tj again do not differ much because the main tasks in doGet are all related to JDBC           |
