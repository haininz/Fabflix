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

function handleInsertResult(resultData) {

    // $("#search_result_table_body").empty();
    // let moviesTableBodyElement = jQuery("#search_result_table_body");
    //
    // console.log("size in the selected movie --->" + resultData.length );
    //
    // for (let i = 0; i < resultData.length; i++) {
    //     let rowHTML = "";
    //     rowHTML += "<tr>";
    //     rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]["movies_id"] + '">'
    //         + resultData[i]["movies_title"] + '</a >' + "</th>";
    //     rowHTML += "<th>" + resultData[i]["movies_year"] + "</th>";
    //     rowHTML += "<th>" + resultData[i]["movies_director"] + "</th>";
    //     rowHTML += "<th>" + resultData[i]["movies_genres"] + "</th>" + "<th>" ;
    //
    //     let actors = resultData[i]["movies_stars_id"].split("\n");
    //
    //     for (let j = 0; j < 3; j++) {
    //         let actor_split = actors[j].split(",");
    //         let actor_id = actor_split[0];
    //         let actor_name = actor_split[1];
    //         rowHTML += '<a href="single-star.html?id=' + actor_id + '">'
    //             + actor_name + '</a >'; // FIXME
    //         if (j === 2 || j === (actors.length/2)){
    //             rowHTML += "</th>";
    //             break;
    //         }
    //         else {
    //             rowHTML += ", ";
    //         }
    //     }
    //     rowHTML += "<th>" + resultData[i]["movies_rating"] + "</th>" // rating;
    //     rowHTML += '<th>' + '<input type="button" onClick="addToCart(\'' + resultData[i]["movies_id"] + '\',\'' + resultData[i]["movies_title"] + '\')" VALUE="Add to Cart">' + '</th>';
    //     rowHTML += "</tr>";
    //
    //
    //     moviesTableBodyElement.append(rowHTML);
    // }
}

let movie_title = getParameterByName("movie_title");
let movie_year = getParameterByName("movie_year");
let movie_director = getParameterByName("movie_director");
let star_name = getParameterByName("star_name");
let star_birth = getParameterByName("star_birth");
let movie_genre = getParameterByName("movie_genre");
let movie_StarName = getParameterByName("movie_StarName");
let insertType = null;

console.log("======title: " + movie_title);
console.log("======year: " + movie_year);
console.log("======director: " + movie_director);
console.log("======name: " + star_name);
console.log("======birth: " + star_birth);
console.log("======genre: " + movie_genre);

// let insertStar_form = $("#insertStar_form");
// insertStar_form.submit(handleInsertStar);
//
// let insertMovie_form = $("#insertMovie_form");
// insertMovie_form.submit(handleInsertMovie);

function handleInsertStar(resultData){
    document.getElementById("insertStar_form").submit();
    insertType = "insertStar";
    console.log("insertType: " + insertType);



    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "dashboard?star_name=" + star_name + "&star_birth=" + star_birth + "&insertType=" + insertType, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleInsertResult(resultData), // Setting callback function to handle data returned successfully by the StarsServlet
        error: function (data) {
            alert("Wrong information! Please enter star name again!");
        }
    });
}


function handleInsertMovie(resultData){
    document.getElementById("insertMovie_form").submit();
    insertType = "insertMovie";
    console.log("insertType: " + insertType);


    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "dashboard?movie_title=" + movie_title + "&movie_year=" + movie_year + "&movie_director="
            + movie_director + "&movie_StarName=" + movie_StarName + "&movie_genre=" + movie_genre + "&insertType=" + insertType,
        success: (resultData) => handleInsertResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}


function showMetadataButton(){
    window.location.replace("showMetadata.html");
}
// jQuery.ajax({
//     dataType: "json",  // Setting return data type
//     method: "GET",// Setting request method
//     url: "result?movie_title=" + movie_title + "&movie_year=" + movie_year + "&movie_director="
//         + movie_director + "&star_name=" + star_name + "&jump=&number_page=" + number_page.value + "&sort_base=" + sort_base.value,
//     success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
// });
