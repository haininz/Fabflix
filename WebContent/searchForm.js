function addToCart(movie_id, movie_title){
    alert("Movie added to cart");
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "checkout?title=" + movie_title + "&id=" + movie_id + "&show=false",
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

    $("#search_result_table_body").empty();
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
            if (j === 2 || actors.length === 2){
                rowHTML += "</th>";
                break;
            }
            else {
                rowHTML += ", ";
            }
        }
        rowHTML += "<th>" + resultData[i]["movies_rating"] + "</th>" // rating;
        rowHTML += '<th>' + '<input type="button" onClick="addToCart(\'' + resultData[i]["movies_id"] + '\',\'' + resultData[i]["movies_title"] + '\')" VALUE="Add to Cart">' + '</th>';
        rowHTML += "</tr>";


        moviesTableBodyElement.append(rowHTML);
    }
}

let movie_title = getParameterByName("movie_title");
let movie_year = getParameterByName("movie_year");
let movie_director = getParameterByName("movie_director");
let star_name = getParameterByName("star_name");
let number_page = document.getElementById("number_page");
let sort_base = document.getElementById("sort_base");

console.log("======title: " + movie_title);
console.log("======year: " + movie_year);
console.log("======director: " + movie_director);
console.log("======name: " + star_name);

function handleSortChange(resultData) {
    sort_base = document.getElementById("sort_base");
    console.log("Sort base: " + sort_base.value);
    if (sort_base.value === "trascasc") {
        console.log("in trascasc");
        document.getElementById("sort_base").selectedIndex = 0;
    }
    else if (sort_base.value === "trdescdesc") {
        console.log("in trdescdesc");
        document.getElementById("sort_base").selectedIndex = 1;
    }
    else if (sort_base.value === "rtascasc") {
        console.log("in rtascasc");
        document.getElementById("sort_base").selectedIndex = 2;
    }
    else if (sort_base.value === "rtdescdesc"){
        console.log("in rtdescdesc");
        document.getElementById("sort_base").selectedIndex = 3;
    }
    else if (sort_base.value === "trascdesc") {
        console.log("in trascdesc");
        document.getElementById("sort_base").selectedIndex = 4;
    }
    else if (sort_base.value === "trdescasc") {
        console.log("in trdescasc");
        document.getElementById("sort_base").selectedIndex = 5;
    }
    else if (sort_base.value === "rtascdesc") {
        console.log("in rtascdesc");
        document.getElementById("sort_base").selectedIndex = 6;
    }
    else {
        console.log("in rtdescasc");
        document.getElementById("sort_base").selectedIndex = 7;
    }
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "result?movie_title=" + movie_title + "&movie_year=" + movie_year + "&movie_director="
            + movie_director + "&star_name=" + star_name + "&jump=&number_page=" + number_page.value + "&sort_base=" + sort_base.value, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function handlePageChange(resultData) {
    number_page = document.getElementById("number_page");
    if (number_page.value === "10") {
        console.log("in 10");
        document.getElementById("number_page").selectedIndex = 0;
    }
    else if (number_page.value === "25") {
        console.log("in 25");
        document.getElementById("number_page").selectedIndex = 1;
    }
    else if (number_page.value === "50") {
        console.log("in 50");
        document.getElementById("number_page").selectedIndex = 2;
    }
    else {
        console.log("in 100");
        document.getElementById("number_page").selectedIndex = 3;
    }

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "result?movie_title=" + movie_title + "&movie_year=" + movie_year + "&movie_director="
            + movie_director + "&star_name=" + star_name + "&jump=&number_page=" + number_page.value + "&sort_base=" + sort_base.value, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}


function handlePreviousButton() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "result?movie_title=&movie_year=&movie_director=&star_name=&number_page=&jump=previous&sort_base=",
        success: (resultData) => handleSearchResult(resultData)
    });
}

function handleNextButton() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "result?movie_title=&movie_year=&movie_director=&star_name=&number_page=&jump=next&sort_base=",
        success: (resultData) => handleSearchResult(resultData)
    });
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "result?movie_title=" + movie_title + "&movie_year=" + movie_year + "&movie_director="
        + movie_director + "&star_name=" + star_name + "&jump=&number_page=" + number_page.value + "&sort_base=" + sort_base.value,
    success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
