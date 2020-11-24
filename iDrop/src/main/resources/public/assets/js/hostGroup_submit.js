document.addEventListener('DOMContentLoaded', function () {
    const form = {
        bookName: document.getElementById("book_name"),
        groupName: document.getElementById("group_name"),
        beginDate: document.getElementById("begin_date"),
        groupSize: document.getElementById("group_size"),
        groupDescription: document.getElementById("group_description"),
        submit: document.getElementById("host_submit"),
        messages: document.getElementById("form-messages")
    };

    var userId = sessionStorage.getItem("userId");

    form.submit.addEventListener('click', function () {
        if (userId !== null) {
            let req = JSON.stringify({});
            let param = '?userId=' + userId + '&bookName=' + form.bookName.value + '&groupName=' + form.groupName.value +
                '&beginDate=' + form.beginDate.value + '&groupSize=' + form.groupSize.value + '&groupDescription=' +
                form.groupDescription.value;

            ajax('POST',
                '/hostGroup' + param,
                req,
                // successful callback
                function (res) {
                    // var items = JSON.parse(res);
                    if (res === "group created") {
                        location.href = 'success.html';
                    } else {
                        document.getElementById("modal_content").innerText = res;
                        $('#myModal').modal('show');
                    }
                },
                // failed callback
                function () {
                    showErrorMessage('Cannot submit items.');
                });
        } else {
            document.getElementById("modal_content").innerText = "Please login to host a group!";
            $('#myModal').modal('show');
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