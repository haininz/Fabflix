/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
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
        rowHTML += "<th>" + resultData[i]["movies_genres"] + "</th>" + "<th>" ;

        let actors = resultData[i]["movies_stars_id"].split("\n");
        for (let j = 0; j < 3; j++) {
            let actor_split = actors[j].split(",");
            let actor_id = actor_split[0];
            let actor_name = actor_split[1];
            rowHTML += '<a href="single-star.html?id=' + actor_id + '">'
                + actor_name + '</a >'; // FIXME
            if (j === 2){
                rowHTML += "</th>";
            }
            else {
                rowHTML += ", ";
            }
        }


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
    url: "movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

