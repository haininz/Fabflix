let rowHTML = "";

// let saleId = jQuery("#sale_id");
let orderConfirmationTableBodyElement = jQuery("#order_confirmation_table_body");


let id = window.localStorage.getItem("sale_id");

let items = window.localStorage.getItem("items").split(";");

for (let i = 0; i < items.length; i++) {

    console.log("item: " + items[i]);
    let temp = items[i].split(",");
    console.log("temp: " + temp.toString());
    if (temp[1] !== "-"){
        for (let j = 0; j < parseInt(temp[1]); j++) {
            rowHTML += "<tr>";
            rowHTML += "<th>" + id + "</th>";
            rowHTML += "<th>" + temp[0] + "</th>";
            rowHTML += "<th>" + "1" + "</th>";
            rowHTML += "<th>" + (parseInt(temp[2])/parseInt(temp[1])).toString() + "</th>";
            rowHTML += "</tr>";
            id = (parseInt(id) + 1).toString();
        }
    }
    else {
        rowHTML += "<tr>";
        rowHTML += "<th>" + "-" + "</th>"
        rowHTML += "<th>" + temp[0] + "</th>";
        rowHTML += "<th>" + temp[1] + "</th>";
        rowHTML += "<th>" + temp[2] + "</th>";
        rowHTML += "</tr>";
    }


}

orderConfirmationTableBodyElement.append(rowHTML);