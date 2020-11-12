var color_pool = ['success', 'primary', 'info', 'danger', 'warning'];

function renderStatus(code) {
    if (code == 0 ) {
        return '<div class="btn btn-warning">Pending</div>';
    } else if (code == 1) {
        return '<div class="btn btn-success">Approved</div>';
    } else {
        return '<div class="btn btn-danger">Declined</div>';
    }
}

function renderTag(tags) {
    tags_html = '';
    for (var i = 0; i < tags.length; i++) {
        var color = i%5;
        tags_html += '<a class="bg-' + color_pool[color] + '">' + tags[i] + '</a>';
    }
    return tags_html;
}

// render ajax
function renderCourseRequest(all_course_request) {
    $('#cnum').text(all_course_request.length);
    $.each(all_course_request, function(i, request) {
        $('#tabCourse').append(
            '<tr data-request="course-request-' + request['id'] + '">' +
                '<td>' + (i + 1) + '</td>' +
                '<td>' + request['subject_id'] + '</td>' +
                '<td>' + request['course_number'] + '</td>' +
                '<td>' + request['course_name'] + '</td>' +
                '<td>' + request['department_id'] + '</td>' +
                '<td>' + request['term_id'] + '</td>' +
                '<td>' + renderStatus(request['approved']) + '</td>' +
            '</tr>'
        );
    });
}

function renderProfRequest(all_prof_request) {
    $('#pnum').text(all_prof_request.length);
    $.each(all_prof_request, function(i, request) {
        $('#tabProf').append(
            '<tr data-request="prof-request-' + request['id'] + '">' +
                '<td>' + (i + 1) + '</td>' +
                '<td>' + request['name'] + '</td>' +
                '<td>' + request['department_id'] + '</td>' +
                '<td>' + request['course_id'] + '</td>' +
                '<td>' + request['term_id'] + '</td>' +
                '<td>' + renderStatus(request['approved']) + '</td>' +
            '</tr>'
        );
    });
}

function renderComment(all_comment) {
    $.each(all_comment, function(i, current_comment) {
        var rating_html = '';
        var workload_html = '';
        for (var j = 0; j < current_comment['rating']; j++)
            rating_html += '<i class="fa fa-star"></i>';
        for (var j = 0; j < current_comment['workload']; j++)
            workload_html += '<i class="fa fa-pencil"></i>';

        $('.container.card-columns').append(
            '<div class="card">' +
                '<div class="card-body ' + rating_to_color(current_comment['rating']) + '">' +
                    '<button class="btn btn-danger delete" data-toggle="modal" data-target="#deleteModal"><i class="fa fa-trash"></i></button>' +
                    '<button class="btn btn-warning edit" data-toggle="modal" data-target="#commentModal"><i class="fa fa-edit"></i></button>' +
                    '<h5 class="card-title">' + current_comment['title'] + '</h5>' +
                    '<p class="card-text">' + current_comment['content'] + '</p>' +
                    '<a class="btn btn-light" data-toggle="collapse" href="#c' + i + '" role="button" aria-expanded="false" aria-controls="c' + i + '">Show Details <i class="fa fa-caret-down"></i></a>' +
                    '<div class="collapse" id="c' + i + '">' +
                        '<ul class="list-group">' +
                            '<li class="list-group-item">Course: ' + current_comment['subject_id'] + current_comment['course_number'] + '</li>' +
                            '<li class="list-group-item">Instructor: ' + current_comment['professor_name'] + '</li>' +
                            '<li class="list-group-item">Term: ' + current_comment['term_id'] + '</li>' +
                            '<li class="list-group-item">Rating: ' + rating_html + '</li>' +
                            '<li class="list-group-item">Workload: ' + workload_html + '</li>' +
                            '<li class="list-group-item">Grade: ' + current_comment['grade'] + '</li>' +
                            '<li class="list-group-item">Tags: ' + renderTag(current_comment['tags']) + '</li>' +
                            '<li class="list-group-item" style="display:none">' + current_comment['id'] + '</li>' +
                            '<li class="list-group-item" style="display:none">' + current_comment['tags'] + '</li>' +
                            '<li class="list-group-item" style="display:none">' + current_comment['rating'] + '</li>' +
                            '<li class="list-group-item" style="display:none">' + current_comment['workload'] + '</li>' +
                        '</ul>' +
                    '</div>' +
                '</div>' +
            '</div>');

        $('.edit').on('click', function() {
            var index = $('.container.card-columns .card').index($(this).parent().parent());
            var cur_course = $('#c' + index + ' ul li:nth-child(1)').text().split(': ')[1];
            var cur_prof = $('#c' + index + ' ul li:nth-child(2)').text().split(': ')[1];

            var cur_id = $('#c' + index + ' ul li:nth-child(8)').text();
            var cur_title = $('.container.card-columns .card:nth-child(' + (index + 1) + ') h5').text();
            var cur_year = $('#c' + index + ' ul li:nth-child(3)').text().split(': ')[1].split(' ')[1];
            var cur_sem = $('#c' + index + ' ul li:nth-child(3)').text().split(': ')[1].split(' ')[0];
            var cur_grade = $('#c' + index + ' ul li:nth-child(6)').text().split(': ')[1];
            var cur_rating = parseInt($('#c' + index + ' ul li:nth-child(10)').text());
            var cur_workload = parseInt($('#c' + index + ' ul li:nth-child(11)').text());
            var cur_tags = $('#c' + index + ' ul li:nth-child(9)').text();
            var cur_msg = $('.container.card-columns .card:nth-child(' + (index + 1) + ') p').text();

            $('.comment-form input[name=comment-id]').val(cur_id);
            $('input[name=title]').val(cur_title);
            $('select[name=year]').val(cur_year);
            $('select[name=semester]').val(cur_sem);
            $('select[name=grade]').val(cur_grade);
            $('input[name=rating]').val(cur_rating);
            $('input[name=workload]').val(cur_workload);
            $('input[name=tags]').tagsinput('removeAll');
            $('input[name=tags]').tagsinput('add', cur_tags);
            $('textarea[name=message]').val(cur_msg);

            $('.cmt_course').text(cur_course);
            $('#cmt_prof').text(cur_prof);

            for (i = 0; i < cur_rating; i++) {
                var stars = $('#stars li');
                $(stars[i]).addClass('selected');
            }

            for (i = 0; i < cur_workload; i++) {
                var pens = $('#pens li');
                $(pens[i]).addClass('selected');
            }
        });

        $('.delete').on('click', function() {
            var index = $('.container.card-columns .card').index($(this).parent().parent());
            var cur_course = $('#c' + index + ' ul li:nth-child(1)').text().split(': ')[1];
            var cur_id = $('#c' + index + ' ul li:nth-child(8)').text();

            $('.delete-form input[name=comment-id]').val(cur_id);
            $('.cmt_course').text(cur_course);
        });
    });
}

// Pen effect
// hover
$('#pens li').on('mouseover', function() {
    var onPen = parseInt($(this).data('value'));
    $(this).parent().children('li.pen').each(function(e) {
        if (e < onPen) {
            $(this).addClass('hover');
        }
        else {
            $(this).removeClass('hover');
        }
    });
}).on('mouseout', function() {
    $(this).parent().children('li.pen').each(function(e) {
        $(this).removeClass('hover');
    });
});

// Star effect
// hover
$('#stars li').on('mouseover', function() {
    var onStar = parseInt($(this).data('value'));
    $(this).parent().children('li.star').each(function(e) {
        if (e < onStar) {
            $(this).addClass('hover');
        }
        else {
            $(this).removeClass('hover');
        }
    });
}).on('mouseout', function() {
    $(this).parent().children('li.star').each(function(e) {
        $(this).removeClass('hover');
    });
});

// click
$('#stars li').on('click', function() {
    var onStar = parseInt($(this).data('value'), 10);
    var stars = $(this).parent().children('li.star');

    for (i = 0; i < stars.length; i++) {
        $(stars[i]).removeClass('selected');
    }

    for (i = 0; i < onStar; i++) {
        $(stars[i]).addClass('selected');
    }

    // response
    var ratingValue = parseInt($('#stars li.selected').last().data('value'));
    $('#rating').val(ratingValue);
});

// click
$('#pens li').on('click', function() {
    var onPen = parseInt($(this).data('value'), 10);
    var pens = $(this).parent().children('li.pen');

    for (i = 0; i < pens.length; i++) {
        $(pens[i]).removeClass('selected');
    }

    for (i = 0; i < onPen; i++) {
        $(pens[i]).addClass('selected');
    }

    // response
    var workloadValue = parseInt($('#pens li.selected').last().data('value'));
    $('#workload').val(workloadValue);
});

$('.comment-form').submit(function(e){
    if ($('#rating').val() == '') {
        $('#error').text('Please input a rating scale.');
        return false;
    } else if ($('#workload').val() == '') {
        $('#error').text('Please input a workload scale.');
        return false;
    } else {
        return true;
    }
});

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

