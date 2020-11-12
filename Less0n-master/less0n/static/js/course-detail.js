var cur = 0;
var color_pool = ['success', 'primary', 'info', 'danger', 'warning'];
var sortby = 'rating';

$('#sortby').on('change', function() {
    sortby = $('#sortby select').val();
    changeProf(cur, all_profs);
});

function changeProf(index, all_profs) {
    var current_prof = all_profs[index];

    if (sortby == 'rating') {
        current_prof['comments'].sort(compareRating);
    } else if (sortby == 'grade') {
        current_prof['comments'].sort(compareGrade);
    } else {
        current_prof['comments'].sort(compareWorkload);
    }

    if (current_prof['avatar'] == "") {
        $('#prof-pic').attr('src', '/static/img/icon_square.png');
    } else {
        $('#prof-pic').attr('src', current_prof['avatar']);
    }
    $('#faculty_choice h4 a').text(current_prof['name']);
    if (current_prof['uni']) {
        $('#prof-pic').parent().attr('href', '/prof/' + current_prof['uni']);
        $('#faculty_choice h4 a').attr('href', '/prof/' + current_prof['uni']);
    }
    else {
        $('#prof-pic').parent().removeAttr('href');
        $('#faculty_choice h4 a').removeAttr('href');
    }

    $('#tag_list .tags').empty();
    for (var i = 0; i < current_prof['tags'].length; i++) {
        var color = i%5;
        $('#tag_list .tags').append('<a class="' + color_pool[color] + '">' + current_prof['tags'][i] + '</a>');
    }

    $('#rating_progress_bar').css('width', current_prof['rating'] / 5 * 100 + '%');
    $('#rating_progress_bar').removeClass();
    $('#rating_progress_bar').addClass('progress_bar');
    $('#rating_progress_bar').addClass(rating_to_color(current_prof['rating']));
    if (current_prof['rating'] == -1) {
        $('#rating_numerical').text('N/A');
    } else {
        $('#rating_numerical').text(current_prof['rating'].toFixed(2));
    }

    $('#grade_progress_bar').css('width', current_prof['grade'] / 4.33 * 100 + '%');
    $('#grade_progress_bar').removeClass();
    $('#grade_progress_bar').addClass('progress_bar');
    $('#grade_progress_bar').addClass(gpa_to_color(current_prof['grade']))
    if (current_prof['grade'] == -1) {
        $('#grade_numerical').text('N/A');
    } else {
        $('#grade_numerical').text(current_prof['grade'].toFixed(2));
    }

    $('#workload_progress_bar').css('width', current_prof['workload'] / 5 * 100 + '%');
    $('#workload_progress_bar').removeClass();
    $('#workload_progress_bar').addClass('progress_bar');
    $('#workload_progress_bar').addClass(workload_to_color(current_prof['workload']));
    if (current_prof['workload'] == -1) {
        $('#workload_numerical').text('N/A');
    } else {
        $('#workload_numerical').text(current_prof['workload'].toFixed(2));
    }

    // Load comments
    $('.container.card-columns').empty();
    for (var i = 0; i < current_prof['comments'].length; i++) {
        var current_comment = current_prof['comments'][i];

        var rating_html = '';
        var workload_html = '';
        for (var j = 0; j < current_comment['rating']; j++)
            rating_html += '<i class="fa fa-star"></i>';
        for (var j = 0; j < current_comment['workload']; j++)
            workload_html += '<i class="fa fa-pencil"></i>';

        $('.container.card-columns').append(
            '<div class="card">' +
                '<div class="card-body ' + rating_to_color(current_comment['rating']) + '">' +
                    '<h5 class="card-title">' + current_comment['title'] + '</h5>' +
                    '<p class="card-text">' + current_comment['content'] + '</p>' +
                    '<a class="btn btn-light" data-toggle="collapse" href="#c' + i + '" role="button" aria-expanded="false" aria-controls="c' + i + '">Show Details <i class="fa fa-caret-down"></i></a>' +
                    '<div class="collapse" id="c' + i + '">' +
                        '<ul class="list-group">' +
                            '<li class="list-group-item">Term: ' + current_comment['term'] + '</li>' +
                            '<li class="list-group-item">Instructor: ' + current_comment['professor_name'] + '</li>' +
                            '<li class="list-group-item">Rating: ' + rating_html + '</li>' +
                            '<li class="list-group-item">Workload: ' + workload_html + '</li>' +
                            '<li class="list-group-item">Grade: '  + current_comment['grade'] +  '</li>' +
                            '<li class="list-group-item">'  + current_comment['timestamp'] +  '</li>' +
                        '</ul>' +
                    '</div>' +
                '</div>' +
            '</div>');
    }

    // Default prof
    if (current_prof['uni'] == null) {
        $('#professor-selection').val('');
    } else {
        $('#professor-selection').val(current_prof['uni']);
    }
}

$('#left i').click(function() {
    cur--;
    if (cur < 0)
        cur = all_profs.length - 1;
    changeProf(cur, all_profs);
});

$('#right i').click(function() {
    cur++;
    if (cur >= all_profs.length)
        cur = 0;
    changeProf(cur, all_profs);
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