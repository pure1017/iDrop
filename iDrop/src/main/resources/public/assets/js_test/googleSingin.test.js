test('click signinButton', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<li class="get-started"><a id="signinButton" href="#about">Sign in with Google</a></li>';

  const $ = require('jquery');

  $('#signinButton').on("click", mockFn);
  $('#signinButton').click();

  expect(mockFn).toBeCalled();
});

test('ajax function', () => {
  const ajax_func = require('../js/googleSignin');
  var result = ajax_func('POST', '/', {}, function(res) {
                console.log(res);
            }, function() {
                showErrorMessage('error occurs.');
            });
  expect(result).toBe("");
});