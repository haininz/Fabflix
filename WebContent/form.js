function handleMovieSearch(resultData) {
    $.ajax({
        type: "POST",
        dataType: "json",
        url: "/form",
        data: $('#login_form').serialize(),
        success: function (result) {
            console.log(result);
            if (result.resultCode === 200) {
                alert("SUCCESS");
            }
        },
        error : function() {
            alert("Exceptions!");
        }
    });
}