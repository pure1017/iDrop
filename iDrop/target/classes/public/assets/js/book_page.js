var userId = sessionStorage.getItem("userId");
var userName = sessionStorage.getItem("userName");

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById("book_cover").src = '';

    var bookName = sessionStorage.getItem("bookName");
    var bookCover = sessionStorage.getItem("bookCover");
    var rate = sessionStorage.getItem("rate");
    var author = sessionStorage.getItem("author");
    var category = sessionStorage.getItem("category");
    var summary = sessionStorage.getItem("summary");
    var itemId = sessionStorage.getItem("itemId");
    var isRated = sessionStorage.getItem("isRated");

    rate = Math.round(rate);
    if (rate === 0) {
        rate = 4;
    }

    for (let i = 1; i<= rate; i++) {
        document.querySelector('.rate_static #star'+i+' ~ label').style.color = '#ffc700';
    }

    document.getElementById("author").innerHTML = "Author: " + author;
    document.getElementById("category").innerHTML = "Category: " + category;
    document.getElementById("summary").innerHTML = "Summary: " + summary;
    document.getElementById("book_name_1").innerHTML = bookName;
    document.getElementById("book_name_2").innerHTML = bookName;
    document.getElementById("book_cover").src = bookCover;


    document.getElementById("favorite-button").addEventListener("click", function() {
        if (userId) {
            let req = JSON.stringify({});
            let param = "";
            if (document.querySelector('#favorite-button i').classList.contains('active')) {
                document.querySelector('#favorite-button i').classList.remove('active');
                param = '?userId=' + userId + '&itemId=' + itemId;
                ajax('DELETE', '/unsetfavorite' + param, req,
                    // successful callback
                    function (res) {
                        console.log(res);
                    },
                    // failed callback
                    function () {
                        showErrorMessage('Cannot unsetfavorite items.');
                    });
            } else {
                document.querySelector('#favorite-button i').classList.add('active');
                param = '?userId=' + userId + '&itemId=' + itemId;
                ajax('POST', '/setfavorite' + param, req,
                    // successful callback
                    function (res) {
                        console.log(res);
                    },
                    // failed callback
                    function () {
                        showErrorMessage('Cannot setfavorite items.');
                    });
            }
        } else {
            document.getElementById("modal_content").innerText = "Please login to favorite!";
            $('#myModal').modal('show');
        }
    });

    const form = {
        input5: document.getElementById("star52"),
        input4: document.getElementById("star42"),
        input3: document.getElementById("star32"),
        input2: document.getElementById("star22"),
        input1: document.getElementById("star12"),
        reader_comment: document.getElementById("reader_comment")
    }

    document.getElementById("reader_submit_btn").addEventListener('click', function (){
        let req = JSON.stringify({});
        let reader_rate = 0;
        if (form.input1.checked) {
            reader_rate = 1;
        } else if (form.input2.checked) {
            reader_rate = 2;
        } else if (form.input3.checked) {
            reader_rate = 3;
        } else if (form.input4.checked) {
            reader_rate = 4;
        } else if (form.input5.checked) {
            reader_rate = 5;
        }
        let reader_comment = form.reader_comment.value

        let param = '?userId='+userId+'&itemId='+itemId+'&rating='+reader_rate+'&comment='+reader_comment+'&time='+moment.utc().format();
        ajax('POST', '/rating'+param, req,
            // successful callback
            function(res) {
                if (res === "book rated") {
                    document.getElementById("modal_content").innerText = "Rating submitted!";
                    $('#myModal').modal('show');
                    document.querySelector('.bg-modal').style.display = 'none';
                } else {
                    document.getElementById("modal_content").innerText = res;
                    $('#myModal').modal('show');
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot rate items.');
            });
    });

    //get user rating and comments
    let req = JSON.stringify({});
    let param = "?itemId="+itemId;
    ajax('GET', '/getrating'+param, req,
        // successful callback
        function(res) {
            let comment_HTML = ''
            const div_comment = document.getElementById("comment_list");
            div_comment.innerHTML = '';
            if (res !== "input error") {
                var items = JSON.parse(res);
                console.log(items);
                for (let item in items) {
                    var rating = items[item][1];
                    var comment = items[item][2];
                    var time = items[item][3];
                    var localtime = moment.utc(time).local().format('YYYY-MM-DD HH:mm:ss');
                    var user = items[item][4];
                    comment_HTML += '<hr style="filter: alpha(opacity=80,finishOpacity=30,style=1)" width="80%" color=lightgray size=3>\n' +
                        '          <div style="clear: left;">\n' +
                        '            <p style="float: left;"><img src="assets/img/user_icon.jpg" height="100px" width="100px" border="1px" style="margin-right: 20px"></p>\n' +
                        '            <p style="font-size: small; color: lightgray">Time: '+ localtime +'</p>\n' +
                        '            <p>User: '+ user +'</p>\n' +
                        '            <p>Rating: '+ rating +'</p>\n' +
                        '            <p>Comment: '+ comment +'</p>\n' +
                        '          </div>'
                }
            }
            div_comment.innerHTML = comment_HTML;
        },
        // failed callback
        function() {
            showErrorMessage('Cannot rate items.');
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

exports.method = ajax;

