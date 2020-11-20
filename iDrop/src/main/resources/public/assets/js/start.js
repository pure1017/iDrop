const ajax = require("./book_page");
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
                for (var item in items['myArrayList']) {

                }
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