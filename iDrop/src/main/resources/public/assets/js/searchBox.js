document.addEventListener('DOMContentLoaded', function () {
  const form = {
        bookName: document.getElementById("search-input"),
        submit: document.getElementById("search-submit")
    };

  var bookName = "bookName"
  var rate = 3;
  var author = "author";
  var category = "category";
  var summary = "summary";
  var itemId = "1"
  var bookCover = ""
  sessionStorage.setItem("bookName", bookName);
  sessionStorage.setItem("rate", rate);
  sessionStorage.setItem("author", author);
  sessionStorage.setItem("category", category);
  sessionStorage.setItem("summary", summary);
  sessionStorage.setItem("itemId", itemId);
  sessionStorage.setItem("bookCover", bookCover);

  form.submit.addEventListener('click', function () {
     let req = JSON.stringify({});
     let param = "?bookName="+form.bookName.value;
     ajax('POST',
            '/search'+param,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if(res){
                    bookCover = items["cover_url"];
                    bookName = items["title"];
                    rate = items["rating"];
                    author = items["author"];
                    category = items["subject"];
                    summary = items["description"];
                    itemId = items["item_id"]
                    location.href='book_page.html';
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot submit items.');
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