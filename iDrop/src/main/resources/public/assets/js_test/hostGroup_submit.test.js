test('click host_submit button', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<div class="text-center"><button type="submit" id="host_submit">Submit</button></div>';

  const $ = require('jquery');

  $('#host_submit').on("click", mockFn);
  $('#host_submit').click();

  expect(mockFn).toBeCalled();
});

test('ajax function', () => {
  const ajax_func = require('../js/hostGroup_submit');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});