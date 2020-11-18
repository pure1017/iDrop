test('click host_submit button', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<div class="text-center"><button type="submit" id="host_submit">Submit</button></div>';

  const $ = require('jquery');

  $('#host_submit').on("click", mockFn);
  $('#host_submit').click();

  expect(mockFn).toBeCalled();
});