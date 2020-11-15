document.addEventListener('DOMContentLoaded', function () {
    var bookName = sessionStorage.getItem("bookName");
    var rate = sessionStorage.getItem("rate");
    var author = sessionStorage.getItem("author");
    var category = sessionStorage.getItem("category");
    var summary = sessionStorage.getItem("summary");
    var favorite_btn = document.getElementById("favorite-button");

    rate = Math.round(rate);

    for (var i = 1; i<= rate; i++) {
        document.querySelector('.rate_static #star'+i+' ~ label').style.color = '#ffc700';
    }
    document.querySelector('.rate_static #star'+3+' ~ label').style.color = '#ffc700';
    document.getElementById("author").innerHTML = "Author: " + author;
    document.getElementById("category").innerHTML = "Category: " + category;
    document.getElementById("summary").innerHTML = "Summary: " + summary;

    document.getElementById("favorite-button").addEventListener("click", function() {
        let req = JSON.stringify({});
        if (document.querySelector('#favorite-button i').classList.contains('active')) {
            document.querySelector('#favorite-button i').classList.remove('active');
            ajax('POST', '/like?bookName='+bookName, req,
                // successful callback
            function(res) {
            },
            // failed callback
            function() {
                showErrorMessage('Cannot like items.');
            });
        } else {
            document.querySelector('#favorite-button i').classList.add('active');
            ajax('POST', '/cancel_like?bookName='+bookName, req,
                // successful callback
            function(res) {
            },
            // failed callback
            function() {
                showErrorMessage('Cannot cancel_like items.');
            });
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

        xhr.open(method, url, true);

        xhr.onload = function() {
        	if (xhr.status === 200) {
        		callback(xhr.responseText);
        	} else {
        		errorHandler();
        	}
        };

        xhr.onerror = function() {
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
    }
});