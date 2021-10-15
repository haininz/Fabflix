/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}



/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let moviesTableBodyElement = jQuery("#search_movieList_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movies_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_genres"] + "</th>" + "<th>" ;
        rowHTML += "-" + "</th>";
        /*
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
         */


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
let movieTitle = getParameterByName('movie_title');
let movieYear = getParameterByName('movie_year');
let movieDirector = getParameterByName('movie_director');
let starName = getParameterByName('star_name');
console.log("---------------------------------------movieTitle:" + movieTitle +"----------------------------------------movieTitle:" + movieYear
+ "----------movieDirector:" + movieDirector + "----------starName:" + starName)

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    // url: "form?movie_title=" + movieTitle + "&movie_year=" + movieYear + "&movie_director=" + movieDirector + "&star_name=" + starName, // Setting request url, which is mapped by StarsServlet in Stars.java
    url: "movies",
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});