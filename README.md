# cs122b-fall21-team-25

1. Project 2 Demo Url: https://youtu.be/rUBsMfOPdy0
   <br><br>
   
2. How to deploy application on Tomcat
    - install maven 
    - create a new directory and git clone
    - mvn clean and mvn package
    - copy war file under tomcat web apps file
    - run war file on tomcat manager page
      <br><br>
      
3. Substring matching design:
   <br>
   So basically, we use % to match an arbitrary number of characters for MySQL in our substring match function. 
   For searching the movie title, director, and star name, we use two % to include the substring that users input in 
   our search query.  It will search the data to find which contains the substring part and return. But for the 
   searching the year, we don't use this method because it must be a specific number.


4. Each member's contribution:
      <br><br>

   - Haoxin Lin:
     - Implemented login page
     - Implemented main page
     - Implemented shopping cart page
     - Implemented payment page
     - Implemented place order action
     <br><br>

   - Haining Zhou:
     - Implemented searching
     - Implemented browsing
     - Implemented movie list page
     - Implemented single pages
     - Implemented jump functionality