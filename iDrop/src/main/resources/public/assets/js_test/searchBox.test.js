test('click search-submit button', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<button type="submit" id="search-submit" class="searchButton">\n' +
      '          <i class="fa fa-search"/>\n' +
      '        </button>';

  const $ = require('jquery');

  $('#search-submit').on("click", mockFn);
  $('#search-submit').click();

  expect(mockFn).toBeCalled();
});

test('click key enter to trigger search', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<input type="text" id="search-input" class="tbox" placeholder="Book Name"/>';

  const $ = require('jquery');

  $('#search-input').on("keyup", mockFn);
  $('#search-input').trigger("keyup");

  expect(mockFn).toBeCalled();
});


test('ajax function', () => {
  const ajax_func = require('../js/searchBox');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});