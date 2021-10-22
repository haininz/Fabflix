function setCart(movie_id, movie_title){
    alert("Movie added to cart");
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "checkout?title=" + movie_title + "&id=" + movie_id + "&show=false",
        // success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
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

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let moviesTableBodyElement = jQuery("#movieList_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
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
                + actor_name + '</a >';
            if (j === 2 || j === (actors.length/2)){
                rowHTML += "</th>";
                break;
            }
            else {
                rowHTML += ", ";
            }
        }


        rowHTML += "<th>" + resultData[i]["movies_rating"] + "</th>" // rating;
        rowHTML += '<th>' + '<input type="button" onClick="setCart(\'' + resultData[i]["movies_id"] + '\',\'' + resultData[i]["movies_title"] + '\')" VALUE="Add to Cart">' + '</th>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        // console.log(rowHTML);
        moviesTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

let title = getParameterByName("title");
let genre = getParameterByName("genre");
var number_page = document.getElementById("number_page");

console.log("======Title: " + title);
console.log("======Genre: " + genre);
console.log("======Num page: " + number_page.value)

function handlePageChange(resultData) {
    number_page = document.getElementById("number_page");
    console.log("======New Num page: " + number_page.value)
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "movies?title=" + title + "&genre=" + genre + "&number_page=" + number_page.value + "&jump=", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function handlePreviousButton() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "movies?title=&genre=&number_page=&jump=previous",
        success: (resultData) => handleMovieResult(resultData)
    });
}

function handleNextButton() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "movies?title=&genre=&number_page=&jump=previous",
        success: (resultData) => handleMovieResult(resultData)
    });
}


// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "movies?title=" + title + "&genre=" + genre + "&number_page=" + number_page.value + "&jump=", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

