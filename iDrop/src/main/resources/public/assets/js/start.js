const userId = sessionStorage.getItem("userId");

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById("favorite_list").addEventListener('click', function () {
        let req = JSON.stringify({});
        let param = '?userId='+userId;
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
        ajax('POST',
            '/recommend'+param,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                const bookName_recommend_tmp = [];
                const author_recommend_tmp = [];
                const cover_recommend_tmp = [];
                const desc_recommend_tmp = [];
                for (var item in items['myArrayList']) {
                    bookName_recommend_tmp.push(items["myArrayList"][item]["map"]["title"]);
                    author_recommend_tmp.push(items["myArrayList"][item]["map"]["author"]);
                    cover_recommend_tmp.push(items["myArrayList"][item]["map"]["cover_url"]);
                    desc_recommend_tmp.push(items["myArrayList"][item]["map"]["description"]);
                }
                sessionStorage.setItem("bookName_recommend_tmp", JSON.stringify(bookName_recommend_tmp));
                sessionStorage.setItem("author_recommend_tmp", JSON.stringify(author_recommend_tmp));
                sessionStorage.setItem("cover_recommend_tmp", JSON.stringify(cover_recommend_tmp));
                sessionStorage.setItem("desc_recommend_tmp", JSON.stringify(desc_recommend_tmp));
                console.log(bookName_recommend_tmp);

                const div_recommend_list = document.getElementById("recommend_list_container");
                var recommend_innerHTML = '';
                for (let i = 0; i < bookName_recommend_tmp.length; i++) {
                    // let div_recommend = document.getElementById("recommend3");
                    // console.log(i);
                    // div_recommend.innerHTML = "";
                    // div_recommend.innerHTML = '<img src=' + cover_recommend_tmp[2] + ' class="testimonial-img" alt="">\n' +
                    //     '              <h3>' + bookName_recommend_tmp[2] + '</h3>\n' +
                    //     '              <h4>' + author_recommend_tmp[2] + '</h4>\n' +
                    //     '              <p>\n' +
                    //     '                <i class="bx bxs-quote-alt-left quote-icon-left"></i>\n' + desc_recommend_tmp[2] +
                    //     '                <i class="bx bxs-quote-alt-right quote-icon-right"></i>\n' +
                    //     '              </p>';
                    recommend_innerHTML += '<div class="testimonial-wrap">\n' +
                        '            <div id="recommend'+ i+1 +'" class="testimonial-item">\n' +
                        '              <img src='+ cover_recommend_tmp[i] +' class="testimonial-img" alt="">\n' +
                        '              <h3>'+ bookName_recommend_tmp[i] +'</h3>\n' +
                        '              <h4>'+ author_recommend_tmp[i] +'</h4>\n' +
                        '              <p>\n' +
                        '                <i class="bx bxs-quote-alt-left quote-icon-left"></i>\n' + desc_recommend_tmp[i] +
                        '                <i class="bx bxs-quote-alt-right quote-icon-right"></i>\n' +
                        '              </p>\n' +
                        '            </div>\n' +
                        '          </div>';
                }
                div_recommend_list.innerHTML = recommend_innerHTML;

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