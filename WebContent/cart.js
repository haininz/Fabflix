function addItem(movie_id, movie_title){
    window.location.reload();
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "checkout?title=" + movie_title + "&id=" + movie_id + "&show=false",
        // success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

function decreaseItem(movie_id, movie_title){
    window.location.reload();
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "checkout?title=" + movie_title + "&id=" + movie_id +  "&show=true"+"&isDecrease=true",
        success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

function deleteItem(movie_id, movie_title){
    window.location.reload();
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "checkout?title=" + movie_title + "&id=" + movie_id + "&show=true" + "&isRemove=true",
        success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
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

function handleCartResult(resultData){
    let cartTableBodyElement = jQuery("#cart_table_body");
    checkEmpty = resultData[0]["title"];
    console.log("Handling cart checkout");
    let items = "";
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"]+ "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML += "<th>" + resultData[i]["price"] + "</th>";
        items += resultData[i]["title"] + "?" + resultData[i]["quantity"] + "?" + resultData[i]["price"] + ";";
        if(i === resultData.length - 1){
            rowHTML += '<th>'+'</th>';
            rowHTML += '<th>'+'</th>';
            rowHTML += '<th>'+'</th>';
        }else{
            rowHTML += '<th>' + '<input type="button" onClick="addItem(\'' + resultData[i]["id"] + '\',\'' + resultData[i]["title"] + '\')" VALUE="Increase">' + '</th>';
            rowHTML += '<th>' + '<input type="button" onClick="decreaseItem(\'' + resultData[i]["id"] + '\',\'' + resultData[i]["title"] + '\')" VALUE="Decrease">' + '</th>';
            rowHTML += '<th>' + '<input type="button" onClick="deleteItem(\'' + resultData[i]["id"] + '\',\'' + resultData[i]["title"] + '\')" VALUE="Remove">' + '</th>';
        }
        rowHTML += "</tr>";
        cartTableBodyElement.append(rowHTML);
    }
    window.localStorage.setItem("items", items);
}

function paymentClick(){
    if(checkEmpty === "Total"){
        alert("Your cart is empty!")
    }
    else {
        window.location.replace("payment.html");
    }
    //console.log("checkEmpty in button:" + checkEmpty )

}

let show = getParameterByName("show");
let movies_id = getParameterByName("movies_id");
let movies_title = getParameterByName("movies_title");
let checkEmpty = null;
console.log("checkEmpty: " + checkEmpty)

console.log("Show options from html: " + show);

$.ajax({
    dataType: "json",
    method: "GET",
    url: "checkout?id=" + movies_title + "&id=" + movies_id + "&show=" + show,
    success: (resultData) => handleCartResult(resultData)

});