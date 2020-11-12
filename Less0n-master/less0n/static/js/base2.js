function rating_to_color(n) {
    if (n >= 3.5 && n <= 5)
        return 'good';
    else if (n >= 2.5 && n < 3.5)
        return 'neutral';
    else if (n >=0 && n < 2.5)
        return 'bad';
    else
        return 'bg-secondary';
}

function gpa_to_color(n) {
    if (n >= 3.67 && n <= 4.33)
        return 'good';
    else if (n >= 2.33 && n < 3.67)
        return 'neutral';
    else if (n >=0 && n < 2.33)
        return 'bad';
    else
        return 'bg-secondary';
}

function workload_to_color(n) {
    if (n >= 3.5 && n <= 5)
        return 'bad';
    else if (n >= 2.5 && n < 3.5)
        return 'neutral';
    else if (n >=0 && n < 2.5)
        return 'good';
    else
        return 'bg-secondary';
}

function compareRating(a, b) {
    if (a.rating < b.rating)
        return 1;
    if (a.rating > b.rating)
        return -1;
    return 0;
}

function compareGrade(a, b) {
    var ag = a.grade;
    var bg = b.grade;
    ga = ag.charCodeAt(0);
    gb = bg.charCodeAt(0);
    if (ag.charAt(ag.length-1) == '-') {
        ga += 0.1;
    }
    if (ag.charAt(ag.length-1) == '+') {
        ga -= 0.1;
    }
    if (bg.charAt(bg.length-1) == '-') {
        gb += 0.1;
    }
    if (bg.charAt(bg.length-1) == '+') {
        gb -= 0.1;
    }
    if (ga < gb)
        return -1;
    if (ga > gb)
        return 1;
    return 0;
}

function compareWorkload(a, b) {
    if (a.workload < b.workload)
        return -1;
    if (a.workload > b.workload)
        return 1;
    return 0;
}