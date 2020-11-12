function checkEmpty(id, added) {
    for (var i = 0; i < $("tr[data-request=" + id + "] td").length - 1; i++) {
        var text = "";
        if (added == 0) {
            text = $("tr[data-request=" + id + "] td:nth-child(" + (i + 1) + ")").text();
        } else {
            text = $("tr[data-request=" + id + "] td:nth-child(" + (i + 1) + ") input").val();
        }
        if (text == "") {
            var field = $("#tabProf tr:nth-child(1) th:nth-child(" + (i + 1) + ")").text();
            if (((field == "Avatar") || (field == "Website")) && id.charAt(0) == 'p') {
                continue;
            } else {
                return i + 1;
            }
        }
    }
    return -1;
}

// ajax
function renderCourseRequest(all_course_request) {
    $('#tabCourse tr').slice(1).remove();
    $.each(all_course_request, function(i, request) {
        $('#tabCourse').append(
            '<tr data-request="course-request-' + request['id'] + '">' +
                '<td>' + request['id'] + '</td>' +
                '<td>' + request['subject_id'] + '</td>' +
                '<td>' + request['course_number'] + '</td>' +
                '<td>' + request['course_name'] + '</td>' +
                '<td>' + request['department_id'] + '</td>' +
                '<td>' + request['term_id'] + '</td>' +
                '<td><button class="btn btn-success approve"><i class="fa fa-check"></i></button><button class="btn btn-danger decline"><i class="fa fa-trash"></i></button></td>' +
            '</tr>'
        );
    });
}

function renderProfRequest(all_prof_request) {
    $('#tabProf tr').slice(1).remove();
    $.each(all_prof_request, function(i, request) {
        $('#tabProf').append(
            '<tr data-request="prof-request-' + request['id'] + '">' +
                '<td>' + request['id'] + '</td>' +
                '<td>' + request['name'] + '</td>' +
                '<td></td>' +
                '<td>' + request['department_id'] + '</td>' +
                '<td>' + request['course_id'] + '</td>' +
                '<td>' + request['term_id'] + '</td>' +
                '<td></td>' +
                '<td></td>' +
                '<td><button class="btn btn-success approve"><i class="fa fa-check"></i></button><button class="btn btn-danger decline"><i class="fa fa-trash"></i></button></td>' +
            '</tr>'
        );
    });
}

// tab switching
function switchTab(evt, tab) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(tab).style.display = "block";
    evt.currentTarget.className += " active";
}

// Get the element with id="defaultOpen" and click on it
document.getElementById("defaultOpen").click();


// editable table
// set multiple table editable
function EditTables() {
    for (var i=0;i<arguments.length;i++){
        SetTableCanEdit(arguments[i]);
    }
}

// set editable tables
function SetTableCanEdit(table) {
    for (var i=1; i<table.rows.length;i++) {
        SetRowCanEdit(table.rows[i]);
    }
}

function SetRowCanEdit(row) {
    for (var j=0;j<row.cells.length; j++) {
        // see if the current tab is set to be editable
        var editType = row.cells[j].getAttribute("EditType");
        if (!editType) {
            // if not, see whether the row has
            editType = row.parentNode.rows[0].cells[j].getAttribute("EditType");
        }
        if (editType) {
            row.cells[j].onclick = function (){
                EditCell(this);
            }
        }
    }
}    

// set current editable
function EditCell(element, editType) {
    var editType = element.getAttribute("EditType");
    if (!editType) {
        // if not, see whether the row has
        editType = element.parentNode.parentNode.rows[0].cells[element.cellIndex].getAttribute("EditType");
    }
    switch(editType) {
        case "TextBox":
            CreateTextBox(element, element.innerHTML);
            break;
        case "DropDownList":
            CreateDropDownList(element);
            break;
        default:
            break;
    }
}    

// set editable input box
function CreateTextBox(element, value) {
    // check editable status, if so, skip
    var editState = element.getAttribute("EditState");
    if (editState != "true") {
        // create input
        var textBox = document.createElement("INPUT");
        textBox.type = "text";
        textBox.className="EditCell_TextBox";
        if (!value) {
            value = element.getAttribute("Value");
        }
        textBox.value = value;

        // set lost focus event
        textBox.onblur = function() {
            CancelEditCell(this.parentNode, this.value);
        }

        // add text
        ClearChild(element);
        element.appendChild(textBox);
        textBox.focus();
        textBox.select();

        // change status
        element.setAttribute("EditState", "true");
        element.parentNode.parentNode.setAttribute("CurrentRow", element.parentNode.rowIndex);
    }
}

// cancel editable status
function CancelEditCell(element, value, text) {
    element.setAttribute("Value", value);
    if (text) {
        element.innerHTML = text;
    } else {
        element.innerHTML = value;
    }
    element.setAttribute("EditState", "false");
}    
    
// clear child
function ClearChild(element) {
    element.innerHTML = "";
}    


// notify
function notify(msg, type) {
    $.notify({
        message: msg
    },{
        element: 'body',
        position: 'fixed',
        type: type,
        allow_dismiss: true,
        placement: {
            from: "top",
            align: "center"
        },
        offset: 60,
        spacing: 10,
        z_index: 1031,
        delay: 3000,
        timer: 1000,
        url_target: '_blank',
        animate: {
            enter: 'animated fadeInDown',
            exit: 'animated fadeOutUp'
        },
        onShow: null,
        onShown: null,
        onClose: null,
        onClosed: null,
        icon_type: 'class',
        template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert">' +
            '<span data-notify="icon"></span> ' +
            '<span data-notify="title">{1}</span> ' +
            '<span data-notify="message">{2}</span>' +
            '<div class="progress" data-notify="progressbar">' +
                '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
            '</div>' +
            '<a href="{3}" target="{4}" data-notify="url"></a>' +
        '</div>'
    });
}
