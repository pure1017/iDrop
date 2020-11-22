test('click pass_btn', () => {
  const mockFn = jest.fn();

  const group_name = "groupName";
  const user = "user";
  const message = "message";
  document.body.innerHTML =
    '<div style="clear: left;">\n' +
            '              <p style="float: left;"><img src="assets/img/application_form.jpg" height="200px" width="180px" border="1px" style="margin-right: 20px"></p>\n' +
            '              <p>Group Name: '+ group_name +'</p>\n' +
            '              <p>Apply Message: '+ message +'</p>\n' +
            '              <div class="application_list_content"><button class="application_list_btn" id="p'+ user + group_name +'" type="submit"><i class="fa fa-check"></i></button></div>\n' +
            '              <div class="application_list_content"><button class="application_list_btn" id="f'+ user + group_name +'" type="submit"><i class="fa fa-times"></i></button></div>\n' +
            '            </div>';

  const $ = require('jquery');

  $('#p' + user + group_name).on("click", mockFn);
  $('#p' + user + group_name).click();

  expect(mockFn).toBeCalled();
});

test('click fail_btn', () => {
  const mockFn = jest.fn();

  const group_name = "groupName";
  const user = "user";
  const message = "message";
  document.body.innerHTML =
    '<div style="clear: left;">\n' +
            '              <p style="float: left;"><img src="assets/img/application_form.jpg" height="200px" width="180px" border="1px" style="margin-right: 20px"></p>\n' +
            '              <p>Group Name: '+ group_name +'</p>\n' +
            '              <p>Apply Message: '+ message +'</p>\n' +
            '              <div class="application_list_content"><button class="application_list_btn" id="p'+ user + group_name +'" type="submit"><i class="fa fa-check"></i></button></div>\n' +
            '              <div class="application_list_content"><button class="application_list_btn" id="f'+ user + group_name +'" type="submit"><i class="fa fa-times"></i></button></div>\n' +
            '            </div>';

  const $ = require('jquery');

  $('#f' + user + group_name).on("click", mockFn);
  $('#f' + user + group_name).click();

  expect(mockFn).toBeCalled();
});

test('ajax function', () => {
  const ajax_func = require('../js/my_group');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});