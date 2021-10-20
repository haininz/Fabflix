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
    console.log("Handling cart checkout")
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"]+ "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML += "<th>" + resultData[i]["price"]+ "</th>";
        rowHTML += "</tr>";
        cartTableBodyElement.append(rowHTML);
    }
}

let show = getParameterByName("show");
console.log("Show options from html: " + show);

$.ajax({
    dataType: "json",
    method: "GET",
    url: "checkout?id=&title=&show=" + show,
    success: (resultData) => handleCartResult(resultData)

});