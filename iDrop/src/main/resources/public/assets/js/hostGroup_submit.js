document.addEventListener('DOMContentLoaded', function () {
    const form = {
        bookName: document.getElementById("book_name"),
        groupName: document.getElementById("group_name"),
        beginDate: document.getElementById("begin_date"),
        groupSize: document.getElementById("group_size"),
        groupDescription: document.getElementById("group_description"),
        submit: document.getElementById("host_submit"),
        messages: document.getElementById("form-messages")
    };

    form.submit.addEventListener('click', function () {
        const request = new XMLHttpRequest();
        request.onload = function () {
            let　 responseObject = null;
             try {
                 responseObject = JSON.parse(request.responseText);
             } catch (e) {
                 console.error('Could not parse JSON!');
             }

             if (responseObject) {
                 handleResponse(responseObject);
             }
        };
        const requestData = `bookName=${form.bookName.value}&groupName=${form.groupName.value}
        &beginDate=${form.beginDate.value}&groupSize=${form.groupSize.value}&groupDescription=${form.groupDescription.value}`;

        request.open('post', '/hostGroup?bookName='+form.bookName+'&groupName='+form.groupName+
        '&beginDate='+form.beginDate+'&groupSize='+form.beginDate+'&groupDescription='+form.groupDescription);
        request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        request.send(requestData);
    });

    function handleResponse(responseObject) {
        console.log(responseObject);
        location.href = 'start.html';
    }
});