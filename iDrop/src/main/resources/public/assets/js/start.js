const userId = sessionStorage.getItem("userId");

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById("favorite_list").addEventListener('click', function () {
        let req = JSON.stringify({});
        let param = '?userId='+11111;
        ajax('GET',
            '/getfavorite'+param,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                const bookName_favorite_tmp = [];
                const author_favorite_tmp = [];
                const cover_favorite_tmp = [];
                const itemId_favorite_tmp = [];
                for (var item in items["myArrayList"]) {
                    console.log(items["myArrayList"][item]["map"]);
                    bookName_favorite_tmp.push(items["myArrayList"][item]["map"]["title"]);
                    author_favorite_tmp.push(items["myArrayList"][item]["map"]["author"]);
                    cover_favorite_tmp.push(items["myArrayList"][item]["map"]["cover_url"]);
                    itemId_favorite_tmp.push(items["myArrayList"][item]["map"]["item_id"]);
                }
                sessionStorage.setItem("bookName_favorite_tmp", JSON.stringify(bookName_favorite_tmp));
                sessionStorage.setItem("author_favorite_tmp", JSON.stringify(author_favorite_tmp));
                sessionStorage.setItem("cover_favorite_tmp", JSON.stringify(cover_favorite_tmp));
                sessionStorage.setItem("itemId_favorite_tmp", JSON.stringify(itemId_favorite_tmp));
                location.href='favorite_list.html';
            },
            // failed callback
            function() {
                showErrorMessage('Cannot submit items.');
            });
    });

    document.getElementById("refresh_btn").addEventListener('click', function () {
        let req = JSON.stringify({});
        let param = '?userId='+userId;
        ajax('GET',
            '/recommend'+param,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                for (var item in items['myArrayList']) {

                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot submit items.');
            });
    })
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