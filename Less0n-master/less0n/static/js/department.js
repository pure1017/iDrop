$('#sort-alphabetical').show();
$('#alphabetical-card').show();
$('#sort-school').hide();
$('#school-card').hide();

$('#sort').click(function(){
    $('#sort-alphabetical').show();
    $('#alphabetical-card').show();
    $('#sort-school').hide();
    $('#school-card').hide();
});

$('#school').click(function(){
    $('#sort-alphabetical').hide();
    $('#alphabetical-card').hide();
    $('#sort-school').show();
    $('#school-card').show();
});

$(function() {
    // init Isotope
    // store filter for each group
    var filters = {};
    var $container = $('#alphabetical-card').isotope({
        itemSelector: '.isotope-item',
        filter: checkIfFilterApplies
    });

    function checkIfFilterApplies(){
        var self = $(this),
            match = true,
            data = self.data();
        for (var prop in filters) {
            match = (filters[prop].min <= data[prop] && filters[prop].max >= data[prop]);
            if (!match) return false;
        }
        return match;
    }

    $('#sort-alphabetical').on('click', 'a', function(){
        var $this = $(this);
        // get group key
        var filterGroup = $this.parent().data('filter-group');
        // set filter for group
        filters[ filterGroup ] = {
            min: $this.data('filter-min') || 'A',
            max: $this.data('filter-max') || 'Z'
        };
        $container.isotope();
    });

    // change is-checked class on buttons
    $('#sort-alphabetical').each(function(i, buttonGroup){
        var $buttonGroup = $(buttonGroup);
        $buttonGroup.on('click', 'a', function(){
            $buttonGroup.find('.selected').removeClass('selected');
            $(this).addClass('selected');
        });
    });

});
