test('click nav-menu', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<nav class="nav-menu d-none d-lg-block">'+
    '    <ul>'+
    '      <li class="active"><a href="start.html">Home</a></li>'+
    '      <li><a href="#categories">Categories</a></li>'+
    '      <li><a href="#testimonials">Recommendations</a></li>'+
    '      <li><a href="#group">Group</a></li>'+
    '      <li><a href="#contact">Contact</a></li>'+
    '      <li class="get-started"><a id="signinButton" href="#about">Sign in with Google</a></li>'+
    '    </ul>'+
    '  </nav>'+
    '<a href="#about" class="btn-get-started scrollto">Get Started</a>'
  ;

  const $ = require('jquery');

  $('.nav-menu a, .scrollto').on("click", mockFn);
  $('.nav-menu a, .scrollto').click();

  expect(mockFn).toBeCalled();
});

test('window scroll', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<button type="submit" id="search-submit" class="searchButton">\n' +
      '          <i class="fa fa-search"/>\n' +
      '        </button>';

  const $ = require('jquery');

  $(window).on("scroll", mockFn);
  $(window).trigger("scroll");

  expect(mockFn).toBeCalled();
});

test('back-to-top', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<a href="#" class="back-to-top"><i class="icofont-simple-up"></i></a>';

  const $ = require('jquery');

  $('.back-to-top').on("click", mockFn);
  $('.back-to-top').click();

  expect(mockFn).toBeCalled();
});

test('window load', () => {
  const mockFn = jest.fn();

  document.body.innerHTML =
    '<button type="submit" id="search-submit" class="searchButton">\n' +
      '          <i class="fa fa-search"/>\n' +
      '        </button>';

  const $ = require('jquery');

  $(window).on("load", mockFn);
  $(window).trigger("load");

  expect(mockFn).toBeCalled();
});
