test('click rating_button', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<button id="rating-button" type="submit">Rating</button>';

  const $ = require('jquery');

  $('#rating-button').on("click", mockFn);
  $('#rating-button').click();

  expect(mockFn).toBeCalled();
});

test('click close', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<div class="close">+</div>';

  const $ = require('jquery');

  $('.close').on("click", mockFn);
  $('.close').click();

  expect(mockFn).toBeCalled();
});

test('ajax function', () => {
  const ajax_func = require('../js/book_rating');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});