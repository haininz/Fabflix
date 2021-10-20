function setCart(movie_id, movie_title){
    alert("Movie added to cart");
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "cart?movie_title=" + movie_title + "&movie_id=" + movie_id,
        // success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

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

function handleSearchResult(resultData) {

    let moviesTableBodyElement = jQuery("#search_result_table_body");

    console.log("size in the selected movie --->" + resultData.length );

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
                + actor_name + '</a >'; // FIXME
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
        console.log("=========" + resultData[i]["movies_id"]);
        console.log("=========" + resultData[i]["movies_title"]);
        moviesTableBodyElement.append(rowHTML);
    }
}

let movie_title = getParameterByName("movie_title");
let movie_year = getParameterByName("movie_year");
let movie_director = getParameterByName("movie_director");
let star_name = getParameterByName("star_name");

console.log("======title: " + movie_title);
console.log("======year: " + movie_year);
console.log("======director: " + movie_director);
console.log("======name: " + star_name);

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "result?movie_title=" +movie_title +"&movie_year=" +movie_year +"&movie_director="
        +movie_director +"&star_name=" +star_name,
    success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// let movieTitle = getParameterByName('movie_title');
// let movieYear = getParameterByName('movie_year');
// let movieDirector = getParameterByName('movie_director');
// let starName = getParameterByName('star_name');
// console.log("---------------------------------------movieTitle:" + movieTitle +"----------------------------------------movieTitle:" + movieYear
// + "----------movieDirector:" + movieDirector + "----------starName:" + starName)

// Makes the HTTP GET request and registers on success callback function handleStarResult
// jQuery.ajax({
//     dataType: "json", // Setting return data type
//     method: "GET", // Setting request method
//     url: "form?movie_title=" + movieTitle + "&movie_year=" + movieYear + "&movie_director=" + movieDirector + "&star_name=" + starName, // Setting request url, which is mapped by StarsServlet in Stars.java
//     success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
// });

// function submitSearchForm(formSubmitEvent) {
//     console.log("submit search form");
//     /**
//      * When users click the submit button, the browser will not direct
//      * users to the url defined in HTML form. Instead, it will call this
//      * event handler when the event is triggered.
//      */
//     formSubmitEvent.preventDefault();
//
//     $.ajax(
//         "form", {
//             method: "GET",
//             // Serialize the login form to the data sent by POST request
//             data: search_form.serialize(),
//             success: (resultData) => handleSearchResult(resultData)
//         }
//     );
// }
//
// // Bind the submit action of the form to a handler function
// search_form.submit(submitSearchForm);