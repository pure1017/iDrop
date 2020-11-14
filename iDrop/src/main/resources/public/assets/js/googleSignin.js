document.addEventListener('DOMContentLoaded', function () {
  const signin = document.getElementById("signinButton");
  signin.addEventListener('click', function () {
     console.log("click");
     auth2.grantOfflineAccess().then(signInCallback);
  });

  function signInCallback(authResult, googleUser) {
      console.log(authResult);
      console.log(googleUser);

      if (authResult['code']) {

          // Hide the sign-in button now that the user is authorized, for example:
          // signin.attr('style', 'display: none');
          document.querySelector("#signinButton").style.display = 'none';

        // Send the code to the server
        //   ajax('POST',)
        $.ajax({
          type: 'POST',
          url: 'http://localhost:8080/storeauthcode',
          // Always include an `X-Requested-With` header in every AJAX request,
          // to protect against CSRF attacks.
          headers: {
            'X-Requested-With': 'XMLHttpRequest'
          },
          contentType: 'application/octet-stream; charset=utf-8',
          success: function(result) {
            // Handle or verify the server response.
          },
          processData: false,
          data: authResult['code']
        });
      } else {
        // There was an error.
      }
  }


  /**
   * AJAX helper
   *
   * @param method -
   *            GET|POST|PUT|DELETE
   * @param url -
   *            API end point
   * @param callback -
   *            This the successful callback
   * @param errorHandler -
   *            This is the failed callback
   */
  function ajax(method, url, data, callback, errorHandler) {
      var xhr = new XMLHttpRequest();

      xhr.open(method, url, true);

      xhr.onload = function() {
          if (xhr.status === 200) {
              callback(xhr.responseText);
          } else {
              errorHandler();
          }
      };

      xhr.onerror = function() {
          console.error("The request couldn't be completed.");
          errorHandler();
      };

      if (data === null) {
          xhr.send();
      } else {
          xhr.setRequestHeader("Content-Type",
              "application/x-www-form-urlencoded");
          xhr.send(data);
    }
}
});