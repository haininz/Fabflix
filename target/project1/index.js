/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let moviesTableBodyElement = jQuery("#movieList_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]["movies_id"] + '">'
            + resultData[i]["movies_title"] + '</a >' + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_genres"] + "</th>";  // genres
        rowHTML += "<th>" + resultData[i]["movies_stars"] + "</th>"; // stars


        rowHTML += "<th>" + resultData[i]["movies_rating"] + "</th>" // rating;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        console.log(rowHTML);
        moviesTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});