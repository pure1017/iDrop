test('click join_submit button', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<div class="text-center"><button type="submit" id="join_submit">Submit</button></div>';

  const $ = require('jquery');

  $('#join_submit').on("click", mockFn);
  $('#join_submit').click();

  expect(mockFn).toBeCalled();
});