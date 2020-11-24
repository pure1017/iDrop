document.addEventListener('DOMContentLoaded', function () {

    const form = {
        submit: document.getElementById("search-submit"),
        input: document.getElementById("search-input")
    };

    var bookName = "bookName";
    var rate = 3;
    var author = "author";
    var category = "category";
    var summary = "summary";
    var itemId = "1";
    var bookCover = "";
    var isRated = "";
    // sessionStorage.setItem("bookName", bookName);
    // sessionStorage.setItem("rate", rate);
    // sessionStorage.setItem("author", author);
    // sessionStorage.setItem("category", category);
    // sessionStorage.setItem("summary", summary);
    // sessionStorage.setItem("itemId", itemId);
    // sessionStorage.setItem("bookCover", bookCover);

    form.submit.addEventListener('click', function () {
        let req = JSON.stringify({});
        let userId = sessionStorage.getItem("userId");
        console.log("searchBox userId:" + userId);
        if (userId === null) {
            userId = 11111;
        }
        let param = "?bookName="+form.input.value+"&userId="+userId;
        console.log(param);
        ajax('POST',
            '/search'+param,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                items = JSON.parse(items);
                if(res){
                    bookCover = items["map"]["map"]["cover_url"];
                    if (bookCover[bookCover.length-8] === '.' && bookCover[bookCover.length-7] === '0') {
                        bookCover = bookCover.substring(0, bookCover.length-8) + bookCover.substring(bookCover.length-6, bookCover.length);
                    }
                    bookName = items["map"]["map"]["title"];
                    rate = items["map"]["map"]["rating"];
                    author = items["map"]["map"]["author"];
                    category = items["map"]["map"]["subject"]["myArrayList"];
                    summary = items["map"]["map"]["description"];
                    itemId = items["map"]["map"]["item_id"];
                    isRated = items["rerating"];
                    sessionStorage.setItem("bookName", bookName);
                    sessionStorage.setItem("rate", rate);
                    sessionStorage.setItem("author", author);
                    sessionStorage.setItem("category", category);
                    sessionStorage.setItem("summary", summary);
                    sessionStorage.setItem("itemId", itemId);
                    sessionStorage.setItem("bookCover", bookCover);
                    sessionStorage.setItem("isRated", isRated);
                    location.href='book_page.html';
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot submit items.');
            });
    });

    form.input.addEventListener("keyup", function (event) {
        if(event.keyCode === 13) {
            // Cancel the default action, if needed
            event.preventDefault();
            // Trigger the button element with a click
            form.submit.click();
        }
    });
});

/**
 * AJAX helper
 *
 * @param method -
 *            GET|POST|PUT|DELETE
 * @param url -
 *            API end point
 * @param callback -
 *            This the successful callback
 * @param errorHandler -
 *            This is the failed callback
 */
function ajax(method, url, data, callback, errorHandler) {
    var xhr = new XMLHttpRequest();
    var result = "";

    xhr.open(method, url, true);

    xhr.onload = function() {
        result = "sent";
        if (xhr.status === 200) {
            callback(xhr.responseText);
        } else {
            errorHandler();
        }
    };

    xhr.onerror = function() {
        result = "error";
        console.error("The request couldn't be completed.");
        errorHandler();
    };

    if (data === null) {
        xhr.send();
    } else {
        xhr.setRequestHeader("Content-Type",
            "application/json;charset=utf-8");
        xhr.send(data);
    }

    return result;
}
module.exports = ajax;