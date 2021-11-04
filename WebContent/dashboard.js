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

// let movie_title = getParameterByName("movie_title");
// let movie_year = getParameterByName("movie_year");
// let movie_director = getParameterByName("movie_director");
// let star_name = getParameterByName("star_name");
// // let star_birth = getParameterByName("star_birth");
// let movie_genre = getParameterByName("movie_genre");
// // let movie_StarName = getParameterByName("movie_StarName");
// let insertType = null;
//
// console.log("======title: " + movie_title);
// console.log("======year: " + movie_year);
// console.log("======director: " + movie_director);
// console.log("======star_name: " + star_name);
// // console.log("======birth: " + star_birth);
// console.log("======genre: " + movie_genre);

let insertStar_form = $("#insertStar_form");
insertStar_form.submit(handleInsertStar);

let insertMovie_form = $("#insertMovie_form");
insertMovie_form.submit(handleInsertMovie);

function handleInsertStar(formSubmitEvent){
    formSubmitEvent.preventDefault();

    $.ajax(
        "addStar", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: insertStar_form.serialize(),
            success: function (data) {
                let dataJson = JSON.parse(data);
                console.log("dataJson[\"star_id\"] : " + dataJson["star_id"]);

                alert("Insert a new star successfully.\nStar ID: " + dataJson["star_id"]);

                window.location.reload();
            },
            error: function (data) {
                alert("Failure to insert a new star");
            }
        }
    );
}


function handleInsertMovie(formSubmitEvent){
    formSubmitEvent.preventDefault();
    $.ajax(
        "dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: insertMovie_form.serialize(),
            success: function (data) {
                let dataJson = JSON.parse(data);
                console.log("dataJson[\"check_insert\"] : " + dataJson["check_insert"]);
                if(dataJson["check_insert"] === "same"){
                    alert("The movie is already exist.");
                }
                else{
                    alert("Insert a movie successfully\nMovie ID: " + dataJson["insert_MovieID"] + ", Star ID: "
                        + dataJson["insert_StarID"] + ", Genre ID: " + dataJson["insert_GenreID"]);
                    window.location.reload();
                }
            },
            error: function (data) {
                alert("Failure to insert a new movie");
            }
        }
    );
}


function showMetadataButton(){
    window.location.replace("showMetadata.html");
}

