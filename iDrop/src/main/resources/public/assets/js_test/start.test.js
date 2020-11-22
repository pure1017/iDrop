test('click favorite_list_btn', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<div class="text-center"><button id="favorite_list" type="submit">Favorite</button></div>';

  const $ = require('jquery');

  $('#favorite_list').on("click", mockFn);
  $('#favorite_list').click();

  expect(mockFn).toBeCalled();
});

test('click refresh_btn', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<div class="text-center"><button id="refresh_btn" type="submit">refresh</i></button></div>';

  const $ = require('jquery');

  $('#refresh_btn').on("click", mockFn);
  $('#refresh_btn').click();

  expect(mockFn).toBeCalled();
});

test('ajax function', () => {
  const ajax_func = require('../js/start');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});