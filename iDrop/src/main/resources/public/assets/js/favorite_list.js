const retrievedData1 = sessionStorage.getItem("bookName_favorite_tmp");
bookName_favorite_tmp = JSON.parse(retrievedData1);
const retrievedData2 = sessionStorage.getItem("author_favorite_tmp");
author_favorite_tmp = JSON.parse(retrievedData2);
const retrievedData3 = sessionStorage.getItem("cover_favorite_tmp");
cover_favorite_tmp = JSON.parse(retrievedData3);
const retrievedData4 = sessionStorage.getItem("itemId_favorite_tmp");
itemId_favorite_tmp = JSON.parse(retrievedData4);

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
    for (i = 0; i < bookName_favorite_tmp.length; i++) {
        document.getElementById('id'+itemId_favorite_tmp[i]).addEventListener('click', function () {
            var selector = '#id'+itemId_favorite_tmp[i] + ' i';
            if (document.querySelector(selector).classList.contains('active')) {
                document.querySelector(selector).classList.remove('active');
                document.querySelector(selector).style.color = '#fff';
            } else {
                document.querySelector(selector).classList.add('active');
                document.querySelector(selector).style.color = '#ffc700';
            }
        });
    }
});