function handleCartResult(resultData){

}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "cart",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully
});