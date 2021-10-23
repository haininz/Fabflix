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

function handlePaymentResult(resultData) {

    console.log("resultData : " + resultData);
    let resultJson = JSON.parse(resultData);
    console.log("resultDataJson[\"findPerson\"] : " + resultJson["findPerson"]);


    if (resultJson["findPerson"] === "success"){
        window.localStorage.setItem("sale_id", resultJson["sale_id"]);
        // console.log(window.localStorage.getItem("sale_id"));
        window.location.replace("result.html");
    }
    else{
        alert("Wrong information! Please enter again!");
    }

}


function submitPaymentForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "placeorder", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: placeorder_form.serialize(),
            success: handlePaymentResult,
            error: function (data) {
                alert("Wrong information! Please enter again!");
                console.log("Items: " + window.localStorage.getItem("items"));
            }
        }
    );
}
let placeorder_form = $("#placeorder_form");
placeorder_form.submit(submitPaymentForm);

let first_name = getParameterByName("first_name");
let last_name = getParameterByName("last_name");
let card_number = getParameterByName("card_number");
let exp_data = getParameterByName("exp_data");


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "placeorder",
    success: (resultData) => {} // Setting callback function to handle data returned successfully by the SingleStarServlet
});