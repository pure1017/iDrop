const userId = sessionStorage.getItem("userId");

document.addEventListener('DOMContentLoaded', function () {
    let req = JSON.stringify({});
    let param = '?userId='+userId;
    ajax('GET',
        '/getusergroup'+param,
        req,
        function (res) {
            var items = JSON.parse(res);
            // me as the host
            var host_HTML = ''
            const div_host_container = document.getElementById("host_container");
            div_host_container.innerHTML = '';
            for (let item in items[0]) {
                host_HTML += '<div style="clear: left;">\n' +
                    '              <p style="float: left;"><a href="innerGroup_page.html"><img src='+ items[0][item]["cover_url"] +' height="230px" width="180px" border="1px" style="margin-right: 20px"></a></p>\n' +
                    '              <p>Group Name: '+ items[0][item]["Group Name"] +'</p>\n' +
                    '              <p>Begin Date: '+ items[0][item]["Begin Date"] +'</p>\n' +
                    '              <p>Group Description: '+ items[0][item]["Group Description"] +'</p>\n' +
                    '            </div>';
            }
            div_host_container.innerHTML = host_HTML;

            //me as a member
            var member_HTML = ''
            const div_member_container = document.getElementById("member_container");
            div_member_container.innerHTML = '';
            for (let item in items[1]) {
                member_HTML += '<div style="clear: left;">\n' +
                    '              <p style="float: left;"><a href="innerGroup_page.html"><img src='+ items[1][item]["cover_url"] +' height="230px" width="180px" border="1px" style="margin-right: 20px"></a></p>\n' +
                    '              <p>Group Name: '+ items[1][item]["Group Name"] +'</p>\n' +
                    '              <p>Begin Date: '+ items[1][item]["Begin Date"] +'</p>\n' +
                    '              <p>Group Description: '+ items[1][item]["Group Description"] +'</p>\n' +
                    '            </div>';
            }
            div_member_container.innerHTML = member_HTML;
        },
        // failed callback
        function() {
            showErrorMessage('Cannot submit items.');
        });

    //application
    ajax('GET',
        '/getjoinmessage'+param,
        req,
        function (res) {
            var items = JSON.parse(res);
            var application_HTML = '';
            const div_application_container = document.getElementById("application_container");
            div_application_container.innerHTML = '';
            for (let item in items) {
                var group_name = Object.keys(items[item])[0];
                for (let m in items[item][group_name]) {
                    if (items[item][group_name][m] !== null) {
                        application_HTML += '<div style="clear: left;">\n' +
                            '              <p style="float: left;"><img src="assets/img/application_form.jpg" height="200px" width="180px" border="1px" style="margin-right: 20px"></p>\n' +
                            '              <p>Group Name: '+ group_name +'</p>\n' +
                            '              <p>Apply Message: '+ items[item][group_name][m] +'</p>\n' +
                            '              <div class="application_list_content"><button class="application_list_btn" id="pass" type="submit"><i class="fa fa-check"></i></button></div>\n' +
                            '              <div class="application_list_content"><button class="application_list_btn" id="fail" type="submit"><i class="fa fa-times"></i></button></div>\n' +
                            '            </div>'
                    }
                }
            }
            div_application_container.innerHTML = application_HTML;
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