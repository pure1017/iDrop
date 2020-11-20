test('click join_submit button', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<div class="text-center"><button type="submit" id="join_submit">Submit</button></div>';

  const $ = require('jquery');

  $('#join_submit').on("click", mockFn);
  $('#join_submit').click();

  expect(mockFn).toBeCalled();
});

test('ajax function', () => {
  const ajax_func = require('../js/joinGroup_submit');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});