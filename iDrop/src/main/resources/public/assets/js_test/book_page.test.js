test('click favorite_button', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<button id="favorite-button" type="submit"><i class="bx bxs-like"/></button>';

  const $ = require('jquery');

  $('#favorite-button').on("click", mockFn);
  $('#favorite-button').click();

  expect(mockFn).toBeCalled();
});

test('ajax function', () => {
  const ajax_func = require('../js/book_page');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});