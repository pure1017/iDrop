const retrievedData1 = sessionStorage.getItem("bookName_favorite_tmp");
bookName_favorite_tmp = JSON.parse(retrievedData1);
const retrievedData2 = sessionStorage.getItem("author_favorite_tmp");
author_favorite_tmp = JSON.parse(retrievedData2);
const retrievedData3 = sessionStorage.getItem("cover_favorite_tmp");
cover_favorite_tmp = JSON.parse(retrievedData3);
const retrievedData4 = sessionStorage.getItem("itemId_favorite_tmp");
itemId_favorite_tmp = JSON.parse(retrievedData4);
let userId = sessionStorage.getItem("userId");

document.addEventListener('DOMContentLoaded', function () {
    var p_element = document.getElementById("favorite_container");
    var HTML_tmp = '';
    for (i = 0; i < bookName_favorite_tmp.length; i++) {
        HTML_tmp += '<div style="clear: left;">\n' +
            '            <p style="float: left;"><img src=' + cover_favorite_tmp[i] + ' height="230px" width="180px" border="1px" style="margin-right: 20px"></p>\n' +
            '            <p>Book Name:' + bookName_favorite_tmp[i] + '</p>\n' +
            '            <p>Author:' + author_favorite_tmp[i] + '</p>\n' +
            '            <p style="display: none">itemId: ' + itemId_favorite_tmp[i] + '</p>\n' +
            '            <div class="favorite_list_content"><button class="favorite_list_btn" id= id' + itemId_favorite_tmp[i] + ' type="submit"><i class="bx bxs-like active"></i></button></div>\n' +
            '          </div>';
    }
    p_element.innerHTML = HTML_tmp;
// });

// document.addEventListener('DOMContentLoaded', function () {
    for (let i = 0; i < bookName_favorite_tmp.length; i++) {
        (function () {
            var id_tmp = itemId_favorite_tmp[i];
            let req = JSON.stringify({});
            document.getElementById('id' + itemId_favorite_tmp[i]).addEventListener('click', function () {
                const selector = '#id' + id_tmp.toString() + ' i';
                if (document.querySelector(selector.toString()).classList.contains('active')) { // still favorite, click to remove from favorite
                    document.querySelector(selector.toString()).classList.remove('active');
                    document.querySelector(selector.toString()).style.color = '#fff';
                    param = '?userId='+userId+'&itemId='+id_tmp;
                    ajax('DELETE', '/unsetfavorite'+param, req,
                        // successful callback
                    function(res) {
                        console.log(res);
                    },
                    // failed callback
                    function() {
                        showErrorMessage('Cannot unsetfavorite items.');
                    });

                } else { // regret remove from favorite, add back to favorite
                    document.querySelector(selector.toString()).classList.add('active');
                    document.querySelector(selector.toString()).style.color = '#ffc700';
                    param = '?userId='+userId+'&itemId='+id_tmp;
                    ajax('POST', '/setfavorite'+param, req,
                        // successful callback
                    function(res) {
                        console.log(res);
                    },
                    // failed callback
                    function() {
                        showErrorMessage('Cannot setfavorite items.');
                    });
                }
            })
        }());
    }
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