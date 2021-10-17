

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
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

function handleResult(resultData) {

    console.log("handleResult: populating movies info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info");

    let rowHTML = "";
    rowHTML += "<p>Movie Title: " + resultData[0]["movies_title"] + "</p>" +
        "<p>Year: " + resultData[0]["movies_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movies_director"] + "</p>" +
        "<p>Genres: " + resultData[0]["movies_genres"] + "</p>";

    let actors = resultData[0]["movies_stars_id"].split("\n");
    console.log("========" + actors);
    for (let j = 0; j < actors.length - 1; j++) {
        let actor_split = actors[j].split(",");
        let actor_id = actor_split[0];
        let actor_name = actor_split[1];
        rowHTML += '<a href="single-star.html?id=' + actor_id + '">'
            + actor_name + '</a >'; // FIXME
        if (j === actors.length - 2){
            rowHTML += "</th>";
        }
        else {
            rowHTML += ", ";
        }
    }

    rowHTML += "<p>Rating: " + resultData[0]["movies_rating"] + "</p>";


    starInfoElement.append(rowHTML);

    console.log("handleResult: populating single movie table from resultData");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
