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
    console.log(bookName_favorite_tmp);
    console.log(bookName_favorite_tmp.length);
    for (i = 0; i < bookName_favorite_tmp.length; i++) {
        HTML_tmp += '<div style="clear: left;">\n' +
            '            <p style="float: left;"><img src='+ cover_favorite_tmp[i] +' height="230px" width="180px" border="1px" style="margin-right: 20px"></p>\n' +
            '            <p>Book Name:'+ bookName_favorite_tmp[i] +'</p>\n' +
            '            <p>Author:'+ author_favorite_tmp[i] +'</p>\n' +
            '            <p style="display: none">itemId: '+ itemId_favorite_tmp[i] +'</p>\n' +
            '            <div class="favorite_list_content"><button class="favorite_list_btn" id='+ itemId_favorite_tmp[i] +' type="submit"><i class="bx bxs-like active"></i></button></div>\n' +
            '          </div>';
        document.getElementById(itemId_favorite_tmp[i]).addEventListener('click', function () {
           if (document.querySelector('#'+ itemId_favorite_tmp[i] +' i').classList.contains('active')) {
               document.querySelector('#favorite-button i').classList.remove('active');
               document.querySelector('#favorite-button i').color = '#fff';
           } else {
               document.querySelector('#favorite-button i').classList.add('active');
               document.querySelector('#favorite-button i').color = '#ffc700';
           }
        });
    }
    p_element.innerHTML = HTML_tmp;
});