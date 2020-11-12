$(function() {
    // init Isotope
    var $grid = $('.cardcol .row').isotope({
        // options
        itemSelector: '.grid',
        getSortData: {
            coursenum: function(itemElem) {
                return $(itemElem).find('.coursenum').text();
            },
            rating: function(itemElem) {
                return parseFloat($(itemElem).find('.rating').text().split(': ')[1]);
            },
            workload: function(itemElem) {
                return parseFloat($(itemElem).find('.workload').text().split(': ')[1]);
            },
            grade: function(itemElem) {
                return parseFloat($(itemElem).find('.grade').text().split(': ')[1]);
            },
            comment: function(itemElem) {
                return parseFloat($(itemElem).find('.comment').text().split(': ')[1]);
            }
        }
    });

    $('#sortby select').on('change', function(){
        var cur_entry = $('#sortby select').val();
        if (cur_entry == 'coursenum') {
            $grid.isotope({
                sortBy: 'coursenum',
                sortAscending: true
            });
        } else if (cur_entry == 'rating') {
            $grid.isotope({
                sortBy: 'rating',
                sortAscending: false
            });
        } else if (cur_entry == 'workload') {
            $grid.isotope({
                sortBy: 'workload',
                sortAscending: true
            });
        } else if (cur_entry == 'grade') {
            $grid.isotope({
                sortBy: 'grade',
                sortAscending: false
            });
        } else if (cur_entry == 'popularity') {
            $grid.isotope({
                sortBy: 'comment',
                sortAscending: false
            });
        }
    });
});

$(".card").each(function(index) {
    var ratingdiv = $(".row .col-md-6:nth-child("+(index+1)+") div:nth-child(4)");
    var gradediv = $(".row .col-md-6:nth-child("+(index+1)+") div:nth-child(5)");
    var workloaddiv = $(".row .col-md-6:nth-child("+(index+1)+") div:nth-child(6)");

    var rating = ratingdiv.attr("data-stat-value");
    var grade = gradediv.attr("data-stat-value");
    var workload = workloaddiv.attr("data-stat-value");

    ratingdiv.addClass(rating_to_color(rating));
    gradediv.addClass(gpa_to_color(grade));
    workloaddiv.addClass(workload_to_color(workload));

    var borderdiv = $(".row .col-md-6:nth-child("+(index+1)+") .card .card-body");
    borderdiv.addClass('border-'+rating_to_color(rating));
});
